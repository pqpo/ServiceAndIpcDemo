package pw.qlm.ipctest;

import android.app.Application;

/**
 * Created by Administrator on 2017/3/2.
 */

public class MyApplication extends Application {

//    IBookManager bookManager = new ProgramManagerImpl();

    @Override
    public void onCreate() {
        super.onCreate();
//        try {
//            bookManager.addBook(new Program(1, "Android"));
//            bookManager.addBook(new Program(2, "Ios"));
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//        ServiceManager.addService("mybookmanager", bookManager.asBinder());
    }

}
