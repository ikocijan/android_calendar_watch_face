package com.ivankocijan.cleanwatchface.mvp.interactor;

import com.ivankocijan.cleanwatchface.mvp.listener.CalendarEventListener;

/**
 * @author Koc
 *         ivan.kocijan@infinum.hr
 * @since 21.03.15.
 */
public interface CalendarInteractor {

    void fetchCalendarEvents(CalendarEventListener listener);
    
}
