package com.excilys.gradureau.computer_database.util;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

public class Page<T extends Object> implements Iterable<T> {
	private List<T> content;
	private boolean hasPreviousPage, hasNextPage;
	private Supplier<Page<T>> previousPageProvider, nextPageProvider;
	private int start, resultsCount;
	
	public Page(List<T> content, int start, int resultsCount) {
		super();
		if(start <0) throw new IndexOutOfBoundsException("start offset should be >= 0");
		if(resultsCount <=0) throw new IndexOutOfBoundsException("expected results count should be positive");
		this.content = content;
		this.start = start;
		this.resultsCount = resultsCount;
		hasPreviousPage = ( start > 0 );
		hasNextPage = ( content.size() == resultsCount );
	}
	
	public Page<T> getNextPage() {
		return nextPageProvider.get();
	}
	
	public Page<T> getPreviousPage() {
		return previousPageProvider.get();
	}

	public List<T> getContent() {
		return content;
	}

	public boolean isHasPreviousPage() {
		return hasPreviousPage;
	}

	public boolean isHasNextPage() {
		return hasNextPage;
	}

	public int getStart() {
		return start;
	}

	public int getResultsCount() {
		return resultsCount;
	}

	@Override
	public Iterator<T> iterator() {
		return content.iterator();
	}
	
}
