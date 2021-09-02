package ru.mazitov.timur.drivernotes.drivvo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;

import ru.mazitov.timur.drivernotes.model.AbstractAction;
import ru.mazitov.timur.drivernotes.model.Price;

public abstract class AbstractSection<A> {
	protected String vehicle;
	protected Collection<A> objects;

	public void setObjects(Collection<A> objects) {
		this.objects = objects;
	}

	public boolean hasRows() {
		return !this.objects.isEmpty();
	}

	protected static String getActionDate(AbstractAction action) {
		if (action.getDate() == null) {
			return "";
		}

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		return dateFormat.format(action.getDate());
	}

	protected static String getPrice(Price price) {
		if (price == null) {
			return "";
		}

		return String.valueOf(price.getAmount());
	}

	protected static String getBoolean(boolean bool) {
		return bool ? "Yes" : "No";
	}
}
