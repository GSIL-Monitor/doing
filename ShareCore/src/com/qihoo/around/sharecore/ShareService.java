package com.qihoo.around.sharecore;

import com.qihoo.around.sharecore.aidl.IShareCore;
import com.qihoo.around.sharecore.aidl.IShareResourceFetcher;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * renjh1 2014年12月27日
 */

public class ShareService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    IShareCore.Stub stub = new IShareCore.Stub() {

        @Override
        public void openShare(IShareResourceFetcher callback, boolean isAsync , boolean isNightMode)
                throws RemoteException {
            ShareManager.getInstance().setShareCallback(callback);
            Intent intent = new Intent(ShareService.this, ShareActivity.class);
            intent.putExtra(ShareConstans.IS_ASYNC,isAsync);
            intent.putExtra(ShareConstans.IS_NIGHT_MODE,isNightMode);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    };
}
