package com.edu.flinnt.gui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.FlinntApplication;
import com.edu.flinnt.R;
import com.edu.flinnt.database.ContentDetailsInterface;
import com.edu.flinnt.protocol.ContentsResponse;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.contentlist.Contents;
import com.edu.flinnt.protocol.contentlist.Sections;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * @author Aidan Follestad (afollestad)
 */
public class ContentsAdapter extends SectionedRecyclerViewAdapter<RecyclerView.ViewHolder> {
    private RealmList<Sections> mSectionList;
    private RealmList<Sections> filteredDataset;
    private boolean isSearchMode;
    private String searchQuery = "", mCourseName = "";

    OnItemClickListener mItemClickListener;
    private Activity activity;
    private String mCourseId = "";
    private int contentSectionPosition, contentItemPosition;
    ContentsFragment contentsFragment;
    private Realm myRealm;

    public ContentsAdapter(Activity activity, ContentsFragment contentsFragment, String courseId, String courseName, RealmList<Sections> sectionList) {
        this.contentsFragment = contentsFragment;
        this.activity = activity;
        this.mCourseId = courseId;
        this.mCourseName = courseName;
        mSectionList = new RealmList<Sections>();
        filteredDataset = new RealmList<Sections>();
        this.mSectionList.addAll(sectionList);
        this.filteredDataset.addAll(sectionList);
    }

