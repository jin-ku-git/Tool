package com.youwu.tool.ui.cabinet;


import android.Manifest;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;

import android.os.Bundle;

import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ScreenUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;

import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.youwu.tool.BR;
import com.youwu.tool.R;
import com.youwu.tool.app.AppViewModelFactory;
import com.youwu.tool.databinding.ActivityAddUpdateCabinetBinding;
import com.youwu.tool.ui.cabinet.adapter.ScrollLeftAdapter;
import com.youwu.tool.ui.cabinet.adapter.ScrollRightAdapter;
import com.youwu.tool.ui.cabinet.bean.CabinetBean;
import com.youwu.tool.ui.cabinet.bean.CabinetItemBean;
import com.youwu.tool.ui.cabinet.bean.HeatBean;
import com.youwu.tool.ui.cabinet.bean.ScrollBean;
import com.youwu.tool.ui.cabinet.bean.StoreBean;
import com.youwu.tool.ui.cabinet.bean.TableItemBean;
import com.youwu.tool.ui.cabinet.dialog.CabinetDialog;
import com.youwu.tool.ui.cabinet.dialog.ClearDialog;
import com.youwu.tool.ui.cabinet.dialog.GroupPop;
import com.youwu.tool.ui.decoding.CaptureActivity;
import com.youwu.tool.utils_view.Constant;
import com.youwu.tool.utils_view.DividerItemDecorations;
import com.youwu.tool.utils_view.RxToast;
import com.youwu.tool.utils_view.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.ToastUtils;


/**
 * ??????/???????????????
 * 2021/12/20
 */

public class AddUpdateCabinetActivity extends BaseActivity<ActivityAddUpdateCabinetBinding, AddUpdateCabinetViewModel> {


    private List<StoreBean.DataBean> left=new ArrayList<>();
    private List<ScrollBean> right=new ArrayList<>();
    private ScrollLeftAdapter leftAdapter;
    private ScrollRightAdapter rightAdapter;
    //??????title????????????????????????position??????
    private List<Integer> tPosition = new ArrayList<>();
    private Context mContext;
    //title?????????
    private int tHeight;
    //????????????????????????????????????item???position
    private int first = 0;
    private GridLayoutManager rightManager;

    CabinetItemBean bean =new CabinetItemBean();


    //????????????????????????
    private int position;

    //????????????????????????
    private int Cabinet_type = 0;

    /************************************/
    //?????????
    String address;
    double latitude;//??????
    double longitude;//??????

