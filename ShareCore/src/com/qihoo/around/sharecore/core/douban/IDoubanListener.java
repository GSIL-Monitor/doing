package com.qihoo.around.sharecore.core.douban;

public interface IDoubanListener {
    void onComplete(Object object);

    void onCancel();

    void onError(Exception ex);
}
