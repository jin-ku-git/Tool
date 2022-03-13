package com.youwu.tool.ui.print;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.google.gson.Gson;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;
import com.youwu.tool.BR;
import com.youwu.tool.R;
import com.youwu.tool.app.AppViewModelFactory;
import com.youwu.tool.databinding.ActivityChartBinding;
import com.youwu.tool.databinding.ActivityPrintBinding;
import com.youwu.tool.ui.chart.ChartViewModel;
import com.youwu.tool.ui.chart.utils.BarChartUtils;
import com.youwu.tool.ui.chart.utils.LineChartUtils;
import com.youwu.tool.ui.decoding.CaptureActivity;
import com.youwu.tool.ui.network.NetWorkItemViewModel;
import com.youwu.tool.ui.network.NetWorkViewModel;
import com.youwu.tool.ui.print.bean.DeviceScanBean;
import com.youwu.tool.ui.print.config.Constants;
import com.youwu.tool.utils_view.Constant;
import com.youwu.tool.utils_view.RxToast;
import com.youwu.tool.utils_view.SharePreferecnceUtils;
import com.youwu.tool.utils_view.StatusBarUtil;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.goldze.mvvmhabit.base.BaseActivity;
import site.haoyin.lib.CXComm;
import site.haoyin.lib.bluetooth.CXBlueTooth;
import site.haoyin.lib.bluetooth.listener.IBlueToothListener;
import site.haoyin.lib.bluetooth.listener.IReceiveDataListener;
import site.haoyin.lib.cpcl.CXCpcl;
import site.haoyin.lib.escpos.CXEscpos;
import site.haoyin.lib.tspl.CXTspl;
import site.haoyin.lib.utils.ByteUtil;


/**
 * 打印小票
 * 2021/12/26
 */

public class PrintActivity extends BaseActivity<ActivityPrintBinding, PrintViewModel> {

    private static String TAG = PrintActivity.class.getSimpleName();
    private static final int MSG_START_SCAN = 1001;
    private static final int MSG_STOP_SCAN = 1002;
    private static final int MSG_START_ACTIVITY = 1004;
    private static final int MSG_CONNECT_FAIL = 1005;
    private static final int MSG_CONNECTED = 1006;
    private static final int MSG_DISCONNECTED = 1007;

    private ProgressDialog mProgressDialog;
    private boolean isConnecting = false;
    private boolean isConnect = false;

    private ArrayList<DeviceScanBean> lists = new ArrayList<DeviceScanBean>();


