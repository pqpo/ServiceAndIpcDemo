package pw.qlm.ipctest.aidl;

import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import pw.qlm.ipctest.IOnProgramListChangedListener;
import pw.qlm.ipctest.IProgramManager;
import pw.qlm.ipctest.Program;

/**
 * Created by Administrator on 2017/3/1.
 */
public class ProgramManagerImpl extends IProgramManager.Stub {

    private final HashSet<Program> mProgramList = new HashSet<>();
    private AtomicInteger ids = new AtomicInteger(0);

    //夸进程监听器不能简单的使用List，因为不同的进程，list内保存的对象不是同一个
    private RemoteCallbackList<IOnProgramListChangedListener> callbacks = new RemoteCallbackList<>();

    @Override
    public List<Program> getProgramList() throws RemoteException {
        synchronized (mProgramList) {
            return Arrays.asList(mProgramList.toArray(new Program[]{}));
        }
    }

    //远程调用是运行与Binder线程池，故可以进行耗时操作；
    // 因为是线程池中运行，所以可能会有多个线程同时调用，需要注意线程安全保护；
    // 客户端调用的时候需要注意，如果是耗时操作，需要放置在新线程中执行远程调用
    @Override
    public void addProgram(String program) throws RemoteException {
        //模拟耗时操作
        SystemClock.sleep(1000);
        synchronized (mProgramList) {
            Program program1 = new Program(ids.incrementAndGet(), program);
            if (mProgramList.add(program1)){
                onNotifyProgramListChanged("add", program1);
            }
        }
    }

    @Override
    public void removeProgram(String program) throws RemoteException {
        //模拟耗时操作
        SystemClock.sleep(1000);
        synchronized (mProgramList) {
            Program removed = null;
            for (Program m : mProgramList) {
                if (m.programName.equals(program)) {
                    removed = m;
                    break;
                }
            }
            if (removed != null && mProgramList.remove(removed)) {
                onNotifyProgramListChanged("remove", removed);
            }
        }
    }

    @Override
    public void registerOnProgramListChangedListener(IOnProgramListChangedListener listener) throws RemoteException {
        if(listener != null) {
            callbacks.register(listener);
        }
    }

    @Override
    public void unregisterOnProgramListChangedListener(IOnProgramListChangedListener listener) throws RemoteException {
        if(listener != null) {
            callbacks.unregister(listener);
        }
    }

    private void onNotifyProgramListChanged(String method, Program program1) throws RemoteException {
        //beginBroadcast(),callbacks.finishBroadcast()必须成对出现
        int N = callbacks.beginBroadcast();
        for (int i = 0; i < N; i++) {
            IOnProgramListChangedListener broadcastItem = callbacks.getBroadcastItem(i);
            broadcastItem.onChanged(method, program1);
        }
        callbacks.finishBroadcast();
    }

}
