package com.youwu.tool.ui.cabinet;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.luck.picture.lib.entity.LocalMedia;
import com.youwu.tool.data.DemoRepository;
import com.youwu.tool.ui.cabinet.bean.CabinetBean;
import com.youwu.tool.ui.cabinet.bean.CabinetItemBean;
import com.youwu.tool.ui.cabinet.bean.HeatBean;
import com.youwu.tool.ui.cabinet.bean.ScrollBean;
import com.youwu.tool.ui.cabinet.bean.StoreBean;
import com.youwu.tool.utils_view.RxToast;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.http.BaseBean;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;

import static com.youwu.tool.app.AppApplication.toPrettyFormat;

/**
 * 2021/12/20
 */

public class AddUpdateCabinetViewModel extends BaseViewModel<DemoRepository> {

    //使用LiveData
    public SingleLiveEvent<Integer> typeEvent = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> booleanEvent = new SingleLiveEvent<>();

    public SingleLiveEvent<List<StoreBean.DataBean>> StoreBeanEvent = new SingleLiveEvent<>();
    public SingleLiveEvent<List<ScrollBean>> CabinetBeanEvent = new SingleLiveEvent<>();


    public ObservableField<Boolean> time_judge = new ObservableField<Boolean>();//判断选择时间是否显示
    public ObservableField<Boolean> all_field = new ObservableField<Boolean>();//判断是否全选
    public ObservableField<Integer> currentSelect = new ObservableField<Integer>();//判断显示什么图片


    public AddUpdateCabinetViewModel(@NonNull Application application, DemoRepository repository) {
        super(application,repository);
    }

