package ru.mazitov.timur.drivernotes;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ru.mazitov.timur.drivernotes.model.AbstractAction;
import ru.mazitov.timur.drivernotes.model.Action;
import ru.mazitov.timur.drivernotes.model.Car;
import ru.mazitov.timur.drivernotes.model.FuelAction;
import ru.mazitov.timur.drivernotes.model.IAction;
import ru.mazitov.timur.drivernotes.model.Price;
import ru.mazitov.timur.drivernotes.model.RepairAction;
import ru.mazitov.timur.drivernotes.model.Unit;

@Component
public class DriverNotesParser {
	private static Logger log = LoggerFactory.getLogger(DriverNotesParser.class);

	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
	private static final String BASE_URL = "https://www.drivernotes.net";

	private List<Car> cars = new ArrayList<>();

	public void parse(String login, String password) throws ExportException {
		try {
			log.info("Trying to login to {}", BASE_URL);
			Connection.Response loginResponse = Jsoup.connect(BASE_URL + "/login.html")
					.userAgent(USER_AGENT)
					.data("user_login", login)
					.data("user_password", password)
					.method(Method.POST)
					.execute();

			if (loginResponse.statusCode() == 200) {
				log.info("Login successfull!");
			} else {
				if (log.isErrorEnabled()) {
					log.error("Login failed ({}): {}", loginResponse.statusCode(), loginResponse.body());
				}
				throw new ExportException("Login failed!"); 
			}

			String userCabinetUrl = loginResponse.url().toString();
			log.info("Redirect to: {}", userCabinetUrl);

			Document garageDoc = Jsoup.connect(userCabinetUrl).get();
			Elements garages = garageDoc.select("a[href*=" + userCabinetUrl.replace(BASE_URL, "") + "/]");
			Map<String, String> carUrls = new HashMap<>();
			garages.stream()
					.filter(g -> !g.attr("href").endsWith("/posts") && !g.attr("href").endsWith("/posts-comments")
							&& !g.attr("href").endsWith("/posts-favorite") && !g.attr("href").endsWith("/archive"))
					.filter(c -> c.ownText() != null && !c.ownText().isBlank())
					.filter(c -> c.ownText().contains("Yeti"))
					.forEach(c -> carUrls.put(c.attr("abs:href"), c.ownText()));
			for (Map.Entry<String, String> car : carUrls.entrySet()) {
				exportCar(car.getKey(), car.getValue());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void exportCar(String url, String name) {
		Car car = new Car();
		String[] nameParts = name.split(" ");
		car.setName(nameParts[1]);
		car.setBrand(nameParts[0]);
		car.setModel(nameParts[1]);
		this.cars.add(car);

		try {
			Elements actions;
//			int page = 1;
//			do {
			for (int page = 1; page < 2; page++) {
				String actionsUrl = url + "/actions?order=date-desc&count=10&page=" + page;
				log.info("Exporting data for car: {}", actionsUrl);
				Document carDoc = Jsoup.connect(actionsUrl).get();
				actions = carDoc.select("a[href*=/action-]");

				for (Element element : actions) {
					exportAction(car, element.attr("abs:href"));
				}
			}
//				page++;
//			} while (!actions.isEmpty());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		log.info("Total {} actions exported for {}", car.getActions().size(), name);
	}

	private void exportAction(Car car, String url) {
		try {
			log.info("Exporting action: {}", url);
			Document carDoc = Jsoup.connect(url).get();
			Elements details = carDoc.select(".car-details").select("tr");
			String detailsHtml = details.html();

			IAction action;
			if (detailsHtml.contains("<th>Вид топлива</th>")) {
				action = new FuelAction();
			} else if (detailsHtml.contains("<th>Событие</th>")) {
				action = new Action();
			} else {
				action = new RepairAction();
			}
			car.getActions().add(action);

			Unit unit = null;
			boolean unitContext = false;

			for (int i = 0; i < details.size(); i++) {
				Element name = details.get(i).selectFirst("th");
				Element value = details.get(i).select("td").stream()
						.filter(this::filterDetails)
						.findFirst().orElse(null);
				if (getText(name).equals("Узел:")) {
					unitContext = true;
					unit = new Unit();
					((RepairAction) action).getUnits().add(unit);
					parseUnit(unit, name, value);
				} else if (unitContext) {
					parseUnit(unit, name, value);

					if (getText(name).equals("Стоимость:")) {
						unitContext = false;
					}
				} else {
					parseAction(action, name, value);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean filterDetails(Element d) {
		return (d.ownText() != null && !d.ownText().isBlank() || d.html().contains("<span>"))
				&& !d.html().contains("<img ");
	}

	private void parseAction(IAction action, Element name, Element value) {
		String nameText = getText(name);
		String valueText = getText(value);

		switch (nameText) {
		case "Дата:":
			Date date = null;
			if (!valueText.isBlank()) {
				try {
					date = new SimpleDateFormat("dd.MM.yyyy").parse(valueText);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			((AbstractAction) action).setDate(date);
			break;
		case "Пробег:":
			String[] valueParts = valueText.trim().split(" ");
			if (valueParts.length == 2) {
				((AbstractAction) action).setMileage(Integer.parseInt(valueParts[0]));
			}
			break;
		case "Общая стоимость:":
		case "Стоимость:":
			((AbstractAction) action).setTotalPrice(getPrice(valueText));
			break;
		case "Вид топлива":
			((FuelAction) action).setType(valueText);
			break;
		case "АЗС":
		case "Место:":
			((AbstractAction) action).setStation(valueText);
			break;
		case "Кол-во литров:":
			String litrage = valueText.trim();
			if (valueText.contains(" (полный бак)")) {
				((FuelAction) action).setFullTank(true);
				String[] litrageParts = valueText.trim().split(" ");
				litrage = litrageParts[0];
			}
			((FuelAction) action).setAmount(Float.parseFloat(litrage));
			break;
		case "Цена за литр:":
			((FuelAction) action).setPrice(getPrice(valueText));
			break;
		case "Название СТО:":
			((RepairAction) action).setStation(valueText);
			break;
		case "Стоимость работы:":
			((RepairAction) action).setWorkPrice(getPrice(valueText));
			break;
		case "Событие":
			((Action) action).setType(valueText);
			break;
		default:
			if (nameText != null && !nameText.isBlank() || valueText != null && !valueText.isBlank()) {
				log.error("Unknown action element! name={}, value={}", nameText, valueText);
			}
		}
	}

	private void parseUnit(Unit unit, Element name, Element value) {
		String nameText = getText(name);
		String valueText = getText(value);

		switch (nameText) {
		case "Узел:":
			unit.setType(valueText);
			break;
		case "Агрегат (работа):":
			unit.setSubtype(valueText);
			break;
		case "Наименование детали (точное):":
		case "Наименование детали":
		case "Название детали:":
			unit.setName(valueText);
			break;
		case "Производитель:":
			unit.setManufacturer(valueText);
			break;
		case "Номер (модель):":
		case "Номер(модель):":
			unit.setModel(valueText);
			break;
		case "Стоимость:":
			unit.setPrice(getPrice(valueText));
			break;
		default:
			if (nameText != null && !nameText.isBlank() || valueText != null && !valueText.isBlank()) {
				log.error("Unknown unit element! name={}, value={}", nameText, valueText);
			}
		}
	}

	private static String getText(Element element) {
		if (element == null) {
			return "";
		}

		if (element.childrenSize() > 0) {
			for (Element child : element.children()) {
				String text = getText(child);
				if (text != null && !text.isBlank()) {
					return text;
				}
			}
		}

		return element.ownText().trim();
	}

	private static Price getPrice(String value) {
		Price price = new Price();
		if (value != null && !value.isBlank()) {
			String[] valueParts = value.trim().split(" ");
			if (valueParts.length == 2) {
				price.setAmount(Float.parseFloat(valueParts[0]));
				price.setCurrency(valueParts[1]);
			}
		}
		return price;
	}

	public List<Car> getCars() {
		return cars;
	}
}
