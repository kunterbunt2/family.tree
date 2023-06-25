package de.bushnaq.abdalla.family.tree.util;

public class ExpectedResult {
    private int id;
    private float x;
    private float y;
    private int pageIndex;

    public ExpectedResult(int id, float x, float y, int pageIndex) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.pageIndex = pageIndex;
    }

    public int getId() {
        return id;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
