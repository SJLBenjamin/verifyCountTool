package com.example.verifycounttool.bean;

import org.litepal.crud.LitePalSupport;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DeviceCountBean extends LitePalSupport {

    double i0t;
    double i1t;
    double i2t;
    double i3t;
    double i4t;
    double i5t;
    double k0n;
    double k1n;
    double k;//作废
    double Sg0t;
    double Sg1t;
    double Sg2t;
    int t;
    long dateEndoc;
    boolean isEffective = true;//当前值是否有效,默认有效
    public long getDateEndoc() {
        return dateEndoc;
    }

    public void setDateEndoc(long dateEndoc) {
        this.dateEndoc = dateEndoc;
    }

    public boolean isEffective() {
        return isEffective;
    }

    public void setEffective(boolean effective) {
        isEffective = effective;
    }

    public double getK0n() {
        return k0n;
    }

    public void setK0n(double k0n) {
        this.k0n = k0n;
    }

    public double getK1n() {
        return k1n;
    }

    public void setK1n(double k1n) {
        this.k1n = k1n;
    }



    public double getI0t() {
        return i0t;
    }

    public void setI0t(double i0t) {
        this.i0t = i0t;
    }

    public double getI1t() {
        return i1t;
    }

    public void setI1t(double i1t) {
        this.i1t = i1t;
    }

    public double getI2t() {
        return i2t;
    }

    public void setI2t(double i2t) {
        this.i2t = i2t;
    }

    public double getI3t() {
        return i3t;
    }

    public void setI3t(double i3t) {
        this.i3t = i3t;
    }

    public double getI4t() {
        return i4t;
    }

    public void setI4t(double i4t) {
        this.i4t = i4t;
    }

//    public long getDate() {
//        return date;
//    }
//
//    public void setDate(long date) {
//        this.date = date;
//    }

    public double getK() {
        return k;
    }

    public void setK(double k) {
        this.k = k;
    }

    public double getI5t() {
        return i5t;
    }

    public void setI5t(double i5t) {
        this.i5t = i5t;
    }

    public double getSg0t() {
        return Sg0t;
    }

    public void setSg0t(double sg0t) {
        Sg0t = sg0t;
    }

    public double getSg1t() {
        return Sg1t;
    }

    public void setSg1t(double sg1t) {
        Sg1t = sg1t;
    }

    public double getSg2t() {
        return Sg2t;
    }

    public void setSg2t(double sg2t) {
        Sg2t = sg2t;
    }

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }

    public DeviceCountBean(double sg2t) {
        Sg2t = sg2t;
    }

    public DeviceCountBean( double i0t, double i1t, double i2t, double i3t, double i4t, double i5t, double k, double sg0t, double sg1t, double sg2t, int t, Date date) {
        this.i0t = i0t;
        this.i1t = i1t;
        this.i2t = i2t;
        this.i3t = i3t;
        this.i4t = i4t;
        this.i5t = i5t;
        this.k = k;
        Sg0t = sg0t;
        Sg1t = sg1t;
        Sg2t = sg2t;
        this.t = t;
        this.dateEndoc = getLongTime(date);
    }

    public DeviceCountBean( double sg0t, double sg1t, double sg2t, int t, Date date) {
        Sg0t = sg0t;
        Sg1t = sg1t;
        Sg2t = sg2t;
        this.t = t;
        this.dateEndoc = getLongTime(date);
    }

    public long getLongTime(Date date) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MILLISECOND, 0);
        return instance.getTime().getTime();
    }

    @Override
    public String toString() {
        return "BloodDataBean{" +
                "i0t=" + i0t +
                ", i1t=" + i1t +
                ", i2t=" + i2t +
                ", i3t=" + i3t +
                ", i4t=" + i4t +
                ", i5t=" + i5t +
                ", k0=" + k0n +
                ", k1=" + k1n +
                ", Sg0t=" + Sg0t +
                ", Sg1t=" + Sg1t +
                ", Sg2t=" + Sg2t +
                ", isEffective=" + isEffective +
                ", t=" + t +
                ", date=" + new SimpleDateFormat("YYYY-MM-dd HH:mm").format(new Date(dateEndoc)) +
                '}';
    }
}