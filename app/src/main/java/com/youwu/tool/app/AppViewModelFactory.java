package com.youwu.tool.app;

import android.annotation.SuppressLint;
import android.app.Application;

import com.youwu.tool.data.DemoRepository;
import com.youwu.tool.ui.address.AddressParsingViewModel;
import com.youwu.tool.ui.cabinet.AddUpdateCabinetViewModel;
import com.youwu.tool.ui.camera.CameraChoiceViewModel;
import com.youwu.tool.ui.chart.ChartViewModel;
import com.youwu.tool.ui.fragment.OneViewModel;
import com.youwu.tool.ui.login.LoginViewModel;
import com.youwu.tool.ui.main.MainViewModel;
import com.youwu.tool.ui.network.NetWorkViewModel;
import com.youwu.tool.ui.print.PrintViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * Created by goldze on 2019/3/26.
 */
public class AppViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    @SuppressLint("StaticFieldLeak")
    private static volatile AppViewModelFactory INSTANCE;
    private final Application mApplication;
    private final DemoRepository mRepository;

    public static AppViewModelFactory getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (AppViewModelFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppViewModelFactory(application, Injection.provideDemoRepository());
                }
            }
        }
        return INSTANCE;
    }

    @VisibleForTesting
    public static void destroyInstance() {
        INSTANCE = null;
    }

    private AppViewModelFactory(Application application, DemoRepository repository) {
        this.mApplication = application;
        this.mRepository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(NetWorkViewModel.class)) {
            return (T) new NetWorkViewModel(mApplication, mRepository);
        } else if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(mApplication, mRepository);
        } else if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(mApplication, mRepository);
        }else if (modelClass.isAssignableFrom(OneViewModel.class)) {
            return (T) new OneViewModel(mApplication, mRepository);
        }else if (modelClass.isAssignableFrom(AddressParsingViewModel.class)) {
            return (T) new AddressParsingViewModel(mApplication, mRepository);
        }else if (modelClass.isAssignableFrom(CameraChoiceViewModel.class)) {
            return (T) new CameraChoiceViewModel(mApplication, mRepository);
        }else if (modelClass.isAssignableFrom(ChartViewModel.class)) {
            return (T) new ChartViewModel(mApplication, mRepository);
        }else if (modelClass.isAssignableFrom(AddUpdateCabinetViewModel.class)) {
            return (T) new AddUpdateCabinetViewModel(mApplication, mRepository);
        }else if (modelClass.isAssignableFrom(PrintViewModel.class)) {
            return (T) new PrintViewModel(mApplication, mRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
