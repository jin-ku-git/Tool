package com.youwu.tool.data.source.http;

import com.youwu.tool.bean.TencentBean;
import com.youwu.tool.bean.UpDateBean;
import com.youwu.tool.data.source.HttpDataSource;
import com.youwu.tool.data.source.http.service.DemoApiService;
import com.youwu.tool.entity.DemoEntity;
import com.youwu.tool.ui.cabinet.bean.HeatBean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import me.goldze.mvvmhabit.http.BaseBean;
import me.goldze.mvvmhabit.http.BaseResponse;

/**
 * Created by goldze on 2019/3/26.
 */
public class HttpDataSourceImpl implements HttpDataSource {
    private DemoApiService apiService;
    private volatile static HttpDataSourceImpl INSTANCE = null;

    public static HttpDataSourceImpl getInstance(DemoApiService apiService) {
        if (INSTANCE == null) {
            synchronized (HttpDataSourceImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpDataSourceImpl(apiService);
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    private HttpDataSourceImpl(DemoApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public Observable<Object> login() {
        return Observable.just(new Object()).delay(3, TimeUnit.SECONDS); //延迟3秒
    }

    @Override
    public Observable<DemoEntity> loadMore() {
        return Observable.create(new ObservableOnSubscribe<DemoEntity>() {
            @Override
            public void subscribe(ObservableEmitter<DemoEntity> observableEmitter) throws Exception {
                DemoEntity entity = new DemoEntity();
                List<DemoEntity.ItemsEntity> itemsEntities = new ArrayList<>();
                //模拟一部分假数据
                for (int i = 0; i < 10; i++) {
                    DemoEntity.ItemsEntity item = new DemoEntity.ItemsEntity();
                    item.setId(-1);
                    item.setName("模拟条目");
                    itemsEntities.add(item);
                }
                entity.setItems(itemsEntities);
                observableEmitter.onNext(entity);
            }
        }).delay(3, TimeUnit.SECONDS); //延迟3秒
    }

    @Override
    public Observable<BaseResponse<DemoEntity>> demoGet() {
        return apiService.demoGet();
    }

    @Override
    public Observable<BaseResponse<DemoEntity>> demoPost(String catalog) {
        return apiService.demoPost(catalog);
    }

    /**
     * 地址解析
     * @param address 地址名称
     * @param key 腾讯地图key
     * @return
     */
    @Override
    public Observable<BaseBean<TencentBean>> GET_PARSING(String address,String key) {
        return apiService.GET_PARSING(address,key);
    }

    /**
     * 获取门店列表
     * @return
     */
    @Override
    public Observable<BaseBean<Object>> GET_STORE_LIST() {
        return apiService.GET_STORE_LIST();
    }
    /**
     * 获取柜子列表
     * @return
     */
    @Override
    public Observable<BaseBean<Object>> GET_CABINET_LIST(String store_id) {
        return apiService.GET_CABINET_LIST(store_id);
    }

    /**
     * 添加或修改柜子信息
     * @return
     */
    @Override
    public Observable<BaseBean<Object>> SET_CABINET_INFO(String store_id, String longitude, String latitude, String name, String address, String topic, String num,String id) {
        return apiService.SET_CABINET_INFO(store_id,longitude,latitude,name,address,topic,num,id);
    }
    /**
     * 打开柜子
     * @return
     */
    @Override
    public Observable<BaseBean<Object>> OPEN_CABINET_TABLE(String topic, String channel, int type) {
        return apiService.OPEN_CABINET_TABLE(topic,channel,type);
    }
    /**
     * 检查更新
     * @return
     */
    @Override
    public Observable<BaseBean<UpDateBean>> GET_APP_VERSION() {
        return apiService.GET_APP_VERSION();
    }
    /**
     * 设置紫光灯
     * @return
     */
    @Override
    public Observable<BaseBean<Object>> SET_BLB(int type, int class_type, String topic) {
        return apiService.SET_BLB(type,class_type,topic);
    }
    /**
     * 开关加热
     * @return
     */
    @Override
    public Observable<BaseBean<Object>> SET_HEATING_CABINET(HeatBean bean) {
        return apiService.SET_HEATING_CABINET(bean.topic,bean.type,bean.class_type,bean.t_heat,bean.b_heat,bean.start_time,bean.end_time);
    }
    /**
     * 重启
     * @return
     */
    @Override
    public Observable<BaseBean<Object>> SET_RESTART(String topic,int type) {
        return apiService.SET_RESTART(topic,type);
    }
}
