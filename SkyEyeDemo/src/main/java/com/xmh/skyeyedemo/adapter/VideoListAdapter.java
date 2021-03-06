package com.xmh.skyeyedemo.adapter;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmh.skyeyedemo.R;
import com.xmh.skyeyedemo.activity.VideoPlayActivity;
import com.xmh.skyeyedemo.bean.FileBmobBean;
import com.xmh.skyeyedemo.utils.FileUtil;
import com.xmh.skyeyedemo.utils.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.listener.DownloadFileListener;

/**
 * Created by mengh on 2016/3/9 009.
 */
public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoViewHolder>{

    private Context mContext;
    private List<FileBmobBean> mFileList=new ArrayList<>();
    private View mSnackbarContainer;

    public VideoListAdapter(Context context,View snackbarContainer){
        mContext=context;
        mSnackbarContainer = snackbarContainer;
    }

    public void setVideoList(List<FileBmobBean> list){
        mFileList.clear();
        if(list!=null&&!list.isEmpty()){
            mFileList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_video_item, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final VideoViewHolder holder, int position) {
        holder.bean=mFileList.get(position);
        String str = holder.bean.getVideoFile().getFilename();
        str= FileUtil.parseDateFromFilename(str);
        holder.tvVideo.setText(str);
        holder.llControl.setVisibility(View.VISIBLE);
        //region set listener
        holder.tvVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放
                Intent intent = new Intent(mContext, VideoPlayActivity.class);
                intent.putExtra(VideoPlayActivity.EXTRA_TAG_VIDEO_URL,holder.bean);
                mContext.startActivity(intent);
            }
        });
        holder.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放
                Intent intent = new Intent(mContext, VideoPlayActivity.class);
                intent.putExtra(VideoPlayActivity.EXTRA_TAG_VIDEO_URL,holder.bean);
                mContext.startActivity(intent);
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.bean.getVideoFile().delete(mContext);
                mFileList.remove(holder.bean);
                notifyDataSetChanged();
            }
        });
        holder.btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(mContext);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage(mContext.getString(R.string.downloading));
                progressDialog.show();
                final String dstPath=FileUtil.getDownloadPath()+holder.bean.getVideoFile().getFilename();
                holder.bean.getVideoFile().download(mContext, new File(dstPath), new DownloadFileListener() {
                    @Override
                    public void onSuccess(String path) {
                        holder.btnDownload.post(new Runnable() {
                            @Override
                            public void run() {
                                LogUtil.e("xmh-download", dstPath);
                                progressDialog.dismiss();
                                //snackbar滑动消失
                                Snackbar.make(mSnackbarContainer,mContext.getString(R.string.download_path)+dstPath,Snackbar.LENGTH_INDEFINITE).setAction(R.string.open, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent();
                                        intent.setAction(Intent.ACTION_VIEW);
                                        Uri uri = Uri.fromFile(new File(dstPath));
                                        intent.setDataAndType(uri, "video/mp4");
                                        mContext.startActivity(intent);
                                    }
                                }).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        progressDialog.dismiss();
                        Snackbar.make(mSnackbarContainer,mContext.getString(R.string.download_failed)+dstPath,Snackbar.LENGTH_INDEFINITE).setAction(R.string.retry, new View.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
                            @Override
                            public void onClick(View v) {
                                holder.btnDownload.callOnClick();
                            }
                        }).show();
                    }
                });
            }
        });
        //endregion
    }

    @Override
    public int getItemCount() {
        return mFileList.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder{

        public FileBmobBean bean;

        public TextView tvVideo;

        public LinearLayout llControl;
        public Button btnPlay;
        public Button btnDownload;
        public Button btnDelete;

        public VideoViewHolder(View itemView) {
            super(itemView);
            tvVideo= (TextView) itemView.findViewById(R.id.tv_video);

            llControl= (LinearLayout) itemView.findViewById(R.id.ll_control);
            btnPlay= (Button) itemView.findViewById(R.id.btn_play);
            btnDownload= (Button) itemView.findViewById(R.id.btn_download);
            btnDelete= (Button) itemView.findViewById(R.id.btn_delete);
        }
    }
}
