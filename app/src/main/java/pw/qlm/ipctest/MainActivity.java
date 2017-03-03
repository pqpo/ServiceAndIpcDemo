package pw.qlm.ipctest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import pw.qlm.ipctest.aidl.ProgramManagerService;
import pw.qlm.ipctest.ipc.ConfigManagerImpl;
import pw.qlm.ipctest.ipc.ConfigManagerService;
import pw.qlm.ipctest.ipc.IConfigManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int MSG_CONNECT_TO_SERVICE = 100;
    private static final int MSG_REFRESH_LIST = 101;
    private static final int MSG_CHANGED = 102;

    private static final String TAG = "IPC/MainActivity";
    private IProgramManager mProgramManager;
    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.i(TAG, "binderDied :　disconnected!" + " Thread:" + Thread.currentThread().getName());
            if (mProgramManager != null) {
                mProgramManager.asBinder().unlinkToDeath(mDeathRecipient, 0);
                mProgramManager = null;
            }
            mHandler.sendEmptyMessage(MSG_CONNECT_TO_SERVICE);
        }
    };

    //夸进程监听器也是一个Binder,简单的接口没有夸进程的能力
    private IOnProgramListChangedListener mListener = new IOnProgramListChangedListener.Stub() {
        @Override
        public void onChanged(String method, Program program) throws RemoteException {
            Message.obtain(mHandler, MSG_CHANGED, method + " " + program + " success").sendToTarget();
        }
    };

    private ServiceConnection mProgramServiceConnection;

    private ServiceConnection mConfigServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IConfigManager iConfigManager = ConfigManagerImpl.asInterface(service);
            try {
                Log.i(TAG, iConfigManager.getValue());
                iConfigManager.setValue("set in MainActivity!");
                Log.i(TAG, iConfigManager.getValue());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_CONNECT_TO_SERVICE) {
                bindToService();
            } else if (msg.what == MSG_REFRESH_LIST) {
                mAdapter.clear();
                mAdapter.addAll((List<Program>) msg.obj);
            } else if (msg.what == MSG_CHANGED) {
                toast((String) msg.obj);
            }
        }
    };

    private ArrayAdapter<Program> mAdapter;

    Button btnStart;
    Button btnConnect;
    Button btnUnbind;
    Button btnKill;
    EditText etProgramName;
    Button btnAdd;
    Button btnRemove;
    Button btnRefresh;
    ListView lvPrograms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnStart = (Button) findViewById(R.id.btn_start);
        btnConnect = (Button) findViewById(R.id.btn_connect);
        btnUnbind = (Button) findViewById(R.id.btn_unbind);
        btnKill = (Button) findViewById(R.id.btn_kill);
        etProgramName = (EditText) findViewById(R.id.et_program);
        btnAdd = (Button) findViewById(R.id.btn_add);
        btnRemove = (Button) findViewById(R.id.btn_remove);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);
        lvPrograms = (ListView) findViewById(R.id.lv_result);

        btnStart.setOnClickListener(this);
        btnKill.setOnClickListener(this);
        btnUnbind.setOnClickListener(this);
        btnConnect.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnRemove.setOnClickListener(this);
        btnRefresh.setOnClickListener(this);

        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        lvPrograms.setAdapter(mAdapter);
        TextView emptyView = new TextView(this);
        emptyView.setText("empty");
        lvPrograms.setEmptyView(emptyView);

        Intent configServiceIntent = new Intent(this, ConfigManagerService.class);
        bindService(configServiceIntent, mConfigServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void bindToService() {
        Log.i(TAG, "connect to service");
        Intent intent = new Intent(this, ProgramManagerService.class);
        mProgramServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                if (service == null) {
                    toast("permission denied!");
                    return;
                }
                mProgramManager = IProgramManager.Stub.asInterface(service);
                Log.i(TAG, "connect success!");
                toast("connect success!");
                try {
                    mProgramManager.registerOnProgramListChangedListener(mListener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i(TAG, "onServiceDisconnected :　disconnected!" + " Thread:" + Thread.currentThread().getName());
                mProgramManager = null;
                bindToService();
            }
        };
        bindService(intent, mProgramServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        String programName = etProgramName.getText().toString().trim();
        switch (id){
            case R.id.btn_start:
                startProgramService();
                toast("started");
                break;
            case R.id.btn_kill:
                unbindProgramService();
                stopService(new Intent(this, ProgramManagerService.class));
                toast("stopped");
                break;
            case R.id.btn_connect:
                unbindProgramService();
                bindToService();
                break;
            case R.id.btn_unbind:
                if (unbindProgramService()) {
                    toast("Unbind");
                } else {
                    toast("Unbounded, can only unbind once");
                }
                break;
            case R.id.btn_add:
                if(checkConnectionAndInput(programName)) {
                    if (TextUtils.isEmpty(programName)) {
                        toast("please input");
                    } else {
                        remoteAdd(programName);
                    }
                }
                break;
            case R.id.btn_remove:
                if(checkConnectionAndInput(programName)) {
                    if (TextUtils.isEmpty(programName)) {
                        toast("please input");
                    } else {
                        remoteRemove(programName);
                    }
                }
                break;
            case R.id.btn_refresh:
                if(checkConnection()) {
                    remoteRefresh();
                }
                break;

        }
    }

    private void startProgramService() {
        Intent programServiceIntent = new Intent(this, ProgramManagerService.class);
        startService(programServiceIntent);
    }

    //远程调用为耗时操作
    private void remoteAdd(final String programName) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    mProgramManager.addProgram(programName);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //远程调用为耗时操作
    private void remoteRemove(final String programName) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    mProgramManager.removeProgram(programName);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //远程调用为耗时操作
    private void remoteRefresh() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    List<Program> programList = mProgramManager.getProgramList();
                    Message.obtain(mHandler, MSG_REFRESH_LIST, programList).sendToTarget();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private boolean unbindProgramService() {
        if (mProgramServiceConnection != null) {
            unbindService(mProgramServiceConnection);
            mProgramServiceConnection = null;
            mProgramManager = null;
            Log.i(TAG, "unbind ProgramManagerService");
            return true;
        }
        return false;
    }

    private boolean checkConnection() {
        boolean result = mProgramManager != null;
        if (!result) {
            toast("The service is not connected");
        }
        return result;
    }

    private boolean checkConnectionAndInput(String programName) {
        boolean result = checkConnection();
        if (result && TextUtils.isEmpty(programName)) {
            toast("please input the program name");
            result = false;
        }
        return result;
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        try {
            if (mProgramManager != null && mListener != null) {
                mProgramManager.unregisterOnProgramListChangedListener(mListener);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        unbindProgramService();
        unbindService(mConfigServiceConnection);
        Log.i(TAG, "unbind ConfigManagerService");
        super.onDestroy();
    }
}
