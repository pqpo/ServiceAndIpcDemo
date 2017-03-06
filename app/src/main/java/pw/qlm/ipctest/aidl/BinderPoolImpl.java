package pw.qlm.ipctest.aidl;

import android.os.IBinder;
import android.os.RemoteException;

import pw.qlm.ipctest.IBinderPool;

/**
 * 连接池，查询不同业务的binder
 * Created by Administrator on 2017/3/6.
 */
public class BinderPoolImpl extends IBinderPool.Stub {

    public static final int PROGRAM_MANAGER = 11;
    public static final int COMPUTER_MANAGER = 12;

    @Override
    public IBinder query(int bindId) throws RemoteException {
        IBinder iBinder = null;
        if (bindId == PROGRAM_MANAGER) {
            iBinder = new ProgramManagerImpl();
        } else if (bindId == COMPUTER_MANAGER) {
            iBinder = new ComputerImpl();
        }
        return iBinder;
    }

}
