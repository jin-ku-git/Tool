package me.goldze.mvvmhabit.base;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mingle.widget.ShapeLoadingDialog;
import com.trello.rxlifecycle2.components.support.RxFragment;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import me.goldze.mvvmhabit.R;
import me.goldze.mvvmhabit.base.BaseViewModel.ParameterField;
import me.goldze.mvvmhabit.bus.Messenger;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.MaterialDialogUtils;
import me.goldze.mvvmhabit.utils.MaterialShapeDialogUtils;

/**
 * Created by goldze on 2017/6/15.
 */
public abstract class BaseFragment<V extends ViewDataBinding, VM extends BaseViewModel> extends RxFragment implements IBaseView {
    protected V binding;
    protected VM viewModel;
    private int viewModelId;
    private ShapeLoadingDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initParam();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, initContentView(inflater, container, savedInstanceState), container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //??????Messenger??????
        Messenger.getDefault().unregister(viewModel);
        if (viewModel != null) {
            viewModel.removeRxBus();
        }
        if (binding != null) {
            binding.unbind();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //??????????????????Databinding???ViewModel??????
        initViewDataBinding();
        //?????????ViewModel???View???????????????????????????
        registorUIChangeLiveDataCallBack();
        //???????????????????????????
        initData();
        //??????????????????????????????????????????ViewModel?????????View??????????????????
        initViewObservable();
        //??????RxBus
        viewModel.registerRxBus();
    }

    /**
     * ????????????
     */
    private void initViewDataBinding() {
        viewModelId = initVariableId();
        viewModel = initViewModel();
        if (viewModel == null) {
            Class modelClass;
            Type type = getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                modelClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[1];
            } else {
                //????????????????????????????????????????????????BaseViewModel
                modelClass = BaseViewModel.class;
            }
            viewModel = (VM) createViewModel(this, modelClass);
        }
        binding.setVariable(viewModelId, viewModel);
        //??????LiveData??????xml??????????????????UI???????????????
        binding.setLifecycleOwner(this);
        //???ViewModel??????View?????????????????????
        getLifecycle().addObserver(viewModel);
        //??????RxLifecycle????????????
        viewModel.injectLifecycleProvider(this);
    }

    /**
     * =====================================================================
     **/
    //??????ViewModel???View?????????UI????????????
    protected void registorUIChangeLiveDataCallBack() {
        //?????????????????????
        viewModel.getUC().getShowDialogEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String title) {
                showDialog(title);
            }
        });
        //?????????????????????
        viewModel.getUC().getDismissDialogEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void v) {
                dismissDialog();
            }
        });
        //???????????????
        viewModel.getUC().getStartActivityEvent().observe(this, new Observer<Map<String, Object>>() {
            @Override
            public void onChanged(@Nullable Map<String, Object> params) {
                Class<?> clz = (Class<?>) params.get(ParameterField.CLASS);
                Bundle bundle = (Bundle) params.get(ParameterField.BUNDLE);
                startActivity(clz, bundle);
            }
        });
        //??????ContainerActivity
        viewModel.getUC().getStartContainerActivityEvent().observe(this, new Observer<Map<String, Object>>() {
            @Override
            public void onChanged(@Nullable Map<String, Object> params) {
                String canonicalName = (String) params.get(ParameterField.CANONICAL_NAME);
                Bundle bundle = (Bundle) params.get(ParameterField.BUNDLE);
                startContainerActivity(canonicalName, bundle);
            }
        });
        //????????????
        viewModel.getUC().getFinishEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void v) {
                getActivity().finish();
            }
        });
        //???????????????
        viewModel.getUC().getOnBackPressedEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void v) {
                getActivity().onBackPressed();
            }
        });
        //????????????
        viewModel.getUC().getShowPhoneEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String phone) {
                callPhone(phone);
            }
        });
    }

    public void showDialog(String title) {
        if (dialog != null) {
            dialog = new ShapeLoadingDialog.Builder(getActivity())
                    .loadText(title)
                    .build();
            dialog.show();
        } else {
            ShapeLoadingDialog.Builder builder = MaterialShapeDialogUtils.showIndeterminateProgressDialog(getActivity(), title);
            dialog = builder.show();
        }

    }

    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    /**
     * ????????????
     *
     * @param clz ??????????????????Activity???
     */
    public void startActivity(Class<?> clz) {
        startActivity(new Intent(getContext(), clz));
    }

    /**
     * ????????????
     *
     * @param clz    ??????????????????Activity???
     * @param bundle ????????????????????????
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent(getContext(), clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * ??????????????????
     *
     * @param canonicalName ????????? : Fragment.class.getCanonicalName()
     */
    public void startContainerActivity(String canonicalName) {
        startContainerActivity(canonicalName, null);
    }

    /**
     * ??????????????????
     *
     * @param canonicalName ????????? : Fragment.class.getCanonicalName()
     * @param bundle        ????????????????????????
     */
    public void startContainerActivity(String canonicalName, Bundle bundle) {
        Intent intent = new Intent(getContext(), ContainerActivity.class);
        intent.putExtra(ContainerActivity.FRAGMENT, canonicalName);
        if (bundle != null) {
            intent.putExtra(ContainerActivity.BUNDLE, bundle);
        }
        startActivity(intent);
    }

    /**
     * =====================================================================
     **/

    //????????????
    public void refreshLayout() {
        if (viewModel != null) {
            binding.setVariable(viewModelId, viewModel);
        }
    }

    @Override
    public void initParam() {

    }

    /**
     * ??????????????????
     *
     * @return ??????layout???id
     */
    public abstract int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    /**
     * ?????????ViewModel???id
     *
     * @return BR???id
     */
    public abstract int initVariableId();

    /**
     * ?????????ViewModel
     *
     * @return ??????BaseViewModel???ViewModel
     */
    public VM initViewModel() {
        return null;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initViewObservable() {

    }

    public boolean isBackPressed() {
        return false;
    }

    /**
     * ??????ViewModel
     *
     * @param cls
     * @param <T>
     * @return
     */
    public <T extends ViewModel> T createViewModel(Fragment fragment, Class<T> cls) {
        return ViewModelProviders.of(fragment).get(cls);
    }

    /**
     * ????????????????????????????????????
     * @param phoneNum ????????????
     */

    public void callPhone(String phoneNum){

        //android6????????????????????????
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.CALL_PHONE};
            //????????????????????????
            for (String str : permissions) {
                if (getActivity().checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //????????????
                    requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }
        //???????????????????????????Intent.ACTION_CALL??????Intent.ACTION_DIAL??????????????????????????????????????????????????????
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        startActivity(intent);
    }
}
