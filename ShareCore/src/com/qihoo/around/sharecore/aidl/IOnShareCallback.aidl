package com.qihoo.around.sharecore.aidl;


interface IOnShareCallback{
/**
 * 异步操作开始
 * @param cancelable 异步操作过程中,是否允许用户取消操作
 * @throws android.os.RemoteException
 */
void onStart(boolean cancelable);
/**
 * 异步操作开始,需要显示统一的进度条时,调用该方法
 * @param cancelable 异步操作过程中,是否允许用户取消操作
 * @param title 标题
 * @param msg 文本内容
 * @throws android.os.RemoteException
 */
void onStartProgressDialog(boolean cancelable,String title,String msg);
/**
 * 进度显示,需要显示统一的进度条时,调用该方法
 * @param total 总数
 * @param cur 当前进度
 * @throws android.os.RemoteException
 */
void onProgress(int total,int cur);
/**
 * 异步操作成功,可以进行分享
 * @throws android.os.RemoteException
 */
void onSuccess();
/**
 * 异步操作失败
 * @throws android.os.RemoteException
 */
void onError();
/**
 * 异步操作失败,并提示(可以自定义提示,调用onError()即可)
 * @param msg 提示文本
 * @throws android.os.RemoteException
 */
void onErrorWithMsg(String errMsg);
}
