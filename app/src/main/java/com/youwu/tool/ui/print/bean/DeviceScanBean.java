package com.youwu.tool.ui.print.bean;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

public class DeviceScanBean implements Parcelable {

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    private BluetoothDevice bluetoothDevice;
    String macAddress;
    String deviceName;
    String serialNumber = "";
    boolean isConnect;
    int rssi;


    public DeviceScanBean(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord) {
        this.bluetoothDevice = bluetoothDevice;
        this.macAddress = bluetoothDevice.getAddress();
        this.deviceName = bluetoothDevice.getName();
        this.rssi = rssi;
    }

    public DeviceScanBean(BluetoothDevice bluetoothDevice, int rssi, String sn) {
        // TODO Auto-generated constructor stub
        this.macAddress = bluetoothDevice.getAddress();
        this.deviceName = bluetoothDevice.getName();
        this.rssi = rssi;
        this.serialNumber = sn;
    }

    public DeviceScanBean(Parcel source) {
        // TODO Auto-generated constructor stub
        this.macAddress = source.readString();
        this.deviceName = source.readString();
        this.rssi = source.readInt();
        this.serialNumber = source.readString();
    }


    public DeviceScanBean(String addr, String deviceName) {
        // TODO Auto-generated constructor stub
        this.macAddress = addr;
        this.deviceName = deviceName;
    }

    public boolean isConnect() {
        return isConnect;
    }

    public void setConnect(boolean connect) {
        isConnect = connect;
    }



    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getRssi() {
        return rssi;
    }


    public void setRssi(int rssi) {
        this.rssi = rssi;
    }


    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }


    @Override
    public boolean equals(Object o) {
        // TODO Auto-generated method stub

        return macAddress.equals(((DeviceScanBean) o).getMacAddress());
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
        dest.writeString(macAddress);
        dest.writeString(deviceName);
        dest.writeInt(rssi);
        dest.writeString(serialNumber);
    }

    public static final Creator<DeviceScanBean> CREATOR = new Creator<DeviceScanBean>() {

        @Override
        public DeviceScanBean[] newArray(int size) {
            // TODO Auto-generated method stub
            return new DeviceScanBean[size];
        }

        @Override
        public DeviceScanBean createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            return new DeviceScanBean(source);
        }
    };
}
