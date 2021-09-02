package ru.mazitov.timur.drivernotes.model;

import java.util.Date;

public abstract class AbstractAction implements IAction {
	private Date date;
	private int mileage;
	private String station;
	private Price totalPrice;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getMileage() {
		return mileage;
	}

	public void setMileage(int mileage) {
		this.mileage = mileage;
	}

	public Price getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Price totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getStation() {
		return station;
	}

	public void setStation(String station) {
		this.station = station;
	}
}
