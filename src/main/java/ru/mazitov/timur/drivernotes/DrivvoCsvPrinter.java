package ru.mazitov.timur.drivernotes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ru.mazitov.timur.drivernotes.model.AbstractAction;
import ru.mazitov.timur.drivernotes.model.Action;
import ru.mazitov.timur.drivernotes.model.Car;
import ru.mazitov.timur.drivernotes.model.FuelAction;
import ru.mazitov.timur.drivernotes.model.RepairAction;

@Component
public class DrivvoCsvPrinter {
	private static Logger log = LoggerFactory.getLogger(DrivvoCsvPrinter.class);

	private static final char LF = '\n';
	private static final char BACKSLASH = '\\';
	private static final String[] FUEL_HEADERS = { "Одометр (km)", "Дата", "Топливо", "Цена / L", "Общая стоимость", "Объем",
			"Полный бак", "Второе топливо", "Цена / L", "Общая стоимость", "Объем", "Полный бак", "Третье топливо", "Цена / L",
			"Общая стоимость", "Объем", "Полный бак", "Эффективный расход топлива", "Расстояние", "Азс", "Тип расхода",
			"Примечание" };
	private static final String[] EXPENSE_HEADERS = { "Одометр (km)", "Дата", "Общая стоимость", "Вид расхода",
			"Местный расход", "Тип расхода", "Примечание" };
	private static final String[] REPAIR_HEADERS = { "Одометр (km)", "Дата", "Общая стоимость", "Вид сервиса",
			"Название сервиса", "Примечание" };

	public void print(String path, Car car) {
		File file = new File(path + car.getName().replace(" ", "_") + ".csv");
		log.info("Exporting Drivvo csv: {}", file.getAbsolutePath());

		try (FileWriter out = new FileWriter(file)) {
			appendFuelActions(car, out);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try (FileWriter out = new FileWriter(file, true)) {
			appendActions(car, out);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try (FileWriter out = new FileWriter(file, true)) {
			appendRepairActions(car, out);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private void appendFuelActions(Car car, FileWriter out) throws IOException {
		List<FuelAction> fuelActions = car.getActions().stream().filter(FuelAction.class::isInstance)
				.map(FuelAction.class::cast).collect(Collectors.toList());
		if (fuelActions.isEmpty()) {
			return;
		}

		out.append("##Refuelling");
		out.append(LF);
		out.append(Stream.of(FUEL_HEADERS).collect(Collectors.joining("\",\"", "\"", "\"")));
		out.append(LF);
		try (CSVPrinter printer = new CSVPrinter(out,
				CSVFormat.DEFAULT.withQuoteMode(QuoteMode.ALL).withRecordSeparator(LF).withEscape(BACKSLASH))) {
			for (FuelAction action : fuelActions) {
				printer.printRecord(action.getMileage(), getActionDate(action),
						action.getType(), action.getPrice().getAmount(), action.getTotalPrice().getAmount(),
						action.getAmount(), action.isFullTank() ? "Да" : "Нет", "", "0", "0", "0", "Нет", "", "0", "0",
						"0", "Нет", "0 L/100km", "", action.getStation(), "", "");
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void appendActions(Car car, FileWriter out) throws IOException {
		List<Action> actions = car.getActions().stream().filter(Action.class::isInstance).map(Action.class::cast)
				.collect(Collectors.toList());
		if (actions.isEmpty()) {
			return;
		}

		out.append(LF);
		out.append("##Expense");
		out.append(LF);
		out.append(Stream.of(EXPENSE_HEADERS).collect(Collectors.joining("\",\"", "\"", "\"")));
		out.append(LF);
		try (CSVPrinter printer = new CSVPrinter(out,
				CSVFormat.DEFAULT.withQuoteMode(QuoteMode.ALL).withRecordSeparator(LF).withEscape(BACKSLASH))) {
			for (Action action : actions) {
				printer.printRecord(action.getMileage(), getActionDate(action), action.getTotalPrice().getAmount(),
						action.getType(), action.getStation(), "", "");
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void appendRepairActions(Car car, FileWriter out) throws IOException {
		List<RepairAction> repairActions = car.getActions().stream().filter(RepairAction.class::isInstance)
				.map(RepairAction.class::cast).collect(Collectors.toList());
		if (repairActions.isEmpty()) {
			return;
		}

		out.append(LF);
		out.append("##Service");
		out.append(LF);
		out.append(Stream.of(REPAIR_HEADERS).collect(Collectors.joining("\",\"", "\"", "\"")));
		out.append(LF);
		try (CSVPrinter printer = new CSVPrinter(out,
				CSVFormat.DEFAULT.withQuoteMode(QuoteMode.ALL).withRecordSeparator(LF).withEscape(BACKSLASH))) {
			for (RepairAction action : repairActions) {
				String actionType = "";
				StringBuilder comments = new StringBuilder();

				if (!action.getUnits().isEmpty()) {
					actionType = action.getUnits().get(0).getSubtype();
					if (actionType == null || actionType.isBlank()) {
						actionType = action.getUnits().get(0).getName();
					}
					if (actionType == null || actionType.isBlank()) {
						actionType = action.getUnits().get(0).getType();
					}

					action.getUnits().forEach(a -> {
						comments.append(a.getManufacturer());
						if (a.getModel() != null && !a.getModel().isBlank()) {
							comments.append(" ");
							comments.append(a.getModel());
						}
						comments.append(" = ");
						comments.append(a.getPrice().getAmount());
						comments.append("; ");
					});
				}

				if (action.getWorkPrice() != null) {
					comments.append("Работа = ");
					comments.append(action.getWorkPrice().getAmount());
				}

				printer.printRecord(action.getMileage(), getActionDate(action), action.getTotalPrice().getAmount(), actionType,
						action.getStation(), comments);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private String getActionDate(AbstractAction action) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return dateFormat.format(action.getDate());
	}
}
