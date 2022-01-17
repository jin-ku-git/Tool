package com.youwu.tool.ui.cabinet.adapter;

import android.view.View;
import android.widget.CompoundButton;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.youwu.tool.R;
import com.youwu.tool.ui.cabinet.bean.CabinetBean;
import com.youwu.tool.ui.cabinet.bean.ScrollBean;
import com.youwu.tool.utils_view.RxToast;

import java.util.List;

/**
 * 2021/12/21
 */

public class ScrollRightAdapter extends BaseSectionQuickAdapter<ScrollBean, BaseViewHolder> {

    public ScrollRightAdapter(int layoutResId, int sectionHeadResId, List<ScrollBean> data) {
        super(layoutResId, sectionHeadResId, data);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, ScrollBean item) {
        helper.setText(R.id.right_title, item.header);

        helper.setImageResource(R.id.store_unchecked_img,item.getCurrentSelect()==1?R.mipmap.checked_iv:R.mipmap.unchecked_iv);

        helper.setGone(R.id.store_unchecked_img,item.choiceSelect == 1);
        helper.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * item选择
                 */
                if (mStoreItemListener != null) {
                    mStoreItemListener.onStoreItem(item);
                }
            }
        });
    }

    @Override
    protected void convert(BaseViewHolder helper, ScrollBean item) {
        ScrollBean.ScrollItemBean t = item.t;
        helper.setText(R.id.right_text, t.getText());
        helper.setText(R.id.tv_name, t.getName());
        helper.setText(R.id.tv_location, t.getAddress());
        helper.setText(R.id.status_type, t.getStatus()==1?"使用中":"禁用中");
        helper.setBackgroundRes(R.id.status_type,t.getStatus()==1?R.drawable.jianbian_radius_green:R.drawable.jianbian_radius_red);
        helper.setImageResource(R.id.unchecked_img,t.getCurrentSelect()==1?R.mipmap.checked_iv:R.mipmap.unchecked_iv);

        helper.setVisible(R.id.unchecked_img, t.choiceSelect == 1);

        if (t.getChoiceSelect()==1){
            helper.itemView.setEnabled(true);
        }else {
            helper.itemView.setEnabled(false);
        }

        helper.setOnClickListener(R.id.open_text, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 打开操作
                 */
                if (mOpenListener != null) {
                    mOpenListener.onOpen(item.t.getWith_table());
                }
            }
        });

        helper.setOnClickListener(R.id.update_text, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * 更新操作
                 */
                if (mUpdateListener != null) {
                    mUpdateListener.onUpdate(item);
                }
            }
        });

        helper.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * item选择
                 */
                if (mItemListener != null) {
                    mItemListener.onItem(item);
                }

            }
        });


    }

    //打开的回调
    public interface OnOpenListener {

        void onOpen(List<CabinetBean.DataBean.WithTableBean> with_table);
    }

    public void setOnOpenListener(OnOpenListener listener) {
        mOpenListener = listener;
    }

    private OnOpenListener mOpenListener;

    //更新的回调
    public interface OnUpdateListener {
        void onUpdate(ScrollBean item);
    }

    public void setOnUpdateListener(OnUpdateListener listener) {
        mUpdateListener = listener;
    }

    private OnUpdateListener mUpdateListener;


    //item选择的回调
    public interface OnItemListener {
        void onItem(ScrollBean item);
    }

    public void setOnItemListener(OnItemListener listener) {
        mItemListener = listener;
    }

    private OnItemListener mItemListener;


    //门店item选择的回调
    public interface OnStoreItemListener {
        void onStoreItem(ScrollBean item);
    }

    public void setOnStoreItemListener(OnStoreItemListener listener) {
        mStoreItemListener = listener;
    }

    private OnStoreItemListener mStoreItemListener;

}
