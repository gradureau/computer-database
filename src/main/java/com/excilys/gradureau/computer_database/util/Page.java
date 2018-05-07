package com.excilys.gradureau.computer_database.util;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Page<T> implements Iterable<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Page.class);
    
    private List<T> content;
    private boolean hasPreviousPage, hasNextPage;
    private Pageable pageable;
    private Supplier<Long> totalResultsCounter;

    // the start offset (!= page number), and the number of results per page (!= total)
    private int start, resultsPerPageCount;
    
    private Long total;

    public Page(List<T> content, int start, int resultsCount) {
        super();
        if (start < 0) {
            throw new IndexOutOfBoundsException("start offset should be >= 0");
        }
        if (resultsCount <= 0) {
            throw new IndexOutOfBoundsException("expected results count should be positive");
        }
        this.content = content;
        this.start = start;
        this.resultsPerPageCount = resultsCount;
        hasPreviousPage = (start > 0);
        hasNextPage = (content.size() == resultsCount);
    }
    public Page(List<T> content, int start, int resultsCount, Pageable pageable) {
        this(content, start, resultsCount);
        this.pageable =  pageable;
    }

    public Page<?> getNextPage() {
        Page<?> nextPage = pageable.pagination(start + resultsPerPageCount, resultsPerPageCount);
        nextPage.setPageable(pageable);
        nextPage.setTotalResultsCounter(totalResultsCounter);
        return nextPage;
    }

    public Page<?> getPreviousPage() {
        int offset = start - resultsPerPageCount;
        if (offset < 0) {
            offset = 0;
        }
        Page<?> previousPage = pageable.pagination(offset, resultsPerPageCount);
        previousPage.setPageable(pageable);
        previousPage.setTotalResultsCounter(totalResultsCounter);
        return previousPage;
    }
    
    public Page<?> getPageFromPageNumber(int pageNumber) {
        int offset = (pageNumber - 1) * resultsPerPageCount;
        Page<?> requestedPage = pageable.pagination(offset, resultsPerPageCount);
        requestedPage.setPageable(pageable);
        requestedPage.setTotalResultsCounter(totalResultsCounter);
        return requestedPage;
    }

    public List<T> getContent() {
        return content;
    }

    public boolean hasPreviousPage() {
        return hasPreviousPage;
    }

    public boolean hasNextPage() {
        return hasNextPage;
    }

    public int getStart() {
        return start;
    }

    public int getResultsPerPageCount() {
        return resultsPerPageCount;
    }

    public Pageable getPageable() {
        return pageable;
    }

    public void setPageable(Pageable pageable) {
        this.pageable = pageable;
    }
    
    public Supplier<Long> getTotalResultsCounter() {
        return totalResultsCounter;
    }
    
    public void setTotalResultsCounter(Supplier<Long> totalResultsCounter) {
        this.totalResultsCounter = totalResultsCounter;
    }
    
    public long getTotal() {
        return total;
    }
    
    public long getTotal(boolean refresh) {
        if(refresh) {
            if(totalResultsCounter != null) {
                total = totalResultsCounter.get();
            } else {
                LOGGER.warn("no result counter set, cannot update total");
            }
        }
        return total;
    }

    public int getCurrentPageNumber() {
        return start / resultsPerPageCount + 1;
    }
    
    public int getLastPageNumber() {
        return getLastPageNumber(false);
    }
    
    public int getLastPageNumber(boolean refresh) {
        long total = getTotal(refresh);
        return (int) (
                total / resultsPerPageCount
                + (total % resultsPerPageCount == 0 ? 0 : 1)
                );
    }

    @Override
    public Iterator<T> iterator() {
        return content.iterator();
    }


}
