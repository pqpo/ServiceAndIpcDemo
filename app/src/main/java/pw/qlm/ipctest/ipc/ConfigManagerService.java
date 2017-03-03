package pw.qlm.ipctest.ipc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Administrator on 2017/3/2.
 */

public class ConfigManagerService extends Service {

    private static final String TAG = "IPC/ConfigService";

    IConfigManager configManager = new ConfigManagerImpl(this);

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        try {
            configManager.setValue("set in ConfigManagerService!");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return configManager.asBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }
}