    //新建BroadcastReceiver
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG, "onReceive: " + action);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.e(TAG, "onReceive: " + "ACTION_FOUND");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getName() != null) {

                }
                for (DeviceScanBean list : lists) {
                    if (list.getBluetoothDevice().getAddress().equals(device.getAddress())) {
                        return;
                    }
                }
                if (isConnect) {
                    if ((device.getType() == BluetoothDevice.DEVICE_TYPE_CLASSIC || device.getType() == BluetoothDevice.DEVICE_TYPE_DUAL)
                            && !((device.getBluetoothClass().getDeviceClass()) == BluetoothClass.Device.PHONE_SMART)) {
                        lists.add(new DeviceScanBean(device, 0, new byte[0]));
                        viewModel.observableList.clear();
                        for (int i=0;i<lists.size();i++){
                            PrintItemViewModel itemViewModel = new PrintItemViewModel(viewModel, lists.get(i));
                            viewModel.observableList.add(itemViewModel);
                        }

                        if (lists.size() != 0) {
                            binding.tvNone.setVisibility(View.GONE);
                        }


                        setConnectList(deviceScanBeanSharePre);

                    }
                } else {
                    if ((device.getType() == BluetoothDevice.DEVICE_TYPE_CLASSIC || device.getType() == BluetoothDevice.DEVICE_TYPE_DUAL)
                            && !((device.getBluetoothClass().getDeviceClass()) == BluetoothClass.Device.PHONE_SMART)) {
                        lists.add(new DeviceScanBean(device, 0, new byte[0]));
                        viewModel.observableList.clear();
                        for (int i=0;i<lists.size();i++){
                            PrintItemViewModel itemViewModel = new PrintItemViewModel(viewModel, lists.get(i));
                            viewModel.observableList.add(itemViewModel);
                        }

                        if (lists.size() != 0) {
                            binding.tvNone.setVisibility(View.GONE);
                        }
                    }
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (binding.ivRight != null) {
                    binding.ivRight.clearAnimation();
                }
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START_SCAN:
                    startScan();
                    break;
                case MSG_CONNECTED:
                    setConnected();
                    break;
                case MSG_DISCONNECTED:
                    setDisConected();
                    break;
                case MSG_STOP_SCAN:
                    dismissDialog();
                case MSG_START_ACTIVITY:
                    break;
                case MSG_CONNECT_FAIL:
                    break;
                default:
                    break;
            }
        }

    };

    private DeviceScanBean deviceScanBean;
    private Context mContext;
    private DeviceScanBean deviceScanBeanSharePre;
    private Unbinder bind;

    private CXBlueTooth BT;
    private CXComm cxcomm;


    @Override
    public void initParam() {
        super.initParam();

    }

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_print;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public PrintViewModel initViewModel() {
        //使用自定义的ViewModelFactory来创建ViewModel，如果不重写该方法，则默认会调用NetWorkViewModel(@NonNull Application application)构造方法
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getApplication());
        return ViewModelProviders.of(this, factory).get(PrintViewModel.class);
    }

    @Override
    public void initViewObservable() {

        viewModel.IntegerEvent.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                switch (integer){
                    case 1:
                        sendEscposData();
                        break;
                    case 2:
                        if (isConnect) {
                            
                            binding.btnDisconnet.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_setting_normal));

                            binding.btnSenddata.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_setting_normal));
                            isConnect = false;
                            isConnecting = false;

                            CXBlueTooth.getInstance().disconnect();

                            binding.rlConnectdevicename.setVisibility(View.GONE);
                            viewModel.observableList.clear();
                            for (int i=0;i<lists.size();i++){
                                PrintItemViewModel itemViewModel = new PrintItemViewModel(viewModel, lists.get(i));
                                viewModel.observableList.add(itemViewModel);
                            }
                            SharePreferecnceUtils.setConnectDevicee("");
