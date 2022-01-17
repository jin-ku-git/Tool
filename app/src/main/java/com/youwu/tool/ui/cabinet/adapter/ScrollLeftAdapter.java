package com.youwu.tool.ui.cabinet.adapter;


import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.youwu.tool.R;
import com.youwu.tool.ui.cabinet.bean.StoreBean;

import java.util.ArrayList;
import java.util.List;

import static com.xuexiang.xui.utils.ResUtils.getResources;

/**
 * Created by Raul_lsj on 2018/3/22.
 */

public class ScrollLeftAdapter extends BaseQuickAdapter<StoreBean.DataBean, BaseViewHolder> {

    private List<TextView> tv = new ArrayList<>();

    public ScrollLeftAdapter(int layoutResId, @Nullable List<StoreBean.DataBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, StoreBean.DataBean item) {
        helper.setText(R.id.left_text, item.name)
                .addOnClickListener(R.id.item);
        //将左侧item中的TextView添加到集合中
        tv.add((TextView) helper.getView(R.id.left_text));
        //设置进入页面之后,左边列表的初始状态
        if (tv != null && getData() != null && tv.size() == getData().size()) {
            selectItem(0);
        }

        helper.getView(R.id.item).setSelected(true);
    }

    //传入position,设置左侧列表相应item高亮
    public void selectItem(int position) {
        for (int i = 0; i < getData().size(); i++) {
            if (position == i) {
                tv.get(i).setBackgroundColor(getResources().getColor(R.color.blue_color));
                tv.get(i).setTextColor(ContextCompat.getColor(mContext, R.color.white));

                //以下是指定某一个TextView滚动的效果
                tv.get(i).setEllipsize(TextUtils.TruncateAt.MARQUEE);
                tv.get(i).setFocusable(true);
                tv.get(i).setFocusableInTouchMode(true);
                tv.get(i).setMarqueeRepeatLimit(-1);
            } else {
                tv.get(i).setBackgroundColor(0xffffffff);
                tv.get(i).setTextColor(ContextCompat.getColor(mContext, R.color.black));

                //失去焦点则停止滚动
                tv.get(i).setEllipsize(TextUtils.TruncateAt.END);
                tv.get(i).setFocusable(false);
                tv.get(i).setFocusableInTouchMode(false);
                tv.get(i).setMarqueeRepeatLimit(0);
            }
        }
    }
}
