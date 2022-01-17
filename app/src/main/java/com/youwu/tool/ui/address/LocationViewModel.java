package com.youwu.tool.ui.address;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.luck.picture.lib.entity.LocalMedia;
import com.youwu.tool.utils_view.RxToast;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.utils.KLog;

/**
 * 2021/12/15
 */

public class LocationViewModel extends BaseViewModel {
    //使用Observable
    public SingleLiveEvent<Boolean> requestCameraPermissions = new SingleLiveEvent<>();
    //使用LiveData
    public SingleLiveEvent<Integer> typeEvent = new SingleLiveEvent<>();

    public ObservableField<String> address = new ObservableField<>();//详细地址
    public ObservableField<String> Latitude = new ObservableField<>();//纬度
    public ObservableField<String> Longitude = new ObservableField<>();//经度
    public ObservableField<String> name = new ObservableField<>();//名字
    public ObservableField<String> store = new ObservableField<>();//门店
    public ObservableField<String> cabinet = new ObservableField<>();//柜子


    public ObservableField<List<LocalMedia>> photoList = new ObservableField<>();//照片


    public LocationViewModel(@NonNull Application application) {
        super(application);
    }

    //返回点击事件
    public BindingCommand finishOnClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            finish();
        }
    });
    //门店选择点击事件
    public BindingCommand store_onClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            typeEvent.setValue(1);
        }
    });
    //柜子选择点击事件
    public BindingCommand cabinet_onClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            typeEvent.setValue(2);
        }
    });
    //提交点击事件
    public BindingCommand SubmitOnClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            String url = "";

            for (int i=0;i<photoList.get().size();i++){
                url+=photoList.get().get(i).getPath()+",\n";
            }
            KLog.d("photoList长度："+photoList.get().size()+"\n提交图片："+url);
            RxToast.info("提交内容：\n"+"门店："+store.get()+"，\n柜子："+cabinet.get()+"，\n纬度："+Latitude.get()+",\n经度："+Longitude.get()+
                    "，\n详细地址："+address.get()+"，\n提交人名字："+name.get()+"，\n照片："+url);
        }
    });

}
