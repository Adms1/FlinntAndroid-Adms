package com.edu.flinnt.downloads;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.edu.flinnt.R;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.gui.CommunicationFragment;
import com.edu.flinnt.helper.AskPermition;
import com.edu.flinnt.protocol.Post;
import com.edu.flinnt.util.Helper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<AppInfoDataSet> mAppInfoDataSets;
    private final ImageLoader mImageLoader;
    private OnItemClickListener mListener;
    CommunicationFragment mCommunicationFragment;
    Post mPost;
    Context context;
    int postPosition;

    public RecyclerViewAdapter(Context context, CommunicationFragment mCommunicationFragment, Post post, int pPosition) {
        mImageLoader = Requester.getInstance().getImageLoader();
        this.mAppInfoDataSets = new ArrayList<AppInfoDataSet>();
        this.context = context;
        this.mCommunicationFragment = mCommunicationFragment;
        mPost = post;
        postPosition = pPosition;

    }


    public RecyclerViewAdapter(Context context) {
        mImageLoader = Requester.getInstance().getImageLoader();
        this.mAppInfoDataSets = new ArrayList<AppInfoDataSet>();
        this.context = context;
    }


    public void setData(List<AppInfoDataSet> appInfoDataSets) {
        this.mAppInfoDataSets.clear();
        this.mAppInfoDataSets.addAll(appInfoDataSets);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_details_item, parent, false);
        final AppViewHolder holder = new AppViewHolder(itemView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        bindData((AppViewHolder) holder, position);
    }

    @Override
    public int getItemCount() {
        return mAppInfoDataSets.size();
    }

    private void bindData(final AppViewHolder holder, final int position) {
        final AppInfoDataSet appInfoDataSet = mPost.appInfoDataSets.get(position);
        holder.albumImg.setDefaultImageResId(R.drawable.album_default_new);
        holder.albumImg.setImageUrl(appInfoDataSet.getImage(), mImageLoader);
        holder.albumProgressBar.setProgress(appInfoDataSet.getProgress());
        holder.albumProgressBarHint.setVisibility(View.GONE);
        if (Helper.isFileExistsAtPath(Helper.getFlinntImagePath(), mAppInfoDataSets.get(position).getName()) && appInfoDataSet.getStatusText().equals("Not Download")) {
            holder.albumFrame.setVisibility(View.GONE);
        } else {
            if (appInfoDataSet.getStatusText().equals("Complete")) {
                holder.albumFrame.setVisibility(View.GONE);
            } else if (appInfoDataSet.getStatusText().equals("Not Download")) {
//              ((AlbumDetailActivity) context).download(position, appInfoDataSet.getUrl(), appInfoDataSet);
                holder.albumFrame.setVisibility(View.VISIBLE);
                holder.downloadBtn.setBackgroundResource(R.drawable.ic_download);
            } else if (appInfoDataSet.getStatusText().equals("Connecting")) {
                holder.albumProgressBarHint.setVisibility(View.VISIBLE);
            } else if (appInfoDataSet.getStatusText().equals("Downloading")) {
                holder.albumFrame.setVisibility(View.VISIBLE);
                holder.downloadBtn.setBackgroundResource(R.drawable.ic_pause);
                holder.albumProgressBar.setProgress(appInfoDataSet.getProgress());
            } else if (appInfoDataSet.getStatusText().equals("Pause")) {
                holder.albumFrame.setVisibility(View.VISIBLE);
                holder.downloadBtn.setBackgroundResource(R.drawable.ic_play);
            } else if (appInfoDataSet.getStatusText().equals("Resume")) {
                holder.albumFrame.setVisibility(View.VISIBLE);
                holder.downloadBtn.setBackgroundResource(R.drawable.ic_play);
                holder.albumProgressBar.setProgress(appInfoDataSet.getProgress());
            }
        }


        holder.albumFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helper.isConnected()) {
                    if (AskPermition.getInstance(context).isPermitted()) {
                        holder.albumProgressBarHint.setVisibility(View.VISIBLE);
                        int finalPosition = postPosition * position;
                        if (appInfoDataSet.getStatus() == AppInfoDataSet.STATUS_DOWNLOADING || appInfoDataSet.getStatus() == AppInfoDataSet.STATUS_CONNECTING) {
                            mCommunicationFragment.pause(postPosition, appInfoDataSet.getUrl(), mPost, "album", position);
                        } else {
                            mCommunicationFragment.download(postPosition, appInfoDataSet.getUrl(), mPost, "album", position);
                        }
                    }
                } else {
                    Helper.showNetworkAlertMessage(context);
                }
            }
        });


        holder.albumImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (AskPermition.getInstance(context).isPermitted()) {
                    try {
                        String filename = Helper.getFlinntImagePath() + mAppInfoDataSets.get(position).getName();
                        File file = new File(filename);
                        if (file.exists() && file.length() > 0) {
                            Bitmap bitmap = Helper.getBitmapFromSDcard(Helper.getFlinntImagePath(), mAppInfoDataSets.get(position).getName());
                            if (bitmap != null) {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.fromFile(new File(filename)), "image/*");
                                context.startActivity(intent);
                            } else {
                                //   Helper.showAlertMessage(context, "Image", "This image is not downloaded yet", "Ok");
                            }
                        } else {
                            //  Helper.showAlertMessage(context, "Image", "This image is not downloaded yet", "Ok");
                        }

                    } catch (Exception e) {
                    }
                }
            }
        });

    }

    public static final class AppViewHolder extends RecyclerView.ViewHolder {
        public FrameLayout albumFrame;
        public NetworkImageView albumImg;
        public ImageView downloadBtn;
        public com.github.lzyzsd.circleprogress.DonutProgress albumProgressBar;
        public ProgressBar albumProgressBarHint;

        public AppViewHolder(View v) {
            super(v);
            albumFrame = (FrameLayout) v.findViewById(R.id.albumFrame);
            albumImg = (NetworkImageView) v.findViewById(R.id.albumImg);
            downloadBtn = (ImageView) v.findViewById(R.id.downloadBtn);
            albumProgressBar = (com.github.lzyzsd.circleprogress.DonutProgress) v.findViewById(R.id.albumProgressBar);
            albumProgressBar.setTextColor(R.color.gray);
            albumProgressBar.setTextSize((float) 5.0);
            albumProgressBarHint = (ProgressBar) v.findViewById(R.id.albumProgressBarHint);
        }
    }
}