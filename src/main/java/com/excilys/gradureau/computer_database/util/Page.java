package com.excilys.gradureau.computer_database.util;

import java.util.Iterator;
import java.util.List;

public class Page<T> implements Iterable<T> {
    private List<T> content;
    private boolean hasPreviousPage, hasNextPage;
    private Pageable pageable;

    private int start, resultsCount;

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
        this.resultsCount = resultsCount;
        hasPreviousPage = (start > 0);
        hasNextPage = (content.size() == resultsCount);
    }
    public Page(List<T> content, int start, int resultsCount, Pageable pageable) {
        this(content, start, resultsCount);
        this.pageable =  pageable;
    }

    public Page<?> getNextPage() {
        return pageable.pagination(start + resultsCount, resultsCount);
    }

    public Page<?> getPreviousPage() {
        int offset = start - resultsCount;
        if (offset < 0) {
            offset = 0;
        }
        return pageable.pagination(offset, resultsCount);
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

    public Pageable getPageable() {
        return pageable;
    }

    public void setPageable(Pageable pageable) {
        this.pageable = pageable;
    }

    @Override
    public Iterator<T> iterator() {
        return content.iterator();
    }

}
