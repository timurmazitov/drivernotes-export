package ru.mazitov.timur.drivernotes.model;

public class FuelAction extends AbstractAction {
	private String type;
	private float amount;
	private Price price;
	private boolean fullTank;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public Price getPrice() {
		return price;
	}

	public void setPrice(Price price) {
		this.price = price;
	}

	public boolean isFullTank() {
		return fullTank;
	}

	public void setFullTank(boolean fullTank) {
		this.fullTank = fullTank;
	}
}
