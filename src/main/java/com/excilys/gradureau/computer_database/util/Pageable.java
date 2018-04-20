package com.excilys.gradureau.computer_database.util;

@FunctionalInterface
public interface Pageable {
	Page<?> pagination(int startOffset, int resultsCount);
}
