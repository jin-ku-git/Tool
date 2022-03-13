package com.youwu.tool.ui.address;



import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.lifecycle.ViewModelProviders;

import com.youwu.tool.BR;
import com.youwu.tool.R;
import com.youwu.tool.app.AppViewModelFactory;
import com.youwu.tool.databinding.ActivityAddressParsingBinding;

import com.youwu.tool.ui.fragment.OneViewModel;
import com.youwu.tool.utils_view.StatusBarUtil;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.utils.KLog;


/**
 * 地址解析/逆解析
 * 2021/12/12
 */

public class AddressParsingActivity extends BaseActivity<ActivityAddressParsingBinding, AddressParsingViewModel> {



    @Override
    public void initParam() {
        super.initParam();

    }

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_address_parsing;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public AddressParsingViewModel initViewModel() {
        //使用自定义的ViewModelFactory来创建ViewModel，如果不重写该方法，则默认会调用NetWorkViewModel(@NonNull Application application)构造方法
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getApplication());
        return ViewModelProviders.of(this, factory).get(AddressParsingViewModel.class);
    }

    @Override
    public void initViewObservable() {

    }

    @Override
    public void initData() {
        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
        //修改状态栏是状态栏透明
        StatusBarUtil.setTransparentForWindow(this);
        StatusBarUtil.setDarkMode(this);//使状态栏字体变为白色
        // 可以调用该方法，设置不允许滑动退出
        setSwipeBackEnable(true);

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}
