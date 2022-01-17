package com.youwu.tool.ui.print;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.ObservableField;

import com.youwu.tool.R;
import com.youwu.tool.entity.DemoEntity;
import com.youwu.tool.ui.network.NetWorkViewModel;
import com.youwu.tool.ui.network.detail.DetailFragment;
import com.youwu.tool.ui.print.bean.DeviceScanBean;

import me.goldze.mvvmhabit.base.ItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.utils.ToastUtils;

/**
 * 2021/12/26
 */

public class PrintItemViewModel extends ItemViewModel<PrintViewModel> {
    public ObservableField<DeviceScanBean> entity = new ObservableField<>();
    public Drawable drawableImg;
    public int type=1;


    public PrintItemViewModel(@NonNull PrintViewModel viewModel, DeviceScanBean entity) {
        super(viewModel);
        this.entity.set(entity);
        //ImageView的占位图片，可以解决RecyclerView中图片错误问题
        drawableImg = ContextCompat.getDrawable(viewModel.getApplication(), R.mipmap.ic_launcher);
    }

    /**
     * 获取position的方式有很多种,indexOf是其中一种，常见的还有在Adapter中、ItemBinding.of回调里
     *
     * @return
     */
    public int getPosition() {
        return viewModel.getItemPosition(this);
    }

    //条目的点击事件
    public BindingCommand itemClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            viewModel.itemEvent.setValue(getPosition());
        }
    });

}
