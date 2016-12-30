package com.webianks.hatkemessenger;

/**
 * Created by R Ankit on 24-12-2016.
 */

public class SMS {

    private long _id;
    private String _address;
    private String _msg;
    private String _readState; //"0" for have not read sms and "1" for have read sms
    private long _time;
    private String _folderName;
    private int color;

    public long getId() {
        return _id;
    }

    public String getAddress() {
        return _address;
    }

    public String getMsg() {
        return _msg;
    }

    public String getReadState() {
        return _readState;
    }

    public long getTime() {
        return _time;
    }

    public String getFolderName() {
        return _folderName;
    }


    public void setId(long id) {
        _id = id;
    }

    public void setAddress(String address) {
        _address = address;
    }

    public void setMsg(String msg) {
        _msg = msg;
    }

    public void setReadState(String readState) {
        _readState = readState;
    }

    public void setTime(long time) {
        _time = time;
    }

    public void setFolderName(String folderName) {
        _folderName = folderName;
    }

    @Override
    public boolean equals(Object obj) {

        SMS sms = (SMS) obj;

        return _address.equals(sms._address);
    }

    public int hashCode() {
        return this._address.hashCode();
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}