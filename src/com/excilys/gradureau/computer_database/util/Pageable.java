package com.excilys.gradureau.computer_database.util;

@FunctionalInterface
public interface Pageable<T> {
	Page<T> pagination(int startOffset, int resultsCount);
}