    @Override
    public int getSectionCount() {
        try {
            return filteredDataset.size();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int getItemCount(int section) {
        try {
            return filteredDataset.get(section).getContents().size();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int section) {
        String sectionName = filteredDataset.get(section).getTitle();
        ContentHeader contentHeader = (ContentHeader) holder;
        contentHeader.resorceTitleTxt.setText(sectionName);
        /*if(section == 0){
            contentHeader.mBetweenSpaceTxt.setVisibility(View.GONE);
        }
        if(section > 0){
            contentHeader.marginTxt.setVisibility(View.VISIBLE);
        }*/
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int section, final int relativePosition, int absolutePosition) {
        String itemName = filteredDataset.get(section).getContents().get(relativePosition).getTitle();

        ContentItem resorcesItem = (ContentItem) holder;


        if (relativePosition == 0) {
            resorcesItem.deviderTxt.setVisibility(View.GONE);
        } else {
            resorcesItem.deviderTxt.setVisibility(View.VISIBLE);
        }
        try {
            if (mSectionList.get(section).getContents().size() == (relativePosition + 1)) {
                resorcesItem.contentItemLinear.setBackgroundResource(R.drawable.content_bottom_boarder);
            } else {
                resorcesItem.contentItemLinear.setBackgroundColor(Color.WHITE);
            }
        } catch (Exception e) {
            LogWriter.err(e);
            resorcesItem.contentItemLinear.setBackgroundColor(Color.WHITE);
        }

        resorcesItem.cItemTitleTxt.setText(itemName);
        resorcesItem.cItemLikesTxt.setText(filteredDataset.get(section).getContents().get(relativePosition).getStatistics().getLikes() + " Likes");
        resorcesItem.cItemCommentsTxt.setText(filteredDataset.get(section).getContents().get(relativePosition).getStatistics().getComments() + " Comments");


        if (filteredDataset.get(section).getContents().get(relativePosition).getViewed() == Flinnt.FALSE) {
            resorcesItem.cItemTitleTxt.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            resorcesItem.cItemTitleTxt.setTypeface(Typeface.DEFAULT);
        }
        for (int i = 0; i < filteredDataset.get(section).getContents().get(relativePosition).getAttachments().size(); i++) {
            if (filteredDataset.get(section).getContents().get(relativePosition).getAttachments().get(i).getAttach_type() != null) {
                int drawableID = Helper.getDrawableIdFromType(Integer.parseInt(filteredDataset.get(section).getContents().get(relativePosition).getAttachments().get(i).getAttach_type()));
                if (Flinnt.INVALID == drawableID) {
                    resorcesItem.contentTypeImage.setVisibility(View.GONE);
                } else {
                    resorcesItem.contentTypeImage.setVisibility(View.VISIBLE);
                    resorcesItem.contentTypeImage.setImageDrawable(Helper.getDrawable(FlinntApplication.getContext(), drawableID));
                }
            }
        }

        if (Integer.parseInt(filteredDataset.get(section).getContents().get(relativePosition).getTypeId()) == ContentsResponse.QUIZ_TYPE_ID) {
            ((ContentItem) holder).statsLayout.setVisibility(View.GONE);
        } else {
            ((ContentItem) holder).statsLayout.setVisibility(View.VISIBLE);
        }

        ((ContentItem) holder).contentItemLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Log.d("Contt", "ContentsAdater : item clicked..");

                if (!Helper.isConnected()) {
                    if (ContentDetailsInterface.getInstance().getContentDetailsData(filteredDataset.get(section).getContents().get(relativePosition).getId(), Config.getStringValue(Config.USER_ID)) == null) {
                        Helper.showNetworkAlertMessage(activity);
                        return;
                    }
                }


                contentSectionPosition = section;
                contentItemPosition = relativePosition;

                if (Integer.parseInt(filteredDataset.get(section).getContents().get(relativePosition).getTypeId()) == ContentsResponse.QUIZ_TYPE_ID) {

                    Intent quizIntent = new Intent(activity, QuizHelpActivty.class);
                    quizIntent.putExtra(QuizQuestionsActivity.KEY_COURSE_ID, mCourseId);
                    quizIntent.putExtra(QuizQuestionsActivity.KEY_CONTENT_ID, filteredDataset.get(section).getContents().get(relativePosition).getId());
                    quizIntent.putExtra(QuizQuestionsActivity.KEY_QUIZ_ID, filteredDataset.get(section).getContents().get(relativePosition).getQuizId());
                    quizIntent.putExtra(QuizQuestionsActivity.KEY_QUIZ_NAME, filteredDataset.get(section).getContents().get(relativePosition).getTitle());
                    activity.startActivity(quizIntent);
                    return;
                }

                Intent contentDetailIntent = new Intent(activity, ContentsDetailActivity.class);
                contentDetailIntent.putExtra("course_id", mCourseId);
                contentDetailIntent.putExtra("content_id", filteredDataset.get(section).getContents().get(relativePosition).getId());
                contentDetailIntent.putExtra(Course.COURSE_NAME_KEY, mCourseName);

                //Log.d("Contt", "ContentsAdater : course_id : " + mCourseId);
                //Log.d("Contt", "ContentsAdater : content_id : " + filteredDataset.get(section).getContents().get(relativePosition).getId());

                contentsFragment.startActivityForResult(contentDetailIntent, ContentsFragment.CONTENT_DELETED_CALL_BACK);
                //*******change v2.0.27
                try {
                    if (myRealm == null) {
                        myRealm = Realm.getInstance(Helper.createRealmObj());
                    }
                    filteredDataset.get(section).getContents().get(relativePosition).setViewed(Flinnt.ENABLED);

                } catch (Exception e) {
                    LogWriter.err(e);
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
            layout = R.layout.content_header_list_item;

            v = LayoutInflater.from(parent.getContext())
                    .inflate(layout, parent, false);
            return new ContentHeader(v);
        } else {
            layout = R.layout.content_list_item;

            v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
            return new ContentItem(v);
        }
    }

    public static class ContentHeader extends RecyclerView.ViewHolder {
        final TextView resorceTitleTxt;

        public ContentHeader(View itemView) {
            super(itemView);
            resorceTitleTxt = (TextView) itemView.findViewById(R.id.resorceTitleTxt);
            //   marginTxt = (TextView) itemView.findViewById(R.id.marginTxt);
            //   mBetweenSpaceTxt = (TextView) itemView.findViewById(R.id.between_space_txt);

        }
    }

    public class ContentItem extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView cItemTitleTxt, cItemLikesTxt, cItemCommentsTxt, deviderTxt;
        ImageView contentTypeImage;
        LinearLayout contentItemLinear;
        LinearLayout statsLayout;

        public ContentItem(View itemView) {
            super(itemView);
            cItemTitleTxt = (TextView) itemView.findViewById(R.id.cItemTitleTxt);
            cItemLikesTxt = (TextView) itemView.findViewById(R.id.cItemLikesTxt);
            cItemCommentsTxt = (TextView) itemView.findViewById(R.id.cItemCommentsTxt);
            deviderTxt = (TextView) itemView.findViewById(R.id.deviderTxt);
            // mContentUnreadDot = (ImageView) itemView.findViewById(R.id.content_unread_dot);
            contentTypeImage = (ImageView) itemView.findViewById(R.id.content_type_image);
            contentItemLinear = (LinearLayout) itemView.findViewById(R.id.contentItemLinear);
            statsLayout = (LinearLayout) itemView.findViewById(R.id.statsLayout);
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

    /**
     * Set search results list and display it
     *
     * @param queryText search query
     */
    public void setFilter(String queryText) {
        searchQuery = queryText;
        isSearchMode = true;
        filteredDataset = new RealmList<>();
        queryText = queryText.toLowerCase();

        for (int i = 0; i < mSectionList.size(); i++) {
            if (mSectionList.get(i).getTitle().toLowerCase().contains(queryText)) {
                filteredDataset.add(mSectionList.get(i));
            } else {

                RealmList<Contents> filteredContent = new RealmList<>();
                boolean isContent = false;
                for (int j = 0; j < mSectionList.get(i).getContents().size(); j++) {
                    if (mSectionList.get(i).getContents().get(j).getTitle().toLowerCase().contains(queryText)) {
                        isContent = true;
                        Contents contents = new Contents();
                        contents = mSectionList.get(i).getContents().get(j);
                        filteredContent.add(contents);
                    }
                }
                if (isContent) {
                    Sections sections = new Sections();
                    sections.setType(mSectionList.get(i).getType());
                    sections.setId(mSectionList.get(i).getId());
                    sections.setCourseID(mSectionList.get(i).getCourseID());
                    sections.setTitle(mSectionList.get(i).getTitle());
                    sections.setContents(filteredContent);
                    filteredDataset.add(sections);
                }
            }
        }
        notifyDataSetChanged();
    }

    public boolean getSearchMode() {
        return isSearchMode;
    }

    public void setSearchMode(boolean mode) {
        isSearchMode = mode;
        filteredDataset = new RealmList<>();
    }

    public void addSearchedItems(RealmList<Sections> sectionList) {
        filteredDataset.addAll(sectionList);
        notifyDataSetChanged();
    }

    public void remove() {
        if (contentSectionPosition != Flinnt.INVALID && contentItemPosition != Flinnt.INVALID) {
            filteredDataset.get(contentSectionPosition).getContents().remove(contentItemPosition);
        }
    }

    public Sections getItem(int position) {
        if (position >= 0 && position < filteredDataset.size()) {
            return filteredDataset.get(position);
        }
        return null;
    }

    public void addItems(RealmList<Sections> sectionList) {
        int positionStart = mSectionList.size() + 1; // position after last course added
        int size = sectionList.size();
        mSectionList.addAll(sectionList);

        if (isSearchMode && !searchQuery.isEmpty()) {
            positionStart = filteredDataset.size() + 1;
            String searchQueryLowerCase = searchQuery.toLowerCase();
            for (Sections sections : sectionList) {
                if (sections.getTitle().toLowerCase().contains(searchQueryLowerCase)) {
                    filteredDataset.add(sections);
                }
            }
            size = filteredDataset.size() - (positionStart - 1);
        } else {
            filteredDataset.addAll(sectionList);
        }
        notifyItemRangeInserted(positionStart, size);
    }

    public void clearData() {
        filteredDataset.clear();
        if (!isSearchMode) mSectionList.clear();
    }

    /**
     * Remove search results and display all data
     */
    public void removeFilter() {
        searchQuery = "";
        isSearchMode = false;
        filteredDataset = new RealmList<>();
        filteredDataset.addAll(mSectionList);
        notifyDataSetChanged();
    }
}