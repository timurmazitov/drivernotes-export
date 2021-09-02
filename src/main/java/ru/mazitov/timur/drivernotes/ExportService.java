package ru.mazitov.timur.drivernotes;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ru.mazitov.timur.drivernotes.model.Car;

@Service
public class ExportService {
	private static Logger log = LoggerFactory.getLogger(ExportService.class);

	private DriverNotesParser parser;
	private DrivvoCsvPrinter csvPrinter;
	private JsonPrinter jsonPrinter;

	@Value("${login}")
	private String login;
	@Value("${password}")
	private String password;
	@Value("${output}")
	private String output;
	@Value("${outputTypes}")
	String outputTypes;

	@Autowired
	public ExportService(DriverNotesParser parser, DrivvoCsvPrinter csvPrinter, JsonPrinter jsonPrinter) {
		this.parser = parser;
		this.csvPrinter = csvPrinter;
		this.jsonPrinter = jsonPrinter;
	}

	public void export() throws ExportException {
		log.info("Export started...");
		parser.parse(login, password);
		List<Car> cars = parser.getCars();

		for (Car car : cars) {
			if (outputTypes.toUpperCase().contains("DRIVVO")) {
				csvPrinter.print(output, car);
			}

			if (outputTypes.toUpperCase().contains("JSON")) {
				jsonPrinter.print(output, car);
			}
		}
		log.info("Export finished.");
	}
}
