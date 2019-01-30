package com.edu.flinnt.gui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.FlinntApplication;
import com.edu.flinnt.R;
import com.edu.flinnt.core.ContentsShowHideDelete;
import com.edu.flinnt.core.CopyContent;
import com.edu.flinnt.core.SectionShowHideDelete;
import com.edu.flinnt.core.SendContentNotification;
import com.edu.flinnt.protocol.AddContentRequest;
import com.edu.flinnt.protocol.Attachment;
import com.edu.flinnt.protocol.Content;
import com.edu.flinnt.protocol.ContentsEditResponse;
import com.edu.flinnt.protocol.ContentsShowHideDeleteRequest;
import com.edu.flinnt.protocol.Course;
import com.edu.flinnt.protocol.SectionShowHideDeleteRequest;
import com.edu.flinnt.protocol.Sections;
import com.edu.flinnt.protocol.SendContentNotificationRequest;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.ContentSectionsOptions;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by flinnt-android-2 on 16/11/16.
 * Content Edit Custom adapter for display content items
 */

public class ContentEditAdapter extends SectionedRecyclerViewAdapter<RecyclerView.ViewHolder> {
    private ArrayList<Sections> mSectionList;
    private ArrayList<Sections> filteredDataset;
    private Activity activity;
    private String mCourseId = "", mCourseName = "";
    ContentsEditResponse mContentsEditResponse;
    static OnItemClickListener mItemClickListener;
    private int contentSectionPosition, contentItemPosition;
    String sectionId = "", contnetId = "";
    private BottomSheetDialog mSectionDialog;
    private SendContentNotificationRequest mSendContentNotificationRequest = null;
    public BottomSheetOptionsAdapter mBottomSheetAdapter;


    public ContentEditAdapter(Activity activity, String courseId, String mCourseName, ArrayList<Sections> sectionList) {
        this.activity = activity;
        this.mCourseId = courseId;
        this.mCourseName = mCourseName;
        mSectionList = new ArrayList<Sections>();
        filteredDataset = new ArrayList<Sections>();
        LogWriter.write("Response Section Data:" + sectionList);
        this.mSectionList.addAll(sectionList);
        this.filteredDataset.addAll(sectionList);
    }


    public void updateNotificationStatus() {
        filteredDataset.get(contentSectionPosition).getContents().get(contentItemPosition).setNotificationSent(Flinnt.ENABLED);
    }

    @Override
    public int getSectionCount() {
        return filteredDataset.size();
    }

