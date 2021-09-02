package ru.mazitov.timur.drivernotes.drivvo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ru.mazitov.timur.drivernotes.model.Action;
import ru.mazitov.timur.drivernotes.model.Car;
import ru.mazitov.timur.drivernotes.model.FuelAction;
import ru.mazitov.timur.drivernotes.model.RepairAction;

/**
 * see model https://drivvo.page.link/doc_import_model
 */
@Component
public class DrivvoCsvPrinter {
	private static Logger log = LoggerFactory.getLogger(DrivvoCsvPrinter.class);

	private static final char LF = '\n';

	public void print(String path, Car car) {
		File file = new File(path + car.getName().replace(" ", "_") + ".csv");
		log.info("Exporting Drivvo csv: {}", file.getAbsolutePath());
		
		ISection<Car> vehiclesSection = new VehiclesSection();
		vehiclesSection.setObjects(Arrays.asList(car));
		writeSection(file, vehiclesSection, false);
		
		ISection<FuelAction> refuellingSection = new RefuellingSection(car.getName());
		List<FuelAction> fuelActions = car.getActions().stream().filter(FuelAction.class::isInstance)
				.map(FuelAction.class::cast).collect(Collectors.toList());
		refuellingSection.setObjects(fuelActions);
		writeSection(file, refuellingSection, true);
		
		ISection<RepairAction> serviceSection = new ServiceSection(car.getName());
		List<RepairAction> serviceActions = car.getActions().stream().filter(RepairAction.class::isInstance)
				.map(RepairAction.class::cast).collect(Collectors.toList());
		serviceSection.setObjects(serviceActions);
		writeSection(file, serviceSection, true);

		ISection<Action> expenseSection = new ExpenseSection(car.getName());
		List<Action> expenseActions = car.getActions().stream().filter(Action.class::isInstance)
				.map(Action.class::cast).collect(Collectors.toList());
		expenseSection.setObjects(expenseActions);
		writeSection(file, expenseSection, true);
	}

	private void writeSection(File file, ISection section, boolean append) {
		if (!section.hasRows()) {
			return;
		}

		try (FileWriter out = new FileWriter(file, append)) {
			if (append) {
				out.append(LF);
			}
			out.append(section.getSectionName());
			out.append(LF);
			out.append(Stream.of(section.getHeader()).collect(Collectors.joining("\",\"", "\"", "\"")));
			out.append(LF);
			print(section, out);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private void print(ISection section, FileWriter out) throws IOException {
		try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withRecordSeparator(LF))) {
			printer.printRecords(section.getRows());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
