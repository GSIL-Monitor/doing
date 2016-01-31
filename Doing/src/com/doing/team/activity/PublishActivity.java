package com.doing.team.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.doing.team.R;
import com.doing.team.util.BlurImage;
import com.doing.team.util.ImageUtils;
import com.doing.team.util.InputTool;


/**
 * Created by wangzhiheng on 2016/1/24.
 */
public class PublishActivity extends Activity implements View.OnClickListener {
    private EditText publishEditText;
    private ImageView image1;
    private ImageView image2;
    private TextView location;
    private int whichImage = 1;
    private View publishCancel;
    private View publishButton;
    private ImageView publishBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageUtils.openCameraImage(PublishActivity.this);
        setContentView(R.layout.publish_activity);
        publishEditText = (EditText) findViewById(R.id.publish_text);
        image1 = (ImageView) findViewById(R.id.publish_image1);
        image2 = (ImageView) findViewById(R.id.publish_image2);
        location = (TextView) findViewById(R.id.publish_location);
        publishBackground = (ImageView) findViewById(R.id.publish_bg);
        publishCancel = findViewById(R.id.publish_cancel);
        publishButton = findViewById(R.id.publish_pulish);
        publishButton.setOnClickListener(this);
        publishCancel.setOnClickListener(this);
        image1.setOnClickListener(this);
        image2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.publish_image1:
                whichImage = 1;
                ImageUtils.openCameraImage(PublishActivity.this);
                break;
            case R.id.publish_image2:
                whichImage = 2;
                ImageUtils.openCameraImage(PublishActivity.this);
                break;
            case R.id.publish_cancel:
                finish();
                break;
            case R.id.publish_pulish:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int RESULT_CANCELED = 0;
        if (resultCode == RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
            // 拍照获取图片
            case ImageUtils.GET_IMAGE_BY_CAMERA:
                // uri传入与否影响图片获取方式,以下二选一
                // 方式一,自定义Uri(ImageUtils.imageUriFromCamera),用于保存拍照后图片地址
                if (ImageUtils.imageUriFromCamera != null) {
                    // 可以直接显示图片,或者进行其他处理(如压缩或裁剪等)
                    // iv.setImageURI(ImageUtils.imageUriFromCamera);

                    // 对图片进行裁剪
//                    ImageUtils.cropImage(this, ImageUtils.imageUriFromCamera);

                    if (whichImage == 1) {
                        image1.setImageURI(ImageUtils.imageUriFromCamera);
                        image2.setVisibility(View.VISIBLE);
                    } else if (whichImage == 2) {
                        image2.setImageURI(ImageUtils.imageUriFromCamera);
                    }
                    InputTool.KeyBoard(publishEditText,true);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            BitmapFactory.Options optins = new BitmapFactory.Options();
                            optins.outHeight = publishBackground.getMeasuredHeight();
                            optins.outWidth = publishBackground.getMeasuredWidth();
                            Bitmap backImage = BitmapFactory.decodeFile(getRealFilePath(ImageUtils.imageUriFromCamera), optins);
                            Drawable blurImage = BlurImage.BoxBlurFilter(backImage);
                            Message message = new Message();
                            message.what = 23;
                            message.obj = blurImage;
                            handler.sendMessage(message);
                            backImage.recycle();
                        }
                    }).start();

                    break;
                }
                break;
            default:
                break;
        }

    }

    public String getRealFilePath(final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = this.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        InputTool.KeyBoard(publishEditText, false);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 23) {
                if (msg.obj != null)
                    publishBackground.setImageDrawable((Drawable) msg.obj);
            }
        }
    };


}
