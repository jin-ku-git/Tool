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
 * ??????????????????
 * 2021/12/12
 */

public class LocationActivity extends BaseActivity<ActivityLocationBinding, LocationViewModel> {

    private MDPopupView popupView;
    private MDPopupView popupView_cabinet;
    ArrayList<StoreBean.DataBean> list_store= new ArrayList<>();//??????
    ArrayList<StoreBean.DataBean> list_cabinet= new ArrayList<>();//??????

    /***************************************************************/
    //?????? ??????
    private final static String TAG = CameraChoiceActivity.class.getSimpleName();

    private List<LocalMedia> selectList = new ArrayList<>();
    private GridImageAdapter adapter;
    private int chooseMode = PictureMimeType.ofAll();// ??????.PictureMimeType.ofAll()?????????.ofImage()?????????.ofVideo()?????????.ofAudio()
    private int maxSelectNum = 9;//??????????????????
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
                    case 1://????????????
                        showStore(binding.tvStore);
                        break;
                    case 2://????????????
                        showCabinet(binding.tvCabinet);
                        break;
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

        list_store = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            StoreBean.DataBean bean=new StoreBean.DataBean();
            bean.name="??????" + (i + 1);
            list_store.add(bean);
        }
        binding.tvStore.setText(list_store.get(0).name);
        viewModel.store.set(list_store.get(0).name);
        list_cabinet = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            StoreBean.DataBean bean=new StoreBean.DataBean();
            bean.name="??????" + (i + 1);
            list_cabinet.add(bean);
        }
        binding.tvCabinet.setText(list_cabinet.get(0).name);
        viewModel.cabinet.set(list_cabinet.get(0).name);
        //????????????
        requestDynamicPermisson();

        themeId = R.style.picture_default_style;

        initWidget();

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
     * ????????????
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
     * ????????????
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
                        // ????????????
                        PictureSelector.create(LocationActivity.this).externalPictureVideo(media.getPath());
                        break;
                    case PictureConfig.TYPE_AUDIO:
                        // ????????????
                        PictureSelector.create(LocationActivity.this).externalPictureAudio(media.getPath());
                        break;
                    default:
                        // ???????????? ???????????????????????????
                        PictureWindowAnimationStyle animationStyle = new PictureWindowAnimationStyle();
                        animationStyle.activityPreviewEnterAnimation = R.anim.picture_anim_up_in;
                        animationStyle.activityPreviewExitAnimation = R.anim.picture_anim_down_out;
                        PictureSelector.create(LocationActivity.this)
                                .openGallery(chooseMode)// ??????.PictureMimeType.ofAll()?????????.ofImage()?????????.ofVideo()?????????.ofAudio()
                                .imageEngine(GlideEngine.createGlideEngine())// ??????????????????????????????????????????
                                .theme(themeId)// ?????????????????? ???????????? values/styles   ?????????R.style.picture.white.style v2.3.3??? ????????????setPictureStyle()????????????
//                                .setPictureStyle(mPictureParameterStyle)// ???????????????????????????
                                //.setPictureWindowAnimationStyle(animationStyle)// ???????????????????????????
                                .isNotPreviewDownload(false)// ????????????????????????????????????
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

            //????????????
            requestPermissions();
        }
    };
    /**
     * ????????????
     */
    private void requestPermissions() {

        //??????????????????
        PermissionX.init(this)
                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA)
                .request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList) {
                        if (allGranted) {
                            //??????????????????????????????????????????dialog
//                                showPop();

                            //????????????????????????????????????????????? ????????????????????????
                            showAlbum();
                        } else {
                            ToastUtils.showShort("???????????????");
                        }
                    }
                });
    }

    private void showAlbum() {
        // ???????????? ??????????????????????????????api????????????
        PictureSelector.create(LocationActivity.this)
                .openGallery(chooseMode)// ??????.PictureMimeType.ofAll()?????????.ofImage()?????????.ofVideo()?????????.ofAudio()
                .imageEngine(GlideEngine.createGlideEngine())// ??????????????????????????????????????????
                .theme(themeId)// ?????????????????? ???????????? values/styles   ?????????R.style.picture.white.style v2.3.3??? ????????????setPictureStyle()????????????
                .isWeChatStyle(true)// ????????????????????????????????????
                .selectionData(adapter.getData())// ????????????????????????
                .forResult(new MyResultCallback(adapter));
    }



    /**
     * ??????????????????
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
                Log.i(TAG, "????????????:" + media.isCompressed());
                Log.i(TAG, "??????:" + media.getCompressPath());
                Log.i(TAG, "??????:" + media.getPath());
                Log.i(TAG, "????????????:" + media.isCut());
                Log.i(TAG, "??????:" + media.getCutPath());
                Log.i(TAG, "??????????????????:" + media.isOriginal());
                Log.i(TAG, "????????????:" + media.getOriginalPath());
                Log.i(TAG, "Android Q ??????Path:" + media.getAndroidQToPath());
                Log.i(TAG, "??????: " + media.getWidth() + "x" + media.getHeight());
                Log.i(TAG, "Size: " + media.getSize());

//                imageFile = getFileByUrl(media.getPath());

                ExifInterface exifInterface= PictureSelectorExternalUtils.getExifInterface(getBaseContext(),media.getPath());
                KLog.d("????????????:"+exifInterface.getAttribute(exifInterface.TAG_IMAGE_LENGTH));
                KLog.d("????????????:"+exifInterface.getAttribute(exifInterface.TAG_IMAGE_WIDTH));
                KLog.d("????????????:"+exifInterface.getAttribute(exifInterface.TAG_MAKE));
                KLog.d("????????????:"+exifInterface.getAttribute(exifInterface.TAG_MODEL));
                KLog.d("????????????:"+exifInterface.getAttribute(exifInterface.TAG_DATETIME));


                viewModel.photoList.set(result);
                // TODO ????????????PictureSelectorExternalUtils.getExifInterface();??????????????????????????????????????????????????????????????????????????????
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
            if (requestCode == PictureConfig.CHOOSE_REQUEST) {// ????????????????????????

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
