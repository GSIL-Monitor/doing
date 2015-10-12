package com.qihoo.around.sharecore;

/**
 * Created by renjh1 on 15-1-12.
 */
public interface IShareEvent {
    /**
     * 微博分享
     */
    public void onEventWeibo();

    /**
     * 微信朋友圈分享
     */
    public void onEventWeixinTimeline();

    /**
     * 微信朋友分享
     */
    public void onEventWeixinFriends();

    /**
     * 豆瓣分享
     */
    public void onEventDouban();

    /**
     * QQ分享
     */
    public void onEventQQ();

    /**
     * QQ空间分享
     */
    public void onEventQZone();
    /**
     * 短信分享
     */
    public void onEventMessage();
    /**
     * 更多分享
     */
    public void onEventShareMore();
    /**
     * 复制链接
     */
    public void onEventClipBoard();

    /**
     * 取消分享
     */
    public void onEventCancel();
}
