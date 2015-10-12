
package com.qihoo.around.mywebview.utils;

import android.os.Handler;

public class FakeProgress {

    private int mInterval = 500;
    private int mDivide = 3;
    private int mInit = 0;
    private int mMaxShow = 98;
    private Handler mHandler;
    private OnProgressChangedListener mListener;

    private int mProgress = 0;

    public interface OnProgressChangedListener {
        /**
         * 当进度发生变化时发生
         *
         * @param progress
         */
        void onProgressChanged(int progress);
    }

    /**
     * 初始化假进度
     *
     * @param listener
     */
    public FakeProgress(OnProgressChangedListener listener) {
        this(new Handler(), listener);
    }

    /**
     * 初始化假进度
     *
     * @param handler
     * @param listener
     */
    public FakeProgress(Handler handler, OnProgressChangedListener listener) {
        mListener = listener;
        mHandler = handler;
    }

    private Runnable mProgressRunnable = new Runnable() {

        @Override
        public void run() {
            int mRemain = mMaxShow - mProgress;

            double divide = mRemain / mDivide;
            mProgress += divide;
            mListener.onProgressChanged(mProgress);
            if (mRemain <= 0) {
                finish();
                return;
            }
            mHandler.postDelayed(mProgressRunnable, mInterval);
        }
    };

    /**
     * 开始刷新假进度
     */
    public void start() {
        if (mProgress < mInit) {
            mProgress = mInit;
        }

        mHandler.post(mProgressRunnable);
    }

    /**
     * 停止刷新
     */
    public void finish() {
        mHandler.removeCallbacks(mProgressRunnable);
        mProgress = 0;
    }

    /**
     * 设置进度的最大值
     *
     * @param max
     */
    public void setMaxShow(int max) {
        this.mMaxShow = max;
    }

    /**
     * 设置每一次刷新进度的时间间隔
     *
     * @param interval
     */
    public void setInterval(int interval) {
        mInterval = interval;
    }

    /**
     * 设置进度的份数，这直接关系到每次刷新的步长
     *
     * @param divide
     */
    public void setDivide(int divide) {
        mDivide = divide;
    }

    /**
     * 获取初始的值
     *
     * @param init
     */
    public void setInit(int init) {
        mInit = init;
    }
    public void setProgress(int progress) {
        if (mProgress == 0 && progress > 0) {
            start();
        }
        if (mProgress < progress) {
            mProgress = progress;
        }else {
            return;
        }
        if (progress > 99) {
            mHandler.post(mProgressRunnable);
        }

    }
}
