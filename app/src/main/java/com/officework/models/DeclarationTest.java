package com.officework.models;

import android.os.Parcel;
import android.os.Parcelable;

public class DeclarationTest implements Parcelable
        ,Cloneable
{
    private String DeclarationID;
    private String DeclarationTest;
    private String DeclarationTestDetail;
    private boolean DeclarationValue;

    protected DeclarationTest(Parcel in) {
        DeclarationID = in.readString();
        DeclarationTest = in.readString();
        DeclarationTestDetail = in.readString();
        DeclarationValue = in.readByte() != 0;
    }

    public static final Creator<DeclarationTest> CREATOR = new Creator<DeclarationTest>() {
        @Override
        public DeclarationTest createFromParcel(Parcel in) {
            return new DeclarationTest(in);
        }

        @Override
        public DeclarationTest[] newArray(int size) {
            return new DeclarationTest[size];
        }
    };

    public String getDeclarationID() {
        return DeclarationID;
    }

    public void setDeclarationID(String declarationID) {
        DeclarationID = declarationID;
    }

    public String getDeclarationTest() {
        return DeclarationTest;
    }

    public void setDeclarationTest(String declarationTest) {
        DeclarationTest = declarationTest;
    }

    public String getDeclarationTestDetail() {
        return DeclarationTestDetail;
    }

    public void setDeclarationTestDetail(String declarationTestDetail) {
        DeclarationTestDetail = declarationTestDetail;
    }

    public boolean isDeclarationValue() {
        return DeclarationValue;
    }

    public void setDeclarationValue(boolean declarationValue) {
        DeclarationValue = declarationValue;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(DeclarationID);
        dest.writeString(DeclarationTest);
        dest.writeString(DeclarationTestDetail);
        dest.writeByte((byte) (DeclarationValue ? 1 : 0));
    }

    public DeclarationTest clone() throws CloneNotSupportedException{
        DeclarationTest declarationTest = (DeclarationTest)super.clone();

        return declarationTest;
    }
}
