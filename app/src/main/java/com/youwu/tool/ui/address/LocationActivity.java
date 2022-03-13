package com.youwu.tool.ui.address;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.location.LocationListener;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.PictureSelectorExternalUtils;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.luck.picture.lib.style.PictureWindowAnimationStyle;
import com.lxj.xpopup.XPopup;


import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.youwu.tool.BR;
import com.youwu.tool.R;
import com.youwu.tool.custom.FullyGridLayoutManager;
import com.youwu.tool.custom.GlideEngine;
import com.youwu.tool.databinding.ActivityLocationBinding;
import com.youwu.tool.popwindow.MDPopupView;
import com.youwu.tool.ui.cabinet.bean.StoreBean;
import com.youwu.tool.ui.camera.CameraChoiceActivity;
import com.youwu.tool.ui.camera.image_adapter.GridImageAdapter;
import com.youwu.tool.utils_view.RxToast;
import com.youwu.tool.utils_view.StatusBarUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.ToastUtils;


/**
 * 提交位置信息
 * 2021/12/12
 */

public class LocationActivity extends BaseActivity<ActivityLocationBinding, LocationViewModel> {

    private MDPopupView popupView;
    private MDPopupView popupView_cabinet;
    ArrayList<StoreBean.DataBean> list_store= new ArrayList<>();//门店
    ArrayList<StoreBean.DataBean> list_cabinet= new ArrayList<>();//柜子

    /***************************************************************/
    //相机 相册
    private final static String TAG = CameraChoiceActivity.class.getSimpleName();

    private List<LocalMedia> selectList = new ArrayList<>();
    private GridImageAdapter adapter;
    private int chooseMode = PictureMimeType.ofAll();// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
    private int maxSelectNum = 9;//最大选择数量
    private int themeId;
    Activity activity;

    @Override
    public void initParam() {
        super.initParam();

    }

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_location;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initViewObservable() {

        viewModel.typeEvent.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                switch (integer){
                    case 1://选择门店
                        showStore(binding.tvStore);
                        break;
                    case 2://选择柜子
                        showCabinet(binding.tvCabinet);
                        break;
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

        list_store = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            StoreBean.DataBean bean=new StoreBean.DataBean();
            bean.name="门店" + (i + 1);
            list_store.add(bean);
        }
        binding.tvStore.setText(list_store.get(0).name);
        viewModel.store.set(list_store.get(0).name);
        list_cabinet = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            StoreBean.DataBean bean=new StoreBean.DataBean();
            bean.name="柜子" + (i + 1);
            list_cabinet.add(bean);
        }
        binding.tvCabinet.setText(list_cabinet.get(0).name);
        viewModel.cabinet.set(list_cabinet.get(0).name);
        //获取权限
        requestDynamicPermisson();

        themeId = R.style.picture_default_style;

