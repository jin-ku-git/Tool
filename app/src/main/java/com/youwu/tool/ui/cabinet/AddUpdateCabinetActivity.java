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
 * 增加/更新取餐柜
 * 2021/12/20
 */

public class AddUpdateCabinetActivity extends BaseActivity<ActivityAddUpdateCabinetBinding, AddUpdateCabinetViewModel> {


    private List<StoreBean.DataBean> left=new ArrayList<>();
    private List<ScrollBean> right=new ArrayList<>();
    private ScrollLeftAdapter leftAdapter;
    private ScrollRightAdapter rightAdapter;
    //右侧title在数据中所对应的position集合
    private List<Integer> tPosition = new ArrayList<>();
    private Context mContext;
    //title的高度
    private int tHeight;
    //记录右侧当前可见的第一个item的position
    private int first = 0;
    private GridLayoutManager rightManager;

    CabinetItemBean bean =new CabinetItemBean();


    //点击的第几条数据
    private int position;

    //柜子接口走了几次
    private int Cabinet_type = 0;

    /************************************/
    //经纬度
    String address;
    double latitude;//维度
    double longitude;//经度

    String store_id;//门店id
    int store_position=0;//
    @Override
    public void initParam() {
        super.initParam();
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
        //使用自定义的ViewModelFactory来创建ViewModel，如果不重写该方法，则默认会调用NetWorkViewModel(@NonNull Application application)构造方法
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getApplication());
        return ViewModelProviders.of(this, factory).get(AddUpdateCabinetViewModel.class);
    }

    @Override
    public void initViewObservable() {
        //判断item是否可点击
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
                    case 1://新增
                        requestDynamicPermisson();
                        bean=new CabinetItemBean();
                        bean.setAddress(address);
                        bean.setLongitude(longitude+"");
                        bean.setLatitude(latitude+"");
                        showDialogs(1,bean,left);
                        break;
                    case 2://修改信息成功
                        RxToast.success("修改成功!");

                        right.get(position).t.setAddress(bean.address);;
                        right.get(position).t.setLongitude(bean.longitude);;
                        right.get(position).t.setLatitude(bean.Latitude);;
                        right.get(position).t.setName(bean.name);;
                        rightAdapter.notifyItemChanged(position);
                        break;
                    case 3:
                        RxToast.success("添加成功!");

                        //获取数据
                        left.clear();
                        right.clear();

                        viewModel.getStoreList();
                        break;
                    case 4://打开柜子
                        RxToast.success("打开成功!");
                        break;
                    case 5://取消全选
                        for (int i=0;i<right.size();i++){
                            viewModel.currentSelect.set(0);
                            right.get(i).setCurrentSelect(0);
                            if (right.get(i).t!=null){
                                right.get(i).t.setCurrentSelect(0);
                            }
                        }
                        rightAdapter.notifyDataSetChanged();
                        break;
                    case 6://全选
                        for (int i=0;i<right.size();i++){
                            viewModel.currentSelect.set(1);
                            right.get(i).setCurrentSelect(1);
                            if (right.get(i).t!=null){
                                right.get(i).t.setCurrentSelect(1);
                            }

                        }
                        rightAdapter.notifyDataSetChanged();
                        break;
                    case 7://修改
                        String topic="";
                        for (int i=0;i<right.size();i++){
                            if (right.get(i).t!=null&&right.get(i).t.getCurrentSelect()==1){
                                topic+=right.get(i).t.getWith_table().get(0).getTopic()+",";
                            }
                        }
                        topic = topic.substring(0, topic.length() -1);
                        if (topic.isEmpty()){
                            RxToast.normal("请选择修改的柜子");
                            return;
                        }
                        if (viewModel.all_field.get()){
                            showCabinetDialogs(2,topic);
                        }else {
                            showCabinetDialogs(1,topic);
                        }

                        break;
                    case 8://顶部门店点击事件
                            KLog.d("门店id："+store_id+"，第几个："+store_position);

                        KLog.d("门店item点击:"+right.get(position).id+"，第几个："+position);
                        if (right.get(store_position).getCurrentSelect()==1){//1  选择 0 未选择
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
                        //刷新
                        rightAdapter.notifyDataSetChanged();
                        break;
                }
            }
        });
        //门店监听
        viewModel.StoreBeanEvent.observe(this, new Observer<List<StoreBean.DataBean>>() {
            @Override
            public void onChanged(List<StoreBean.DataBean> dataBeans) {
                left.addAll(dataBeans);
                for (int i=0;i<dataBeans.size();i++){

                    viewModel.getCabinetList(dataBeans.get(i).id,dataBeans.get(i).name);
                }

            }
        });
        //柜子监听
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
                    KLog.d("走了几次left.size():"+left.size());
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
        //修改状态栏是状态栏透明
        StatusBarUtil.setTransparentForWindow(this);
        StatusBarUtil.setDarkMode(this);//使状态栏字体变为白色
        // 可以调用该方法，设置不允许滑动退出
        setSwipeBackEnable(true);
        mContext=this;
        viewModel.time_judge.set(false);
        viewModel.all_field.set(false);
        //获取数据
        viewModel.getStoreList();

        //获取权限
        requestDynamicPermisson();

    }

    /**
     * 修改信息弹窗
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
            public void onBean(CabinetItemBean beans,int type) {//type 1 新增 2 修改
                String submitJson = new Gson().toJson(beans);

                bean=beans;
                viewModel.set_cabinet_info(beans,type);
                KLog.d("提交的json实体数据：\r\n"+submitJson);

            }
        });
        //扫描
        dialog_clear.setOnScanningListener(new ClearDialog.OnScanningListener() {
            @Override
            public void onScanning() {
                startQrCode();
            }
        });
    }

    /**
     * 修改柜子加热等弹窗
     * @param type 2全部
     * @param topic
     */
    private void showCabinetDialogs(int type, String topic) {
        KLog.d("弹窗传值"+topic);
        CabinetDialog dialog_cabinet =new CabinetDialog(type,mContext, topic);

        new  XPopup.Builder(mContext)
                .autoOpenSoftInput(false)
                .asCustom(dialog_cabinet)
                .show();
        //紫光灯
        dialog_cabinet.setOnBlbListener(new CabinetDialog.OnBlbListener() {
            @Override
            public void onBlb(int type, int class_type, String topic) {
            viewModel.setBlb(type,class_type,topic);
            }
        });
        //开关加热
        dialog_cabinet.setOnBeanListener(new CabinetDialog.OnHeatListener() {
            @Override
            public void onHeat(HeatBean bean) {
                viewModel.setHeatingCabinet(bean);
            }
        });
        //重启
        dialog_cabinet.setOnRestartListener(new CabinetDialog.OnRestartListener() {
            @Override
            public void onRestart(String topic, int type) {
                viewModel.setRestart(topic,type);
            }
        });

    }

    //初始化右边
    private void initRight() {


        rightManager = new GridLayoutManager(mContext, 1);

        if (rightAdapter == null) {
            rightAdapter = new ScrollRightAdapter(R.layout.scroll_right, R.layout.layout_right_title, null);
            binding.recRight.setLayoutManager(rightManager);

            binding.recRight.setAdapter(rightAdapter);
        } else {
            rightAdapter.notifyDataSetChanged();
        }

        rightAdapter.setNewData(right);
        //打开取餐柜
        rightAdapter.setOnOpenListener(new ScrollRightAdapter.OnOpenListener() {
            @Override
            public void onOpen(List<CabinetBean.DataBean.WithTableBean> with_table) {
                initTG(with_table);
            }
        });
        //更新取餐柜信息
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
                KLog.d("ScrollBean数据"+submitJson);
                position=right.indexOf(item);

                showDialogs(2,bean,left);
            }
        });
        //item点击事件
        rightAdapter.setOnItemListener(new ScrollRightAdapter.OnItemListener() {
            @Override
            public void onItem(ScrollBean item) {
                //获取下标
               int  position=right.indexOf(item);

                if (right.get(position).t.getCurrentSelect()==1){//1  选择 0 未选择
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
                //刷新
                rightAdapter.notifyItemChanged(position);

            }
        });
        //门店item点击事件
        rightAdapter.setOnStoreItemListener(new ScrollRightAdapter.OnStoreItemListener() {
            @Override
            public void onStoreItem(ScrollBean item) {

                //获取下标
                int  position=right.indexOf(item);
                KLog.d("门店item点击:"+right.get(position).id+"，第几个："+position);
                if (right.get(position).getCurrentSelect()==1){//1  选择 0 未选择
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
                //刷新
                rightAdapter.notifyDataSetChanged();
            }
        });


        //设置右侧初始title
        if (right.size()>0&&right.get(first).isHeader) {
            binding.rightTitle.setText(right.get(first).header);
            store_id=right.get(first).id;
            viewModel.currentSelect.set(right.get(first).getCurrentSelect());

        }

        binding.recRight.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //获取右侧title的高度
                tHeight = binding.rightTitle.getHeight();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                //判断如果是header
                if (right.get(first).isHeader) {
                    //获取此组名item的view
                    View view = rightManager.findViewByPosition(first);
                    if (view != null) {
                        //如果此组名item顶部和父容器顶部距离大于等于title的高度,则设置偏移量
                        if (view.getTop() >= tHeight) {
                            binding.rightTitle.setY(view.getTop() - tHeight);
                        } else {
                            //否则不设置
                            binding.rightTitle.setY(0);
                        }
                    }
                }

                //因为每次滑动之后,右侧列表中可见的第一个item的position肯定会改变,并且右侧列表中可见的第一个item的position变换了之后,
                //才有可能改变右侧title的值,所以这个方法内的逻辑在右侧可见的第一个item的position改变之后一定会执行
                int firstPosition = rightManager.findFirstVisibleItemPosition();
                if (first != firstPosition && firstPosition >= 0) {
                    //给first赋值
                    first = firstPosition;
                    //不设置Y轴的偏移量
                    binding.rightTitle.setY(0);

                    //判断如果右侧可见的第一个item是否是header,设置相应的值
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

                //遍历左边列表,列表对应的内容等于右边的title,则设置左侧对应item高亮
                for (int i = 0; i < left.size(); i++) {
                    if (left.get(i).name.equals(binding.rightTitle.getText().toString())) {
                        leftAdapter.selectItem(i);
                    }
                }

                //如果右边最后一个完全显示的item的position,等于bean中最后一条数据的position(也就是右侧列表拉到底了),
                //则设置左侧列表最后一条item高亮
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
            //设置item的分割线
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
                    //点击左侧列表的相应item,右侧列表相应的title置顶显示
                    //(最后一组内容若不能填充右侧整个可见页面,则显示到右侧列表的最底端)
                    case R.id.item:
                        leftAdapter.selectItem(position);
                        rightManager.scrollToPositionWithOffset(tPosition.get(position), 0);
                        break;
                }
            }
        });

    }

    //获取数据(若请求服务端数据,请求到的列表需有序排列)
    private void initDatas() {

        for (int i = 0; i < right.size(); i++) {
            if (right.get(i).isHeader) {
                //遍历右侧列表,判断如果是header,则将此header在右侧列表中所在的position添加到集合中
                tPosition.add(i);
            }
        }
    }

    /**
     * 格子弹窗
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
            //全部打开
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
     * 获取权限
     */
    private void requestDynamicPermisson() {
        //请求定位权限
        PermissionX.init(this)
                .permissions(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)
                .request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList) {
                        if (allGranted) {
                            KLog.d("定位权限已经打开");
                            initTencentLocationRequest();
                        } else {
                            ToastUtils.showShort("权限被拒绝");
                        }
                    }
                });
    }

    /**
     * 开启定位监听器
     */
    private void initTencentLocationRequest() {

        TencentLocationManager locationManager = TencentLocationManager.getInstance(this);
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

        // 开始扫码
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
                            RxToast.normal("权限被拒绝，无法使用此功能");
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //扫描结果回调
        if (requestCode == Constant.REQ_QR_CODE && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString(Constant.INTENT_EXTRA_KEY_QR_SCAN);

            if (scanResult!=null){
                KLog.d("是否包含："+scanResult.indexOf("IMEI"));

                if (scanResult.indexOf("IMEI")==-1){
                    //将扫描出的信息显示出来
                    KLog.d("扫描出的信息："+scanResult);
//                    RxToast.warning("请扫描正确的二维码");
                    EventBus.getDefault().post(scanResult);
                }else {
                    String a=scanResult.substring(0, scanResult.indexOf(",MAC:"));
                    String IMEI= a.replace("IMEI:", "");
                    EventBus.getDefault().post(IMEI);

                    //将扫描出的信息显示出来
                    KLog.d("扫描出的信息："+scanResult);
                    KLog.d("扫描出的信息："+a);
                    KLog.d("扫描出的信息："+IMEI);

                }
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}
