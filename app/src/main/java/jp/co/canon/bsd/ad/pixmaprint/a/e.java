package jp.co.canon.bsd.ad.pixmaprint.a;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author wujie
 */
public class e implements Parcelable {
    private String path;

    public e(String path) {
        this.path = path;
    }

    protected e(Parcel in) {
        path = in.readString();
        in.readInt();
        in.readByte();
    }

    public static final Creator<e> CREATOR = new Creator<e>() {
        @Override
        public e createFromParcel(Parcel in) {
            return new e(in);
        }

        @Override
        public e[] newArray(int size) {
            return new e[size];
        }
    };

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeInt(-1);
        dest.writeByte((byte) 0);
    }
}
