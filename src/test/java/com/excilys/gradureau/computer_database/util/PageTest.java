package com.excilys.gradureau.computer_database.util;

import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class PageTest {

    static final int INITIAL_OFFSET = 0;
    static final int RESULTS_COUNT = 5;

    static Pageable pageable = new Pageable() {
        @Override
        public Page<?> pagination(int startOffset, int resultsCount) {
            return new Page<>(
                    Arrays.asList(startOffset + 0, startOffset + 1, startOffset + 2, startOffset + 3, startOffset + 4),
                    startOffset, resultsCount, this);
        }
    };
    static Page<?> firstPage = pageable.pagination(INITIAL_OFFSET, RESULTS_COUNT);

    @Test
    public void getNextPage() {
        assertSame(INITIAL_OFFSET + RESULTS_COUNT, firstPage.getNextPage().getContent().get(0));
    }

}