        initWidget();

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
                KLog.d("tencentLocation:"+tencentLocation.getAddress());
                String address=tencentLocation.getAddress();
                double latitude=tencentLocation.getLatitude();
                double longitude=tencentLocation.getLongitude();
                viewModel.address.set(""+address);
                viewModel.Latitude.set(""+latitude);
                viewModel.Longitude.set(""+longitude);
            }

            @Override
            public void onStatusUpdate(String s, int i, String s1) {
                KLog.d("i=:"+i+",String s="+s+",String s1="+s1);
            }
        };
        locationManager.requestSingleFreshLocation(null, mLocationListener, Looper.getMainLooper());

    }


    /**
     * 门店弹窗
     * @param v
     */
    private void showStore(final View v){
        if(popupView==null){
            popupView = (MDPopupView) new XPopup.Builder(this)
                    .atView(v)
                    .asCustom(new MDPopupView(this));
        }
        popupView.setItemsData(list_store);
        popupView.setOnChoiceener(new MDPopupView.OnChoiceener() {

            @Override
            public void onChoice(StoreBean.DataBean data) {
                binding.tvStore.setText(data.name);
                viewModel.store.set(data.name);
                popupView.dismiss();
            }
        });

        popupView.show();

    }
    /**
     * 柜子弹窗
     * @param v
     */
    private void showCabinet(final View v){

        popupView_cabinet = (MDPopupView) new XPopup.Builder(this)
                .atView(v)
                .asCustom(new MDPopupView(this));

        popupView_cabinet.setItemsData(list_cabinet);
        popupView_cabinet.setOnChoiceener(new MDPopupView.OnChoiceener() {
            @Override
            public void onChoice(StoreBean.DataBean data) {
                binding.tvCabinet.setText(data.name);
                viewModel.cabinet.set(data.name);
                popupView_cabinet.dismiss();
            }
        });

        popupView_cabinet.show();

    }

    private void initWidget() {
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        binding.recyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(this, onAddPicClickListener);
        adapter.setList(selectList);
        adapter.setSelectMax(maxSelectNum);
        binding.recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((v, position) -> {
            List<LocalMedia> selectList = adapter.getData();
            if (selectList.size() > 0) {
                LocalMedia media = selectList.get(position);
                String mimeType = media.getMimeType();
                int mediaType = PictureMimeType.getMimeType(mimeType);
                switch (mediaType) {
                    case PictureConfig.TYPE_VIDEO:
                        // 预览视频
                        PictureSelector.create(LocationActivity.this).externalPictureVideo(media.getPath());
                        break;
                    case PictureConfig.TYPE_AUDIO:
                        // 预览音频
                        PictureSelector.create(LocationActivity.this).externalPictureAudio(media.getPath());
                        break;
                    default:
                        // 预览图片 可自定长按保存路径
                        PictureWindowAnimationStyle animationStyle = new PictureWindowAnimationStyle();
                        animationStyle.activityPreviewEnterAnimation = R.anim.picture_anim_up_in;
                        animationStyle.activityPreviewExitAnimation = R.anim.picture_anim_down_out;
                        PictureSelector.create(LocationActivity.this)
                                .openGallery(chooseMode)// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                                .imageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
                                .theme(themeId)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style v2.3.3后 建议使用setPictureStyle()动态方式
//                                .setPictureStyle(mPictureParameterStyle)// 动态自定义相册主题
                                //.setPictureWindowAnimationStyle(animationStyle)// 自定义页面启动动画
                                .isNotPreviewDownload(false)// 预览图片长按是否可以下载
                                .openExternalPreview(position, selectList);

                        break;
                }
            }
        });
    }

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {

        @SuppressLint("CheckResult")
        @Override
        public void onAddPicClick() {

            //获取权限
            requestPermissions();
        }
    };
    /**
     * 获取权限
     */
    private void requestPermissions() {

        //请求定位权限
        PermissionX.init(this)
                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA)
                .request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList) {
                        if (allGranted) {
                            //第一种方式，弹出选择和拍照的dialog
//                                showPop();

                            //第二种方式，直接进入相册，但是 是有拍照得按钮的
                            showAlbum();
                        } else {
                            ToastUtils.showShort("权限被拒绝");
                        }
                    }
                });
    }

    private void showAlbum() {
        // 进入相册 以下是例子：不需要的api可以不写
        PictureSelector.create(LocationActivity.this)
                .openGallery(chooseMode)// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .imageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
                .theme(themeId)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style v2.3.3后 建议使用setPictureStyle()动态方式
                .isWeChatStyle(true)// 是否开启微信图片选择风格
                .selectionData(adapter.getData())// 是否传入已选图片
                .forResult(new MyResultCallback(adapter));
    }



    /**
     * 返回结果回调
     */
    private class MyResultCallback implements OnResultCallbackListener<LocalMedia> {
        private WeakReference<GridImageAdapter> mAdapterWeakReference;

        public MyResultCallback(GridImageAdapter adapter) {
            super();
            this.mAdapterWeakReference = new WeakReference<>(adapter);
        }

        /**
         * return LocalMedia result
         *
         * @param result
         */
        @Override
        public void onResult(List<LocalMedia> result) {

            for (LocalMedia media : result) {
                Log.i(TAG, "是否压缩:" + media.isCompressed());
                Log.i(TAG, "压缩:" + media.getCompressPath());
                Log.i(TAG, "原图:" + media.getPath());
                Log.i(TAG, "是否裁剪:" + media.isCut());
                Log.i(TAG, "裁剪:" + media.getCutPath());
                Log.i(TAG, "是否开启原图:" + media.isOriginal());
                Log.i(TAG, "原图路径:" + media.getOriginalPath());
                Log.i(TAG, "Android Q 特有Path:" + media.getAndroidQToPath());
                Log.i(TAG, "宽高: " + media.getWidth() + "x" + media.getHeight());
                Log.i(TAG, "Size: " + media.getSize());

//                imageFile = getFileByUrl(media.getPath());

                ExifInterface exifInterface= PictureSelectorExternalUtils.getExifInterface(getBaseContext(),media.getPath());
                KLog.d("图片高度:"+exifInterface.getAttribute(exifInterface.TAG_IMAGE_LENGTH));
                KLog.d("图片宽度:"+exifInterface.getAttribute(exifInterface.TAG_IMAGE_WIDTH));
                KLog.d("设备品牌:"+exifInterface.getAttribute(exifInterface.TAG_MAKE));
                KLog.d("设备型号:"+exifInterface.getAttribute(exifInterface.TAG_MODEL));
                KLog.d("拍摄时间:"+exifInterface.getAttribute(exifInterface.TAG_DATETIME));


                viewModel.photoList.set(result);
                // TODO 可以通过PictureSelectorExternalUtils.getExifInterface();方法获取一些额外的资源信息，如旋转角度、经纬度等信息
            }


            if (mAdapterWeakReference.get() != null) {
                mAdapterWeakReference.get().setList(result);
                mAdapterWeakReference.get().notifyDataSetChanged();
            }
        }

        /**
         * Cancel
         */
        @Override
        public void onCancel() {

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<LocalMedia> images;
        if (resultCode == RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST) {// 图片选择结果回调

                images = PictureSelector.obtainMultipleResult(data);
                selectList.addAll(images);



                adapter.setList(selectList);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}
