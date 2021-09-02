package ru.mazitov.timur.drivernotes.drivvo;

import java.util.ArrayList;
import java.util.List;

import ru.mazitov.timur.drivernotes.model.RepairAction;

public class ServiceSection extends AbstractSection<RepairAction> implements ISection<RepairAction> {
	private static final String[] HEADER = { "Vehicle", "Odometer", "Date", "TotalCost", "ServiceName", "Local", "Latitude",
			"Longitude", "Notes" };

	public ServiceSection(String vehicle) {
		this.vehicle = vehicle;
	}

	@Override
	public String[] getHeader() {
		return HEADER;
	}

	@Override
	public String getSectionName() {
		return "##Service";
	}

	@Override
	public Iterable<String[]> getRows() {
		List<String[]> rows = new ArrayList<>();
		objects.forEach(service -> {
			String[] row = { vehicle, String.valueOf(service.getMileage()), getActionDate(service),
					getPrice(service.getTotalPrice()), this.getServiceName(service), service.getStation(), "", "",
					getNotes(service) };
			rows.add(row);
		});

		return rows;
	}

	private String getServiceName(RepairAction service) {
		String serviceName = "";
		if (!service.getUnits().isEmpty()) {
			serviceName = service.getUnits().get(0).getSubtype();
			if (serviceName == null || serviceName.isBlank()) {
				serviceName = service.getUnits().get(0).getName();
			}
			if (serviceName == null || serviceName.isBlank()) {
				serviceName = service.getUnits().get(0).getType();
			}
		}
		return serviceName;
	}

	private String getNotes(RepairAction service) {
		StringBuilder notes = new StringBuilder();

		if (!service.getUnits().isEmpty()) {
			service.getUnits().forEach(a -> {
				notes.append(a.getManufacturer());
				if (a.getModel() != null && !a.getModel().isBlank()) {
					notes.append(" ");
					notes.append(a.getModel());
				}
				notes.append(" = ");
				notes.append(a.getPrice().getAmount());
				notes.append("; ");
			});
		}

		if (service.getWorkPrice() != null) {
			notes.append("Работа = ");
			notes.append(service.getWorkPrice().getAmount());
		}

		return notes.toString();
	}
}
