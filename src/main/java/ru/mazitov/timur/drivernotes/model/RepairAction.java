package ru.mazitov.timur.drivernotes.model;

import java.util.ArrayList;
import java.util.List;

public class RepairAction extends AbstractAction {
	private List<Unit> units = new ArrayList<>();
	private Price workPrice;

	public List<Unit> getUnits() {
		return units;
	}

	public Price getWorkPrice() {
		return workPrice;
	}

	public void setWorkPrice(Price workPrice) {
		this.workPrice = workPrice;
	}

}
