package com.youwu.tool.bean;

import java.io.Serializable;

/**
 * 腾讯地图返回数据
 * 2021/12/15
 */
public class TencentBean implements Serializable {


        /**
         * title : 惠民早餐
         * location : {"lng":118.393692,"lat":35.07349}
         * ad_info : {"adcode":"371312"}
         * address_components : {"province":"山东省","city":"临沂市","district":"河东区","street":"","street_number":""}
         * similarity : 0.8
         * deviation : 1000
         * reliability : 7
         * level : 11
         */

        private String title;
        private LocationBean location;
        private AdInfoBean ad_info;
        private String address;
        private AddressComponentsBean address_components;
        private double similarity;
        private int deviation;
        private int reliability;
        private int level;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public LocationBean getLocation() {
            return location;
        }

        public void setLocation(LocationBean location) {
            this.location = location;
        }

        public AdInfoBean getAd_info() {
            return ad_info;
        }

        public void setAd_info(AdInfoBean ad_info) {
            this.ad_info = ad_info;
        }

        public AddressComponentsBean getAddress_components() {
            return address_components;
        }

        public void setAddress_components(AddressComponentsBean address_components) {
            this.address_components = address_components;
        }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getSimilarity() {
            return similarity;
        }

        public void setSimilarity(double similarity) {
            this.similarity = similarity;
        }

        public int getDeviation() {
            return deviation;
        }

        public void setDeviation(int deviation) {
            this.deviation = deviation;
        }

        public int getReliability() {
            return reliability;
        }

        public void setReliability(int reliability) {
            this.reliability = reliability;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public static class LocationBean {
            /**
             * lng : 118.393692
             * lat : 35.07349
             */

            private double lng;
            private double lat;

            public double getLng() {
                return lng;
            }

            public void setLng(double lng) {
                this.lng = lng;
            }

            public double getLat() {
                return lat;
            }

            public void setLat(double lat) {
                this.lat = lat;
            }
        }

        public static class AdInfoBean {
            /**
             * adcode : 371312
             */

            private String adcode;

            public String getAdcode() {
                return adcode;
            }

            public void setAdcode(String adcode) {
                this.adcode = adcode;
            }
        }

        public static class AddressComponentsBean {
            /**
             * province : 山东省
             * city : 临沂市
             * district : 河东区
             * street :
             * street_number :
             */

            private String province;
            private String city;
            private String district;
            private String street;
            private String street_number;

            public String getProvince() {
                return province;
            }

            public void setProvince(String province) {
                this.province = province;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getDistrict() {
                return district;
            }

            public void setDistrict(String district) {
                this.district = district;
            }

            public String getStreet() {
                return street;
            }

            public void setStreet(String street) {
                this.street = street;
            }

            public String getStreet_number() {
                return street_number;
            }

            public void setStreet_number(String street_number) {
                this.street_number = street_number;
            }
        }
}
