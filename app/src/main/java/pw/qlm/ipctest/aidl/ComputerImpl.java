package pw.qlm.ipctest.aidl;

import android.os.RemoteException;

import pw.qlm.ipctest.IComputer;

/**
 * Created by Administrator on 2017/3/6.
 */

public class ComputerImpl extends IComputer.Stub {

    @Override
    public int add(int x, int y) throws RemoteException {
        return x + y;
    }

}
