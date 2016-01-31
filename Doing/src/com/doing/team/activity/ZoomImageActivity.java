package com.doing.team.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Window;

import com.doing.team.R;
import com.doing.team.properties.Constant;
import com.doing.team.view.ZoomImageView;

/**
 * Created by wangzhiheng on 2016/1/31.
 */
public class ZoomImageActivity extends Activity {
    private ZoomImageView zoomImageView;
    private String imageUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent =getIntent();
        imageUrl = intent.getStringExtra(Constant.ZOOM_IMAGE_URL);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.zoom_iamge_activity);
        zoomImageView = (ZoomImageView) findViewById(R.id.zoom_image_view);


        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ex_horizontal);
        zoomImageView.setImageBitmap(bitmap);


    }
}
