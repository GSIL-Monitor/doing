package com.qihoo.around.sharecore;

/**
 * Created by renjihai on 2015/1/15.
 */
public class ShareConstans {
    public static final String IS_ASYNC="is_async";
    public static final String IS_NIGHT_MODE="is_night_mode";
    public static final String WEB_SHARE_TO_QQ="web_share_to_qq";
    public static final String SHARE_URL="share_url";

    public static final String TYPE_WEIBO="weibo";
    public static final String TYPE_WEIXIN_TIMELINE="weixintimeline";
    public static final String TYPE_WEIXIN_FRTENDS="weixinfriends";
    public static final String TYPE_QQ="qq";
    public static final String TYPE_QZONE="qzone";
    public static final String TYPE_DOUBAN="douban";
    public static final String TYPE_COPY_LINK="copy_link";
    public static final String TYPE_MESSAGE = "com.android.mms";
    public static final String TYPE_MORE = "share_more";

    public static final int MSG_WHAT_START=0x01;
    public static final int MSG_WHAT_PROGRESS=0x02;
    public static final int MSG_WHAT_SUCCESS=0x03;
    public static final int MSG_WHAT_ERROR=0x04;

    public static final String JS_NIGHT_MODE="javascript:(function(){var e='360app_night_mode_style',t=document.getElementById(e);if(!t){t=document.createElement('link'),t.id=e,t.rel='stylesheet',t.href='data:text/css,html,body,h2,h3,h4,h5,h6,table,tr,td,th,tbody,form,ul,ol,li,dl,dd,section,footer,nav,strong,aside,header,label{background:#0b0f10!important;background-image:none!important;background-color:#0b0f10!important;color:#616a71!important;border-color:#212a32!important;box-shadow:0 0 0!important;text-shadow:0 0 0!important}html body{background-color:#0b0f10!important}article,dt,h1{background-color:#0b0f10!important;color:#616a71!important;border-color:#212a32!important;box-shadow:0 0 0!important;text-shadow:0 0 0!important}div{background-color:transparent!important;color:#616a71!important;border-color:#212a32!important;box-shadow:0 0 0!important;text-shadow:0 0 0!important}p{color:#616a71!important;border-color:#212a32!important;box-shadow:0 0 0!important;text-shadow:0 0 0!important}div:empty,div[id=x-video-button],div[class=x-advert],div[class=player_controls svp_ctrl]{background-color:transparent!important}span,em{background-color:transparent!important;color:#616a71!important;border-color:#212a32!important;box-shadow:0 0 0!important;text-shadow:0 0 0!important}html,html body{scrollbar-base-color:#46567b!important;scrollbar-face-color:#56688f!important;scrollbar-shadow-color:#222!important;scrollbar-highlight-color:#56688f!important;scrollbar-dlight-color:#2e3952!important;scrollbar-darkshadow-color:#222!important;scrollbar-track-color:#46567b!important;scrollbar-arrow-color:#000!important;scrollbar-3dlight-color:#7a7967!important}html input,html select,html button,html textarea{box-shadow:0 0 0!important;color:#616a71!important;background-color:#0b0f10!important;border-color:#212a32!important}html input:focus,html select:focus,html option:focus,html button:focus,html textarea:focus{background-color:#0b0f10!important;color:#616a71!important;border-color:#1a3973!important;outline:2px solid #1a3973!important}html input:hover,html select:hover,html option:hover,html button:hover,html textarea:hover{background-color:#0b0f10!important;color:#616a71!important;border-color:#1a3973!important;outline:2px solid #1a3973!important}html input[type=text],html input[type=password]{background-image:none!important}html input[type=submit],html button{opacity:.5;border:1px solid #212a32!important}html input[type=submit]:hover,html button:hover{opacity:1;border:1px solid #1a3973!important;outline:2px solid #1a3973!important}html img[src],html input[type=image]{opacity:.5}html input[type=image]:hover{opacity:1}div[class=img-view],ul[id=imgview],a[class^=prev],a[class^=next]a[class^=topic_img],a[class^=arrow],a:active[class^=arrow],a:visited[class^=arrow],img[src^=data],img[loaded=1]{background:none!important}a[class^=arrow]{height:0}.anythingSlider .arrow{background:none!important}html a,html a *{background-color:transparent!important;color:#366ba6!important;text-decoration:none!important;border-color:#212a32!important;text-shadow:0 0 0!important}html a:visited,html a:visited *{color:#a716b9!important}html a:hover,html a:active{color:none!important;border-color:none!important}a img{background:none!important}#toolbarBox,#move_tip{background:none!important}#logolink,#mask{background-color:#0b0f10!important;border-bottom:none!important}div::after{background-color:transparent!important}*:before,*:after{background-color:transparent!important;border-color:#212a32!important;color:#616a71!important}input::-webkit-input-placeholder{color:#616a71!important}div[class=x-prompt],div[class=x-dashboard]{background:none!important}div[class=x-progress-play-mini]{background:#eb3c10!important}div[class=suggest-box]{background:#000!important}div[class=x-console],div[class=x-progress],div[class=x-progress-seek]{background:none!important}div[class=x-progress-track]{background-color:#555!important}div[class=x-progress-load]{background-color:#909090!important}div[class=x-progress-play],div[class=x-seek-handle]{background-color:#eb3c10!important}ins{background:#0b0f10!important}iframe{opacity:.5}#mso-goto-top{background-image:url(http://p2.qhimg.com/t01bca331d0bd2b0d90.png)!important}body .g-night-transparent{background-color:rgba(0,0,0,0.5)!important}.mh-space-ellipsis,.space-ellipsis{color:#0b0f10!important;background-color:#0b0f10!important}.mh-space-ellipsis::before,.space-ellipsis::before{background-color:inherit!important}.mh-space-ellipsis>:first-child,.space-ellipsis>:first-child{color:inherit!important}';var n=document.head;n?n.appendChild(t):document.body.appendChild(t)}})()";
    public static final String JS_DAY_MODE= "javascript:(function(){var e=document.getElementById('360app_night_mode_style');e&&(document.head?document.head.removeChild(e):document.body.removeChild(e))})()";

}
