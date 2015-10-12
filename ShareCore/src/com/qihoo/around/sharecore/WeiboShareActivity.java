package com.qihoo.around.sharecore;

import com.qihoo.around.sharecore.aidl.IShareResourceFetcher;
import com.qihoo.around.sharecore.core.ShareToWeibo;
import com.qihoo.around.sharecore.core.weibo.WeiboRequestLsn;
import com.sina.weibo.sdk.utils.LogUtil;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by wushuai on 2015/3/30.
 */
public class WeiboShareActivity extends Activity{
	private final int MAXWEIBO_LENGTH = 140;
    private String TAG = "WeiboShareActivity";
    private String mMsg = "";
    private Bitmap mBitmap = null;
    private Bitmap mThumbBitmap = null;
    private boolean isNightMode = false;
    private boolean mShareWithImage = true;
    private IShareResourceFetcher shareCallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_core_weibo);
        LogUtil.d("share", "On Create!");
        initData();
        LogUtil.d("share", "Init Data!");
        final View captureView = findViewById(R.id.share_capture_pic);
        final TextView mCancel = (TextView) findViewById(R.id.weibo_share_cancel);
        final TextView mOK = (TextView) findViewById(R.id.weibo_share_ok);
        final TextView mWords = (TextView) findViewById(R.id.weibo_share_words);
        final EditText mContent = (EditText) findViewById(R.id.weibo_share_content);
        final ImageView mImage = (ImageView) findViewById(R.id.weibo_share_photo);
        final ImageView mCloseImage = (ImageView) findViewById(R.id.weibo_share_closephoto);
        mCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(final View v) {
				// TODO Auto-generated method stub
				try{
					InputMethodManager m = (InputMethodManager) v.getContext().getSystemService(
			                        Context.INPUT_METHOD_SERVICE);
					if(m.isActive())
						m.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				finish();
			}
		});
        mOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					shareToWeiboLocal();
					InputMethodManager m = (InputMethodManager) v.getContext().getSystemService(
	                        Context.INPUT_METHOD_SERVICE);
					if(m.isActive())
						m.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finish();
			}
		});
        mCloseImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mShareWithImage = false;
				captureView.setVisibility(View.GONE);
	        	mCloseImage.setVisibility(View.GONE);
			}
		});
        mContent.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				int len = MAXWEIBO_LENGTH - mContent.getText().length();
				if(len < 0)
		        	len = 0;
				mMsg = mContent.getText().toString();
				mWords.setText("还可以输入" + len + "字");
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
        if(mBitmap != null){
        	mImage.setImageBitmap(mBitmap);
        }else{
        	captureView.setVisibility(View.GONE);
        	mCloseImage.setVisibility(View.GONE);
        }
        int first = mMsg.indexOf("#");
        int last = mMsg.lastIndexOf("#");
        LogUtil.d("share","First and Last:" + first + ";" + last);
        if(first < last && first >= 0){
        	SpannableStringBuilder style = new SpannableStringBuilder(mMsg);
        	style.setSpan(new ForegroundColorSpan(Color.argb(0xff, 0xfa, 0x7d, 0x3c)), first, last + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        	mContent.setText(style);
        }else{
        	mContent.setText(mMsg);
        }
        int len = MAXWEIBO_LENGTH - mMsg.length();
        if(len < 0)
        	len = 0;
        mWords.setText("还可以输入" + len + "字");
    }
    /**
     * 静默分享到微博
     *
     * @throws android.os.RemoteException
     */
    private void shareToWeiboLocal() throws RemoteException {
        if (mBitmap != null && mShareWithImage) {
            ShareToWeibo.share(getApplicationContext(), mMsg, mBitmap,
                    WeiboRequestLsn.getInstance(getApplicationContext()));
        } else if(mThumbBitmap != null && mShareWithImage){
        	ShareToWeibo.share(getApplicationContext(), mMsg, mThumbBitmap,
                    WeiboRequestLsn.getInstance(getApplicationContext()));
        }
        else {
            ShareToWeibo.share(getApplicationContext(), mMsg,
                    WeiboRequestLsn.getInstance(getApplicationContext()));
        }
    }
    
    private void initData(){
    	mShareWithImage = true;
    	isNightMode = getIntent().getBooleanExtra(ShareConstans.IS_NIGHT_MODE,false);
    	try{
    		shareCallback = ShareManager.getInstance().getShareCallback();
	    	mBitmap = shareCallback.getShareImg(ShareConstans.TYPE_WEIBO);
	    	mThumbBitmap = shareCallback.getShareThumbImg(ShareConstans.TYPE_WEIBO);
	    	String shareTitle = shareCallback.getShareTitle(ShareConstans.TYPE_WEIBO);
	        String shareUrl = shareCallback.getShareUrl(ShareConstans.TYPE_WEIBO);
	        String shareContent = shareCallback.getShareSummary(ShareConstans.TYPE_WEIBO);
	        String shareFrom = shareCallback.getShareFrom(ShareConstans.TYPE_WEIBO);
	        mMsg = "";
	        if (TextUtils.isEmpty(shareTitle)) {
	            mMsg = shareContent;
	        }else {
	        	mMsg =  "#" + shareTitle + "#" + shareContent;
	        }
	        int len = MAXWEIBO_LENGTH - 2;
	        if(!TextUtils.isEmpty(shareUrl)){
	            len = len - shareUrl.length();
	        }
	        if(!TextUtils.isEmpty(shareFrom)){
                len = len - shareFrom.length();
            }
	        if(mMsg.length() > len){
	        	mMsg = mMsg.substring(0,len-3) + "...";
	        }
	        if(!TextUtils.isEmpty(shareUrl)){
	            mMsg += shareUrl;
            }
            if(!TextUtils.isEmpty(shareFrom)){
                mMsg += " " + shareFrom;
            }
	        LogUtil.e("share", "shareUrl :" + shareUrl);
    	}catch(Exception ex){
    		LogUtil.e("share", "Error:" + ex.toString());
    		ex.printStackTrace();
    	}
    }
    
    @Override
    public void finish() {
        super.finish();
        ShareManager.getInstance().setShareCallback(null);
    }
}
