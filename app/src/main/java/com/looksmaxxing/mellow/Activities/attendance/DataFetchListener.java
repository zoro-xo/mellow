package com.looksmaxxing.mellow.Activities.attendance;

public interface DataFetchListener<T> {

    void onLoading();

    void onError(String message);

    void onDataLoad(T data);
}
