package com.youwu.tool.ui.print;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;

import com.youwu.tool.BR;
import com.youwu.tool.R;
import com.youwu.tool.data.DemoRepository;
import com.youwu.tool.ui.network.NetWorkItemViewModel;

import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.tatarka.bindingcollectionadapter2.ItemBinding;

/**
 * 2021/12/26
 */

public class PrintViewModel extends BaseViewModel<DemoRepository> {

    //使用LiveData
    public SingleLiveEvent<Integer> IntegerEvent = new SingleLiveEvent<>();

    public SingleLiveEvent<Integer> itemEvent = new SingleLiveEvent<>();


    public PrintViewModel(@NonNull Application application, DemoRepository repository) {
        super(application,repository);
    }

    //给RecyclerView添加ObservableList
    public ObservableList<PrintItemViewModel> observableList = new ObservableArrayList<>();
    //给RecyclerView添加ItemBinding
    public ItemBinding<PrintItemViewModel> itemBinding = ItemBinding.of(BR.viewModel, R.layout.connectdevice_recycleview_item);

    //返回点击事件
    public BindingCommand finishOnClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            finish();
        }
    });
    //打印小票
    public BindingCommand sendOutOnClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            IntegerEvent.setValue(1);
        }
    });
    //断开连接
    public BindingCommand breakOffOnClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            IntegerEvent.setValue(2);
        }
    });
    //刷新
    public BindingCommand RefreshOnClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            IntegerEvent.setValue(3);
        }
    });

    /**
     * 获取条目下标
     *
     * @param printItemViewModel
     * @return
     */
    public int getItemPosition(PrintItemViewModel printItemViewModel) {
        return observableList.indexOf(printItemViewModel);
    }
}
