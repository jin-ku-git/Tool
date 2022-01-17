package com.youwu.tool.data;

import com.youwu.tool.bean.TencentBean;
import com.youwu.tool.bean.UpDateBean;
import com.youwu.tool.data.source.HttpDataSource;
import com.youwu.tool.data.source.LocalDataSource;
import com.youwu.tool.entity.DemoEntity;
import com.youwu.tool.ui.cabinet.bean.HeatBean;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import io.reactivex.Completable;
import io.reactivex.Observable;
import me.goldze.mvvmhabit.base.BaseModel;
import me.goldze.mvvmhabit.http.BaseBean;
import me.goldze.mvvmhabit.http.BaseResponse;

/**
 * MVVM的Model层，统一模块的数据仓库，包含网络数据和本地数据（一个应用可以有多个Repositor）
 * Created by goldze on 2019/3/26.
 */
public class DemoRepository extends BaseModel implements HttpDataSource, LocalDataSource {
    private volatile static DemoRepository INSTANCE = null;
    private final HttpDataSource mHttpDataSource;

    private final LocalDataSource mLocalDataSource;

    private DemoRepository(@NonNull HttpDataSource httpDataSource,
                           @NonNull LocalDataSource localDataSource) {
        this.mHttpDataSource = httpDataSource;
        this.mLocalDataSource = localDataSource;
    }

    public static DemoRepository getInstance(HttpDataSource httpDataSource,
                                             LocalDataSource localDataSource) {
        if (INSTANCE == null) {
            synchronized (DemoRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DemoRepository(httpDataSource, localDataSource);
                }
            }
        }
        return INSTANCE;
    }

    @VisibleForTesting
    public static void destroyInstance() {
        INSTANCE = null;
    }


    @Override
    public Observable<Object> login() {
        return mHttpDataSource.login();
    }

    @Override
    public Observable<DemoEntity> loadMore() {
        return mHttpDataSource.loadMore();
    }

    @Override
    public Observable<BaseResponse<DemoEntity>> demoGet() {
        return mHttpDataSource.demoGet();
    }

    @Override
    public Observable<BaseResponse<DemoEntity>> demoPost(String catalog) {
        return mHttpDataSource.demoPost(catalog);
    }

    /**
     * 保存账号
     * @param userName
     */
    @Override
    public void saveUserName(String userName) {
        mLocalDataSource.saveUserName(userName);
    }

    /**
     * 获取账号
     * @return
     */
    @Override
    public String getUserName() {
        return mLocalDataSource.getUserName();
    }

    /**
     * 保存密码
     * @param password
     */
    @Override
    public void savePassword(String password) {
        mLocalDataSource.savePassword(password);
    }

    /**
     * 获取密码
     * @return
     */
    @Override
    public String getPassword() {
        return mLocalDataSource.getPassword();
    }


    /**
     * 地址解析
     * @return
     */
    public Observable<BaseBean<TencentBean>> GET_PARSING(String address,String key) {
        return mHttpDataSource.GET_PARSING(address,key);
    }

    /**
     * 获取门店列表
     * @return
     */
    public Observable<BaseBean<Object>> GET_STORE_LIST () {
        return mHttpDataSource.GET_STORE_LIST();
    }
    /**
     * 获取柜子列表
     * @return
     */
    public Observable<BaseBean<Object>> GET_CABINET_LIST (String store_id) {
        return mHttpDataSource.GET_CABINET_LIST(store_id);
    }
    /**
     * 添加或修改柜子信息
     * @return
     */
    public Observable<BaseBean<Object>> SET_CABINET_INFO(String store_id, String longitude, String latitude, String name, String address, String topic, String num,String id) {
        return mHttpDataSource.SET_CABINET_INFO(store_id,longitude,latitude,name,address,topic,num,id);
    }
    /**
     * 打开柜子
     * @return
     */
    public Observable<BaseBean<Object>> OPEN_CABINET_TABLE(String topic, String channel, int type) {
        return mHttpDataSource.OPEN_CABINET_TABLE(topic,channel,type);
    }
    /**
     * 检查更新
     * @return
     */
    public Observable<BaseBean<UpDateBean>> GET_APP_VERSION() {
        return mHttpDataSource.GET_APP_VERSION();
    }

    /**
     * 设置紫光灯
     * @return
     */
    public Observable<BaseBean<Object>> SET_BLB (int type, int class_type, String topic) {
        return mHttpDataSource.SET_BLB(type,class_type,topic);
    }

    /**
     * 开关加热
     * @return
     */
    public Observable<BaseBean<Object>> SET_HEATING_CABINET (HeatBean bean) {
        return mHttpDataSource.SET_HEATING_CABINET(bean);
    }

    /**
     * 重启
     * @return
     */
    public Observable<BaseBean<Object>> SET_RESTART (String topic,int type) {
        return mHttpDataSource.SET_RESTART(topic,type);
    }

}
