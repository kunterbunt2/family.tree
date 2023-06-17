package de.bushnaq.abdalla.family.tree;

public class PageError {
    private final String error;

    private final Integer pageIndex;

    public PageError(Integer pageIndex, String error) {
        this.pageIndex = pageIndex;
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }
}
