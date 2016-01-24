package com.doing.team.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.doing.team.DoingApplication;
import com.doing.team.R;
import com.doing.team.activity.BaseActivity;
import com.doing.team.activity.DoingActivity;
import com.doing.team.bean.RegisterRespond;
import com.doing.team.bean.UserInfo;
import com.doing.team.eventbus.QEventBus;
import com.doing.team.eventdefs.ApplicationEvents;
import com.doing.team.http.HttpManager;
import com.doing.team.properties.Constant;
import com.doing.team.util.AESCoder;
import com.doing.team.util.ImageUtils;
import com.doing.team.util.MultipartRequest;
import com.doing.team.util.SharePreferenceHelper;
import com.doing.team.view.CircleImageView;
import com.google.gson.Gson;
import com.qihoo.haosou.msearchpublic.util.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RegisterInfoFragment extends BaseFragment implements View.OnFocusChangeListener{
    private View mView;
    private EditText nickEt;
    private EditText ageEt;
    private EditText professionEt;
    private TextView confirm;
    private CircleImageView headImag;
    private Context mContext;
    private UserInfo userInfo;
    private View back;
    private String uploadUrl = "http://123.57.223.85/do/gi";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        mView = inflater.inflate(R.layout.reigster_info_fragment, container, false);
        mView.setOnClickListener(null);
        mContext = getActivity();
        userInfo = DoingApplication.getInstance().getUserInfo();
        mView.findViewById(R.id.register_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                QEventBus.getEventBus().post(new ApplicationEvents.SwitchFragmentToRegisterGender(RegisterGenderFragment.class,false));
            }
        });
        headImag = (CircleImageView) mView.findViewById(R.id.head_image);
        headImag.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });
        nickEt = (EditText) mView.findViewById(R.id.register_nick_et);
        ageEt = (EditText) mView.findViewById(R.id.register_age_et);
        professionEt = (EditText) mView.findViewById(R.id.register_profession_et);
        nickEt.setOnFocusChangeListener(this);
        ageEt.setOnFocusChangeListener(this);
        professionEt.setOnFocusChangeListener(this);
        confirm = (TextView) mView.findViewById(R.id.register_confirm);
        confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                QEventBus.getEventBus().post(new ApplicationEvents.SwitchToFragment(ContentListFragment.class,true));

                if (TextUtils.isEmpty(ageEt.getText().toString())) {
                    userInfo.age = Integer.valueOf(getActivity().getResources().getString(
                            R.string.register_age));
                } else {
                    userInfo.age = Integer.valueOf(ageEt.getText().toString());
                }
                if (TextUtils.isEmpty(nickEt.getText().toString())) {
                    userInfo.nick = getActivity().getResources().getString(R.string.register_nick);
                } else {
                    userInfo.nick = nickEt.getText().toString();
                }
                if (TextUtils.isEmpty(professionEt.getText().toString())) {
                    userInfo.profession = getActivity().getResources().getString(
                            R.string.register_profession);
                } else {
                    userInfo.profession = professionEt.getText().toString();
                }
                DoingApplication.getInstance().saveUserinfo(userInfo);
//                uploadUserInfo();
            }
        });
        return mView;
    }

    // 上传用户注册信息
    private void uploadUserInfo() {

        // 上传文件
        File imagFile = null;
        List<File> imagFiles = new ArrayList<File>();

        if (userInfo.headImag!=null){
            String path = getRealPathFromURI(userInfo.headImag);
            imagFile = new File(path);
            imagFiles.add(imagFile);
//            imagFiles.add(imagFile);

        }
        String dataString = new Gson().toJson(userInfo.getData());
        LogUtils.i("wzh",dataString);
        String encryptData = AESCoder.encryptToBase64(dataString, Constant.SECRET_KEY);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("imgSelect", "0");
        params.put("data", encryptData);

        Listener<String> listener = new Listener<String>() {

            @Override
            public void onResponse(String arg0) {
                Gson gson = new Gson();
                try {
                    RegisterRespond respon = gson.fromJson(arg0,RegisterRespond.class);
                    if (respon!=null){
                        if (respon.status == 200) {
                            SharePreferenceHelper.saveResponceUserInfo(respon);
                        }else{
                            Toast.makeText(getActivity(), respon.statusText,Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        ErrorListener errorListener = new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                Toast.makeText(getActivity(),"注册失败，请检查网络",Toast.LENGTH_SHORT).show();
            }
        };
        MultipartRequest multipartRequest = new MultipartRequest(uploadUrl,
                errorListener, listener, "imgFile", imagFiles, params);

        HttpManager.getInstance().addToRequestQueue(multipartRequest);
    }
    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = mContext.getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){;
           int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
           res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
    // 获取照片
    public void showImagePickDialog() {
        String title = "获取图片的方式";
        String[] choices = new String[] { "拍照", "从手机中选择" };

        new AlertDialog.Builder(mContext).setTitle(title)
                .setItems(choices, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                        case 0:
                            ImageUtils.openCameraImage(getActivity());
                            break;
                        case 1:
                            ImageUtils.openLocalImage(getActivity());
                            break;
                        }
                    }
                }).setNegativeButton("返回", null).show();
    }

    // 处理获取照片后的回调
    public void onEventMainThread(BaseActivity.OnActivityResult event) {

        int requestCode = event.requestCode;
        int resultCode = event.resultCode;
        Intent data = event.data;
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
                ImageUtils.cropImage(getActivity(), ImageUtils.imageUriFromCamera);
                break;
            }

            break;
        // 手机相册获取图片
        case ImageUtils.GET_IMAGE_FROM_PHONE:
            if (data != null && data.getData() != null) {
                // 可以直接显示图片,或者进行其他处理(如压缩或裁剪等)
                // iv.setImageURI(data.getData());

                // 对图片进行裁剪
                ImageUtils.cropImage(getActivity(), data.getData());
            }
            break;
        // 裁剪图片后结果
        case ImageUtils.CROP_IMAGE:
            if (ImageUtils.cropImageUri != null) {
                // 可以直接显示图片,或者进行其他处理(如压缩等)
                headImag.setImageURI(ImageUtils.cropImageUri);
                userInfo.headImag = ImageUtils.cropImageUri;
            }
            break;
        default:
            break;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        QEventBus.getEventBus(DoingActivity.class.getName()).register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        QEventBus.getEventBus(DoingActivity.class.getName()).unregister(this);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
            EditText etV=(EditText)v;
            if (!hasFocus) {// 失去焦点
                etV.setHint(etV.getTag().toString());
            } else {
                String hint=etV.getHint().toString();
                etV.setTag(hint);
                etV.setHint("");
            }
    }
}
