package pw.qlm.ipctest;

import pw.qlm.ipctest.Program;

interface IOnProgramListChangedListener {

    void onChanged(String method, in Program list);

}
