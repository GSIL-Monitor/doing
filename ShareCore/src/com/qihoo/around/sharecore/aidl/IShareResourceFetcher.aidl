package com.qihoo.around.sharecore.aidl;

import com.qihoo.around.sharecore.aidl.IOnShareCallback;
/**
*获取分享的资源<br><big>分享类别分别有weibo,weixintimeline,weixinfriends,qq,qzone,douban<big>
*/
interface IShareResourceFetcher{
/**
*获取分享的文字标题
*/
String getShareTitle(String src);
/**
*获取分享的来源
*/
String getShareFrom(String src);
/**
*获取分享的文字内容详情
*/
String getShareSummary(String src);
/**
*获取分享的跳转链接
*/
String getShareUrl(String src);
/**
*获取分享图片的缩略图(原则上小于300*300)
*/
Bitmap getShareThumbImg(String src);
/**
*获取分享图片的Bitmap
*/
Bitmap getShareImg(String src);
/**
* 获取分享图片的链接地址，没有时，返回空
*/
String getShareImgUrl(String src);
/**
*获取批量分享图片的网络地址
*/
String[] getShareWebImgs(String src);

/**
*获取批量分享图片的本地地址
*/
String getShareLocalImg(String src);

/**
*获取批量分享图片的本地地址
*/
String[] getShareLocalImgs(String src);

/**
*针对异步情况,分享过程中需要异步准备数据时,在调用IShareCore的openShare方法时,设置isAsync为true
*/
void onAsyncShare(String src,IOnShareCallback onShareCallback);

/**
*针对异步情况,如果异步的时候,允许用户取消分享,则在IOnShareCallback的onStart或者onStartProgressDialog方法中设置可取消,然后在用户点击后退时,响应该方法
*/
void onCancel();

}
