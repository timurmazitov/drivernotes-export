package ru.mazitov.timur.drivernotes.model;

import java.util.ArrayList;
import java.util.List;

public class Car {
	private String name;
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
}
