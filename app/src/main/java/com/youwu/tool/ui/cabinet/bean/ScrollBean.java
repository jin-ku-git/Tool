package com.youwu.tool.ui.cabinet.bean;

import com.chad.library.adapter.base.entity.SectionEntity;

import java.util.List;

/**
 * Created by Raul_lsj on 2018/3/22.
 */

public class ScrollBean extends SectionEntity<ScrollBean.ScrollItemBean> {

    public String id;
    public int currentSelect;//1 已选  0 未选
    public int choiceSelect;//1可点击  0不可点击
    public ScrollBean(boolean isHeader, String header,String id,int currentSelect,int choiceSelect) {
        super(isHeader, header);
        this.id=id;
        this.currentSelect=currentSelect;
        this.choiceSelect=choiceSelect;
    }

    public int getCurrentSelect() {
        return currentSelect;
    }

    public void setCurrentSelect(int currentSelect) {
        this.currentSelect = currentSelect;
    }

    public int getChoiceSelect() {
        return choiceSelect;
    }

    public void setChoiceSelect(int choiceSelect) {
        this.choiceSelect = choiceSelect;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ScrollBean(ScrollBean.ScrollItemBean bean) {
        super(bean);
    }


    public static class ScrollItemBean {
        private String text;
        private String store_name;
        private int types;
        private String name;
        private String address;
        private String id;
        private int create_time;
        private int update_time;
        private String code;
        private int status; //1 启用 2
        private String longitude;
        private String latitude;

        private String store_id;

        public int currentSelect;//1 已选  0 未选
        public int choiceSelect;//1可点击  0不可点击




        private List<CabinetBean.DataBean.WithTableBean> with_table;

        public int getCurrentSelect() {
            return currentSelect;
        }

        public void setCurrentSelect(int currentSelect) {
            this.currentSelect = currentSelect;
        }

        public int getChoiceSelect() {
            return choiceSelect;
        }

        public void setChoiceSelect(int choiceSelect) {
            this.choiceSelect = choiceSelect;
        }

        public String getStore_name() {
            return store_name;
        }

        public void setStore_name(String store_name) {
            this.store_name = store_name;
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


        public String getStore_id() {
            return store_id;
        }

        public void setStore_id(String store_id) {
            this.store_id = store_id;
        }

        public List<CabinetBean.DataBean.WithTableBean> getWith_table() {
            return with_table;
        }

        public void setWith_table(List<CabinetBean.DataBean.WithTableBean> with_table) {
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


        public ScrollItemBean(String name,String address,String id,String store_name,String store_id,int status,String latitude,String longitude,List<CabinetBean.DataBean.WithTableBean> with_table,int currentSelect,int choiceSelect) {
            this.name = name;
            this.address = address;
            this.id = id;
            this.store_name = store_name;
            this.status = status;
            this.latitude = latitude;
            this.longitude = longitude;
            this.with_table = with_table;
            this.store_id = store_id;
            this.currentSelect = currentSelect;
            this.choiceSelect = choiceSelect;

        }

        public int getTypes() {
            return types;
        }

        public void setTypes(int types) {
            this.types = types;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

    }
}
