package de.bushnaq.abdalla.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class FlexibleDate {
    private final long time;
    private Date date = null;
    private Integer day;
    private Integer month;
    private Integer year;

    public FlexibleDate(Date date) {
        this.date = date;
        time = date.getTime();
    }

    public FlexibleDate(int year) {
        this.year = year;
        time = new GregorianCalendar(year, 0, 1).getTime().getTime();
    }

    public boolean before(FlexibleDate other) {
        return time < other.getTime();
    }

    public int compareTo(FlexibleDate other) {
        return (time < other.getTime() ? -1 : (time == other.getTime() ? 0 : 1));
    }

    public Date getDate() {
        return date;
    }

    public Integer getDay() {
        return day;
    }

    public Integer getMonth() {
        return month;
    }

    public String getString() {
        if (getDate() != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return simpleDateFormat.format(getDate());
        } else {
            return String.valueOf(year);
        }
    }

    public long getTime() {
        return time;
    }

    public Integer getYear() {
        return year;
    }

}
