package com.ivankocijan.cleanwatchface.mvp.interactor.impl;

import com.ivankocijan.cleanwatchface.WatchFaceApplication;
import com.ivankocijan.cleanwatchface.model.CalendarEvent;
import com.ivankocijan.cleanwatchface.mvp.interactor.CalendarInteractor;
import com.ivankocijan.cleanwatchface.mvp.listener.CalendarEventListener;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.support.wearable.provider.WearableCalendarContract;
import android.text.format.DateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Koc
 *         ivan.kocijan@infinum.hr
 * @since 21.03.15.
 *
 * This interactor fetches user calendar events.
 *
 */
public class CalendarInteractorImpl implements CalendarInteractor {

    @Override
    public void fetchCalendarEvents(final CalendarEventListener listener) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                List<CalendarEvent> calendarEventList = new ArrayList<>();

                long begin = System.currentTimeMillis();
                Uri.Builder builder =
                        WearableCalendarContract.Instances.CONTENT_URI.buildUpon();
                ContentUris.appendId(builder, begin);
                ContentUris.appendId(builder, begin + (2 * DateUtils.DAY_IN_MILLIS));

                //TODO check out why "begin ASC" in query returns java.lang.IllegalArgumentException: Sort order is not supported.
                final Cursor cursor = WatchFaceApplication.getInstance().getContentResolver().query(builder.build(),
                        new String[]{"title", "begin", "end", "allDay"}, null,
                        null, null);

                while (cursor.moveToNext()) {

                    CalendarEvent event = new CalendarEvent();
                    event.setTitle(cursor.getString(0));
                    event.setStartDate(cursor.getLong(1));
                    event.setEndDate(cursor.getLong(2));
                    event.setAllDay(cursor.getInt(3) == 1);
                    calendarEventList.add(event);


                }

                cursor.close();

                listener.onSuccess(calendarEventList);

            }
        }).start();


    }
}
