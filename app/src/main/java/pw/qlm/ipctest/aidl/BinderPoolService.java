package pw.qlm.ipctest.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import pw.qlm.ipctest.IBinderPool;

/**
 * binder 连接池
 * Created by Administrator on 2017/3/6.
 */
public class BinderPoolService extends Service {

    private IBinderPool iBinderPool = new BinderPoolImpl();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinderPool.asBinder();
    }

}
