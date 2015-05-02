package com.ivankocijan.cleanwatchface;

import com.ivankocijan.cleanwatchface.mvp.presenter.CalendarPresenter;
import com.ivankocijan.cleanwatchface.mvp.presenter.impl.CalendarPresenterImpl;
import com.ivankocijan.cleanwatchface.mvp.view.CanvasView;
import com.ivankocijan.cleanwatchface.watchface.CleanCalendarWatchFace;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.wearable.provider.WearableCalendarContract;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.view.SurfaceHolder;

import java.util.concurrent.TimeUnit;

/**
 * @author Koc
 *         ivan.kocijan@infinum.hr
 * @since 16.03.15.
 */
public class CleanWatchFaceService extends CanvasWatchFaceService {

    private static final long TICK_PERIOD_MILLIS = TimeUnit.MINUTES.toMillis(1);

    private Handler timeTick = new Handler(Looper.myLooper());

    private CalendarPresenter presenter;

    /**
     * Broadcast receiver will notify watch if new event is added.
     */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_PROVIDER_CHANGED.equals(intent.getAction())
                    && WearableCalendarContract.CONTENT_URI.equals(intent.getData())) {
                presenter.cancelEventFetch();
                presenter.getCalendarEvents();
            }

        }
    };


    @Override
    public Engine onCreateEngine() {
        return new CleanWatchFaceEngine();
    }


    private class CleanWatchFaceEngine extends Engine implements CanvasView {

        private CleanCalendarWatchFace cleanCalendarWatchFace;

        private boolean isReceiverRegistered;

        private boolean mLowBitAmbient;

        private boolean mBurnInProtection;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            cleanCalendarWatchFace = CleanCalendarWatchFace.newInstance(CleanWatchFaceService.this);

            setWatchFaceStyle(new WatchFaceStyle.Builder(CleanWatchFaceService.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setAmbientPeekMode(WatchFaceStyle.AMBIENT_PEEK_MODE_HIDDEN)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .build());

            startTimerIfNecessary();

            presenter = new CalendarPresenterImpl(this);
            presenter.getCalendarEvents();

        }

        private void startTimerIfNecessary() {
            timeTick.removeCallbacks(timeRunnable);
            if (isVisible() && !isInAmbientMode()) {
                timeTick.post(timeRunnable);
            }

        }

        private final Runnable timeRunnable = new Runnable() {
            @Override
            public void run() {

                if (isVisible() && !isInAmbientMode()) {

                    invalidate();
                    long timeMs = System.currentTimeMillis();
                    long delayMs =
                            TICK_PERIOD_MILLIS - (timeMs % TICK_PERIOD_MILLIS);

                    timeTick.postDelayed(this, delayMs);
                }
            }
        };


        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            startTimerIfNecessary();
            setUpBroadcastReceiver(visible);

            if (!visible) {
                presenter.cancelEventFetch();
            }

        }


        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            super.onDraw(canvas, bounds);
            cleanCalendarWatchFace.draw(canvas, bounds);
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);

            cleanCalendarWatchFace.setAntiAlias(!inAmbientMode);
            cleanCalendarWatchFace.setColor(inAmbientMode ? Color.GRAY : getResources().getColor(R.color.watch_face_hours));

            invalidate();
            startTimerIfNecessary();

        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onDestroy() {
            timeTick.removeCallbacks(timeRunnable);
            super.onDestroy();
        }


        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
            mBurnInProtection = properties.getBoolean(PROPERTY_BURN_IN_PROTECTION,
                    false);

        }

        @Override
        public void onEventFetchStarted() {

        }

        @Override
        public void onEventFetchCanceled() {
        }

        @Override
        public void onEventsFetched() {
            invalidate();
        }

        @Override
        public void onEventFetchFailed() {
        }


        /**
         * This method registers broadcast receiver if the watch is visible.
         * If the watch face is not visible the receiver will be unregistered because events
         * do not need to be fetched.
         *
         * @param visible is watch face visible
         */
        private void setUpBroadcastReceiver(boolean visible) {

            if (visible) {

                IntentFilter filter = new IntentFilter(Intent.ACTION_PROVIDER_CHANGED);
                filter.addDataScheme("content");
                filter.addDataAuthority(WearableCalendarContract.AUTHORITY, null);
                registerReceiver(mBroadcastReceiver, filter);
                isReceiverRegistered = true;

            } else {

                if (isReceiverRegistered) {
                    unregisterReceiver(mBroadcastReceiver);
                    isReceiverRegistered = false;
                }
                presenter.cancelEventFetch();
            }

        }

    }


}
