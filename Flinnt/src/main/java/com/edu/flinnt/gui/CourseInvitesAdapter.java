package com.edu.flinnt.gui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.protocol.InvitationItem;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.SelectableRoundedImageView;

import java.io.File;
import java.util.ArrayList;

/**
 * Course invites mContentsAdapter class
 */
public class CourseInvitesAdapter extends RecyclerView.Adapter<CourseInvitesAdapter.ViewHolder> {

    private ArrayList<InvitationItem> mDataset;
    private ArrayList<InvitationItem> filteredDataset;
    private ImageLoader mImageLoader;
    private String mCoursePicUrl = "";
    private String mUserPicUrl = "";
    private String mCourseUserPicUrl = "";
    OnButtonClickListener mButtonClickListener;
    private Context mContext;


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView courseName;
        public TextView dateText;
        public TextView userName;
        public TextView labelText;
        public TextView userProfile;
        public SelectableRoundedImageView courseImage;
        public Button rejectBtn;
        public Button acceptBtn;


        public ViewHolder(View itemView) {
            super(itemView);

            courseName = (TextView) itemView.findViewById(R.id.course_name_invites_text);
            dateText = (TextView) itemView.findViewById(R.id.course_invites_date_text);
            userName = (TextView) itemView.findViewById(R.id.course_invite_by_name_text);
            labelText = (TextView) itemView.findViewById(R.id.label_text);
            userProfile = (TextView) itemView.findViewById(R.id.text_view_profile);
            courseImage = (SelectableRoundedImageView) itemView.findViewById(R.id.course_invite_image);
            rejectBtn = (Button) itemView.findViewById(R.id.reject_invited_course_btn);
            acceptBtn = (Button) itemView.findViewById(R.id.accept_invited_course_btn);

            rejectBtn.setOnClickListener(this);
            acceptBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mButtonClickListener != null) {
                mButtonClickListener.onButtonClick(v, getAdapterPosition());
            }
        }

    }

    public void add(int position, InvitationItem item) {
        filteredDataset.add(position, item);
        notifyItemInserted(position);
    }

    public void addItems(ArrayList<InvitationItem> items) {
        //filteredDataset.clear();
        filteredDataset.addAll(items);
        notifyDataSetChanged();
    }

    public void clearData() {
        filteredDataset.clear();
        notifyDataSetChanged();
    }

    public void remove(InvitationItem item) {
        int position = filteredDataset.indexOf(item);
        if (position > -1) {
            filteredDataset.remove(position);
            notifyItemRemoved(position);
        }
    }

    public CourseInvitesAdapter(Context context, ArrayList<InvitationItem> myDataset) {
        this.mContext = context;
        filteredDataset = myDataset;
        mDataset = filteredDataset;
        mImageLoader = Requester.getInstance().getImageLoader();
    }

    public InvitationItem getItem(int position) {
        if (position >= 0 && position < filteredDataset.size()) {
            return filteredDataset.get(position);
        } else return null;
    }

    @SuppressLint("InflateParams")
    @Override
    public CourseInvitesAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.course_invites_list_item, null);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int i) {

        final InvitationItem invitation = filteredDataset.get(i);

        holder.courseName.setText(invitation.getCourseName());
        holder.dateText.setText(Helper.formateTimeMillis(Long.parseLong(invitation.getInvitationDate())));
        holder.userName.setText(invitation.getUserName());

        String url = getCourseProfilePictureUrl() + Flinnt.PROFILE_LARGE + File.separator + invitation.getCourseUserPicture();
        final String userUrl = getmUserPicUrl() + Flinnt.PROFILE_LARGE + File.separator + invitation.getUserPicture();
        if (invitation.getIsRequest().equals("1")) {
            holder.labelText.setText("Requested by: ");
            holder.userProfile.setVisibility(View.VISIBLE);
            mImageLoader.get(userUrl, ImageLoader.getImageListener(holder.courseImage, R.drawable.default_course_image, R.drawable.default_course_image));
        } else {
            holder.labelText.setText("Invited by: ");
            holder.userProfile.setVisibility(View.GONE);
            mImageLoader.get(url, ImageLoader.getImageListener(holder.courseImage, R.drawable.default_course_image, R.drawable.default_course_image));
        }


        holder.courseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (invitation.getIsRequest().equals("1")) {
                    ((CourseInvitesActivity) mContext).bottomSheetClickEvent(userUrl, invitation.getUserModInviteNote(),invitation.getCourseName(), invitation.getRequestUserId());
                }
            }
        });

    }

    public int getItemCount() {
        return filteredDataset.size();
    }

    public String getCoursePicUrl() {
        return mCoursePicUrl;
    }

    public void setCoursePicUrl(String mCoursePicUrl) {
        this.mCoursePicUrl = mCoursePicUrl;
    }

    public String getmUserPicUrl() {
        return mUserPicUrl;
    }

    public void setmUserPicUrl(String mUserPicUrl) {
        this.mUserPicUrl = mUserPicUrl;
    }

    public String getCourseProfilePictureUrl() {
        return mCourseUserPicUrl;
    }

    public void setCourseProfilePictureUrl(String mCourseUserPicUrl) {
        this.mCourseUserPicUrl = mCourseUserPicUrl;
    }

    public interface OnButtonClickListener {
        public void onButtonClick(View view, int position);
    }

    public void setOnButtonClickListener(
            final OnButtonClickListener buttonClickListener) {
        this.mButtonClickListener = buttonClickListener;
    }



}
