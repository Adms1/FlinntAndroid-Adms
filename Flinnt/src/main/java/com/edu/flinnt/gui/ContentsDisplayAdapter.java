package com.edu.flinnt.gui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.protocol.ContentsResponse;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.contentlist.Sections;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import io.realm.RealmList;

/**
 * Created by flinnt-android-2 on 5/9/16.
 */
public class ContentsDisplayAdapter extends SectionedRecyclerViewAdapter<RecyclerView.ViewHolder>{
    private RealmList<Sections> mSectionList;
    private RealmList<Sections> filteredDataset;
    private Activity activity;
    private String mCourseId = "", mCourseName = "";
    ContentsResponse mContentsResponse;
    OnItemClickListener mItemClickListener;
    private int mVisibleSection,mVisibleContent;

    public ContentsDisplayAdapter(Activity activity, String courseId, String mCourseName, RealmList<Sections> sectionList) {
        this.activity = activity;
        this.mCourseId = courseId;
        this.mCourseName = mCourseName;
        mSectionList = new RealmList<Sections>();
        filteredDataset = new RealmList<Sections>();
        LogWriter.write("Response Section Data:" + sectionList);
        this.mSectionList.addAll(sectionList);
        this.filteredDataset.addAll(sectionList);
    }

    @Override
    public int getSectionCount() {
        if(mVisibleSection != 0 && filteredDataset.size() > mVisibleSection){
            return mVisibleSection;
        }
        return filteredDataset.size();
    }

    @Override
    public int getItemCount(int section) {
        if(mVisibleContent != 0 && filteredDataset.get(section).getContents().size() > mVisibleContent){
            return mVisibleContent;
        }
        return filteredDataset.get(section).getContents().size();
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int section) {
        String sectionName = filteredDataset.get(section).getTitle();
        ContentHeader contentHeader = (ContentHeader) holder;
        contentHeader.mResorceTitleTxt.setText(sectionName);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int section, final int relativePosition, int absolutePosition) {
        String itemName = filteredDataset.get(section).getContents().get(relativePosition).getTitle();

        ContentItem resorcesItem = (ContentItem) holder;

        if (mSectionList.get(section).getContents().size() == (relativePosition + 1)) {
            resorcesItem.mContentItemLinear.setBackgroundResource(R.drawable.content_bottom_boarder);
        } else {
            resorcesItem.mContentItemLinear.setBackgroundColor(Color.WHITE);
        }
        resorcesItem.mCItemTitleTxt.setText(itemName);

        String preview = filteredDataset.get(section).getContents().get(relativePosition).getPreview();
        if (preview.equals(Flinnt.ENABLED)) {
            resorcesItem.mContentPreviewTxt.setVisibility(View.VISIBLE);
        } else {
            resorcesItem.mContentPreviewTxt.setVisibility(View.GONE);
        }

        ((ContentItem) holder).mContentItemLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Helper.isConnected()) {
                    String preview = filteredDataset.get(section).getContents().get(relativePosition).getPreview();
                    if (preview != null) {
                        if (preview.equals("1")) {
                            Intent contentDetailIntent = new Intent(activity, ContentsDetailActivity.class);
                            contentDetailIntent.putExtra("course_id", mCourseId);
                            contentDetailIntent.putExtra("content_id", filteredDataset.get(section).getContents().get(relativePosition).getId());
                            contentDetailIntent.putExtra(Course.COURSE_NAME_KEY, mCourseName);
                            contentDetailIntent.putExtra("comeFrom", "BrowseCourseDescriptionActivity");
                            activity.startActivity(contentDetailIntent);
                        }
                    } else {
                        Helper.showNetworkAlertMessage(activity);
                    }
                }
            }
        });
    }

    @Override
    public int getItemViewType(int section, int relativePosition, int absolutePosition) {
        if (section == 1)
            return 0; // VIEW_TYPE_HEADER is -2, VIEW_TYPE_ITEM is -1. You can return 0 or greater.
        return super.getItemViewType(section, relativePosition, absolutePosition);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout;
        View v = null;
        if (viewType == VIEW_TYPE_HEADER) {
            layout = R.layout.bcourse_content_header_list_item;

            v = LayoutInflater.from(parent.getContext())
                    .inflate(layout, parent, false);
            return new ContentHeader(v);
        } else {
            layout = R.layout.bcourse_content_list_item;

            v = LayoutInflater.from(parent.getContext())
                    .inflate(layout, parent, false);
            return new ContentItem(v);
        }
    }


    public static class ContentHeader extends RecyclerView.ViewHolder {
        final TextView mResorceTitleTxt;

        public ContentHeader(View itemView) {
            super(itemView);
            mResorceTitleTxt = (TextView) itemView.findViewById(R.id.resorceTitleTxt);
        }
    }

    public class ContentItem extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mCItemTitleTxt;//, mDeviderTxt;
        ImageView mContentUnreadDot;
        TextView mContentPreviewTxt;
        LinearLayout mContentItemLinear;

        public ContentItem(View itemView) {
            super(itemView);
            mCItemTitleTxt = (TextView) itemView.findViewById(R.id.cItemTitleTxt);
            mContentUnreadDot = (ImageView) itemView.findViewById(R.id.content_unread_dot);
            mContentPreviewTxt = (TextView) itemView.findViewById(R.id.content_preview_text);
            mContentItemLinear = (LinearLayout) itemView.findViewById(R.id.contentItemLinear);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(
            final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void addItems(RealmList<Sections> sectionList, ContentsResponse mContentsResponse,int visibleSection,int visibleContent) {
        int positionStart = mSectionList.size() + 1; // position after last course added
        int size = sectionList.size();
        this.mVisibleSection = visibleSection;
        this.mVisibleContent = visibleContent;
        this.mContentsResponse = mContentsResponse;
        mSectionList.addAll(sectionList);
        size = filteredDataset.size() - (positionStart - 1);
        filteredDataset.addAll(sectionList);
        //notifyItemRangeInserted(positionStart, size);
        notifyDataSetChanged();

    }
}