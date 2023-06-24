package de.bushnaq.abdalla.family.tree.util;

public class ExpectedResult {
    private int id;
    private float x;
    private float y;

    public ExpectedResult(int id, float x, float y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
