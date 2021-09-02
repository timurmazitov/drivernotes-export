package ru.mazitov.timur.drivernotes.drivvo;

import java.util.ArrayList;
import java.util.List;

import ru.mazitov.timur.drivernotes.model.Action;

public class ExpenseSection extends AbstractSection<Action> implements ISection<Action> {
	private static final String[] HEADER = { "Vehicle", "Odometer", "Date", "TotalCost", "ExpenseName", "Local", "Latitude",
			"Longitude", "Reason", "Notes" };

	public ExpenseSection(String vehicle) {
		this.vehicle = vehicle;
	}

	@Override
	public String[] getHeader() {
		return HEADER;
	}

	@Override
	public String getSectionName() {
		return "##Expense";
	}

	@Override
	public Iterable<String[]> getRows() {
		List<String[]> rows = new ArrayList<>();
		objects.forEach(e -> {
			String[] row = { vehicle, String.valueOf(e.getMileage()), getActionDate(e), getPrice(e.getTotalPrice()),
					e.getType(), e.getStation(), "", "", "", "" };
			rows.add(row);
		});

		return rows;
	}
}
