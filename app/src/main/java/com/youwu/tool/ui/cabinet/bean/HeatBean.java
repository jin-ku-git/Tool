package com.youwu.tool.ui.cabinet.bean;

import java.io.Serializable;

public class HeatBean implements Serializable {
    public String topic;        //芯片编码，多个以逗号分隔
    public int type;            //1加热指定柜子，2加热全部启用柜子,3加热全部禁用的柜子
    public int t_heat;          //加热最高温度
    public int b_heat;          //加热最低温度
    public int duration;        //加热时长
    public int class_type;      //1开启加热,2关闭加热
    public String start_time;      //开始时间
    public String end_time;        //结束时间


    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public int getClass_type() {
        return class_type;
    }

    public void setClass_type(int class_type) {
        this.class_type = class_type;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getT_heat() {
        return t_heat;
    }

    public void setT_heat(int t_heat) {
        this.t_heat = t_heat;
    }

    public int getB_heat() {
        return b_heat;
    }

    public void setB_heat(int b_heat) {
        this.b_heat = b_heat;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
