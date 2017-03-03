package pw.qlm.ipctest.ipc;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * Created by Administrator on 2017/3/2.
 */

public class ConfigManagerImpl extends Binder implements IConfigManager {

    private static final java.lang.String DESCRIPTOR = "pw.qlm.ipctest.ipc.ConfigManagerImpl";

    private String value;
    private Context mContext;

    public ConfigManagerImpl(Context context) {
        this.attachInterface(this, DESCRIPTOR);
        mContext = context;
    }

    @Override
    public synchronized void setValue(String value) throws RemoteException{
        this.value = value;
    }

    @Override
    public synchronized String getValue() throws RemoteException {
        return value;
    }

    @Override
    public IBinder asBinder() {
        return this;
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        //鉴权
        if (mContext.checkCallingOrSelfPermission("qw.qlm.ipctest.PERMISSION_CALL_REMOTE_SERVICE") == PackageManager.PERMISSION_DENIED) {
            return false;
        }
        String packageName = "";
        String[] packagesForUid = mContext.getPackageManager().getPackagesForUid(getCallingUid());
        if (packagesForUid != null && packagesForUid.length > 0) {
            packageName = packagesForUid[0];
        }
        if (packageName == null || !packageName.startsWith("pw.qlm")) {
            return false;
        }

        switch (code) {
            case INTERFACE_TRANSACTION:
                reply.writeString(DESCRIPTOR);
                return true;
            case ConfigManagerProxy.TRANSACT_getValue:
                data.enforceInterface(DESCRIPTOR);
                String result = getValue();
                reply.writeNoException();
                reply.writeString(result);
                return true;
            case ConfigManagerProxy.TRANSACT_setValue:
                data.enforceInterface(DESCRIPTOR);
                String value = data.readString();
                setValue(value);
                reply.writeNoException();
                return true;
        }
        return super.onTransact(code, data, reply, flags);
    }

    public static IConfigManager asInterface(IBinder binder) {
        if (binder == null) {
            return null;
        }
        IInterface iInterface = binder.queryLocalInterface(DESCRIPTOR);
        if (iInterface != null && iInterface instanceof IConfigManager) {
            return (IConfigManager) iInterface;
        }
        return new ConfigManagerProxy(binder);
    }

    private static class ConfigManagerProxy implements IConfigManager {

        private IBinder remote;

        public ConfigManagerProxy(IBinder remote) {
            this.remote = remote;
        }

        @Override
        public void setValue(String value) throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            try {
                data.writeInterfaceToken(DESCRIPTOR);
                data.writeString(value);
                remote.transact(TRANSACT_setValue, data, reply, 0);
                reply.readException();
            } finally {
                data.recycle();
                reply.recycle();
            }
        }

        @Override
        public String getValue() throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            String result = null;
            try {
                data.writeInterfaceToken(DESCRIPTOR);
                remote.transact(TRANSACT_getValue, data, reply, 0);
                reply.readException();
                result = reply.readString();
            } finally {
                data.recycle();
                reply.recycle();
            }
            return result;
        }

        @Override
        public IBinder asBinder() {
            return remote;
        }

        static final int TRANSACT_setValue = IBinder.FIRST_CALL_TRANSACTION + 0;
        static final int TRANSACT_getValue = IBinder.FIRST_CALL_TRANSACTION + 1;

    }

}
