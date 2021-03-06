package pw.qlm.ipctest.aidl;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import pw.qlm.ipctest.IProgramManager;
import pw.qlm.ipctest.Program;

/**
 * Created by Administrator on 2017/3/1.
 */
public class ProgramManagerService extends Service {

    private static final String TAG = "IPC/ProgramService";

    IProgramManager programManager = new ProgramManagerImpl();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        //鉴权，防止任意客户端调用
        if (checkCallingOrSelfPermission("qw.qlm.ipctest.PERMISSION_CALL_REMOTE_SERVICE") == PackageManager.PERMISSION_DENIED) {
            return null;
        }
        return programManager.asBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        //耗时操作需要在新线程中调用
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    programManager.addProgram("Java");
                    programManager.addProgram("Python");
                    programManager.addProgram("C++");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //onBind 与 onUnbind 只会被调用过一次;
    // 返回true, 重新绑定的时候会回调onRebind();
    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind");
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.i(TAG, "onRebind");
    }
}
