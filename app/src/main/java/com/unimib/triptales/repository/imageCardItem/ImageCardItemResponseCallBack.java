package com.unimib.triptales.repository.imageCardItem;

import com.unimib.triptales.model.ImageCardItem;

import java.util.List;

public interface ImageCardItemResponseCallBack {
    void onSuccessFromRemote(List<ImageCardItem> imageCardItems);
    void onFailureFromRemote(Exception exception);
    void onSuccessDeleteFromRemote();

    void onSuccessFromLocal(List<ImageCardItem> imageCardItems);
    void onFailureFromLocal(Exception exception);
    void onSuccessDeleteFromLocal();
    void onSuccessSelectionFromLocal(List<ImageCardItem> imageCardItems);

}
