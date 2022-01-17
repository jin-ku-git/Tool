package com.youwu.tool.ui.cabinet.bean;

import java.io.Serializable;
import java.util.List;

public class CabinetBean implements Serializable {


    public List<DataBean> data;


    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements Serializable{


        /**
         * id : 1
         * create_time : 1612002531
         * update_time : 1630813940
         * name : 先每集团取餐柜
         * code : 2101301821220
         * status : 2
         * province_id : 0
         * address : 河东区凤凰大街先每集团门卫
         * city_id : 0
         * district_id : 0
         * longitude : 118.420261
         * latitude : 35.070781
         * image : jpg/f446c2f159202102021121044341.jpg
         * store_id : 1
         * qrcode_img : /static/cabinet_qr_code/1_1_2101301821220.jpg
         * company_id : 1
         * operator_id : 1
         * alias : null
         * sort : null
         * with_table : [{"id":1,"create_time":1612002531,"update_time":1620652179,"cabinet_id":1,"cabinet_number":"A门","topic":"86895604631199311","channel":1},{"id":2,"create_time":1612002531,"update_time":1620652179,"cabinet_id":1,"cabinet_number":"B门","topic":"86895604631199311","channel":2},{"id":3,"create_time":1612002531,"update_time":1620652179,"cabinet_id":1,"cabinet_number":"C门","topic":"86895604631199311","channel":3}]
         */

        private int id;
        private int create_time;
        private int update_time;
        private String name;
        private String code;
        private int status;
        private int province_id;
        private String address;
        private int city_id;
        private int district_id;
        private String longitude;
        private String latitude;
        private String image;
        private int store_id;
        private String qrcode_img;
        private int company_id;
        private int operator_id;
        private Object alias;
        private Object sort;
        private List<WithTableBean> with_table;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getCreate_time() {
            return create_time;
        }

        public void setCreate_time(int create_time) {
            this.create_time = create_time;
        }

        public int getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(int update_time) {
            this.update_time = update_time;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getProvince_id() {
            return province_id;
        }

        public void setProvince_id(int province_id) {
            this.province_id = province_id;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public int getCity_id() {
            return city_id;
        }

        public void setCity_id(int city_id) {
            this.city_id = city_id;
        }

        public int getDistrict_id() {
            return district_id;
        }

        public void setDistrict_id(int district_id) {
            this.district_id = district_id;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public int getStore_id() {
            return store_id;
        }

        public void setStore_id(int store_id) {
            this.store_id = store_id;
        }

        public String getQrcode_img() {
            return qrcode_img;
        }

        public void setQrcode_img(String qrcode_img) {
            this.qrcode_img = qrcode_img;
        }

        public int getCompany_id() {
            return company_id;
        }

        public void setCompany_id(int company_id) {
            this.company_id = company_id;
        }

        public int getOperator_id() {
            return operator_id;
        }

        public void setOperator_id(int operator_id) {
            this.operator_id = operator_id;
        }

        public Object getAlias() {
            return alias;
        }

        public void setAlias(Object alias) {
            this.alias = alias;
        }

        public Object getSort() {
            return sort;
        }

        public void setSort(Object sort) {
            this.sort = sort;
        }

        public List<WithTableBean> getWith_table() {
            return with_table;
        }

        public void setWith_table(List<WithTableBean> with_table) {
            this.with_table = with_table;
        }

        public static class WithTableBean {
            /**
             * id : 1
             * create_time : 1612002531
             * update_time : 1620652179
             * cabinet_id : 1
             * cabinet_number : A门
             * topic : 86895604631199311
             * channel : 1
             */

            private int id;
            private int create_time;
            private int update_time;
            private int cabinet_id;
            private String cabinet_number;
            private String topic;
            private int channel;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getCreate_time() {
                return create_time;
            }

            public void setCreate_time(int create_time) {
                this.create_time = create_time;
            }

            public int getUpdate_time() {
                return update_time;
            }

            public void setUpdate_time(int update_time) {
                this.update_time = update_time;
            }

            public int getCabinet_id() {
                return cabinet_id;
            }

            public void setCabinet_id(int cabinet_id) {
                this.cabinet_id = cabinet_id;
            }

            public String getCabinet_number() {
                return cabinet_number;
            }

            public void setCabinet_number(String cabinet_number) {
                this.cabinet_number = cabinet_number;
            }

            public String getTopic() {
                return topic;
            }

            public void setTopic(String topic) {
                this.topic = topic;
            }

            public int getChannel() {
                return channel;
            }

            public void setChannel(int channel) {
                this.channel = channel;
            }
        }
    }



}
