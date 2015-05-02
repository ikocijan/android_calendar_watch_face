package com.ivankocijan.cleanwatchface.mvp.presenter.impl;

import com.ivankocijan.cleanwatchface.CalendarEventsHelper;
import com.ivankocijan.cleanwatchface.model.CalendarEvent;
import com.ivankocijan.cleanwatchface.mvp.interactor.CalendarInteractor;
import com.ivankocijan.cleanwatchface.mvp.interactor.impl.CalendarInteractorImpl;
import com.ivankocijan.cleanwatchface.mvp.listener.CalendarEventListener;
import com.ivankocijan.cleanwatchface.mvp.presenter.CalendarPresenter;
import com.ivankocijan.cleanwatchface.mvp.view.CanvasView;

import java.util.List;

/**
 * @author Koc
 *         ivan.kocijan@infinum.hr
 * @since 21.03.15.
 */
public class CalendarPresenterImpl implements CalendarPresenter {

    private CanvasView view;

    private CalendarInteractor calendarInteractor;

    private boolean canceled;

    private boolean fetchInProgress;

    public CalendarPresenterImpl(CanvasView view) {
        this.view = view;
        calendarInteractor = new CalendarInteractorImpl();
        this.canceled = false;
    }

    @Override
    public void getCalendarEvents() {

        view.onEventFetchStarted();

        if (fetchInProgress) {
            cancelEventFetch();
        } else {
            fetchInProgress = true;
        }

        calendarInteractor.fetchCalendarEvents(new CalendarEventListener() {
            @Override
            public void onSuccess(List<CalendarEvent> calendarEventList) {
                fetchInProgress = false;
                CalendarEventsHelper.getInstance().addEvents(calendarEventList);

                if (!canceled) {
                    view.onEventsFetched();
                } else {
                    canceled = false;
                    view.onEventFetchCanceled();
                }

            }

            @Override
            public void onError() {
                if (canceled) {
                    view.onEventFetchCanceled();
                    canceled = false;
                }
                view.onEventFetchFailed();
                fetchInProgress = false;

            }
        });
    }

    @Override
    public void cancelEventFetch() {
        if (fetchInProgress) {
            this.canceled = true;
        }
    }
}
