package com.ivankocijan.cleanwatchface.mvp.listener;

import com.ivankocijan.cleanwatchface.model.CalendarEvent;

import java.util.List;

/**
 * @author Koc
 *         ivan.kocijan@infinum.hr
 * @since 21.03.15.
 */
public interface CalendarEventListener {

    void onSuccess(List<CalendarEvent> calendarEventList);

    void onError();

}
