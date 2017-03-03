package pw.qlm.ipctest;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/3/1.
 */
public class Program implements Parcelable {

    public int programId;
    public String programName;

    public Program(int programId, String programName) {
        this.programId = programId;
        this.programName = programName;
    }

    protected Program(Parcel in) {
        programId = in.readInt();
        programName = in.readString();
    }

    public static final Creator<Program> CREATOR = new Creator<Program>() {
        @Override
        public Program createFromParcel(Parcel in) {
            return new Program(in);
        }

        @Override
        public Program[] newArray(int size) {
            return new Program[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(programId);
        parcel.writeString(programName);
    }

    @Override
    public String toString() {
        return "(" + programId + " ," + programName +")";
    }

    @Override
    public int hashCode() {
        return programName.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o instanceof Program && ((Program) o).programName.equals(programName);
    }
}
