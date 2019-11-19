package com.officework.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Diksha on 10/5/2018.
 */

public class SocketDataSync implements Parcelable {

    private int status;
    private boolean IsSuccess;
    private String ErrorMessage;
    private String Message;
    private DataPojo Data;



    protected SocketDataSync(Parcel in) {
        status = in.readInt();
        IsSuccess = in.readByte() != 0;
        ErrorMessage = in.readString();
        Message = in.readString();
        Data = in.readParcelable(DataPojo.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(status);
        dest.writeByte((byte) (IsSuccess ? 1 : 0));
        dest.writeString(ErrorMessage);
        dest.writeString(Message);
        dest.writeParcelable(Data, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SocketDataSync> CREATOR = new Creator<SocketDataSync>() {
        @Override
        public SocketDataSync createFromParcel(Parcel in) {
            return new SocketDataSync(in);
        }

        @Override
        public SocketDataSync[] newArray(int size) {
            return new SocketDataSync[size];
        }
    };

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public DataPojo getData() {
        return Data;
    }

    public void setData(DataPojo data) {
        Data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return IsSuccess;
    }

    public void setSuccess(boolean success) {
        IsSuccess = success;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        ErrorMessage = errorMessage;
    }


}
