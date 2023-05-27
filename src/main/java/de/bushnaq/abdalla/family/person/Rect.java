package de.bushnaq.abdalla.family.person;

public class Rect {
	float	x1;

	float	x2;

	float	y1;

	float	y2;

	public Rect(float x1, float y1, float x2, float y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	public void expandToInclude(Rect cr) {
		if (cr.x1 < x1)
			x1 = cr.x1;
		if (cr.x2 > x2)
			x2 = cr.x2;
		if (cr.y1 < y1)
			y1 = cr.y1;
		if (cr.y2 > y2)
			y2 = cr.y2;
	}

	public float getX1() {
		return x1;
	}

	public float getX2() {
		return x2;
	}

	public float getY1() {
		return y1;
	}

	public float getY2() {
		return y2;
	}
}
