package com.youwu.tool.ui.cabinet.dialog;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.CenterPopupView;
import com.xuexiang.xui.widget.alpha.XUIAlphaButton;
import com.youwu.tool.R;
import com.youwu.tool.popwindow.MDPopupView;
import com.youwu.tool.ui.cabinet.AddUpdateCabinetActivity;
import com.youwu.tool.ui.cabinet.bean.CabinetItemBean;
import com.youwu.tool.ui.cabinet.bean.StoreBean;
import com.youwu.tool.utils_view.RxToast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.utils.KLog;

import static me.goldze.mvvmhabit.base.BaseActivity.subZeroAndDot;


public class ClearDialog extends CenterPopupView {

    EditText name_text;//柜子名称
    EditText address_text;//地址

    EditText latitude_text;//纬度
    EditText longitude_text;//经度
    EditText number_text;//主板编号
    EditText lattice_text;//格子数量

    TextView tv_store;//门店

    ImageView scanning_image;//扫描

    CabinetItemBean bean;
    LinearLayout top_layout;

    XUIAlphaButton determine;

    private MDPopupView popupView;
    List<StoreBean.DataBean> list_store= new ArrayList<>();//门店
    String store_id;//门店id

    Context mContext;
    String text;//扫描的信息
    public int type;//1 新增 2 修改


    public ClearDialog(int type, @NonNull Context context, CabinetItemBean bean, List<StoreBean.DataBean> bean_list) {
        super(context);
        this.mContext=context;
        this.bean = bean;
        this.type = type;
        this.list_store = bean_list;
    }



    @Override
    protected void onCreate() {
        super.onCreate();

        name_text=findViewById(R.id.name_text);//柜子名称
        latitude_text=findViewById(R.id.latitude_text);//纬度
        longitude_text=findViewById(R.id.longitude_text);//经度
        number_text=findViewById(R.id.number_text);//主板编号
        lattice_text=findViewById(R.id.lattice_text);//格子数量
        address_text=findViewById(R.id.address_text);//地址
        tv_store=findViewById(R.id.tv_store);//选择门店
        scanning_image=findViewById(R.id.scanning_image);//扫描

        top_layout=findViewById(R.id.top_layout);

        determine=findViewById(R.id.determine);


        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        /**
         * 选择门店
         */
        tv_store.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showStore(v);
            }
        });
        /**
         * 扫描
         */
        scanning_image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 扫描操作
                 */
                if (mScanningListener != null) {
                    mScanningListener.onScanning();
                }
                //隐藏键盘
                InputMethodManager imms = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imms.hideSoftInputFromWindow(getWindowToken(), 0);
            }
        });


        findViewById(R.id.tv_cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        determine.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String latitude = latitude_text.getText().toString();
                String longitude = longitude_text.getText().toString();
                String number = number_text.getText().toString();
                String name = name_text.getText().toString();
                String lattice = lattice_text.getText().toString();
                String address = address_text.getText().toString();
                if (latitude.isEmpty()) {
                    ToastUtils.showShort("请输入纬度");
                    return;
                }
                if (longitude.isEmpty()) {
                    ToastUtils.showShort("请输入经度");
                    return;
                }
                if (name.isEmpty()) {
                    ToastUtils.showShort("请输入柜子名");
                    return;
                }
                if (address.isEmpty()) {
                    ToastUtils.showShort("请输入柜子地址");
                    return;
                }
                if (number.isEmpty()) {
                    ToastUtils.showShort("请输入主板标号");
                    return;
                }
                if (lattice.isEmpty()) {
                    ToastUtils.showShort("请输入格子数量");
                    return;
                }
                bean.setLatitude(latitude);
                bean.setLongitude(longitude);
                bean.setNumber(number);
                bean.setAddress(address);
                bean.setName(name);
                bean.setLattice_num(lattice);
                bean.setStore_id(subZeroAndDot(store_id));
                /**
                 * 确定操作
                 */
                if (mBeanListener != null) {
                    mBeanListener.onBean(bean,type);
                }
                dismiss();
            }
        });
        if (bean!=null){
            setData();
        }
    }

    //获取从LingYouGuiQuanRecycleAdapter传递的值
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void AddressEventBus(String message) { //方法名任意，这里的参数和  EventBus.getDefault().post(111);对应即可
        System.out.println("------>" + message);

        number_text.setText(message);

    }

    /**
     * 门店弹窗
     * @param v
     */
    private void showStore(final View v){
        if(popupView==null){
            popupView = (MDPopupView) new XPopup.Builder(getContext())
                    .atView(v)
                    .asCustom(new MDPopupView(getContext()));
        }
        popupView.setItemsData(list_store);
        popupView.setOnChoiceener(new MDPopupView.OnChoiceener() {
            @Override
            public void onChoice(StoreBean.DataBean data) {
                tv_store.setText(data.name);
                store_id=data.id;
                RxToast.normal("门店id："+data.id);
                popupView.dismiss();
            }
        });

        popupView.show();

    }

    /**
     * 赋值
     */
    public void setData() {
        name_text.setText(bean.getName());
        number_text.setText(bean.getNumber());
        latitude_text.setText(bean.getLatitude());
        longitude_text.setText(bean.getLongitude());
        address_text.setText(bean.getAddress());
        lattice_text.setText(bean.getLattice_num());

        if (bean.getStore_name()!=null){
            tv_store.setText(bean.getStore_name());
            store_id=bean.getStore_id();
        }


    }




    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_clear;
    }


    //确定的回调
    public interface OnBeanListener {
        void onBean(CabinetItemBean bean,int type);
    }

    public void setOnBeanListener(OnBeanListener listener) {
        mBeanListener = listener;
    }

    private OnBeanListener mBeanListener;



    //扫描的回调
    public interface OnScanningListener {
        void onScanning();
    }

    public void setOnScanningListener(OnScanningListener listener) {
        mScanningListener = listener;
    }

    private OnScanningListener mScanningListener;


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