//                    sendBroadcast(new Intent(Constants.ACTION_SPPDISCONNECT));
                        }
                        break;
                    case 3:
                        openBle();
                        break;

                }
            }
        });

        viewModel.itemEvent.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {

                if (isConnect) {
                    RxToast.normal(getString(R.string.just_connect_onedevice));

                } else {
                    if (isConnecting) {
                        RxToast.normal(getString(R.string.just_connect_onedevice));

                    } else {
                        if (lists.size() != 0) {
                            deviceScanBean = lists.get(integer);
                            if (deviceScanBean != null && deviceScanBean.getBluetoothDevice() != null) {
                                CXBlueTooth.getInstance().stopScan();
                                isConnecting = true;
                                CXBlueTooth.getInstance().disconnect();
                                CXBlueTooth.getInstance().conn(deviceScanBean.getBluetoothDevice());


                            }
                        }
                    }
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
        mContext = this;

        bind = ButterKnife.bind(this);

        //获取权限
        requestPermissions();



        BT = CXBlueTooth.getInstance();
        cxcomm = BT;
        cxcomm.recv(new IReceiveDataListener() {
            @Override
            public void onReceiveData(byte[] data) {
                //接收蓝牙发送的消息
                try {
                    int len = ByteUtil.returnActualLength(data);
                    byte [] actualData = new byte[len];
                    System.arraycopy(data,0,actualData,0,len);
                    String content = new String(actualData, "utf-8");
                    Log.e(TAG, "actualDataLen:"+ actualData.length + "content==" + content);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        BT.setBlueToothListener(new IBlueToothListener() {
            @Override
            public void onConnected() {
                mHandler.sendEmptyMessageDelayed(MSG_CONNECTED, 1000);
            }

            @Override
            public void onDisConnected() {
                mHandler.sendEmptyMessageDelayed(MSG_DISCONNECTED, 1000);
            }
            @Override
            public void onError() {
                mHandler.sendEmptyMessageDelayed(MSG_DISCONNECTED, 1000);
            }
        });

        registerReceiver(receiver, getIntentFilter());
        String source = SharePreferecnceUtils.getConectDevice();
        if (!TextUtils.isEmpty(source)) {
            boolean connected = BT.isConnected();
            if (connected) {
                binding.rlConnectdevicename.setVisibility(View.VISIBLE);
                
                binding.btnDisconnet.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_setting_check));
                
                binding.btnSenddata.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_setting_check));
                isConnect = true;
                try {
                    deviceScanBeanSharePre = new Gson().fromJson(source, DeviceScanBean.class);
                    binding.tvContent.setText(deviceScanBeanSharePre.getDeviceName() + "\r\n" + deviceScanBeanSharePre.getMacAddress());
                } catch (Exception e) {

                }
            } else {
                binding.rlConnectdevicename.setVisibility(View.GONE);
                binding.btnDisconnet.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_setting_normal));
                binding.btnSenddata.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_setting_normal));
            }
        }
    }

    private void openBle() {
        if (CXBlueTooth.getInstance().isBlueToothOpen()) {
            startScan();
        } else {
            Log.e("openBle", "false");
            startActivityForResult((new Intent(this, OpenBleActivity.class)),
                    Constants.REQUESTCODE_OPEN_BLE);
        }
    }


    //新建一个IntentFilter
    private IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        return intentFilter;
    }


    /**
     * 获取权限
     */
    private void requestPermissions() {

        PermissionX.init(this)
                .permissions(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION)
                .request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList) {
                        if (allGranted) {
                            openBle();
                        } else {
                            RxToast.normal("权限被拒绝，无法使用此功能");
                        }
                    }
                });
    }


    public void startScan() {
        CXBlueTooth.getInstance().scanDevices();
        Set<BluetoothDevice> paireDevices = CXBlueTooth.getInstance().getBondedDevices();
        Log.e(TAG, "paireDevices SIZE==" + paireDevices.size());
        if (paireDevices.size() > 0) {
            A:
            for (BluetoothDevice device : paireDevices) {
                for (DeviceScanBean list : lists) {
                    if (list.getBluetoothDevice().getAddress().equals(device.getAddress())) {
                        break A;
                    }
                }
                if (!TextUtils.isEmpty(device.getName())) {
                    lists.add(new DeviceScanBean(device, 0, new byte[0]));
                }
            }
        }
        if (lists.size() != 0) {
            viewModel.observableList.clear();
            for (int i=0;i<lists.size();i++){
                PrintItemViewModel itemViewModel = new PrintItemViewModel(viewModel, lists.get(i));
                viewModel.observableList.add(itemViewModel);
            }

            binding.tvNone.setVisibility(View.GONE);
        }
        binding.ivRight.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.loading_animation));
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            binding.ivRight.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        mHandler.sendEmptyMessageDelayed(MSG_STOP_SCAN, 5000);
    }

    private void setConnected() {
        isConnecting = false;
        isConnect = true;

        if (deviceScanBean != null) {
            if (!TextUtils.isEmpty(deviceScanBean.getDeviceName()) && !TextUtils.isEmpty(deviceScanBean.getMacAddress()))
                binding.tvContent.setText(deviceScanBean.getDeviceName() + "\r\n" + deviceScanBean.getMacAddress());
            binding.rlConnectdevicename.setVisibility(View.VISIBLE);
            Log.e(TAG, "data==" + new Gson().toJson(deviceScanBean));
            SharePreferecnceUtils.setConnectDevicee(new Gson().toJson(deviceScanBean));
        }
        if (mContext != null) {
            binding.btnDisconnet.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_setting_check));
            binding.btnSenddata.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_setting_check));

            if (deviceScanBean != null) {
                setConnectList(deviceScanBean);
            }
        }
        CXBlueTooth.getInstance().setConnected(true);
    }

    private void setDisConected() {
        isConnecting = false;
        isConnect = false;

        CXBlueTooth.getInstance().setConnected(false);
    }

    public void dismissDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public void setConnectList(DeviceScanBean deviceScanBean) {
        for (int i = 0; i < lists.size(); i++) {
            if (deviceScanBean != null && lists.get(i) != null) {
                if (deviceScanBean.getMacAddress().equals(lists.get(i).getMacAddress())) {
                    viewModel.observableList.remove(i);
                }
            }
        }
    }



    private void sendEscposData() {
        CXEscpos cxEscpos = CXEscpos.getInstance(cxcomm, 0);
        /*初始化打印机*/
        cxEscpos.InitializePrinter();
        /*走纸开关*/
        cxEscpos.PrintAndFeedLines( (byte) 3 );
        /* 设置打印居中 */
        cxEscpos.SelectJustification( CXEscpos.JUSTIFICATION.CENTER );
        /* 设置为倍高倍宽 */
        cxEscpos.SelectPrintModes( CXEscpos.FONT.FONTA, CXEscpos.ENABLE.OFF, CXEscpos.ENABLE.ON, CXEscpos.ENABLE.ON, CXEscpos.ENABLE.OFF );
        /* 打印文字 */
        cxEscpos.Text( "有物智能科技\n" );
        /* 打印并换行*/
        cxEscpos.PrintAndLineFeed();
        /* 打印文字 */
        /* 取消倍高倍宽 */
        cxEscpos.SelectPrintModes( CXEscpos.FONT.FONTA, CXEscpos.ENABLE.OFF, CXEscpos.ENABLE.OFF, CXEscpos.ENABLE.OFF, CXEscpos.ENABLE.OFF );
        /* 设置打印居中对齐 */
        cxEscpos.SelectJustification( CXEscpos.JUSTIFICATION.CENTER );
        /* 打印文字 */
        cxEscpos.Text( "---------------------------------\n" );


        /* 打印文字 */
        cxEscpos.Text( getDate()+"\n" );
        /* 打印繁体中文 需要打印机支持繁体字库 */
        String message = "宸芯票據打印機\n";
        cxEscpos.Text( message );
        cxEscpos.PrintAndLineFeed();
        /* 打印文字 */
        cxEscpos.Text( "---------------------------------\n" );
        /* 设置打印左对齐 */
        cxEscpos.SelectJustification( CXEscpos.JUSTIFICATION.LEFT );

        cxEscpos.Text( "序号   商品货号    数量    金额\n" );
        cxEscpos.PrintAndLineFeed();

        for (int i=0;i<11;i++){

            if (i+1<10){
                cxEscpos.Text( "0"+(i+1)+".    A521936     0"+(i+1)+"     2600.0" );
                cxEscpos.PrintAndLineFeed();
            }else {
                cxEscpos.Text( ""+(i+1)+".    A521936     "+(i+1)+"     2600.0" );
                cxEscpos.PrintAndLineFeed();
            }

        }

//        /* 绝对位置  */
//        cxEscpos.Text( "智能" );
//        cxEscpos.SetHorAndVerMotionUnits( (byte) 7, (byte) 8 );
//        cxEscpos.SetAbsolutePrintPosition( (short) 1 );
//        cxEscpos.Text( "网络" );
//        cxEscpos.SetAbsolutePrintPosition( (short) 20 );
//        cxEscpos.Text( "设备" );
//        cxEscpos.PrintAndLineFeed();
        /* 设置打印居中对齐 */

        /* 打印文字 */      /* 打印文字 */
        cxEscpos.Text( "\n" );
        cxEscpos.Text( "---------------------------------" );
        /* 设置打印右对齐 */
        cxEscpos.SelectJustification( CXEscpos.JUSTIFICATION.RIGHT );

        cxEscpos.Text( "合计：12600.0\n" );
        cxEscpos.PrintAndLineFeed();
        /* 打印文字 */
//        cxEscpos.Text( "Print bitmap!\n" );
//        /* 打印图片 */
//        Bitmap b = BitmapFactory.decodeResource( getResources(),
//                R.mipmap.logo_1 );
//        /* 打印图片 */
//        cxEscpos.Bitmap( 100, 100, 100,100, b,CXEscpos.BITMAP_MODE.MODE0.getValue());
//
//        /* 打印一维条码 */
//        /* 打印文字 */
//        cxEscpos.Text( "Print code128\n" );
//        cxEscpos.addSelectPrintingPositionForHRICharacters( CXEscpos.HRI_POSITION.BELOW );
//        /*
//         * 设置条码可识别字符位置在条码下方
//         * 设置条码高度为60点
//         */
//        cxEscpos.addSetBarcodeHeight( (byte) 60 );
//        /* 设置条码单元宽度为1 */
//        cxEscpos.addSetBarcodeWidth( (byte) 1 );
//        /* 打印Code128码 */
//        cxEscpos.addCODE128( cxEscpos.genCodeB( "CXPrinter" ) );
//        cxEscpos.PrintAndLineFeed();
//
//        /*
//         * QRCode命令打印 此命令只在支持QRCode命令打印的机型才能使用。 在不支持二维码指令打印的机型上，则需要发送二维条码图片
//         */
//        /* 打印文字 */
//        cxEscpos.Text( "---------------------------------\n" );
        cxEscpos.SelectJustification( CXEscpos.JUSTIFICATION.CENTER );
        /* 打印文字 */
        cxEscpos.Text( "Print QRcode\n" );
        /* 设置纠错等级 */
        cxEscpos.addSelectErrorCorrectionLevelForQRCode( (byte) 0x31 );
        /* 设置qrcode模块大小 */
        cxEscpos.addSelectSizeOfModuleForQRCode( (byte) 6 );
        /* 设置qrcode内容 */
        cxEscpos.addStoreQRCodeData( "www.baidu.com" );

        cxEscpos.addPrintQRCode(); /* 打印QRCode */
        cxEscpos.PrintAndLineFeed();

        /* 设置打印居中对齐 */
        cxEscpos.SelectJustification( CXEscpos.JUSTIFICATION.CENTER );
        /* 打印文字 */
        cxEscpos.Text( "谢谢惠顾，欢迎再次光临!\r\n" );
        cxEscpos.PrintAndFeedLines( (byte) 8 );
    }


    private static final int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUESTCODE_OPEN_BLE) {
            if (resultCode == Constants.RESULTCODE_OPEN_BLE) {
                if (!CXBlueTooth.getInstance().isBlueToothOpen()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            } else if (resultCode == Constants.RESULTCODE_CLOSE_BLE) {
                finish();
            }
        } else if (requestCode == REQUEST_ENABLE_BT) {
            if (requestCode == REQUEST_ENABLE_BT) {
                if (resultCode == RESULT_OK) {
                    startScan();
                }
                if (resultCode == RESULT_CANCELED) {
                    finish();
                }
            } else {
                finish();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
        mContext = null;
        mHandler.removeMessages(MSG_START_SCAN);
        mHandler.removeMessages(MSG_STOP_SCAN);
        CXBlueTooth.getInstance().stopScan();
        unregisterReceiver(receiver);
        if (binding.ivRight != null) {
            binding.ivRight.clearAnimation();
        }
    }

}
