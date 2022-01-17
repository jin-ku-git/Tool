package com.youwu.tool.ui.cabinet.dialog;

import android.content.Context;
import android.text.InputFilter;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.CenterPopupView;
import com.xuexiang.xui.widget.alpha.XUIAlphaButton;
import com.xuexiang.xui.widget.button.switchbutton.SwitchButton;
import com.youwu.tool.R;
import com.youwu.tool.popwindow.MDPopupView;
import com.youwu.tool.ui.cabinet.bean.CabinetItemBean;
import com.youwu.tool.ui.cabinet.bean.HeatBean;
import com.youwu.tool.ui.cabinet.bean.StoreBean;
import com.youwu.tool.utils_view.AnimationUtil;
import com.youwu.tool.utils_view.DegreeSelectView;
import com.youwu.tool.utils_view.MoneyInputFilter;
import com.youwu.tool.utils_view.RxToast;
import com.youwu.tool.utils_view.TimeSelectView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.utils.KLog;

import static me.goldze.mvvmhabit.base.BaseActivity.subZeroAndDot;


public class CabinetDialog extends CenterPopupView {


    LinearLayout top_layout;
    LinearLayout heat_layout;

    XUIAlphaButton restart;

    SwitchButton cabinet_jr;//柜子加热
    SwitchButton cabinet_zgd;//紫光灯
    DegreeSelectView degree_select;
    TimeSelectView time_select;
    TimeSelectView time_select_two;


    boolean jr_isChecked=false;//加热
    boolean zgd_isChecked=false;//紫光灯


    Context mContext;
    String topic;//选择的id
    public int type;//1 指定 2 全部

    public CabinetDialog(int type, @NonNull Context context,String topic) {
        super(context);
        this.mContext=context;
        this.type = type;
        this.topic = topic;
    }



    @Override
    protected void onCreate() {
        super.onCreate();

        cabinet_jr=findViewById(R.id.cabinet_jr);//柜子加热
        cabinet_zgd=findViewById(R.id.cabinet_zgd);//紫光灯
        degree_select=findViewById(R.id.degree_select);//温度
        time_select=findViewById(R.id.time_select);//时间
        time_select_two=findViewById(R.id.time_select_two);//时间two

        heat_layout=findViewById(R.id.heat_layout);

        top_layout=findViewById(R.id.top_layout);
        restart=findViewById(R.id.restart);//重启


        //柜子加热监听
        cabinet_jr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                jr_isChecked=isChecked;
                if (isChecked){
                    //显示动画
                    AnimationUtil.visibleAnimator(heat_layout);
                }else {
                    //隐藏动画
                    AnimationUtil.invisibleAnimator(heat_layout);
                }
            }
        });
        //紫光灯监听
        cabinet_zgd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                zgd_isChecked=isChecked;

                if (mBlbListener!=null){
                    if (zgd_isChecked){
                        KLog.d("type:"+type+"topic1："+topic);
                        mBlbListener.onBlb(type,1,type==1?topic:"");
                    }else {
//                        KLog.d("topic2："+(type==1?topic:""));
                        KLog.d("type:"+type+"topic1："+topic);
                        mBlbListener.onBlb(type,2,type==1?topic:"");
                    }

                }
            }
        });

        //确定
        findViewById(R.id.determine).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                String submitJson = new Gson().toJson(beans);

//                RxToast.normal("加热:"+jr_isChecked+",\ntype:"+type+",\ntopic："+(type==1?topic:"")+",\n开始温度:"+degree_select.getStart()+",\n结束温度："+degree_select.getEnd()+
//                        ",\n开始时间："+time_select.getStart_second()+",\n结束时间："+time_select.getEnd_second()+
//                        ",\n开始时间two："+time_select_two.getStart_second()+",\n结束时间two："+time_select_two.getEnd_second()+
//                        "，\n紫光灯："+zgd_isChecked);

                initupload();
//                dismiss();
            }
        });

        restart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                RxToast.normal("重启");
                if (mRestartListener!=null){
                    mRestartListener.onRestart(topic,type);
                }

            }
        });

    }
    //确认信息
    private void initupload() {
        HeatBean heatBean=new HeatBean();

        if (jr_isChecked){
            heatBean.topic=topic;
            heatBean.type=type;
            heatBean.b_heat=Integer.parseInt(degree_select.getEnd());
            heatBean.t_heat=Integer.parseInt(degree_select.getStart());
            heatBean.start_time=time_select.getStart_second();
            heatBean.end_time=time_select.getEnd_second();
            heatBean.class_type=1;

        }else {
            heatBean.topic=topic;
            heatBean.type=type;
            heatBean.class_type=2;
        }
        /**
         * 确认的回调
         */
        if (mHeatListener!=null){
            mHeatListener.onHeat(heatBean);
        }
    }




    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_cabinet;
    }


    //确定的回调
    public interface OnHeatListener {
        void onHeat(HeatBean bean);
    }

    public void setOnBeanListener(OnHeatListener listener) {
        mHeatListener = listener;
    }

    private OnHeatListener mHeatListener;

    //重启的回调
    public interface OnRestartListener {
        void onRestart(String topic,int type);
    }

    public void setOnRestartListener(OnRestartListener listener) {
        mRestartListener = listener;
    }

    private OnRestartListener mRestartListener;



    //紫光灯的回调
    public interface OnBlbListener {
        /**
         *
         * @param type   1指定 2 全部
         * @param class_type  1开启紫光灯，2关闭紫光灯
         * @param topic  多个以逗号分隔
         */
        void onBlb(int type,int class_type,String topic);
    }

    public void setOnBlbListener(OnBlbListener listener) {
        mBlbListener = listener;
    }

    private OnBlbListener mBlbListener;


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
