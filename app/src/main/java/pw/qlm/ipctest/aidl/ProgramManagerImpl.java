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
    private RemoteCallbackList<IOnProgramListChangedListener> callbacks = new RemoteCallbackList<>();

    @Override
    public List<Program> getProgramList() throws RemoteException {
        synchronized (mProgramList) {
            return Arrays.asList(mProgramList.toArray(new Program[]{}));
        }
    }

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
        int N = callbacks.beginBroadcast();
        for (int i = 0; i < N; i++) {
            IOnProgramListChangedListener broadcastItem = callbacks.getBroadcastItem(i);
            broadcastItem.onChanged(method, program1);
        }
        callbacks.finishBroadcast();
    }

}
