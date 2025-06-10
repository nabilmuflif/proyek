package com.example.curhatku.models;

/**
 * Model untuk Quote response list
 */
public class QuoteResponse {
    private int count;
    private int totalCount;
    private int page;
    private int totalPages;
    private Quote[] results;

    public QuoteResponse() {}

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }

    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public Quote[] getResults() { return results; }
    public void setResults(Quote[] results) { this.results = results; }

    @Override
    public String toString() {
        return "QuoteResponse{" +
                "count=" + count +
                ", totalCount=" + totalCount +
                ", page=" + page +
                ", totalPages=" + totalPages +
                ", results=" + java.util.Arrays.toString(results) +
                '}';
    }
}