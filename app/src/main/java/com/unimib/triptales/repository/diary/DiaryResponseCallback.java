package com.unimib.triptales.repository.diary;

public interface DiaryResponseCallback<T> {
    void onSuccess(T result);
    void onFailure(Throwable throwable);
}