    String store_id;//??????id
    int store_position=0;//
    @Override
    public void initParam() {
        super.initParam();
//
    }

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_add_update_cabinet;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public AddUpdateCabinetViewModel initViewModel() {
        //??????????????????ViewModelFactory?????????ViewModel????????????????????????????????????????????????NetWorkViewModel(@NonNull Application application)????????????
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getApplication());
        return ViewModelProviders.of(this, factory).get(AddUpdateCabinetViewModel.class);
    }

    @Override
    public void initViewObservable() {
        //??????item???????????????
        viewModel.booleanEvent.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean){
                    for (int i=0;i<right.size();i++){
                        right.get(i).setChoiceSelect(1);
                        if (right.get(i).t!=null){
                            right.get(i).t.setChoiceSelect(1);
                        }
                    }
                    rightAdapter.notifyDataSetChanged();
                }else {
                    for (int i=0;i<right.size();i++){
                        right.get(i).setChoiceSelect(0);
                        if (right.get(i).t!=null){
                            right.get(i).t.setChoiceSelect(0);

                        }
                    }
                    rightAdapter.notifyDataSetChanged();
                }
            }
        });

        viewModel.typeEvent.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                switch (integer){
                    case 1://??????
                        requestDynamicPermisson();
                        bean=new CabinetItemBean();
                        bean.setAddress(address);
                        bean.setLongitude(longitude+"");
                        bean.setLatitude(latitude+"");
                        showDialogs(1,bean,left);
                        break;
                    case 2://??????????????????
                        RxToast.success("????????????!");

                        right.get(position).t.setAddress(bean.address);
                        right.get(position).t.setLongitude(bean.longitude);
                        right.get(position).t.setLatitude(bean.Latitude);
                        right.get(position).t.setName(bean.name);
                        right.get(position).t.getWith_table().get(0).setTopic(bean.number);


                        String submitJson = new Gson().toJson(right.get(position).t);

                        KLog.d("??????????????????\r\n"+submitJson);

                        rightAdapter.notifyItemChanged(position);
                        break;
                    case 3:
                        RxToast.success("????????????!");

                        //????????????
                        left.clear();
                        right.clear();

                        viewModel.getStoreList();
                        break;
                    case 4://????????????
                        RxToast.success("????????????!");
                        break;
                    case 5://????????????
                        for (int i=0;i<right.size();i++){
                            viewModel.currentSelect.set(0);
                            right.get(i).setCurrentSelect(0);
                            if (right.get(i).t!=null){
                                right.get(i).t.setCurrentSelect(0);
                            }
                        }
                        rightAdapter.notifyDataSetChanged();
                        break;
                    case 6://??????
                        for (int i=0;i<right.size();i++){
                            viewModel.currentSelect.set(1);
                            right.get(i).setCurrentSelect(1);
                            if (right.get(i).t!=null){
                                right.get(i).t.setCurrentSelect(1);
                            }

                        }
                        rightAdapter.notifyDataSetChanged();
                        break;
                    case 7://??????
                        String topic="";
                        for (int i=0;i<right.size();i++){
                            if (right.get(i).t!=null&&right.get(i).t.getCurrentSelect()==1){
                                topic+=right.get(i).t.getWith_table().get(0).getTopic()+",";
                            }
                        }
                        topic = topic.substring(0, topic.length() -1);
                        if (topic.isEmpty()){
                            RxToast.normal("????????????????????????");
                            return;
                        }
                        if (viewModel.all_field.get()){
                            showCabinetDialogs(2,topic);
                        }else {
                            showCabinetDialogs(1,topic);
                        }

                        break;
                    case 8://????????????????????????
                            KLog.d("??????id???"+store_id+"???????????????"+store_position);

                        KLog.d("??????item??????:"+right.get(position).id+"???????????????"+position);
                        if (right.get(store_position).getCurrentSelect()==1){//1  ?????? 0 ?????????
                            right.get(store_position).setCurrentSelect(0);
                        }else {
                            right.get(store_position).setCurrentSelect(1);
                        }
                        viewModel.currentSelect.set(right.get(store_position).getCurrentSelect());
                        Boolean flag = true;
                        for (int i=0;i<right.size();i++){
                            if (right.get(i).t!=null&&right.get(store_position).id.equals(right.get(i).t.getStore_id())){
                                right.get(i).t.setCurrentSelect(right.get(store_position).getCurrentSelect());
                            }
                            if (right.get(i).t!=null&&right.get(i).t.getCurrentSelect()!=1){
                                flag=false;
                            }
                        }
                        viewModel.all_field.set(flag);
                        //??????
                        rightAdapter.notifyDataSetChanged();
                        break;
                }
            }
        });
        //????????????
        viewModel.StoreBeanEvent.observe(this, new Observer<List<StoreBean.DataBean>>() {
            @Override
            public void onChanged(List<StoreBean.DataBean> dataBeans) {
                left.addAll(dataBeans);
                for (int i=0;i<dataBeans.size();i++){

                    viewModel.getCabinetList(dataBeans.get(i).id,dataBeans.get(i).name);
                }

            }
        });
        //????????????
        viewModel.CabinetBeanEvent.observe(this, new Observer<List<ScrollBean>>() {
            @Override
            public void onChanged(List<ScrollBean> dataBeans) {
                right.addAll(dataBeans);

                Cabinet_type++;
                if (Cabinet_type==left.size()){
                    Cabinet_type=0;
                    left.clear();
                    for (int i=0;i<right.size();i++){
                        if (right.get(i).isHeader){
                            StoreBean.DataBean bean= new StoreBean.DataBean();
                            bean.name=right.get(i).header;
                            bean.id=right.get(i).id;
                            left.add(bean);
                        }
                    }
                    KLog.d("????????????left.size():"+left.size());
                    tPosition.clear();
                    first=0;
                    leftAdapter=null;
                    rightAdapter=null;
                    rightManager=null;
                    initDatas();
                    initLeft();
                    initRight();
                }
            }
        });

    }

    @Override
    public void initData() {
        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
        //?????????????????????????????????
        StatusBarUtil.setTransparentForWindow(this);
        StatusBarUtil.setDarkMode(this);//??????????????????????????????
        // ???????????????????????????????????????????????????
        setSwipeBackEnable(true);
        mContext=this;
        viewModel.time_judge.set(false);
        viewModel.all_field.set(false);
        //????????????
        viewModel.getStoreList();

        //????????????
        requestDynamicPermisson();


    }

    /**
     * ??????????????????
     * @param type
     * @param beans
     */
    private void showDialogs(int type, CabinetItemBean beans,List<StoreBean.DataBean> bean_list) {
        ClearDialog dialog_clear =new ClearDialog(type,mContext, beans,bean_list);

        new  XPopup.Builder(mContext)
                .autoOpenSoftInput(false)
                .asCustom(dialog_clear)
                .show();

        dialog_clear.setOnBeanListener(new ClearDialog.OnBeanListener() {
            @Override
            public void onBean(CabinetItemBean beans,int type) {//type 1 ?????? 2 ??????
                String submitJson = new Gson().toJson(beans);

                bean=beans;
                viewModel.set_cabinet_info(beans,type);
                KLog.d("?????????json???????????????\r\n"+submitJson);

            }
        });
        //??????
        dialog_clear.setOnScanningListener(new ClearDialog.OnScanningListener() {
            @Override
            public void onScanning() {
                startQrCode();
            }
        });
    }

    /**
     * ???????????????????????????
     * @param type 2??????
     * @param topic
     */
    private void showCabinetDialogs(int type, String topic) {
        KLog.d("????????????"+topic);
        CabinetDialog dialog_cabinet =new CabinetDialog(type,mContext, topic);

        new  XPopup.Builder(mContext)
                .autoOpenSoftInput(false)
                .asCustom(dialog_cabinet)
                .show();
        //?????????
        dialog_cabinet.setOnBlbListener(new CabinetDialog.OnBlbListener() {
            @Override
            public void onBlb(int type, int class_type, String topic) {
            viewModel.setBlb(type,class_type,topic);
            }
        });
        //????????????
        dialog_cabinet.setOnBeanListener(new CabinetDialog.OnHeatListener() {
            @Override
            public void onHeat(HeatBean bean) {
                viewModel.setHeatingCabinet(bean);
            }
        });
        //??????
        dialog_cabinet.setOnRestartListener(new CabinetDialog.OnRestartListener() {
            @Override
            public void onRestart(String topic, int type) {
                viewModel.setRestart(topic,type);
            }
        });

    }

    //???????????????
    private void initRight() {

        /**
         * true:??????,false:??????
         */
        if (isPad(getBaseContext())){
            rightManager = new GridLayoutManager(mContext, 2);
        }else {
            rightManager = new GridLayoutManager(mContext, 1);
        }


        if (rightAdapter == null) {
            rightAdapter = new ScrollRightAdapter(R.layout.scroll_right, R.layout.layout_right_title, null);
            binding.recRight.setLayoutManager(rightManager);

            binding.recRight.setAdapter(rightAdapter);
        } else {
            rightAdapter.notifyDataSetChanged();
        }

        rightAdapter.setNewData(right);
        //???????????????
        rightAdapter.setOnOpenListener(new ScrollRightAdapter.OnOpenListener() {
            @Override
            public void onOpen(List<CabinetBean.DataBean.WithTableBean> with_table) {
                initTG(with_table);
            }
        });
        //?????????????????????
        rightAdapter.setOnUpdateListener(new ScrollRightAdapter.OnUpdateListener() {
            @Override
            public void onUpdate(ScrollBean item) {
                requestDynamicPermisson();
                bean.setLatitude(latitude+"");
                bean.setLongitude(longitude+"");
                bean.setName(item.t.getName());
                bean.setAddress(item.t.getAddress());
                bean.setNumber(item.t.getWith_table().get(0).getTopic());
                bean.setId(item.t.getId());
                bean.setStore_id(item.t.getStore_id());
                bean.setStore_name(item.t.getStore_name());
                bean.setLattice_num(item.t.getWith_table().size()+"");
                String submitJson = new Gson().toJson(item);
                KLog.d("ScrollBean??????"+submitJson);
                position=right.indexOf(item);

                showDialogs(2,bean,left);
            }
        });
        //item????????????
        rightAdapter.setOnItemListener(new ScrollRightAdapter.OnItemListener() {
            @Override
            public void onItem(ScrollBean item) {
                //????????????
               int  position=right.indexOf(item);

                if (right.get(position).t.getCurrentSelect()==1){//1  ?????? 0 ?????????
                    right.get(position).t.setCurrentSelect(0);
                }else {
                    right.get(position).t.setCurrentSelect(1);
                }
                Boolean flag = true;
                for (int i=0;i<right.size();i++){
                    if (right.get(i).t!=null&&right.get(i).t.getCurrentSelect()!=1){
                        flag=false;
                    }
                }
                viewModel.all_field.set(flag);
                //??????
                rightAdapter.notifyItemChanged(position);

            }
        });
        //??????item????????????
        rightAdapter.setOnStoreItemListener(new ScrollRightAdapter.OnStoreItemListener() {
            @Override
            public void onStoreItem(ScrollBean item) {

                //????????????
                int  position=right.indexOf(item);
                KLog.d("??????item??????:"+right.get(position).id+"???????????????"+position);
                if (right.get(position).getCurrentSelect()==1){//1  ?????? 0 ?????????
                    right.get(position).setCurrentSelect(0);
                }else {
                    right.get(position).setCurrentSelect(1);
                }
                Boolean flag = true;
                for (int i=0;i<right.size();i++){
                    if (right.get(i).t!=null&&right.get(position).id.equals(right.get(i).t.getStore_id())){
                        right.get(i).t.setCurrentSelect(right.get(position).getCurrentSelect());
                    }
                    if (right.get(i).t!=null&&right.get(i).t.getCurrentSelect()!=1){
                        flag=false;
                    }
                }
                viewModel.all_field.set(flag);
                //??????
                rightAdapter.notifyDataSetChanged();
            }
        });


        //??????????????????title
        if (right.size()>0&&right.get(first).isHeader) {
            binding.rightTitle.setText(right.get(first).header);
            store_id=right.get(first).id;
            viewModel.currentSelect.set(right.get(first).getCurrentSelect());

        }

        binding.recRight.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //????????????title?????????
                tHeight = binding.rightTitle.getHeight();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                //???????????????header
                if (right.get(first).isHeader) {
                    //???????????????item???view
                    View view = rightManager.findViewByPosition(first);
                    if (view != null) {
                        //???????????????item??????????????????????????????????????????title?????????,??????????????????
                        if (view.getTop() >= tHeight) {
                            binding.rightTitle.setY(view.getTop() - tHeight);
                        } else {
                            //???????????????
                            binding.rightTitle.setY(0);
                        }
                    }
                }

                //????????????????????????,?????????????????????????????????item???position???????????????,???????????????????????????????????????item???position???????????????,
                //????????????????????????title??????,?????????????????????????????????????????????????????????item???position???????????????????????????
                int firstPosition = rightManager.findFirstVisibleItemPosition();
                if (first != firstPosition && firstPosition >= 0) {
                    //???first??????
                    first = firstPosition;
                    //?????????Y???????????????
                    binding.rightTitle.setY(0);

                    //????????????????????????????????????item?????????header,??????????????????
                    if (right.get(first).isHeader) {
                        binding.rightTitle.setText(right.get(first).header);
                        store_id=right.get(first).id;
                        store_position=first;
                        viewModel.currentSelect.set(right.get(first).getCurrentSelect());
                    } else {
                        binding.rightTitle.setText(right.get(first).t.getStore_name());
                        store_id=right.get(first).t.getStore_id();
                        for (int i=0;i<right.size();i++){
                            if (right.get(first).t.getStore_id().equals(right.get(i).id)){
                                viewModel.currentSelect.set(right.get(i).currentSelect);
                            }
                        }
                    }
                }

                //??????????????????,????????????????????????????????????title,?????????????????????item??????
                for (int i = 0; i < left.size(); i++) {
                    if (left.get(i).name.equals(binding.rightTitle.getText().toString())) {
                        leftAdapter.selectItem(i);
                    }
                }

                //???????????????????????????????????????item???position,??????bean????????????????????????position(?????????????????????????????????),
                //?????????????????????????????????item??????
                if (rightManager.findLastCompletelyVisibleItemPosition() == right.size() - 1) {
                    leftAdapter.selectItem(left.size() - 1);
                }
            }
        });
    }

    private void initLeft() {
        if (leftAdapter == null) {
            leftAdapter = new ScrollLeftAdapter(R.layout.scroll_left, left);
            binding.recLeft.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

            binding.recLeft.setAdapter(leftAdapter);
            //??????item????????????
            if (binding.recLeft.getItemDecorationCount()==0) {
                binding.recLeft.addItemDecoration(new DividerItemDecorations(this, DividerItemDecorations.VERTICAL));
            }
        } else {
            leftAdapter.notifyDataSetChanged();
        }

        leftAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    //???????????????????????????item,?????????????????????title????????????
                    //(?????????????????????????????????????????????????????????,????????????????????????????????????)
                    case R.id.item:
                        leftAdapter.selectItem(position);
                        rightManager.scrollToPositionWithOffset(tPosition.get(position), 0);
                        break;
                }
            }
        });

    }

    //????????????(????????????????????????,?????????????????????????????????)
    private void initDatas() {

        for (int i = 0; i < right.size(); i++) {
            if (right.get(i).isHeader) {
                //??????????????????,???????????????header,?????????header???????????????????????????position??????????????????
                tPosition.add(i);
            }
        }
    }

    /**
     * ????????????
     * @param with_table
     */
    private void initTG(List<CabinetBean.DataBean.WithTableBean> with_table) {
        List<TableItemBean> data=new ArrayList<>();

        for (int i=0;i<with_table.size();i++){
            TableItemBean tableItemBean=new TableItemBean();
            tableItemBean.setCabinet_number(with_table.get(i).getCabinet_number());
            tableItemBean.setId(with_table.get(i).getId()+"");
            tableItemBean.setChannel(with_table.get(i).getChannel()+"");
            tableItemBean.setTopic(with_table.get(i).getTopic()+"");
            data.add(tableItemBean);
        }
        GroupPop groupPop =new GroupPop(mContext, data);
        groupPop.setClickInterface(new GroupPop.ClickInterface() {
            @Override
            public void click(int position, TableItemBean bean) {

                viewModel.openGuizi(bean.getTopic(),bean.getChannel(),1);
                KLog.d("topic:"+bean.getTopic()+",channel:"+bean.getChannel());

            }
            //????????????
            @Override
            public void Allclick(String topic) {
                viewModel.openGuizi(topic,"",2);
            }
        });

        new  XPopup.Builder(mContext)
                .asCustom(groupPop)
                .show();
    }

    /**
     * ????????????
     */
    private void requestDynamicPermisson() {
        //??????????????????
        PermissionX.init(this)
                .permissions(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)
                .request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList) {
                        if (allGranted) {
                            KLog.d("????????????????????????");
                            initTencentLocationRequest();
                        } else {
                            ToastUtils.showShort("???????????????");
                        }
                    }
                });
    }

    /**
     * ?????????????????????
     */
    private void initTencentLocationRequest() {

        TencentLocationManager locationManager = TencentLocationManager.getInstance(this);
        locationManager.setCoordinateType(TencentLocationManager.COORDINATE_TYPE_WGS84);
        TencentLocationListener mLocationListener=new TencentLocationListener() {
            @Override
            public void onLocationChanged(TencentLocation tencentLocation, int i, String s) {
                KLog.d("i=:"+i+",String "+s);
                 address=tencentLocation.getAddress();
                 latitude=tencentLocation.getLatitude();
                 longitude=tencentLocation.getLongitude();
            }

            @Override
            public void onStatusUpdate(String s, int i, String s1) {
                KLog.d("i=:"+i+",String s="+s+",String s1="+s1);
            }
        };
        locationManager.requestSingleFreshLocation(null, mLocationListener, Looper.getMainLooper());
    }

        // ????????????
    public void startQrCode(){
        PermissionX.init(this)
                .permissions(Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE)
                .request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList) {
                        if (allGranted) {
                            Intent intent = new Intent(mContext, CaptureActivity.class);
                            startActivityForResult(intent, Constant.REQ_QR_CODE);
                        } else {
                            RxToast.normal("???????????????????????????????????????");
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //??????????????????
        if (requestCode == Constant.REQ_QR_CODE && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString(Constant.INTENT_EXTRA_KEY_QR_SCAN);

            if (scanResult!=null){
                KLog.d("???????????????"+scanResult.indexOf("IMEI"));

                if (scanResult.indexOf("IMEI")==-1){
                    //?????????????????????????????????
                    KLog.d("?????????????????????"+scanResult);
//                    RxToast.warning("???????????????????????????");
                    EventBus.getDefault().post(scanResult);
                }else {
                    String a=scanResult.substring(0, scanResult.indexOf(",MAC:"));
                    String IMEI= a.replace("IMEI:", "");
                    EventBus.getDefault().post(IMEI);

                    //?????????????????????????????????
                    KLog.d("?????????????????????"+scanResult);
                    KLog.d("?????????????????????"+a);
                    KLog.d("?????????????????????"+IMEI);

                }
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}
