package pw.qlm.ipctest;

import pw.qlm.ipctest.Program;
import pw.qlm.ipctest.IOnProgramListChangedListener;

interface IProgramManager {
    List<Program> getProgramList();
    void addProgram(String program);
    void removeProgram(String program);
    void registerOnProgramListChangedListener(IOnProgramListChangedListener listener);
    void unregisterOnProgramListChangedListener(IOnProgramListChangedListener listener);
}
