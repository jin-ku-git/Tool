package com.youwu.tool.data.source.http.service;

import com.youwu.tool.bean.TencentBean;
import com.youwu.tool.bean.UpDateBean;
import com.youwu.tool.entity.DemoEntity;

import io.reactivex.Observable;
import me.goldze.mvvmhabit.http.BaseBean;
import me.goldze.mvvmhabit.http.BaseResponse;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by goldze on 2017/6/15.
 */

public interface DemoApiService {
    @GET("action/apiv2/banner?catalog=1")
    Observable<BaseResponse<DemoEntity>> demoGet();

    @FormUrlEncoded
    @POST("action/apiv2/banner")
    Observable<BaseResponse<DemoEntity>> demoPost(@Field("catalog") String catalog);

    /**
     * 地址解析
     * @param address 地址名称
     * @param key 腾讯地图key
     * @return
     */
    @GET("geocoder/v1")
    Observable<BaseBean<TencentBean>> GET_PARSING(@Field("address") String address, @Field("key") String key);

    /**
     * 获取门店列表
     * @return
     */

    @POST("api/tools/get_store_list")
    Observable<BaseBean<Object>> GET_STORE_LIST();
    /**
     * 获取柜子列表
     * @return
     */
    @FormUrlEncoded
    @POST("api/tools/get_cabinet_list")
    Observable<BaseBean<Object>> GET_CABINET_LIST(@Field("store_id") String store_id);

    /**
     * 添加或修改柜子信息
     * @return
     * @param id
     * @param longitude
     * @param latitude
     * @param name
     * @param address
     * @param topic
     * @param num
     */
    @FormUrlEncoded
    @POST("api/tools/set_cabinet_info")
    Observable<BaseBean<Object>> SET_CABINET_INFO(@Field("store_id")String store_id, @Field("longitude")String longitude,@Field("latitude") String latitude,@Field("name") String name,
                                                  @Field("address")String address,@Field("topic") String topic,@Field("num") String num,@Field("id") String id);

    /**
     * 打开柜子
     * @return
     * @param topic
     * @param channel
     * @param type
     */
    @FormUrlEncoded
    @POST("api/tools/open_cabinet_table")
    Observable<BaseBean<Object>> OPEN_CABINET_TABLE(@Field("topic")String topic, @Field("channel")String channel,@Field("type") int type);

    /**
     * 检查更新
     * @return
     */
    @POST("api/tools/app_version")
    Observable<BaseBean<UpDateBean>> GET_APP_VERSION();

    /**
     * 设置紫光灯
     * @return
     * @param type   1指定 2 全部
     * @param class_type  1开启紫光灯，2关闭紫光灯
     * @param topic  多个以逗号分隔
     */
    @FormUrlEncoded
    @POST("api/tools/blb")
    Observable<BaseBean<Object>> SET_BLB(@Field("type") int type,@Field("class")int class_type,@Field("topic")String topic);


    /**
     * 开关加热
     * @param topic                 芯片编码，多个以逗号分隔
     * @param type                  1加热指定柜子，2加热全部启用柜子,3加热全部禁用的柜子
     * @param class_type            1开启加热,2关闭加热
     * @param t_heat                加热最高温度
     * @param b_heat                加热最低温度
     * @param start_time            开始时间
     * @param end_time              结束时间
     * @return
     */
    @FormUrlEncoded
    @POST("api/tools/heating_cabinet")
    Observable<BaseBean<Object>> SET_HEATING_CABINET(@Field("topic")String topic,@Field("type") int type,@Field("class")int class_type,@Field("t_heat")int t_heat
                                                     ,@Field("b_heat")int b_heat,@Field("start_time")String start_time,@Field("end_time")String end_time);

    /**
     * 重启
     * @param topic                 芯片编码，多个以逗号分隔
     * @param type                  1加热指定柜子，2加热全部启用柜子,3加热全部禁用的柜子
     * @return
     */
    @FormUrlEncoded
    @POST("api/tools/restart_cabinet")
    Observable<BaseBean<Object>> SET_RESTART(@Field("topic")String topic,@Field("type") int type);
}
