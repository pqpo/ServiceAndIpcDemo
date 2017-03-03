package pw.qlm.ipctest.ipc;

import android.os.IInterface;
import android.os.RemoteException;

/**
 * 不使用aidl
 * Created by Administrator on 2017/3/2.
 */
public interface IConfigManager extends IInterface {

    void setValue(String value) throws RemoteException;
    String getValue() throws RemoteException;

}
