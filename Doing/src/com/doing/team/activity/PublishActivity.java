package com.doing.team.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.doing.team.R;
import com.doing.team.util.ImageUtils;
import com.doing.team.util.InputTool;

/**
 * Created by wangzhiheng on 2016/1/24.
 */
public class PublishActivity extends Activity implements View.OnClickListener {
    private EditText publishText;
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
        showImagePickDialog();
        setContentView(R.layout.publish_activity);
        publishText = (EditText) findViewById(R.id.publish_text);
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
                showImagePickDialog();
                break;
            case R.id.publish_image2:
                whichImage = 2;
                showImagePickDialog();
                break;
            case R.id.publish_cancel:
                finish();
                break;
            case R.id.publish_pulish:
                break;
        }
    }

    // 获取照片
    public void showImagePickDialog() {
        String title = "获取图片的方式";
        String[] choices = new String[]{"拍照", "从手机中选择"};

        new AlertDialog.Builder(this).setTitle(title)
                .setItems(choices, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                ImageUtils.openCameraImage(PublishActivity.this);
                                break;
                            case 1:
                                ImageUtils.openLocalImage(PublishActivity.this);
                                break;
                        }
                    }
                }).setNegativeButton("返回", null).show();
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
                    ImageUtils.cropImage(this, ImageUtils.imageUriFromCamera);
                    break;
                }

                break;
            // 手机相册获取图片
            case ImageUtils.GET_IMAGE_FROM_PHONE:
                if (data != null && data.getData() != null) {
                    // 可以直接显示图片,或者进行其他处理(如压缩或裁剪等)
                    // iv.setImageURI(data.getData());

                    // 对图片进行裁剪
                    ImageUtils.cropImage(this, data.getData());
                }
                break;
            // 裁剪图片后结果
            case ImageUtils.CROP_IMAGE:
                if (ImageUtils.cropImageUri != null) {
                    // 可以直接显示图片,或者进行其他处理(如压缩等)
                    if (whichImage == 1) {
                        image1.setImageURI(ImageUtils.cropImageUri);
                        image2.setVisibility(View.VISIBLE);
                    } else if (whichImage == 2) {
                        image2.setImageURI(ImageUtils.cropImageUri);
                    }
                    publishBackground.setImageURI(ImageUtils.cropImageUri);


                    publishText.setSelection(3);
                    InputTool.KeyBoard(publishText, true);
                }
                break;
            default:
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        InputTool.KeyBoard(publishText, false);
    }
}
