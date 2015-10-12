
package com.qihoo.around.mywebview;

import com.qihoo.around._public.eventbus.QEventBus;
import com.qihoo.around._public.eventdefs.BrowserEvents;
import com.qihoo.around.mywebview.utils.FakeProgress;
import com.qihoo.around.mywebview.utils.LogUtils;
import com.qihoo.around.webviewdemo.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 *
 */
public class PageProgressView extends ImageView implements FakeProgress.OnProgressChangedListener {

    public static final int MAX_PROGRESS = 100;
    private static final int MSG_UPDATE = 42;
    private static final int MSG_VIEW_GONE = 43;
    private static final int STEPS = 10;
    private static final int DELAY = 200;

    private float mCurrentProgress;
    private float mTargetProgress;
    private float mIncrement;
    private Rect mBounds;
    private Handler mHandler;
    private FakeProgress mFakeProgress;
    private boolean flag = false;
    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public PageProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public PageProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * @param context
     */
    public PageProgressView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context ctx) {
        setImageResource(R.drawable.url_progress);
        mBounds = new Rect(0, 0, 0, 0);
        mCurrentProgress = 0;
        mTargetProgress = 0;
        mFakeProgress = new FakeProgress(this);
        mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_UPDATE) {
                    mCurrentProgress = Math.min(mTargetProgress,
                            mCurrentProgress + mIncrement);
                    if (mTargetProgress == 100) {
                        mCurrentProgress = 100;
                    }
                    if (mCurrentProgress == MAX_PROGRESS) {
                        sendMessageDelayed(mHandler.obtainMessage(MSG_VIEW_GONE), 100);
                    } else {
                        setVisibility(View.VISIBLE);
                    }

                    mBounds.right = (int)(getWidth() * mCurrentProgress / MAX_PROGRESS);
                    invalidate();
                    if (mCurrentProgress < mTargetProgress) {
                        sendMessageDelayed(mHandler.obtainMessage(MSG_UPDATE), DELAY);
                    }

                } else if (msg.what == MSG_VIEW_GONE) {
                    mTargetProgress = 0;
                    mCurrentProgress = 0;
                    setVisibility(View.INVISIBLE);
                }
            }

        };
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        QEventBus.getEventBus().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        QEventBus.getEventBus().unregister(this);
        super.onDetachedFromWindow();
    }

    @Override
    public void onLayout(boolean f, int l, int t, int r, int b) {
        mBounds.left = 0;
        mBounds.right =(int) ((r - l) * mCurrentProgress / MAX_PROGRESS);
        mBounds.top = 0;
        mBounds.bottom = b - t;
    }

    public void setProgress(int progress) {
        mCurrentProgress = mTargetProgress;
        mTargetProgress = progress;
        mIncrement = (mTargetProgress - mCurrentProgress) / STEPS;
        mHandler.removeMessages(MSG_UPDATE);
        mHandler.sendEmptyMessage(MSG_UPDATE);

    }

    @Override
    public void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        Drawable d = getDrawable();
        d.setBounds(mBounds);
        d.draw(canvas);
    }

    public void onEventMainThread(BrowserEvents.onPageStarted event) {
        flag = true;
    }

    public void onEventMainThread(BrowserEvents.onProgressChanged event) {
        if (event == null) {
            return;
        }
        //因为有个再load的时候有个初始值，newProgress = 1
        if (event.newProgress == 0 || event.newProgress == 1) {
            flag = true;
        }
        if (flag) {
            int progress = 40 + (int)(event.newProgress * 0.6);
//        setProgress(progress);
            mFakeProgress.setProgress(progress);
        }
    }

    public void onEventMainThread(BrowserEvents.onPageFinished event) {
        if (flag) {
            mFakeProgress.finish();
            setProgress(100);
            mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_VIEW_GONE), 100);
        }
    }

    @Override
    public void onProgressChanged(int progress) {
        setProgress(progress);
    }

    public void onEventMainThread(BrowserEvents.FakePageFinish event) {
        LogUtils.d("FunctionTracer", "fake page finish receive");
        flag = false;
        mFakeProgress.finish();
        setProgress(100);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_VIEW_GONE), 100);
    }

}
