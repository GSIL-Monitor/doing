package com.qihoo.around.sharecore.aidl;

import com.qihoo.around.sharecore.aidl.IShareResourceFetcher;

interface IShareCore{
/**
 * 打开分享模块
 * @param resourceFetcher 获取资源的回调,包括分享的标题,文本,图片/缩略图,组图等;<br> <h1>另外,如果需要异步操作,响应里面的回调</h1>
 * @param isAsync 是否需要异步,比如点击分享之后再生成短链接,然后才去分享等设置
 * @param isNightMode 是否为夜间模式
 * @throws android.os.RemoteException
 */
void openShare(IShareResourceFetcher resourceFetcher,boolean isAsync,boolean isNightMode);
}
