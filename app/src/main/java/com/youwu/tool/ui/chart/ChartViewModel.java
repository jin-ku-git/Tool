package com.youwu.tool.ui.chart;

import android.app.Application;

import androidx.annotation.NonNull;

import com.youwu.tool.data.DemoRepository;

import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;

/**
 * 2021/12/16
 */

public class ChartViewModel extends BaseViewModel<DemoRepository> {

    //使用LiveData
    public SingleLiveEvent<Integer> IntegerEvent = new SingleLiveEvent<>();


    public ChartViewModel(@NonNull Application application, DemoRepository repository) {
        super(application,repository);
    }

    //返回点击事件
    public BindingCommand finishOnClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            finish();
        }
    });
    //选择图片
    public BindingCommand choiceOnClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            IntegerEvent.setValue(1);
        }
    });


}
