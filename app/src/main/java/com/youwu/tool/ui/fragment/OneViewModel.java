package com.youwu.tool.ui.fragment;

import android.app.Application;

import androidx.annotation.NonNull;

import com.youwu.tool.data.DemoRepository;
import com.youwu.tool.ui.address.AddressParsingActivity;
import com.youwu.tool.ui.address.LocationActivity;
import com.youwu.tool.ui.cabinet.AddUpdateCabinetActivity;
import com.youwu.tool.ui.camera.CameraChoiceActivity;
import com.youwu.tool.ui.chart.ChartActivity;
import com.youwu.tool.ui.print.PrintActivity;

import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;

/**
 * 2021/12/13
 */

public class OneViewModel extends BaseViewModel<DemoRepository> {
    //使用Observable
    public SingleLiveEvent<Boolean> requestCameraPermissions = new SingleLiveEvent<>();
    //使用LiveData
    public SingleLiveEvent<String> loadUrlEvent = new SingleLiveEvent<>();
    public SingleLiveEvent<Integer> IntegerEvent = new SingleLiveEvent<>();

    public OneViewModel(@NonNull Application application, DemoRepository repository) {
        super(application,repository);
    }

    //大
    public BindingCommand largeClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            IntegerEvent.setValue(1);
        }
    });

    //位置信息
    public BindingCommand locationClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            startActivity(LocationActivity.class);
        }
    });
    //地址解析
    public BindingCommand addressParsingonClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            startActivity(AddressParsingActivity.class);
        }
    });
    //拍照上传
    public BindingCommand imageonClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            startActivity(CameraChoiceActivity.class);
        }
    });
    //折线图/柱状图
    public BindingCommand chartonClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            startActivity(ChartActivity.class);
        }
    });
    //增加/更新取餐柜
    public BindingCommand add_updateOnClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            startActivity(AddUpdateCabinetActivity.class);
        }
    });
    //打印小票
    public BindingCommand PrintOnClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            startActivity(PrintActivity.class);
        }
    });
}
