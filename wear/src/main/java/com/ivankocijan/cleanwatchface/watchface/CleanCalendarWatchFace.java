package com.ivankocijan.cleanwatchface.watchface;

import com.ivankocijan.cleanwatchface.CalendarEventsHelper;
import com.ivankocijan.cleanwatchface.R;
import com.ivankocijan.cleanwatchface.WatchFaceApplication;
import com.ivankocijan.cleanwatchface.model.CalendarEvent;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

/**
 * @author Koc
 *         ivan.kocijan@infinum.hr
 * @since 16.03.15.
 */
public class CleanCalendarWatchFace {

    private static final String TIME_FORMAT = "%02d:%02d";


    private final Paint timePaint;

    private final TextPaint textPaint;

    private DateTime time;

    private int defaultPadding = 0;

    public static CleanCalendarWatchFace newInstance(Context context) {
        Paint hoursPaint = new Paint();
        hoursPaint.setColor(context.getResources().getColor(R.color.watch_face_hours));
        hoursPaint.setTextSize(context.getResources().getDimension(R.dimen.time_text_size));
        hoursPaint.setAntiAlias(true);

        TextPaint eventPaint = new TextPaint();
        eventPaint.setColor(context.getResources().getColor(R.color.watch_face_hours));
        eventPaint.setTextSize(context.getResources().getDimension(R.dimen.event_text_size));
        eventPaint.setAntiAlias(true);

        int defaultPadding = Math.round(context.getResources().getDimension(R.dimen.default_padding));

        return new CleanCalendarWatchFace(hoursPaint, eventPaint, defaultPadding);
    }

    private CleanCalendarWatchFace(Paint timePaint, TextPaint textPaint, int defaultPadding) {
        this.timePaint = timePaint;
        this.textPaint = textPaint;
        this.defaultPadding = defaultPadding;
    }

    public void draw(Canvas canvas, Rect bounds) {

        DateTimeZone defaultZone = DateTimeZone.getDefault();
        time = new DateTime(defaultZone);

        canvas.drawColor(WatchFaceApplication.getInstance().getResources().getColor(R.color.watch_face_background));

        //Drawing hours text - located on top of the watchface
        String hoursText = String
                .format(TIME_FORMAT, time.getHourOfDay(), time.getMinuteOfHour());
        float xCoordinate = getXCoordinate(hoursText, timePaint, bounds);
        float yCoordinate = getYCoordinate(hoursText, timePaint, bounds);

        canvas.drawText(hoursText, xCoordinate, yCoordinate, timePaint);

        //Drawing next event title. If there is are no events in next 12 hours it will show appropriate text
        String text = WatchFaceApplication.getInstance().getString(R.string.no_events_today);

        CalendarEvent nextEvent = CalendarEventsHelper.getInstance().getNextEvent(time);

        if (nextEvent != null) {

            text = nextEvent.getTitle();

            if (text.length() > 40) {
                text = text.substring(0, 40) + "... - " + CalendarEventsHelper.getInstance().getTimeToNextEventString(time);
            } else {
                text = text + " - " + CalendarEventsHelper.getInstance().getTimeToNextEventString(time);
            }

        }

        canvas.save();

        StaticLayout sl = new StaticLayout(text, textPaint, bounds.width(),
                Layout.Alignment.ALIGN_CENTER, 1, 1, true);

        float textHeight = getTextHeight(text, textPaint);
        int numberOfTextLines = sl.getLineCount();

        float textYCoordinate = bounds.exactCenterY() - ((numberOfTextLines * textHeight) / 2);
        float textXCoordinate = bounds.left;

        canvas.translate(textXCoordinate, textYCoordinate);
        sl.draw(canvas);
        canvas.restore();


    }


    private float getXCoordinate(String text, Paint paint, Rect watchBounds) {

        float center = watchBounds.exactCenterX();
        float textLength = paint.measureText(text);
        return center - (textLength / 2.0f);

    }

    private float getYCoordinate(String timeText, Paint timePaint, Rect watchBounds) {

        float topBound = watchBounds.top;
        Rect rect = new Rect();
        timePaint.getTextBounds(timeText, 0, timeText.length(), rect);
        int textHeight = rect.height();
        return topBound + textHeight + 50.0f;
    }

    private float getEventYOffset(String eventText, Paint eventPaint, Rect watchBounds) {
        return watchBounds.exactCenterY() + (eventPaint.measureText(eventText) / 2f);
    }

    private float getTextHeight(String text, Paint paint) {

        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }

    private float computeXOffset(String text, Paint paint, Rect watchBounds) {
        float centerX = watchBounds.exactCenterX();
        float timeLength = paint.measureText(text);
        return centerX - (timeLength / 2.0f);
    }

    private float computeTimeYOffset(String timeText, Paint timePaint, Rect watchBounds) {
        float centerY = watchBounds.exactCenterY();
        Rect textBounds = new Rect();
        timePaint.getTextBounds(timeText, 0, timeText.length(), textBounds);
        int textHeight = textBounds.height();
        return centerY + (textHeight / 2.0f);
    }

    private float computeDateYOffset(String dateText, Paint datePaint) {
        Rect textBounds = new Rect();
        datePaint.getTextBounds(dateText, 0, dateText.length(), textBounds);
        return textBounds.height() + 10.0f;
    }

    public void setAntiAlias(boolean antiAlias) {
        timePaint.setAntiAlias(antiAlias);
        textPaint.setAntiAlias(antiAlias);
    }

    public void setColor(int color) {
        timePaint.setColor(color);
        textPaint.setColor(color);
    }

}