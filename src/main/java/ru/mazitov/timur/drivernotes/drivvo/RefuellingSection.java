package ru.mazitov.timur.drivernotes.drivvo;

import java.util.ArrayList;
import java.util.List;

import ru.mazitov.timur.drivernotes.model.FuelAction;

public class RefuellingSection extends AbstractSection<FuelAction> implements ISection<FuelAction> {
	private static final String[] HEADER = { "Vehicle", "Odometer", "Date", "TotalCost", "Price", "Amount", "Fuel", "FullTank",
			"FreeCharging", "GasStation", "Latitude", "Longitude", "Reason", "ForgetLast", "Notes" };
	public RefuellingSection(String vehicle) {
		this.vehicle = vehicle;
	}

	@Override
	public String[] getHeader() {
		return HEADER;
	}

	@Override
	public String getSectionName() {
		return "##Refuelling";
	}

	@Override
	public Iterable<String[]> getRows() {
		List<String[]> rows = new ArrayList<>();
		objects.forEach(f -> {
			String[] row = { vehicle, String.valueOf(f.getMileage()), getActionDate(f), getPrice(f.getTotalPrice()),
					getPrice(f.getPrice()), String.valueOf(f.getAmount()), f.getType(), getBoolean(f.isFullTank()),
					"", f.getStation(), "", "", "", "", "" };
			rows.add(row);
		});

		return rows;
	}
}
