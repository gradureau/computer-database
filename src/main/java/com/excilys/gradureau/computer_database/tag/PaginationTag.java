package com.excilys.gradureau.computer_database.tag;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.excilys.gradureau.computer_database.util.Page;

public class PaginationTag extends SimpleTagSupport {
    private String uri;
    private boolean hasParameter;
    private Page<Object> page;
    private int maxLinks = 5;
    private boolean refreshCount = true;

    public void setRefreshCount(boolean refreshCount) {
        this.refreshCount = refreshCount;
    }

    public void setPage(Page<Object> page) {
        this.page = page;
    }

    public void setUri(String uri) {
        this.uri = uri;
        hasParameter = uri.contains("?");
    }

    public void setMaxLinks(int maxLinks) {
        this.maxLinks = maxLinks;
    }

    @Override
    public void doTag() throws JspException {
        // inspired by
        // http://www.gotoquiz.com/web-coding/programming/java-programming/simple-pagination-taglib-for-jsp/
        JspWriter out = getJspContext().getOut();

        int currPage = page.getCurrentPageNumber();
        int totalPages = page.getLastPageNumber(refreshCount);
        boolean lastPage = currPage == totalPages;
        int pgStart = Math.max(currPage - maxLinks / 2, 1);
        int pgEnd = pgStart + maxLinks;

        if (pgEnd > totalPages + 1) {
            int diff = pgEnd - totalPages;
            pgStart -= diff - 1;
            if (pgStart < 1)
                pgStart = 1;
            pgEnd = totalPages + 1;
        }
        
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\t\t<ul class=\"pagination\">\n");
        
        if(currPage > 1) {
            // GO TO FIRST PAGE
//            stringBuilder.append("\t\t\t<li><a href=\"");
//            addUri(stringBuilder, 1);
//            stringBuilder.append("\" aria-label=\"First Page\"> <span\n");
//            stringBuilder.append("\t\t\t\taria-hidden=\"true\">&laquo;&laquo;</span></a></li>\n");
            
            // GO TO PREVIOUS PAGE
            stringBuilder.append("\t\t\t<li><a href=\"");
            addUri(stringBuilder, currPage - 1);
            stringBuilder.append("\" aria-label=\"Previous\"> <span\n");
            stringBuilder.append("\t\t\t\taria-hidden=\"true\">&laquo;</span></a></li>\n");
        }
        
        // # LINKS
        for (int i = pgStart; i < pgEnd; i++) {
            stringBuilder.append("\t\t\t<li><a href=\"");
            addUri(stringBuilder, i);
            if (i == currPage)
                stringBuilder.append("\" class=\"active\">");
            else
                stringBuilder.append("\">");
            stringBuilder.append(i);
            stringBuilder.append("</a></li>\n");
        }
        
        if(!lastPage) {
            // GO TO NEXT PAGE
            stringBuilder.append(
                    "\t\t\t<li><a href=\"");
            addUri(stringBuilder, currPage + 1);
            stringBuilder.append("\" aria-label=\"Next\"> <span aria-hidden=\"true\">&raquo;</span>\n");
            stringBuilder.append("\t\t\t\t</a></li>\n");
            
            // GO TO LAST PAGE
//            stringBuilder.append(
//                    "\t\t\t<li><a href=\"");
//            addUri(stringBuilder, page.getLastPageNumber());
//            stringBuilder.append("\" aria-label=\"Last Page\"> <span aria-hidden=\"true\">&raquo;&raquo;</span>\n");
//            stringBuilder.append("\t\t\t\t</a></li>\n");
        }
        stringBuilder.append("\t\t</ul>\n");
        stringBuilder.append("\n");
        
        // SET RESULTS PER PAGE
        stringBuilder.append("\t\t\t<div class=\"btn-group btn-group-sm pull-right\" role=\"group\">\n");
        for(int resultsPerPages : Arrays.asList(10, 50, 100)) {
            stringBuilder.append("\t\t\t\t<a href=\"");
            addUri(stringBuilder, page.getStart() / resultsPerPages + 1, resultsPerPages);
            stringBuilder.append("\"><button type=\"button\" class=\"btn btn-default\">");
            stringBuilder.append(resultsPerPages);
            stringBuilder.append("</button></a>\n");
        }
        
        stringBuilder.append("\t\t\t</div>");

        try {
            out.write(stringBuilder.toString());
        } catch (IOException e) {
            throw new JspException("Error in Pagination tag", e);
        }
    }
    
    private void addUri(StringBuilder writer, int page, int resultsPerPageCount) {
        writer.append(uri);
        writer.append(hasParameter ? "&" : "?");
        writer.append("resultsPerPage=");
        writer.append(resultsPerPageCount);
        writer.append("&pageNo=");
        writer.append(page);
    }
    private void addUri(StringBuilder writer, int page) {
        addUri(writer, page, this.page.getResultsPerPageCount());
    }

}
