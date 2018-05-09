package com.excilys.gradureau.computer_database.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class PageTest {

    static final int INITIAL_OFFSET = 0;
    static final int RESULTS_COUNT = 5;
    static final List<Character> ALPHABET = Arrays.asList("ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("")).stream().map(s -> s.charAt(0)).collect(Collectors.toList());

    static Pageable pageable = new Pageable() {
        @Override
        public Page<?> pagination(int startOffset, int resultsCount) {
            return new Page<>(
                    ALPHABET.subList(startOffset, startOffset + resultsCount),
                    startOffset, resultsCount, this);
        }
    };
    static Page<?> firstPage = pageable.pagination(INITIAL_OFFSET, RESULTS_COUNT);

    @Test
    public void getContent() {
        char firstLetter = (Character)firstPage.getContent().get(0);
        assertSame('A', firstLetter);
    }
    
    @Test
    public void getNextPage() {
        char sixthLetter = (Character)firstPage.getNextPage().getContent().get(0);
        assertSame('F', sixthLetter);        
    }
    
    @Test
    public void getPreviousPageFromNextPage() {
        char secondLetter = (Character)firstPage.getNextPage().getPreviousPage().getContent().get(1);
        assertSame('B', secondLetter);
        assertSame(secondLetter, firstPage.getPreviousPage().getContent().get(1));
    }
    
    @Test
    public void wrongOffsets() {
        assertThrows(IndexOutOfBoundsException.class, ()->{new Page<Object>(null,-1,10);});
        assertThrows(IndexOutOfBoundsException.class, ()->{new Page<Object>(null,0,-1);});
        assertThrows(IndexOutOfBoundsException.class, ()->{new Page<Object>(null,0,0);});
    }
    
    @Test
    public void getPageFromPageNumber() {
        assertEquals(
                firstPage.getContent(),
                firstPage.getPageFromPageNumber(1).getContent()
                );
    }
    
    @Test
    public void hasPreviousPage() {
        assertFalse(firstPage.hasPreviousPage());
    }
    
    @Test
    public void hasNextPage() {
        assertTrue(firstPage.hasNextPage());
    }
    
    @Test
    public void iterator() {
        int i = 0;
        for(@SuppressWarnings("unused") Object c : firstPage)
            ++i;
        assertSame(RESULTS_COUNT, i);
    }
    
    @Test
    public void getCurrentPageNumber() {
        assertSame(1, firstPage.getCurrentPageNumber());
    }
    
    @Test
    public void getLastPageNumber() {
        assertSame(0, firstPage.getLastPageNumber());
        Supplier<Long> counter = () -> (long)ALPHABET.size();
        firstPage.setTotalResultsCounter(counter);
        assertSame(26/5+1, firstPage.getLastPageNumber());
        assertSame(26/5+1, firstPage.getLastPageNumber());
        assertSame(26/5+1, firstPage.getLastPageNumber(true));
    }

}
