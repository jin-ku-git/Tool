package com.youwu.tool.ui.cabinet.dialog;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.lxj.xpopup.core.CenterPopupView;
import com.xuexiang.xui.widget.alpha.XUIAlphaButton;
import com.youwu.tool.R;
import com.youwu.tool.ui.cabinet.adapter.GroupRecycleAdapter;
import com.youwu.tool.ui.cabinet.bean.TableItemBean;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class GroupPop extends CenterPopupView {

    RecyclerView recyclerView;

    ImageView ivClose;
    List<TableItemBean> data=new ArrayList<>();
    ClickInterface clickInterface;

    GroupRecycleAdapter adapter;
    XUIAlphaButton all_button;

    public void setClickInterface(ClickInterface clickInterface) {
        this.clickInterface = clickInterface;
    }

    public GroupPop(@NonNull Context context, List<TableItemBean> data) {
        super(context);
        this.data=data;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_group;
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        recyclerView=findViewById(R.id.recyclerView);
        ivClose=findViewById(R.id.iv_close);
        all_button=findViewById(R.id.all_button);
        //关闭
        ivClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        //全部打开
        all_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickInterface!=null){
                    clickInterface.Allclick(data.get(0).getTopic());
                }
            }
        });
        adapter=new GroupRecycleAdapter(getContext(),data);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new GroupRecycleAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, TableItemBean data,int position) {
                if (clickInterface!=null){
                    clickInterface.click(position,data);
                }
            }
        });
    }


    public interface ClickInterface{
        void click(int position, TableItemBean bean);
        void Allclick(String topic);

    }
}