    //返回点击事件
    public BindingCommand finishOnClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            finish();
        }
    });

    //增加点击事件
    public BindingCommand addOnClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            typeEvent.setValue(1);
        }
    });
    //修改点击事件
    public BindingCommand ModifyOnClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            typeEvent.setValue(7);
        }
    });

    //控温点击事件
    public BindingCommand TimeJudgeOnClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if (time_judge.get()){
                time_judge.set(false);
                booleanEvent.setValue(false);
            }else {
                time_judge.set(true);
                booleanEvent.setValue(true);
            }
        }
    });

    //顶部门店点击事件
    public BindingCommand TopStoreOnClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if (time_judge.get()){
                typeEvent.setValue(8);
            }
        }
    });

    //全选点击事件
    public BindingCommand allOnClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if (all_field.get()){
                all_field.set(false);
                typeEvent.setValue(5);
            }else {
                all_field.set(true);
                typeEvent.setValue(6);
            }

        }
    });

    /**
     * 获取门店列表
     */
    public void getStoreList(){
        model.GET_STORE_LIST()
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        showDialog();
                    }
                })
                .subscribe(new DisposableObserver<BaseBean<Object>>() {
                    @Override
                    public void onNext(BaseBean<Object> response) {
                        if (0==response.getCode()){
                            String submitJsonData = new Gson().toJson(response);
                            StoreBean storeBean= JSON.parseObject(toPrettyFormat(submitJsonData), StoreBean.class);
                            ArrayList<StoreBean.DataBean> list=new ArrayList<>();

                            for (StoreBean.DataBean dataBean:storeBean.data){

                                list.add(dataBean);

                            }
                            StoreBeanEvent.setValue(list);

                        }else {
                            RxToast.normal(response.getMessage());
                        }
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        //关闭对话框
                        dismissDialog();
                        if (throwable instanceof ResponseThrowable) {
                            ToastUtils.showShort(((ResponseThrowable) throwable).message);
                        }
                    }
                    @Override
                    public void onComplete() {
                        //关闭对话框
                        dismissDialog();
                    }
                });

    }

    /**
     * 获取柜子列表
     */
    public void getCabinetList(String store_id,String name){
        model.GET_CABINET_LIST(store_id)
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        showDialog();
                    }
                })
                .subscribe(new DisposableObserver<BaseBean<Object>>() {
                    @Override
                    public void onNext(BaseBean<Object> response) {
                        if (0==response.getCode()){
                            String submitJsonData = new Gson().toJson(response);
                            CabinetBean storeBean= JSON.parseObject(toPrettyFormat(submitJsonData), CabinetBean.class);
                            ArrayList<ScrollBean> list=new ArrayList<>();

                            for (int i=0;i<storeBean.data.size();i++){
                                CabinetBean.DataBean dataBean=new CabinetBean.DataBean();
                                dataBean=storeBean.data.get(i);
                                if (i==0){
                                    list.add(new ScrollBean(true, name,store_id,0,0));
                                }

                                list.add(new ScrollBean(new ScrollBean.ScrollItemBean(dataBean.getName(),dataBean.getAddress(),dataBean.getId()+""
                                        ,name,store_id,dataBean.getStatus(),dataBean.getLatitude(),dataBean.getLongitude(),dataBean.getWith_table(),0,0)));
                            }


                            CabinetBeanEvent.setValue(list);

                        }else {
                            RxToast.normal(response.getMessage());
                        }
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        //关闭对话框
                        dismissDialog();
                        if (throwable instanceof ResponseThrowable) {
                            ToastUtils.showShort(((ResponseThrowable) throwable).message);
                        }
                    }
                    @Override
                    public void onComplete() {
                        //关闭对话框
                        dismissDialog();
                    }
                });

    }


    /**
     * 添加或修改柜子信息
     */
    public void set_cabinet_info(CabinetItemBean bean,int type){
        String id=bean.id;
        String longitude=bean.longitude;
        String latitude=bean.Latitude;
        String name=bean.name;
        String address=bean.address;
        String topic=bean.number;
        String num=bean.lattice_num;
        String store_id=bean.store_id;


        model.SET_CABINET_INFO(store_id,longitude,latitude,name,address,topic,num,id)
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        showDialog();
                    }
                })
                .subscribe(new DisposableObserver<BaseBean<Object>>() {
                    @Override
                    public void onNext(BaseBean<Object> response) {
                        if (0==response.getCode()){
                            if (type==1){//新增
                                typeEvent.setValue(3);
                            }else {//修改
                                typeEvent.setValue(2);
                            }
                        }else {
                            RxToast.normal(response.getMessage());
                        }
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        //关闭对话框
                        dismissDialog();
                        if (throwable instanceof ResponseThrowable) {
                            ToastUtils.showShort(((ResponseThrowable) throwable).message);
                        }
                    }
                    @Override
                    public void onComplete() {
                        //关闭对话框
                        dismissDialog();
                    }
                });
    }
    /**
     * 打开柜子
     */
    public void openGuizi(String topic,String channel,int type){

            model.OPEN_CABINET_TABLE(topic,channel,type)
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        showDialog();
                    }
                })
                .subscribe(new DisposableObserver<BaseBean<Object>>() {
                    @Override
                    public void onNext(BaseBean<Object> response) {
                        if (0==response.getCode()){

                                typeEvent.setValue(4);

                        }else {
                            RxToast.normal(response.getMessage());
                        }
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        //关闭对话框
                        dismissDialog();
                        if (throwable instanceof ResponseThrowable) {
                            ToastUtils.showShort(((ResponseThrowable) throwable).message);
                        }
                    }
                    @Override
                    public void onComplete() {
                        //关闭对话框
                        dismissDialog();
                    }
                });
    }



    /**
     * 设置紫光灯
     * @param type   1指定 2 全部
     * @param class_type  1开启紫光灯，2关闭紫光灯
     * @param topic  多个以逗号分隔
     */
    public void setBlb(int type, int class_type, String topic){
        model.SET_BLB(type,class_type,topic)
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        showDialog();
                    }
                })
                .subscribe(new DisposableObserver<BaseBean<Object>>() {
                    @Override
                    public void onNext(BaseBean<Object> response) {
                        if (0==response.getCode()){
                            String submitJsonData = new Gson().toJson(response);
                            RxToast.normal("返回信息："+submitJsonData);
                            RxToast.success(class_type==1?"紫光灯已打开！":"紫光灯已关闭！");

                        }else {
                            RxToast.normal(response.getMessage());
                        }
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        //关闭对话框
                        dismissDialog();
                        if (throwable instanceof ResponseThrowable) {
                            ToastUtils.showShort(((ResponseThrowable) throwable).message);
                        }
                    }
                    @Override
                    public void onComplete() {
                        //关闭对话框
                        dismissDialog();
                    }
                });

    }

    /**
     * 开关加热
     */
    public void setHeatingCabinet(HeatBean bean){

        model.SET_HEATING_CABINET(bean)
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        showDialog();
                    }
                })
                .subscribe(new DisposableObserver<BaseBean<Object>>() {
                    @Override
                    public void onNext(BaseBean<Object> response) {
                        if (0==response.getCode()){
                            String submitJsonData = new Gson().toJson(response);

                            KLog.d("返回信息："+submitJsonData);

                            RxToast.success(bean.class_type==1?"加热已开启！":"加热已关闭！");

                        }else {
                            RxToast.normal(response.getMessage());
                        }
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        //关闭对话框
                        dismissDialog();
                        if (throwable instanceof ResponseThrowable) {
                            ToastUtils.showShort(((ResponseThrowable) throwable).message);
                        }
                    }
                    @Override
                    public void onComplete() {
                        //关闭对话框
                        dismissDialog();
                    }
                });

    }

    /**
     * 重启
     */
    public void setRestart(String topic,int type){


        model.SET_RESTART(topic,type)
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        showDialog();
                    }
                })
                .subscribe(new DisposableObserver<BaseBean<Object>>() {
                    @Override
                    public void onNext(BaseBean<Object> response) {
                        if (0==response.getCode()){
                            String submitJsonData = new Gson().toJson(response);

                            KLog.d("返回信息："+submitJsonData);

                            RxToast.success("已重启！");

                        }else {
                            RxToast.normal(response.getMessage());
                        }
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        //关闭对话框
                        dismissDialog();
                        if (throwable instanceof ResponseThrowable) {
                            ToastUtils.showShort(((ResponseThrowable) throwable).message);
                        }
                    }
                    @Override
                    public void onComplete() {
                        //关闭对话框
                        dismissDialog();
                    }
                });

    }

}
