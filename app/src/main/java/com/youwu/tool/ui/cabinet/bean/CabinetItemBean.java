package com.youwu.tool.ui.cabinet.bean;

public class CabinetItemBean {
    public String id;
    public String Latitude;//纬度
    public String longitude;//经度
    public String address;//地址
    public String name;//柜子名称
    public String number;//主板编号
    public String lattice_num;//格子数量
    public String store_id;//门店id
    public String store_name;//门店名称

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLattice_num() {
        return lattice_num;
    }

    public void setLattice_num(String lattice_num) {
        this.lattice_num = lattice_num;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }
}
