package com.ivankocijan.cleanwatchface.mvp.view;

/**
 * @author Koc
 *         ivan.kocijan@infinum.hr
 * @since 21.03.15.
 */
public interface CanvasView {

    void onEventFetchStarted();

    void onEventsFetched();

    void onEventFetchCanceled();

    void onEventFetchFailed();

}
