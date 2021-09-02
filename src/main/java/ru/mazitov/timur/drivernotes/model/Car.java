package ru.mazitov.timur.drivernotes.model;

import java.util.ArrayList;
import java.util.List;

public class Car {
	private String name;
	private String brand;
	private String model;
	private int year;

	private List<IAction> actions = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<IAction> getActions() {
		return actions;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}
}
