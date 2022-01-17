package com.youwu.tool.ui.cabinet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.youwu.tool.R;
import com.youwu.tool.ui.cabinet.bean.TableItemBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 适配器
 */
public class GroupRecycleAdapter extends RecyclerView.Adapter<GroupRecycleAdapter.myViewHodler> {
    Context context;
    List<TableItemBean> mData;
    LayoutInflater inflater;

    public GroupRecycleAdapter(Context ctx, List<TableItemBean> data){
        context  = ctx;
        mData = data;
        inflater = LayoutInflater.from(context);
    }


    /**
     * 创建viewhodler，相当于listview中getview中的创建view和viewhodler
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public myViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        //创建自定义布局
        View itemView = View.inflate(context, R.layout.dialog_item_group, null);

        return new myViewHodler(itemView);
    }

    /**
     * 绑定数据，数据与view绑定
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(myViewHodler holder, int position) {
        //根据点击位置绑定数据
        TableItemBean data = mData.get(position);
//        holder.mItemGoodsImg;
        holder.tvName.setText(data.getCabinet_number());


    }


    /**
     * 得到总条数
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return mData.size();
    }

    //自定义viewhodler
    class myViewHodler extends RecyclerView.ViewHolder {

        TextView tvName;
        RelativeLayout layout;





        public myViewHodler(View itemView) {
            super(itemView);

            tvName=itemView.findViewById(R.id.tvName);//


            //点击事件放在adapter中使用，也可以写个接口在activity中调用
            //方法一：在adapter中设置点击事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //可以选择直接在本位置直接写业务处理
                    //Toast.makeText(context,"点击了xxx",Toast.LENGTH_SHORT).show();
                    //此处回传点击监听事件
                    if(onItemClickListener!=null){
                        onItemClickListener.OnItemClick(v, mData.get(getLayoutPosition()),getLayoutPosition());
                    }
                }
            });

        }
    }

    /**
     * 设置item的监听事件的接口
     */
    public interface OnItemClickListener {
        /**
         * 接口中的点击每一项的实现方法，参数自己定义
         *
         * @param view 点击的item的视图
         * @param data 点击的item的数据
         */
        public void OnItemClick(View view, TableItemBean data,int position);
    }

    //需要外部访问，所以需要设置set方法，方便调用
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}