package de.bushnaq.abdalla.family.tree;

public class PageError {
	private String	error;

	private Integer	pageIndex;

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
