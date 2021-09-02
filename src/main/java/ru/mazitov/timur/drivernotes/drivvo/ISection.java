package ru.mazitov.timur.drivernotes.drivvo;

import java.util.Collection;

public interface ISection<A> {
	String[] getHeader();

	String getSectionName();

	Iterable<String[]> getRows();

	void setObjects(Collection<A> objects);

	boolean hasRows();
}
