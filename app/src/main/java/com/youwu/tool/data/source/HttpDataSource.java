package com.youwu.tool.data.source;

import com.youwu.tool.bean.TencentBean;
import com.youwu.tool.bean.UpDateBean;
import com.youwu.tool.entity.DemoEntity;
import com.youwu.tool.ui.cabinet.bean.HeatBean;

import io.reactivex.Observable;
import me.goldze.mvvmhabit.http.BaseBean;
import me.goldze.mvvmhabit.http.BaseResponse;

/**
 * Created by goldze on 2019/3/26.
 */
public interface HttpDataSource {
    //模拟登录
    Observable<Object> login();

    //模拟上拉加载
    Observable<DemoEntity> loadMore();

    Observable<BaseResponse<DemoEntity>> demoGet();

    Observable<BaseResponse<DemoEntity>> demoPost(String catalog);

    //地址解析
    Observable<BaseBean<TencentBean>> GET_PARSING(String address,String key);
    //获取门店列表
    Observable<BaseBean<Object>> GET_STORE_LIST();
    //获取柜子列表
    Observable<BaseBean<Object>> GET_CABINET_LIST(String store_id);
    //添加或修改柜子信息
    Observable<BaseBean<Object>> SET_CABINET_INFO(String store_id, String longitude, String latitude, String name, String address, String topic, String num,String id);
    //打开柜子
    Observable<BaseBean<Object>> OPEN_CABINET_TABLE(String topic, String channel, int type);
    //检查更新
    Observable<BaseBean<UpDateBean>> GET_APP_VERSION();
    //设置紫光灯
    Observable<BaseBean<Object>> SET_BLB(int type, int class_type, String topic);
    //开关加热
    Observable<BaseBean<Object>> SET_HEATING_CABINET(HeatBean bean);
    //重启
    Observable<BaseBean<Object>> SET_RESTART(String topic,int type);
}
