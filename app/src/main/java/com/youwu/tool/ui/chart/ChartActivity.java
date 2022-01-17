package com.youwu.tool.ui.chart;


import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.RadioGroup;

import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.youwu.tool.BR;
import com.youwu.tool.R;
import com.youwu.tool.app.AppViewModelFactory;
import com.youwu.tool.databinding.ActivityAddressParsingBinding;
import com.youwu.tool.databinding.ActivityChartBinding;
import com.youwu.tool.ui.address.AddressParsingViewModel;
import com.youwu.tool.ui.chart.utils.BarChartUtils;
import com.youwu.tool.ui.chart.utils.LineChartUtils;
import com.youwu.tool.utils_view.StatusBarUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;


/**
 * 折线图/柱状图
 * 2021/12/12
 */

public class ChartActivity extends BaseActivity<ActivityChartBinding, ChartViewModel> {

    private int xLableCount = 7;
    private int xRangeMaximum = xLableCount - 1;


    private List<Entry> netLineList = new ArrayList<>();
    private List<BarEntry> netBarList = new ArrayList<>();
    private List<String> netDateList = new ArrayList<>();


    @Override
    public void initParam() {
        super.initParam();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_chart;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public ChartViewModel initViewModel() {
        //使用自定义的ViewModelFactory来创建ViewModel，如果不重写该方法，则默认会调用NetWorkViewModel(@NonNull Application application)构造方法
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getApplication());
        return ViewModelProviders.of(this, factory).get(ChartViewModel.class);
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

        //折线图初始化
        LineChartUtils.initChart(binding.line, true, false, false);
        //柱状图初始化
        BarChartUtils.initChart(binding.barChart, true, false, false);

        //设置数据
        setData();

        binding.rgSelector.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_one:
                        //近7天
                        setData();
                        break;
                    case R.id.rb_two:
                        //近30天
                        setDataFor30();
                        break;
                    default:
                        break;
                }
            }
        });
    }


    /**
     * 获取默认查询的时间(当前时间的前day天)
     */
    public static String formatDatas(int day) {
        Date dNow = new Date(); // 当前时间
        Date dBefore = new Date();
        Calendar calendar = Calendar.getInstance(); // 得到日历
        calendar.setTime(dNow);// 把当前时间赋给日历
        calendar.add(Calendar.DAY_OF_MONTH, -day); // 设置为前三天
        dBefore = calendar.getTime(); // 得到前三天的时间
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // 设置时间格式
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd"); // 设置时间格式
        String defaultStartDate = sdf.format(dBefore); // 格式化
        return defaultStartDate;

    }



    /**
     *    new Entry(x,y) x: 折线图中数据值的位置索引 y: 具体数据值
     */

    private void setData() {
        netLineList.clear();
        netBarList.clear();
        netDateList.clear();
        String s;
        SimpleDateFormat dateFormat1 = new SimpleDateFormat(
                "MM-dd");
        Calendar c = Calendar.getInstance();
        String currentDate = dateFormat1.format(c.getTime());
        netDateList.add(currentDate);
        for (int i = 1; i < 7; i++) {
            s = formatDatas(i);
            netDateList.add(s);
        }
        Collections.reverse(netDateList);

        float[] lineFloat = {11, 15, 16, 17, 16, 16, 12};
        for (int i = 0; i < netDateList.size(); i++) {
//            netLineList.add(new Entry((float) i, (float) Math.random() * 80));
            netLineList.add(new Entry((float) i, lineFloat[i]));
            netBarList.add(new BarEntry((float) i, lineFloat[i]));
        }

        xLableCount = (netDateList.size() + 3) > 7 ? 7 : (netDateList.size() + 3);
        xRangeMaximum = xLableCount - 1;

        LineChartUtils.setXAxis(binding.line, xLableCount, netDateList.size(), xRangeMaximum);
        LineChartUtils.notifyDataSetChanged(binding.line, netLineList, netDateList);
        BarChartUtils.setXAxis(binding.barChart, xLableCount, netDateList.size(), xRangeMaximum);
        BarChartUtils.notifyDataSetChanged(binding.barChart, netBarList, netDateList);

        //无数据
        //LineChartUtils.NotShowNoDataText(mBinding.lineChart);
        //BarChartUtils.NotShowNoDataText(mBinding.barChart);

    }

    private void setDataFor30() {
        netLineList.clear();
        netBarList.clear();
        netDateList.clear();
        String s2;
        SimpleDateFormat dateFormat2 = new SimpleDateFormat(
                "MM-dd");
        Calendar c2 = Calendar.getInstance();
        String currentDate2 = dateFormat2.format(c2.getTime());
        netDateList.add(currentDate2);
        for (int i = 1; i < 30; i++) {
            s2 = formatDatas(i);
            netDateList.add(s2);
        }
        Collections.reverse(netDateList);


        float[] lineFloat = {14, 15, 16, 17, 16, 16, 12, 14, 15, 16, 17, 16, 16, 12, 14, 15, 16, 17, 16, 16, 12, 14, 15, 16, 17, 16, 16, 12, 14, 15};
        for (int i = 0; i < netDateList.size(); i++) {

//            netLineList.add(new Entry((float) i, (float) Math.random() * 80));
//            netBarList.add(new BarEntry((float) i, (float) Math.random() * 80));
            netLineList.add(new Entry((float) i,  lineFloat[i]));
            netBarList.add(new BarEntry((float) i,  lineFloat[i]));

        }

        xLableCount = (netDateList.size() + 3) > 7 ? 7 : (netDateList.size() + 3);
        xRangeMaximum = xLableCount - 1;

        LineChartUtils.setXAxis(binding.line, xLableCount, netDateList.size(), xRangeMaximum);
        LineChartUtils.notifyDataSetChanged(binding.line, netLineList, netDateList);
        BarChartUtils.setXAxis(binding.barChart, xLableCount, netDateList.size(), xRangeMaximum);
        BarChartUtils.notifyDataSetChanged(binding.barChart, netBarList, netDateList);

        //无数据
        //LineChartUtils.NotShowNoDataText(mBinding.lineChart);
        //BarChartUtils.NotShowNoDataText(mBinding.barChart);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}
