package com.youwu.tool.ui.address;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.youwu.tool.app.ApiServices;
import com.youwu.tool.bean.TencentBean;
import com.youwu.tool.data.DemoRepository;
import com.youwu.tool.utils_view.RxToast;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.http.BaseBean;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.youwu.tool.app.AppApplication.toPrettyFormat;

/**
 * 地址解析model
 * 2021/12/15
 */

public class AddressParsingViewModel extends BaseViewModel<DemoRepository> {

    String KEY="KAUBZ-OIA65-VEUIZ-QMQQN-H7UW6-L3FFN";
    //使用LiveData
    public SingleLiveEvent<String> loadUrlEvent = new SingleLiveEvent<>();

    public ObservableField<String> address = new ObservableField<>();//详细地址
    public ObservableField<String> Latitude = new ObservableField<>();//纬度
    public ObservableField<String> Longitude = new ObservableField<>();//经度
    public ObservableField<String> editText = new ObservableField<>();//搜索内容
    public ObservableField<String> latitude_text = new ObservableField<>();//搜索的纬度
    public ObservableField<String> longitude_text = new ObservableField<>();//搜索的经度

    public AddressParsingViewModel(@NonNull Application application, DemoRepository repository) {
        super(application,repository);
    }

    //返回点击事件
    public BindingCommand finishOnClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            finish();
        }
    });
    //地址解析点击事件
    public BindingCommand parsingOnClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            getParsing();
        }
    });
    //经纬度解析点击事件
    public BindingCommand parsingTwoOnClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            getParsingTwo();
        }
    });

    /**
     * 地址解析
     */
    public void getParsing(){

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                //baseUrl:参数之前的部分
                .baseUrl("https://apis.map.qq.com/ws/")
                .build();
        ApiServices services = retrofit.create(ApiServices.class);
        KLog.e("请求路径:"+"https://apis.map.qq.com/ws/geocoder/v1?address="+editText.get()+"&key=KAUBZ-OIA65-VEUIZ-QMQQN-H7UW6-L3FFN");
        //params1:所有参数进行拼接就可以
        Observable<BaseBean<TencentBean>> observable = services.getPage("geocoder/v1?address="+editText.get()+"&key=KAUBZ-OIA65-VEUIZ-QMQQN-H7UW6-L3FFN");
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        showDialog();
                    }
                })
                .subscribe(new Observer<BaseBean>() {
                    @Override
                    public void onError(Throwable throwable) {
                        //关闭对话框
                        dismissDialog();
                        if (throwable instanceof ResponseThrowable) {
                            ToastUtils.showShort(((ResponseThrowable) throwable).message);
                        }
                        throwable.printStackTrace();
                    }
                    @Override
                    public void onComplete() {
                        //关闭对话框
                        dismissDialog();
                    }
                    @Override
                    public void onSubscribe(Disposable d) { }
                    @Override
                    public void onNext(BaseBean response) {
                        String submitJson = new Gson().toJson(response.result);
                        String submitJsons = new Gson().toJson(response);
                        KLog.d("解析数据："+submitJsons);
                        if (0==response.status){
                            TencentBean tencentBean = JSON.parseObject(toPrettyFormat(submitJson), TencentBean.class);
                            Latitude.set("经度："+tencentBean.getLocation().getLat());
                            Longitude.set("纬度："+tencentBean.getLocation().getLng()+"");
                            address.set(tencentBean.getAddress_components().getProvince()+"-"+tencentBean.getAddress_components().getCity()+"-"+tencentBean.getAddress_components().getDistrict()+"-"+tencentBean.getTitle());
                        }else {
                            RxToast.normal(response.getMessage());
                        }
                    }
                });
    }

    /**
     * 经纬度解析
     */
    public void getParsingTwo(){

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                //baseUrl:参数之前的部分
                .baseUrl("https://apis.map.qq.com/ws/geocoder/v1/")
                .build();
        ApiServices services = retrofit.create(ApiServices.class);
        KLog.e("请求路径:"+"https://apis.map.qq.com/ws/geocoder/v1?location="+latitude_text.get()+","+longitude_text.get()+"&key=KAUBZ-OIA65-VEUIZ-QMQQN-H7UW6-L3FFN");
        //params1:所有参数进行拼接就可以
        Observable<BaseBean<TencentBean>> observable = services.getPage("?location="+latitude_text.get()+","+longitude_text.get()+"&key=KAUBZ-OIA65-VEUIZ-QMQQN-H7UW6-L3FFN");
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        showDialog();
                    }
                })
                .subscribe(new Observer<BaseBean>() {
                    @Override
                    public void onError(Throwable throwable) {
                        //关闭对话框
                        dismissDialog();
                        if (throwable instanceof ResponseThrowable) {
                            ToastUtils.showShort(((ResponseThrowable) throwable).message);
                        }
                        throwable.printStackTrace();
                    }
                    @Override
                    public void onComplete() {
                        //关闭对话框
                        dismissDialog();
                    }
                    @Override
                    public void onSubscribe(Disposable d) { }
                    @Override
                    public void onNext(BaseBean response) {
                        String submitJson = new Gson().toJson(response.result);
                        String submitJsons = new Gson().toJson(response);
                        KLog.d("解析数据："+submitJsons);
                        if (0==response.status){
                            TencentBean tencentBean = JSON.parseObject(toPrettyFormat(submitJson), TencentBean.class);
                            Latitude.set("");
                            Longitude.set("");
                            address.set("位置信息："+tencentBean.getAddress());
                        }else {
                            RxToast.normal(response.getMessage());
                        }
                    }
                });
    }
}
