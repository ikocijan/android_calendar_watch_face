package com.ivankocijan.cleanwatchface.model;

/**
 * @author Koc
 *         ivan.kocijan@infinum.hr
 * @since 21.03.15.
 */
public class CalendarEvent implements Comparable<CalendarEvent> {

    private String title;

    private long startDate;

    private long endDate;

    private boolean isAllDay = false;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public boolean isAllDay() {
        return isAllDay;
    }

    public void setAllDay(boolean isAllDay) {
        this.isAllDay = isAllDay;
    }

    @Override
    public int compareTo(CalendarEvent another) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        if (another == null) {
            return BEFORE;
        }

        if (another.getStartDate() < getStartDate()) {
            return AFTER;
        }

        if (another.getStartDate() > getStartDate()) {
            return BEFORE;
        }

        return EQUAL;

    }
}
