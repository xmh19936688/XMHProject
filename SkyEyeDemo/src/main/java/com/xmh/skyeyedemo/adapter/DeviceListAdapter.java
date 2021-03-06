package com.xmh.skyeyedemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.xmh.skyeyedemo.R;
import com.xmh.skyeyedemo.activity.VideoListActivity;
import com.xmh.skyeyedemo.activity.CallActivity;
import com.xmh.skyeyedemo.bean.UserBmobBean;
import com.xmh.skyeyedemo.utils.ContactUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mengh on 2016/2/26 026.
 */
public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.EyeViewHolder> {

    private Context mContext;
    private View mSnackbarContainer;

    private List<String> mEyeNameList =new ArrayList<>();
    private Map<String,UserBmobBean> mEyeUserMap=new HashMap<>();

    public DeviceListAdapter(Context context, View snackbarContainer){
        mContext=context;
        mSnackbarContainer=snackbarContainer;
    }

    public void setEyeList(List<String> list){
        mEyeNameList.clear();
        if(list!=null&&!list.isEmpty()){
            mEyeNameList.addAll(list);
        }
        notifyDataSetChanged();
        //region 根据用户名获取用户完整信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(final String name: mEyeNameList){
                    ContactUtil.pullContactInfoWithUsername(mContext, name, new ContactUtil.OnGetUserInfoListener() {
                        @Override
                        public void onGetUserInfo(UserBmobBean userBmobBean) {
                            mEyeUserMap.put(name, userBmobBean);
                            notifyDataSetChanged();
                        }
                    });
                }
            }
        }).start();
        //endregion
    }

    @Override
    public EyeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.layout_device_item, parent, false);
        return new EyeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final EyeViewHolder holder, int position) {
        final String username = mEyeNameList.get(position);
        //region init layout
        UserBmobBean userBmobBean = mEyeUserMap.get(username);
        if(userBmobBean!=null){
            holder.bean=userBmobBean;
            holder.tvDeviceName.setText(userBmobBean.getNickName());
        }
        holder.rlEdit.setVisibility(View.GONE);
        holder.llControl.setVisibility(View.VISIBLE);
        //endregion
        //region init click
        holder.tvDeviceName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.bean == null) {
                    //数据未请求下来，不理会点击事件
                    return;
                }
                //点击开启视频请求
                if (!EMChatManager.getInstance().isConnected())
                    Snackbar.make(mSnackbarContainer, R.string.network_isnot_available, Snackbar.LENGTH_SHORT).show();
                else{
                    Intent intent = new Intent(mContext, CallActivity.class);
                    intent.putExtra(CallActivity.EXTRA_TAG_EYE_BEAN, mEyeUserMap.get(username));
                    mContext.startActivity(intent);
                }
                holder.rlEdit.setVisibility(View.GONE);
            }
        });
        holder.btnChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.rlEdit.getVisibility()==View.GONE){
                    holder.etName.setText(holder.bean.getNickName());
                    holder.etName.requestFocus();
                    holder.rlEdit.setVisibility(View.VISIBLE);
                    holder.tvDeviceName.setVisibility(View.GONE);
                }else {
                    holder.rlEdit.setVisibility(View.GONE);
                    holder.tvDeviceName.setVisibility(View.VISIBLE);
                }
            }
        });
        holder.btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName=holder.etName.getText().toString().trim();
                if(TextUtils.isEmpty(newName)||newName.equals(holder.bean.getNickName())){
                    //未作修改则不作处理
                    holder.rlEdit.setVisibility(View.GONE);
                    holder.tvDeviceName.setVisibility(View.VISIBLE);
                    return;
                }
                //保存数据到服务器
                holder.bean.setNickName(newName);
                holder.bean.update(mContext);
                //更新UI
                holder.tvDeviceName.setText(newName);
                holder.rlEdit.setVisibility(View.GONE);
                holder.tvDeviceName.setVisibility(View.VISIBLE);
            }
        });
        holder.btnRealTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击开启视频请求
                if (!EMChatManager.getInstance().isConnected())
                    Snackbar.make(mSnackbarContainer, R.string.network_isnot_available, Snackbar.LENGTH_SHORT).show();
                else{
                    Intent intent = new Intent(mContext, CallActivity.class);
                    intent.putExtra(CallActivity.EXTRA_TAG_EYE_BEAN, mEyeUserMap.get(username));
                    mContext.startActivity(intent);
                }
                holder.rlEdit.setVisibility(View.GONE);
            }
        });
        holder.btnHistoryRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入历史记录界面
                Intent intent = new Intent(mContext, VideoListActivity.class);
                intent.putExtra(VideoListActivity.EXTRA_TAG_EYENAME, username);
                mContext.startActivity(intent);
                holder.rlEdit.setVisibility(View.GONE);
            }
        });
        //endregion
    }

    @Override
    public int getItemCount() {
        return mEyeNameList.size();
    }

    class EyeViewHolder extends RecyclerView.ViewHolder{

        public UserBmobBean bean;

        public TextView tvDeviceName;

        public Button btnChangeName;
        public Button btnRealTime;
        public Button btnHistoryRecord;
        public LinearLayout llControl;

        public Button btnSure;
        public EditText etName;
        public RelativeLayout rlEdit;

        public EyeViewHolder(View itemView) {
            super(itemView);
            tvDeviceName = (TextView) itemView.findViewById(R.id.tv_device_name);

            btnChangeName= (Button) itemView.findViewById(R.id.btn_change_name);
            btnRealTime= (Button) itemView.findViewById(R.id.btn_real_time);
            btnHistoryRecord= (Button) itemView.findViewById(R.id.btn_history);
            llControl = (LinearLayout) itemView.findViewById(R.id.ll_control);

            etName= (EditText) itemView.findViewById(R.id.et_name);
            btnSure= (Button) itemView.findViewById(R.id.btn_sure);
            rlEdit= (RelativeLayout) itemView.findViewById(R.id.rl_edit);
        }
    }

}
