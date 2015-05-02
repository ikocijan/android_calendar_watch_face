package com.ivankocijan.cleanwatchface;

import com.ivankocijan.cleanwatchface.model.CalendarEvent;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * @author Koc
 *         ivan.kocijan@infinum.hr
 * @since 26.03.15.
 *
 * Helper class used to store user calendar events. This class uses a hash map which stores
 * list of events for individual day with key DD/MM/YYYY
 */
public class CalendarEventsHelper {

    private static final String TIME_FORMAT = "%02dh %02dm";

    private HashMap<String, List<CalendarEvent>> calendarEventHashMap = new HashMap<>();

    private static CalendarEventsHelper instance;

    private CalendarEvent currentEvent;

    public static synchronized CalendarEventsHelper getInstance() {

        if (instance == null) {
            instance = new CalendarEventsHelper();
        }

        return instance;

    }

    private CalendarEventsHelper() {
    }

    /**
     * This method sorts calendar events and if there are no all day events it returns the first
     * event from ArrayList. If there are all day events the method will first check for other events in current day because they have
     * priority over all day events. If there are only all day events for current day this method will return the first all day event.
     *
     * @param currentTime current users time
     * @return next user event or null if there are no events.
     */
    public CalendarEvent getNextEvent(DateTime currentTime) {

        List<CalendarEvent> calendarEvents = getTodayEvents(currentTime);
        Collections.sort(calendarEvents);

        currentEvent = null;

        for (Iterator<CalendarEvent> it = calendarEvents.iterator(); it.hasNext(); ) {

            CalendarEvent calendarEvent = it.next();

            if (calendarEvent.getEndDate() < currentTime.getMillis()) {
                //This event is finished and it should be removed from array list
                calendarEvents.remove(calendarEvent);
                continue;
            }

            if (calendarEvent.isAllDay()) {
                currentEvent = calendarEvent;
            } else {
                updateEventsByDay(getDateKey(currentTime), calendarEvents);
                return currentEvent = calendarEvent;
            }

        }

        updateEventsByDay(getDateKey(currentTime), calendarEvents);
        return currentEvent;

    }


    /**
     * @return all events in hash map saved with key format DD/MM/YYYY
     */
    private List<CalendarEvent> getTodayEvents(DateTime currentTime) {
        return calendarEventHashMap.get(getDateKey(currentTime));
    }


    /**
     * This method can return three type of strings based on @param time and currentEvent status.
     * 1. "All day" if event is an all day event.
     * 2. "Starts in HH:MM" if event is not yet started.
     * 3. "In progress" if event is currently in progress
     *
     * @return String representing the status of next event
     */
    public String getTimeToNextEventString(DateTime time) {

        if (currentEvent != null) {

            if (currentEvent.isAllDay()) {
                return WatchFaceApplication.getInstance().getString(R.string.all_day);
            } else if (currentEvent.getStartDate() > time.getMillis()) {

                Calendar to = Calendar.getInstance();
                to.setTimeInMillis(currentEvent.getStartDate() - time.getMillis());

                return WatchFaceApplication.getInstance().getString(R.string.starts_in) + String
                        .format(TIME_FORMAT, to.get(Calendar.HOUR), to.get(Calendar.MINUTE));

            } else {
                return WatchFaceApplication.getInstance().getString(R.string.in_progress);
            }

        }

        return "";

    }

    /**
     * Removes all events from hash map with selected key and adds new ones
     * @param key
     * @param events
     */
    private void updateEventsByDay(String key, List<CalendarEvent> events) {

        calendarEventHashMap.remove(key);
        calendarEventHashMap.put(key, events);

    }

    /**
     * This method clears all events from hash map and adds new calendar events.
     */
    public void addEvents(List<CalendarEvent> events) {

        calendarEventHashMap.clear();

        for (CalendarEvent event : events) {
            addEvent(event);
        }

    }

    private void addEvent(CalendarEvent event) {

        DateTime dateTime = new DateTime(event.getStartDate());

        String key = getDateKey(dateTime);

        List<CalendarEvent> calendarEvents = calendarEventHashMap.get(key);

        //If there are no events saved with this key hashmap will return null
        if (calendarEvents == null) {
            calendarEvents = new ArrayList<>();
        }

        calendarEvents.add(event);
        calendarEventHashMap.put(getDateKey(dateTime), calendarEvents);


    }

    /**
     * @return a key for hash map base on @param dateTime. The format of the key is MM/DD/YYYY
     */
    private String getDateKey(DateTime dateTime) {

        return dateTime.getDayOfMonth() + "/" + dateTime.getMonthOfYear() + "/" + dateTime.getYear();


    }
}