    @Override
    public int getItemCount(int section) {
        return filteredDataset.get(section).getContents().size();
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, final int section) {
        final String sectionName = filteredDataset.get(section).getTitle();
        ContentEditAdapter.ContentHeader contentHeader = (ContentEditAdapter.ContentHeader) holder;
        contentHeader.resorceTitleTxt.setText(sectionName);

        if (filteredDataset.get(section).getIsVisible().equals(Flinnt.ENABLED)) {
            contentHeader.resorceTitleTxt.setText(sectionName);
            contentHeader.resorceTitleTxt.setTextColor(activity.getResources().getColor(R.color.black));
        } else {
            contentHeader.resorceTitleTxt.setText(sectionName);
            contentHeader.resorceTitleTxt.setTextColor(activity.getResources().getColor(R.color.gray));
        }

        String strShowHide = filteredDataset.get(section).getIsVisible();
        contentHeader.mSectionOptionsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    contnetId = filteredDataset.get(section).getContents().get(contentItemPosition).getId();
                    SectionBottomDialog(sectionName, v, section);
                } catch (Exception e) {
                    LogWriter.err(e.getMessage());
                }
            }
        });
        /*MenuItem bedMenuItem = contentHeader.sectionToolbar.getMenu().findItem(R.id.content_show_hide);
        if (strShowHide.equals(Flinnt.ENABLED)) {
            bedMenuItem.setTitle(R.string.edit_hide);
        } else {
            bedMenuItem.setTitle(R.string.edit_show);
        }

        if (contentHeader.sectionToolbar != null) {
            contentHeader.sectionToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {

                    contentSectionPosition = section;
                    sectionId = filteredDataset.get(contentSectionPosition).getId();
                    switch (menuItem.getItemId()) {
                        case R.id.content_show_hide:
                            if (Helper.isConnected()) {
                                SectionShowHideDeleteRequest mSectionShowHideDeleteRequest = new SectionShowHideDeleteRequest();
                                mSectionShowHideDeleteRequest.setCourseId(mCourseId);
                                mSectionShowHideDeleteRequest.setUserID(Config.USER_ID);
                                mSectionShowHideDeleteRequest.setSectionId(sectionId);

                                SectionShowHideDelete mSectionShowHideDelete = new SectionShowHideDelete(((ContentEditActivity) activity).mHandler, mCourseId, sectionId, "");
                                mSectionShowHideDelete.sendSectionShowHideDelete();
                                ((ContentEditActivity) activity).startProgressDialog();
                            } else {
                                Helper.showNetworkAlertMessage(activity);
                            }
                            break;

                        case R.id.content_delete:
                            if (Helper.isConnected()) {
                                showDeleteConfirmDialog(activity.getResources().getString(R.string.delete_section_text), "Section");

                            } else {
                                Helper.showNetworkAlertMessage(activity);
                            }
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });
        }*/
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int section, final int relativePosition, int absolutePosition) {
        final String itemName = filteredDataset.get(section).getContents().get(relativePosition).getTitle();

        ContentEditAdapter.ContentItem resorcesItem = (ContentEditAdapter.ContentItem) holder;

        if (relativePosition == 0) {
            resorcesItem.deviderTxt.setVisibility(View.GONE);
        } else {
            resorcesItem.deviderTxt.setVisibility(View.VISIBLE);
        }
        if (mSectionList.get(section).getContents().size() == (relativePosition + 1)) {
            resorcesItem.contentItemLinear.setBackgroundResource(R.drawable.content_bottom_boarder);
        } else {
            resorcesItem.contentItemLinear.setBackgroundColor(Color.WHITE);
        }

//        if(filteredDataset.get(section).getContents().get(relativePosition).getAttachments().size() > relativePosition){
        for (int i = 0; i < filteredDataset.get(section).getContents().get(relativePosition).getAttachments().size(); i++) {
            int drawableID = Helper.getDrawableIdFromType(Integer.parseInt(filteredDataset.get(section).getContents().get(relativePosition).getAttachments().get(i).getAttachType()));

            if (Flinnt.INVALID == drawableID) {
                resorcesItem.contentTypeImage.setVisibility(View.GONE);
            } else {
                resorcesItem.contentTypeImage.setVisibility(View.VISIBLE);
                resorcesItem.contentTypeImage.setImageDrawable(Helper.getDrawable(FlinntApplication.getContext(), drawableID));
            }
        }
//        }

        if (filteredDataset.get(section).getContents().get(relativePosition).getIsVisible().equals(Flinnt.ENABLED)) {
            resorcesItem.cItemTitleTxt.setText(itemName);
            resorcesItem.cItemTitleTxt.setTextColor(activity.getResources().getColor(R.color.black));
            resorcesItem.contentTypeImage.setAlpha(255);
        } else {
            resorcesItem.cItemTitleTxt.setText(itemName);
            resorcesItem.cItemTitleTxt.setTextColor(activity.getResources().getColor(R.color.gray));
            resorcesItem.contentTypeImage.setAlpha(100);
        }

        resorcesItem.mContentOptionsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    contnetId = filteredDataset.get(section).getContents().get(relativePosition).getId();
                    ContentBottomDialog(itemName, v, section, relativePosition);
                } catch (Exception e) {
                    LogWriter.err(e.getMessage());
                }
            }
        });

        /*String strShowHide = filteredDataset.get(section).getContents().get(relativePosition).getIsVisible();
        MenuItem bedMenuItem = resorcesItem.contentToolbar.getMenu().findItem(R.id.content_show_hide);
        if (strShowHide.equals(Flinnt.ENABLED)) {
            bedMenuItem.setTitle(R.string.edit_hide);
        } else {
            bedMenuItem.setTitle(R.string.edit_show);
        }

        if (resorcesItem.contentToolbar != null) {
            resorcesItem.contentToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    contentSectionPosition = section;
                    contentItemPosition = relativePosition;
                    sectionId = filteredDataset.get(contentSectionPosition).getId();
                    contnetId = filteredDataset.get(contentSectionPosition).getContents().get(contentItemPosition).getId();

                    switch (menuItem.getItemId()) {
                        case R.id.content_show_hide:
                            if (Helper.isConnected()) {
                                ContentsShowHideDeleteRequest mContentsShowHideDeleteRequest = new ContentsShowHideDeleteRequest();
                                mContentsShowHideDeleteRequest.setCourseId(mCourseId);
                                mContentsShowHideDeleteRequest.setUserID(Config.USER_ID);
                                mContentsShowHideDeleteRequest.setSectionId(sectionId);
                                mContentsShowHideDeleteRequest.setContentId(contnetId);

                                ContentsShowHideDelete mContentsShowHideDelete = new ContentsShowHideDelete(((ContentEditActivity) activity).mHandler, mCourseId, contnetId, sectionId, "");
                                mContentsShowHideDelete.sendContentsShowHideDelete();
                                ((ContentEditActivity) activity).startProgressDialog();

                            } else {
                                Helper.showNetworkAlertMessage(activity);
                            }
                            break;

                        case R.id.content_delete:
                            if (Helper.isConnected()) {
                                showDeleteConfirmDialog(activity.getResources().getString(R.string.delete_content_text), "Content");

                            } else {
                                Helper.showNetworkAlertMessage(activity);
                            }
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });
        }*/

        if (itemName != null && itemName.equals(activity.getString(R.string.empty_content_txt))) {
            resorcesItem.mContentOptionsImg.setVisibility(View.GONE);
            resorcesItem.contentTypeImage.setVisibility(View.INVISIBLE);
        } else {
            resorcesItem.mContentOptionsImg.setVisibility(View.VISIBLE);
            resorcesItem.contentTypeImage.setVisibility(View.VISIBLE);
        }

        if (filteredDataset.get(section).getContents().get(relativePosition).getAttachments().size() == 0) {
            resorcesItem.contentTypeImage.setVisibility(View.INVISIBLE);
        }

    }

    private boolean dismissDialog() {
        if (mSectionDialog != null && mSectionDialog.isShowing()) {
            mSectionDialog.dismiss();
            return true;
        }

        return false;
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
            layout = R.layout.content_edit_section;

            v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
            return new ContentEditAdapter.ContentHeader(v);
        } else {
            layout = R.layout.content_edit_content;

            v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
            return new ContentEditAdapter.ContentItem(v);
        }
    }


    public static class ContentHeader extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView resorceTitleTxt;
        ImageView mSectionOptionsImg;

        public ContentHeader(View itemView) {
            super(itemView);
            resorceTitleTxt = (TextView) itemView.findViewById(R.id.resorceTitleTxt);
            mSectionOptionsImg = (ImageView) itemView.findViewById(R.id.section_options_img);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    public class ContentItem extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView cItemTitleTxt, deviderTxt;
        ImageView contentTypeImage, mContentOptionsImg;
        LinearLayout contentItemLinear;

        public ContentItem(View itemView) {
            super(itemView);
            cItemTitleTxt = (TextView) itemView.findViewById(R.id.cItemTitleTxt);
            deviderTxt = (TextView) itemView.findViewById(R.id.deviderTxt);
            contentTypeImage = (ImageView) itemView.findViewById(R.id.content_type_image);
            contentItemLinear = (LinearLayout) itemView.findViewById(R.id.contentItemLinear);
            mContentOptionsImg = (ImageView) itemView.findViewById(R.id.content_options_img);
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
            final ContentEditAdapter.OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void removeContent() {
        if (contentSectionPosition != Flinnt.INVALID && contentItemPosition != Flinnt.INVALID) {
            filteredDataset.get(contentSectionPosition).getContents().remove(contentItemPosition);
            if(filteredDataset.get(contentSectionPosition).getContents().size() == 0){
                filteredDataset.get(contentSectionPosition).setContents(addBlackContent());
            }
        }
    }

    public void removeSection() {
        if (contentSectionPosition != Flinnt.INVALID) {
            filteredDataset.remove(contentSectionPosition);
        }
    }


    public void contentShow() {
        if (contentSectionPosition != Flinnt.INVALID && contentItemPosition != Flinnt.INVALID) {
            filteredDataset.get(contentSectionPosition).getContents().get(contentItemPosition).setIsVisible(Flinnt.ENABLED);
        }
    }

    public void contentHide() {
        if (contentSectionPosition != Flinnt.INVALID && contentItemPosition != Flinnt.INVALID) {
            filteredDataset.get(contentSectionPosition).getContents().get(contentItemPosition).setIsVisible(Flinnt.DISABLED);
        }
    }

    public void sectionShow() {
        if (contentSectionPosition != Flinnt.INVALID) {
            filteredDataset.get(contentSectionPosition).setIsVisible(Flinnt.ENABLED);
        }
    }

    public void sectionHide() {
        if (contentSectionPosition != Flinnt.INVALID) {
            filteredDataset.get(contentSectionPosition).setIsVisible(Flinnt.DISABLED);
        }
    }

    public void addItems(ArrayList<Sections> sectionList, ContentsEditResponse mContentsEditResponse) {
        for (int i = 0; i < sectionList.size(); i++) {
            if (sectionList.get(i).getContents().size() == 0) {
                sectionList.get(i).setContents(addBlackContent());
            }
        }
        int positionStart = mSectionList.size() + 1; // position after last course added
        int size = sectionList.size();
        this.mContentsEditResponse = mContentsEditResponse;
        mSectionList.addAll(sectionList);
        size = filteredDataset.size() - (positionStart - 1);
        filteredDataset.addAll(sectionList);
        //notifyItemRangeInserted(positionStart, size);
        notifyDataSetChanged();
    }

    public void showDeleteConfirmDialog(String message, final String deleteType) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        TextView titleText = new TextView(activity);
        titleText.setText(activity.getResources().getString(R.string.show_delete_dialog_title));
        titleText.setPadding(40, 40, 40, 0);
        titleText.setGravity(Gravity.CENTER_VERTICAL);
        titleText.setTextColor(activity.getResources().getColor(R.color.ColorPrimary));
        titleText.setTextSize(20);
        titleText.setTypeface(Typeface.DEFAULT_BOLD);
        alertDialogBuilder.setCustomTitle(titleText);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton(activity.getString(R.string.content_delete), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (Helper.isConnected()) {
                    if (deleteType.equals(activity.getString(R.string.content))) {
                        ContentsShowHideDeleteRequest mContentsShowHideDeleteRequest = new ContentsShowHideDeleteRequest();
                        mContentsShowHideDeleteRequest.setCourseId(mCourseId);
                        mContentsShowHideDeleteRequest.setUserID(Config.USER_ID);
                        mContentsShowHideDeleteRequest.setSectionId(sectionId);
                        mContentsShowHideDeleteRequest.setContentId(contnetId);

                        ContentsShowHideDelete mContentsShowHideDelete = new ContentsShowHideDelete(((ContentEditActivity) activity).mHandler, mCourseId, contnetId, sectionId, ContentsShowHideDelete.DELETE);
                        mContentsShowHideDelete.sendContentsShowHideDelete();
                        ((ContentEditActivity) activity).startProgressDialog();
                    } else {
                        SectionShowHideDeleteRequest mSectionShowHideDeleteRequest = new SectionShowHideDeleteRequest();
                        mSectionShowHideDeleteRequest.setCourseId(mCourseId);
                        mSectionShowHideDeleteRequest.setUserID(Config.USER_ID);
                        mSectionShowHideDeleteRequest.setSectionId(sectionId);

                        SectionShowHideDelete mSectionShowHideDelete = new SectionShowHideDelete(((ContentEditActivity) activity).mHandler, mCourseId, sectionId, SectionShowHideDelete.DELETE);
                        mSectionShowHideDelete.sendSectionShowHideDelete();
                        ((ContentEditActivity) activity).startProgressDialog();
                    }

                } else {
                    Helper.showNetworkAlertMessage(activity);
                }
            }
        });
        alertDialogBuilder.setNegativeButton(activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        // create alert mSectionDialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        if (!Helper.isFinishingOrIsDestroyed(activity)) {
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(activity.getResources().getColor(R.color.ColorPrimary));
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(activity.getResources().getColor(R.color.ColorPrimary));
        }
    }

    private void SectionBottomDialog(String sectionName, final View v, final int section) {

        if (dismissDialog()) {
            return;
        }

        final List<ContentSectionsOptions> mSectionOptionsList = new ArrayList<>();
        mSectionOptionsList.add(new ContentSectionsOptions(R.string.add_files, R.drawable.addfiles));
        mSectionOptionsList.add(new ContentSectionsOptions(R.string.edit_section_name, R.drawable.edit));
        mSectionOptionsList.add(new ContentSectionsOptions(R.string.hide_content, R.drawable.show));
        mSectionOptionsList.add(new ContentSectionsOptions(R.string.delete, R.drawable.delete));
        final int addFiles = 0, editSection = 1, hideContent = 2, deleteContent = 3;

        mBottomSheetAdapter = new BottomSheetOptionsAdapter(mSectionOptionsList);
        String strShowHide = filteredDataset.get(section).getIsVisible();
        if (strShowHide.equals(Flinnt.ENABLED)) {
            mSectionOptionsList.set(hideContent, new ContentSectionsOptions(R.string.hide_content, R.drawable.hide));
        } else {
            mSectionOptionsList.set(hideContent, new ContentSectionsOptions(R.string.show_content, R.drawable.show));
        }
        mBottomSheetAdapter.setOnItemClickListener(new BottomSheetOptionsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BottomSheetOptionsAdapter.ItemHolder item, int position) {
                LogWriter.write("Position : " + position);
                contentSectionPosition = section;
                sectionId = filteredDataset.get(contentSectionPosition).getId();
                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(activity);
                } else {
                    switch (position) {
                        case addFiles:
                            if (Helper.isConnected()) {
                                Intent addFiles = new Intent(activity, AddFileActivity.class);
                                addFiles.putExtra(Flinnt.CONTENT_STATS_ACTION, Flinnt.CONTENT_ADD);
                                addFiles.putExtra(Course.COURSE_ID_KEY, mCourseId);
                                addFiles.putExtra(AddFileActivity.SECTION_POSTITION, String.valueOf(contentSectionPosition));
                                addFiles.putExtra(SectionShowHideDeleteRequest.SECTION_ID, sectionId);
                                activity.startActivityForResult(addFiles, ContentEditActivity.ADD_CONTENT);
                            } else {
                                Helper.showNetworkAlertMessage(activity);
                            }
                            dismissDialog();
                            break;
                        case editSection:
                            if (Helper.isConnected()) {
                                ((ContentEditActivity) activity).showAddSection(Flinnt.SECTION_EDIT, filteredDataset.get(contentSectionPosition), contentSectionPosition);
                            } else {
                                Helper.showNetworkAlertMessage(activity);
                            }
                            dismissDialog();
                            break;
                        case hideContent:

                            SectionShowHideDeleteRequest mSectionShowHideDeleteRequest = new SectionShowHideDeleteRequest();
                            mSectionShowHideDeleteRequest.setCourseId(mCourseId);
                            mSectionShowHideDeleteRequest.setUserID(Config.USER_ID);
                            mSectionShowHideDeleteRequest.setSectionId(sectionId);

                            SectionShowHideDelete mSectionShowHideDelete = new SectionShowHideDelete(((ContentEditActivity) activity).mHandler, mCourseId, sectionId, "");
                            mSectionShowHideDelete.sendSectionShowHideDelete();
                            ((ContentEditActivity) activity).startProgressDialog();
                            dismissDialog();

                            break;
                        case deleteContent:
                            showDeleteConfirmDialog(activity.getResources().getString(R.string.delete_section_text), activity.getString(R.string.section));
                            dismissDialog();
                            break;
                    }
                }
            }
        });

        View view = activity.getLayoutInflater().inflate(R.layout.section_options, null);
        TextView mSectionNameTxt = (TextView) view.findViewById(R.id.section_name_txt);
        mSectionNameTxt.setText(sectionName);

        RecyclerView mSectionOptionRecycler = (RecyclerView) view.findViewById(R.id.section_options_recycler);
        mSectionOptionRecycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mSectionOptionRecycler.setLayoutManager(layoutManager);
        mSectionOptionRecycler.setAdapter(mBottomSheetAdapter);

        mSectionDialog = new BottomSheetDialog(activity);
        mSectionDialog.setContentView(view);
        mSectionDialog.show();
    }

    public void ContentBottomDialog(String itemName, final View v, final int sectionPosition, final int itemPosition) {

        if (dismissDialog()) {
            return;
        }

        final List<ContentSectionsOptions> mSectionOptionsList = new ArrayList<>();
        if (!filteredDataset.get(sectionPosition).getContents().get(itemPosition).getType_id().equals(Flinnt.POST_TYPE_CONTENT_QUIZ)) {
            mSectionOptionsList.add(new ContentSectionsOptions(R.string.edit_contents, R.drawable.edit));
        }

        final String strShowHide = filteredDataset.get(sectionPosition).getContents().get(itemPosition).getIsVisible();
        String mNotificationStatus = filteredDataset.get(sectionPosition).getContents().get(itemPosition).getNotificationSent();
        if (strShowHide.equals(Flinnt.ENABLED)) {
            mSectionOptionsList.add(new ContentSectionsOptions(R.string.hide_content, R.drawable.hide));
        } else {
            mSectionOptionsList.add(new ContentSectionsOptions(R.string.show_content, R.drawable.show));
        }
        if (strShowHide.equals(Flinnt.ENABLED)) {
            if (mNotificationStatus.equalsIgnoreCase(Flinnt.ENABLED)) {
                mSectionOptionsList.add(new ContentSectionsOptions(R.string.resend_notification, R.drawable.resend));
            } else {
                mSectionOptionsList.add(new ContentSectionsOptions(R.string.send_notification, R.drawable.notification));
            }
        }
        mSectionOptionsList.add(new ContentSectionsOptions(R.string.make_copy, R.drawable.make_copy));
        mSectionOptionsList.add(new ContentSectionsOptions(R.string.delete, R.drawable.delete));

        final int editcontents = 0, hideShowContent = 1, sendNotification = 2, makeCopy = 3, deleteContent = 4;


        mBottomSheetAdapter = new BottomSheetOptionsAdapter(mSectionOptionsList);


        contentSectionPosition = sectionPosition;
        contentItemPosition = itemPosition;
        mBottomSheetAdapter.setOnItemClickListener(new BottomSheetOptionsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BottomSheetOptionsAdapter.ItemHolder item, int position) {
                LogWriter.write("Position : " + position);
                sectionId = filteredDataset.get(contentSectionPosition).getId();
                contnetId = filteredDataset.get(contentSectionPosition).getContents().get(contentItemPosition).getId();
                if (filteredDataset.get(contentSectionPosition).getContents().get(contentItemPosition).getType_id().equals(Flinnt.POST_TYPE_CONTENT_QUIZ)) {
                    position = position + 1;
                }
                if (strShowHide.equals(Flinnt.DISABLED)) {
                    if (position > 1) {
                        position = position + 1;
                    }
                }

                if (!Helper.isConnected()) {
                    Helper.showNetworkAlertMessage(activity);
                } else {
                    switch (position) {
                        case editcontents:
                            if (Helper.isConnected()) {
                                Intent addFiles = new Intent(activity, AddFileActivity.class);
                                addFiles.putExtra(Flinnt.CONTENT_STATS_ACTION, Flinnt.CONTENT_EDIT);
                                addFiles.putExtra(Course.COURSE_ID_KEY, mCourseId);
                                addFiles.putExtra(SectionShowHideDeleteRequest.SECTION_ID, sectionId);
                                addFiles.putExtra(ContentsShowHideDeleteRequest.CONTENT_ID, contnetId);
                                addFiles.putExtra(AddFileActivity.SECTION_POSTITION, String.valueOf(contentSectionPosition));
                                addFiles.putExtra(AddFileActivity.CONTENT_POSTITION, String.valueOf(contentItemPosition));
                                addFiles.putExtra(AddContentRequest.TITLE_KEY, filteredDataset.get(contentSectionPosition).getContents().get(contentItemPosition).getTitle());
                                addFiles.putExtra(AddContentRequest.DESCRIPTION_KEY, filteredDataset.get(contentSectionPosition).getContents().get(contentItemPosition).getDescription());
                                addFiles.putExtra(AddContentRequest.CAN_PREVIEW_KEY, filteredDataset.get(contentSectionPosition).getContents().get(contentItemPosition).getPreview());
                                addFiles.putExtra(AddContentRequest.ATTACHMENT_TYPE_KEY, filteredDataset.get(contentSectionPosition).getContents().get(contentItemPosition).getAttachments().get(0).getAttachType());
                                addFiles.putExtra(AddContentRequest.ATTACHMENT_URL_KEY, filteredDataset.get(contentSectionPosition).getContents().get(contentItemPosition).getAttachments().get(0).getAttachmentUrl());
                                addFiles.putExtra(AddFileActivity.FILE_NAME, filteredDataset.get(contentSectionPosition).getContents().get(contentItemPosition).getAttachments().get(0).getFileName());
                                addFiles.putExtra(AddFileActivity.IS_URL, filteredDataset.get(contentSectionPosition).getContents().get(contentItemPosition).getAttachments().get(0).getIsUrl());
                                activity.startActivityForResult(addFiles, ContentEditActivity.EDIT_CONTENT);
                                dismissDialog();
                            } else {
                                Helper.showNetworkAlertMessage(activity);
                            }
                            break;
                        case hideShowContent:
                            sectionId = filteredDataset.get(sectionPosition).getId();
                            ContentsShowHideDeleteRequest mContentsShowHideDeleteRequest = new ContentsShowHideDeleteRequest();
                            mContentsShowHideDeleteRequest.setCourseId(mCourseId);
                            mContentsShowHideDeleteRequest.setUserID(Config.USER_ID);
                            mContentsShowHideDeleteRequest.setSectionId(sectionId);
                            mContentsShowHideDeleteRequest.setContentId(contnetId);

                            ContentsShowHideDelete mContentsShowHideDelete = new ContentsShowHideDelete(((ContentEditActivity) activity).mHandler, mCourseId, contnetId, sectionId, "");
                            mContentsShowHideDelete.sendContentsShowHideDelete();
                            ((ContentEditActivity) activity).startProgressDialog();
                            dismissDialog();
                            break;
                        case deleteContent:
                            showDeleteConfirmDialog(activity.getResources().getString(R.string.delete_content_text), activity.getString(R.string.content));
                            mSectionDialog.dismiss();
                            break;
                        case sendNotification:
                            mSendContentNotificationRequest = new SendContentNotificationRequest();
                            mSendContentNotificationRequest.setUserID(Config.getStringValue(Config.USER_ID));
                            SendContentNotification sendContentNotification = new SendContentNotification(((ContentEditActivity) activity).mHandler, mCourseId, contnetId);
                            sendContentNotification.sendNotificationRequest();
                            ((ContentEditActivity) activity).startProgressDialog();
                            mSectionDialog.dismiss();
                            break;
                        case makeCopy:
                            ContentEditActivity.copyContentSectionPosition = contentSectionPosition;
                            CopyContent mCopyContent = new CopyContent(((ContentEditActivity) activity).mHandler, mCourseId, sectionId, contnetId);
                            mCopyContent.sendCopyContent();
                            ((ContentEditActivity) activity).startProgressDialog();
                            dismissDialog();
                            break;
                    }
                }
            }
        });

        View view = activity.getLayoutInflater().inflate(R.layout.section_options, null);
        TextView mSectionNameTxt = (TextView) view.findViewById(R.id.section_name_txt);
        mSectionNameTxt.setText(itemName);

        RecyclerView mSectionOptionRecycler = (RecyclerView) view.findViewById(R.id.section_options_recycler);
        mSectionOptionRecycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mSectionOptionRecycler.setLayoutManager(layoutManager);
        mSectionOptionRecycler.setAdapter(mBottomSheetAdapter);

        mSectionDialog = new BottomSheetDialog(activity);
        mSectionDialog.setContentView(view);
        mSectionDialog.show();
    }

    public void addSection(Sections mAddSectionResponse) {
        mAddSectionResponse.setContents(addBlackContent());
        mSectionList.add(0, mAddSectionResponse);
        filteredDataset.add(0, mAddSectionResponse);
    }

    public void editSection(Sections mAddSectionResponse, int mSectionPosition) {
        mSectionList.get(mSectionPosition).setTitle(mAddSectionResponse.getTitle());
        filteredDataset.get(mSectionPosition).setTitle(mAddSectionResponse.getTitle());
    }

    public void addContent(int position, Content content) {
        int pos = filteredDataset.get(position).getContents().size();
        filteredDataset.get(position).getContents().add(pos, content);
        if (filteredDataset.get(position).getContents().size() > 0 && filteredDataset.get(position).getContents().get(0).getTitle().equals((activity.getString(R.string.empty_content_txt)))) {
            filteredDataset.get(position).getContents().remove(0);
        }
    }

    public void editContent(int position, int conPosition, Content content) {
        mSectionList.get(position).getContents().set(conPosition, content);
        filteredDataset.get(position).getContents().set(conPosition, content);
    }

    public static class BottomSheetOptionsAdapter extends RecyclerView.Adapter<BottomSheetOptionsAdapter.ItemHolder> {
        private List<ContentSectionsOptions> list;
        private OnItemClickListener onItemClickListener;

        public BottomSheetOptionsAdapter(List<ContentSectionsOptions> list) {
            this.list = list;
        }

        @Override
        public BottomSheetOptionsAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bottomsheet_raw, parent, false);
            return new ItemHolder(itemView, this);
        }

        @Override
        public void onBindViewHolder(BottomSheetOptionsAdapter.ItemHolder holder, int position) {
            holder.bind(list.get(position));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            onItemClickListener = listener;
        }

        public OnItemClickListener getOnItemClickListener() {
            return onItemClickListener;
        }

        public interface OnItemClickListener {
            void onItemClick(ItemHolder item, int position);
        }

        public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private BottomSheetOptionsAdapter bottomSheetOptionsAdapter;
            TextView mSectionOptionsTxt;
            ImageView mSectionOptionsImg;

            public ItemHolder(View itemView, BottomSheetOptionsAdapter parent) {
                super(itemView);
                itemView.setOnClickListener(this);
                this.bottomSheetOptionsAdapter = parent;

                mSectionOptionsImg = (ImageView) itemView.findViewById(R.id.section_options_img);
                mSectionOptionsTxt = (TextView) itemView.findViewById(R.id.section_options_txt);
            }

            public void bind(ContentSectionsOptions item) {
                mSectionOptionsTxt.setText(item.getTitleId());
                mSectionOptionsImg.setImageResource(item.getImageId());
            }

            @Override
            public void onClick(View v) {
                final OnItemClickListener listener = bottomSheetOptionsAdapter.getOnItemClickListener();
                if (listener != null) {
                    listener.onItemClick(this, getAdapterPosition());
                }
            }
        }
    }

    public List<Content> addBlackContent() {
        List<Content> contents = new ArrayList<Content>();
        List<Attachment> attachme = new ArrayList<>();
        Content temp = new Content();
        temp.setType("content");
        temp.setId("");
        temp.setTitle(activity.getString(R.string.empty_content_txt));
        temp.setDescription("");
        temp.setPreview("");
        temp.setIsVisible("");
        temp.setSrNo("");
        temp.setNotificationSent("");
        Attachment temAttach = new Attachment();
        temAttach.setType("attachment");
        temAttach.setId("");
        temAttach.setAttachType("-1");
        temAttach.setIsUrl("");
        temAttach.setFileName("");
        temAttach.setAttachmentUrl("");
        attachme.add(temAttach);
        temp.setAttachments(attachme);
        contents.add(temp);
        return contents;
    }
}