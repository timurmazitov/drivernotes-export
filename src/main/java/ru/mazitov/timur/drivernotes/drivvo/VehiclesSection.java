package ru.mazitov.timur.drivernotes.drivvo;

import java.util.ArrayList;
import java.util.List;

import ru.mazitov.timur.drivernotes.model.Car;

public class VehiclesSection extends AbstractSection<Car> implements ISection<Car> {
	private static final String[] HEADER = { "Name", "Brand", "Model", "Plate", "FuelCapacity", "Year", "Notes" };

	@Override
	public String[] getHeader() {
		return HEADER;
	}

	@Override
	public String getSectionName() {
		return "##Vehicles";
	}

	@Override
	public Iterable<String[]> getRows() {
		List<String[]> rows = new ArrayList<>();
		objects.forEach(c -> {
			String[] row = { c.getName(), c.getBrand(), c.getModel(), "", "", String.valueOf(c.getYear()), "" };
			rows.add(row);
		});

		return rows;
	}
}
