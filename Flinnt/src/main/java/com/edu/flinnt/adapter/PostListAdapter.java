
package com.edu.flinnt.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.FlinntApplication;
import com.edu.flinnt.R;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.protocol.Post;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.RoundedCornersTransformation;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/**
 * mContentsAdapter for posts
 */
public class PostListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    private ArrayList<Post> mDataset;
    private ArrayList<Post> filteredDataset;
    OnItemClickListener mItemClickListener;
    OnItemLongClickListener mItemLongClickListener;
    public static String QUESTION = "<font color=#007DCD><b>Que: </b></font>";
    private boolean isSearchMode;
    private boolean isWithFooter;
    private boolean isOfflineSearch;
    private ImageLoader mImageLoader;
    private Activity mActivity;
    public static String ALBUM_URL = "";


    // Provide a suitable constructor (depends on the kind of dataset)
    public PostListAdapter(Activity activity, ArrayList<Post> myDataset) {
        LogWriter.write("Dataset" + myDataset);
        filteredDataset = new ArrayList<Post>();
        mDataset = new ArrayList<Post>();
        mActivity = activity;
        filteredDataset.addAll(myDataset);
        mDataset.addAll(myDataset);
        mImageLoader = Requester.getInstance().getImageLoader();
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, OnLongClickListener {
        // each data item is just a string in this case
        public TextView title;
        public TextView date;
        //-- public TextView description;
        public TextView like;
        public TextView comment;
        public TextView bookmarkTxt;
        public ImageView postType;
        public ImageView likeViewImage;
        public ImageView commentReplyImage;
        public ImageView thumb;
        public ImageView bookmarkImage;
        public LinearLayout postLinear;
        public RelativeLayout postBottomLinear;
        public RelativeLayout postTopLinear;
        public TextView newLabel;

        public ViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.post_title_text);
            date = (TextView) v.findViewById(R.id.post_date_text);
            //-- description = (TextView) v.findViewById(R.id.post_description_text);
            like = (TextView) v.findViewById(R.id.post_like_text);
            comment = (TextView) v.findViewById(R.id.post_comment_text);
            bookmarkTxt = (TextView) v.findViewById(R.id.post_bookmark_text);
            postType = (ImageView) v.findViewById(R.id.post_type_image);
            likeViewImage = (ImageView) v.findViewById(R.id.post_like_image);
            commentReplyImage = (ImageView) v.findViewById(R.id.post_comment_image);
            bookmarkImage = (ImageView) v.findViewById(R.id.post_bookmark_image);
            postLinear = (LinearLayout) v.findViewById(R.id.postLinear);
            postTopLinear = (RelativeLayout) v.findViewById(R.id.postTopLinear);
            postBottomLinear = (RelativeLayout) v.findViewById(R.id.postBottomLinear);
            newLabel = (TextView) v.findViewById(R.id.new_lable);
            thumb = (ImageView) v.findViewById(R.id.album_thumb_image);
//            thumb.setCornerRadiiDP(4, 4, 4, 4);
            v.setOnClickListener(this);
            v.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mItemLongClickListener != null) {
                mItemLongClickListener.onItemLongClick(v, getAdapterPosition());
            }
            return true;
        }
    }

    // For Read More footer view
    public class FooterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public Button readMore;

        public FooterViewHolder(View v) {
            super(v);

            if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("FooterViewHolder : ");

            readMore = (Button) v.findViewById(R.id.read_more_button);

            readMore.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }

    }


    public void add(int position, Post item) {
        mDataset.add(position, item);
        filteredDataset.add(position, item);
        notifyItemInserted(position);
    }

    public void addItems(ArrayList<Post> items) {
        filteredDataset.addAll(items);
        mDataset.addAll(items);
        //Sorting desc
        Collections.sort(filteredDataset, new Comparator<Post>() {
            @Override
            public int compare(Post post1, Post post2) {
                return post1.getPublishDateLong() > post2.getPublishDateLong() ? -1 :
                        post1.getPublishDateLong() < post2.getPublishDateLong() ? 1 : 0;
            }
        });
        notifyDataSetChanged();
    }

    public void clearData() {
        filteredDataset.clear();
        notifyDataSetChanged();
    }

    public void clearAllData() {
        mDataset.clear();
        filteredDataset.clear();
        notifyDataSetChanged();
    }

    public void prependItems(ArrayList<Post> items) {
        for (int i = 0; i < items.size(); i++) {
            filteredDataset.add(i, items.get(i));
        }
        notifyDataSetChanged();
    }


    /**
     * Update offline post items
     *
     * @param items          items list
     * @param offlinePostIDs item ids list
     */
    public void updateItems(ArrayList<Post> items, ArrayList<String> offlinePostIDs) {
        ArrayList<String> postIDs = new ArrayList<String>();
        for (int i = 0; i < items.size(); i++) {
            Post post = items.get(i);
            postIDs.add(post.getPostID());
            for (int j = 0; j < mDataset.size(); j++) {
                if (post.getPostID().equals(mDataset.get(j).getPostID())) {
                    if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("index : " + j);
                    mDataset.set(j, post);
                    break;
                }
            }
            for (int j = 0; j < filteredDataset.size(); j++) {
                if (post.getPostID().equals(filteredDataset.get(j).getPostID())) {
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.write("filteredDataset index : " + j);
                    filteredDataset.set(j, post);
                    break;
                }
            }
        }

        Collection<String> offlinePostList = offlinePostIDs;
        offlinePostList.removeAll(postIDs);
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("Deleted offlinePostList size : " + offlinePostList.size());
        for (int i = 0; i < offlinePostIDs.size(); i++) {
            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("Deleted offline Post ID : " + offlinePostIDs.get(i));
            for (int j = 0; j < mDataset.size(); j++) {
                if (offlinePostIDs.get(i).equals(mDataset.get(j).getPostID())) {
                    if (LogWriter.isValidLevel(Log.INFO)) LogWriter.write("index : " + j);
                    mDataset.remove(j);
                    break;
                }
            }
            for (int j = 0; j < filteredDataset.size(); j++) {
                if (offlinePostIDs.get(i).equals(filteredDataset.get(j).getPostID())) {
                    if (LogWriter.isValidLevel(Log.INFO))
                        LogWriter.write("filteredDataset index : " + j);
                    filteredDataset.remove(j);
                    break;
                }
            }
        }

        notifyDataSetChanged();
    }

    public void remove(Post item) {
        int position = filteredDataset.indexOf(item);
        if (position > -1) {
            filteredDataset.remove(position);
            mDataset.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Post getItem(int position) {
        if (position >= 0 && position < filteredDataset.size()) {
            return filteredDataset.get(position);
        }
        return null;
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        int itemCount = filteredDataset.size();
        if (isWithFooter) {
            itemCount++;
        }
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("getItemCount itemCount : " + itemCount + ", isWithFooter : " + isWithFooter);
        return itemCount;
    }


    @Override
    public int getItemViewType(int position) {
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("getItemViewType position : " + position);
        if (isWithFooter && isPositionFooter(position)) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    private boolean isPositionFooter(int position) {
        return position == getItemCount() - 1;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("onCreateViewHolder viewType : " + viewType);
        if (viewType == TYPE_ITEM) {
            // create a new view
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_cardview_item, parent, false);
            // set the view's size, margins, paddings and layout parameters
            return new ViewHolder(v);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.read_more, parent, false);
            return new FooterViewHolder(v);
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (LogWriter.isValidLevel(Log.INFO))
            LogWriter.write("onBindViewHolder position : " + position);
        if (viewHolder instanceof ViewHolder) {
            final ViewHolder holder = (ViewHolder) viewHolder;
            Post post = filteredDataset.get(position);
            if (null != post) {
                holder.bookmarkTxt.setVisibility(View.GONE);
                holder.bookmarkImage.setVisibility(View.GONE);
                String title = post.getTitle();
                holder.thumb.setImageResource(R.drawable.album_default);
               if (post.getPostTypeInt() == Flinnt.POST_TYPE_MESSAGE) {
                    holder.title.setMaxLines(3);
                    holder.thumb.setVisibility(View.GONE);
                    holder.postLinear.setBackgroundResource(R.color.white);
                    holder.postBottomLinear.setVisibility(View.VISIBLE);
                    holder.title.setTextColor(Color.BLACK);
                    holder.date.setTextColor(mActivity.getResources().getColor(R.color.date_color));
                    holder.postTopLinear.setBackgroundColor(Color.TRANSPARENT);

                    holder.title.setText(title);

                    holder.likeViewImage.setImageDrawable(Helper.getDrawable(FlinntApplication.getContext(), R.drawable.ic_message_card_views));
                    holder.commentReplyImage.setImageDrawable(Helper.getDrawable(FlinntApplication.getContext(), R.drawable.ic_post_card_comment));

                    holder.like.setText(post.getTotalViews() + " Views");
                    holder.comment.setText(post.getTotalComments() + " Comments");
                    holder.like.setTextColor(Color.parseColor("#878787"));
                    holder.comment.setTextColor(Color.parseColor("#878787"));

                } else if (post.getPostTypeInt() == Flinnt.POST_TYPE_ALBUM) {
                    holder.title.setMaxLines(2);

                    holder.title.setTextColor(Color.WHITE);
                    holder.date.setTextColor(Color.WHITE);
                    holder.thumb.setVisibility(View.VISIBLE);

                    holder.postLinear.setBackgroundResource(R.drawable.album_gradient);
                    holder.postTopLinear.setBackgroundColor(Color.TRANSPARENT);
                    holder.postBottomLinear.setVisibility(View.VISIBLE);
                    holder.postBottomLinear.setBackgroundColor(Color.TRANSPARENT);


                    holder.likeViewImage.setImageDrawable(Helper.getDrawable(FlinntApplication.getContext(), R.drawable.ic_post_card_like_new));
                    holder.commentReplyImage.setImageDrawable(Helper.getDrawable(FlinntApplication.getContext(), R.drawable.ic_post_card_comment_new));

                    holder.like.setText(post.getTotalLikes() + " Likes");
                    holder.comment.setText(post.getTotalComments() + " Comments");

                    holder.like.setTextColor(Color.WHITE);
                    holder.comment.setTextColor(Color.WHITE);

                    holder.title.setText(title);

                    holder.thumb.setImageResource(R.drawable.album_default);

                    String[] albumImages = post.getAlbumImages().split(",");

                    if (albumImages.length > 0) {
                        final String albumImage = albumImages[0];
                        final String urlMobile = ALBUM_URL + post.getPostID() + File.separator + Flinnt.GALLERY_MOBILE + File.separator + albumImage;
                        Glide.with(mActivity)
                                .load(urlMobile)
                                .placeholder(R.drawable.album_default)
                                .bitmapTransform(new RoundedCornersTransformation(mActivity, 0, 0))
                                .into(holder.thumb);
                    }
                } else {
                    holder.title.setMaxLines(3);
                    holder.thumb.setVisibility(View.GONE);
                    holder.postBottomLinear.setVisibility(View.VISIBLE);
                    holder.title.setTextColor(Color.BLACK);
                    holder.date.setTextColor(mActivity.getResources().getColor(R.color.date_color));
                    holder.postTopLinear.setBackgroundColor(Color.TRANSPARENT);
                    holder.postLinear.setBackgroundResource(R.color.white);

                    holder.title.setText(post.getDescription());

                    holder.likeViewImage.setImageDrawable(Helper.getDrawable(FlinntApplication.getContext(), R.drawable.ic_post_card_like));
                    holder.commentReplyImage.setImageDrawable(Helper.getDrawable(FlinntApplication.getContext(), R.drawable.ic_post_card_comment));

                    holder.like.setText(post.getTotalLikes() + " Likes");
                    holder.comment.setText(post.getTotalComments() + " Comments");

                    holder.like.setTextColor(Color.parseColor("#878787"));
                    holder.comment.setTextColor(Color.parseColor("#878787"));

                    if (post.getIsBookmarkInt() == Flinnt.TRUE) {
                        holder.bookmarkTxt.setVisibility(View.VISIBLE);
                        holder.bookmarkImage.setVisibility(View.VISIBLE);
                    } else {
                        holder.bookmarkTxt.setVisibility(View.GONE);
                        holder.bookmarkImage.setVisibility(View.GONE);
                    }
                }

                if (post.getIsReadInt() == Flinnt.FALSE) {
                    holder.title.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    holder.title.setTypeface(Typeface.DEFAULT);
                }


                try {
                    if ((Long.parseLong(post.getPublishDate())) > (System.currentTimeMillis() / 1000)) {
                        holder.date.setText("not yet");
                    } else
                        holder.date.setText(Helper.formateTimeMillis(Long.parseLong(post.getPublishDate())));

                } catch (Exception e) {
                    LogWriter.err(e);
                }

                int drawableID = Helper.getDrawableIdFromType(post.getPostContentTypeInt());
                if (Flinnt.INVALID == drawableID || post.getPostTypeInt() == Flinnt.POST_TYPE_ALBUM) {
                    holder.postType.setVisibility(View.GONE);
                } else {
                    holder.postType.setVisibility(View.VISIBLE);
                    holder.postType.setImageDrawable(Helper.getDrawable(FlinntApplication.getContext(), drawableID));
                }

            }
        } else if (viewHolder instanceof FooterViewHolder) {
            FooterViewHolder footerHolder = (FooterViewHolder) viewHolder;
            if (isOfflineSearch) {
                footerHolder.readMore.setVisibility(View.GONE);
            } else {
                footerHolder.readMore.setVisibility(isWithFooter ? View.VISIBLE : View.GONE);
            }
        }

    }

    /**
     * Set search results list and display it
     *
     * @param queryText search query
     */
    public void setFilter(String queryText) {
        filteredDataset = new ArrayList<Post>();
        isOfflineSearch = true;
        queryText = queryText.toString().toLowerCase();
        for (Post item : mDataset) {
            if (item.getDescription().toLowerCase().contains(queryText)) {
                filteredDataset.add(item);
            }
        }
        notifyDataSetChanged();
    }

    public boolean getSearchMode() {
        return isSearchMode;
    }

    public void setSearchMode(boolean mode) {
        isSearchMode = mode;
        isOfflineSearch = !isSearchMode;
        filteredDataset = new ArrayList<Post>();
    }

    public void addSearchedItems(ArrayList<Post> items) {
        filteredDataset.addAll(items);
        notifyDataSetChanged();
    }

    /**
     * Remove search results and display all data
     */
    public void removeFilter() {
        isSearchMode = false;
        isOfflineSearch = false;
        filteredDataset = new ArrayList<Post>();
        filteredDataset.addAll(mDataset);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(
            final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemLongClickListener {
        public void onItemLongClick(View view, int position);
    }

    public void setOnItemLongClickListener(
            final OnItemLongClickListener mItemLongClickListener) {
        this.mItemLongClickListener = mItemLongClickListener;
    }


    public boolean isWithFooter() {
        return isWithFooter;
    }

    public void setWithFooter(boolean withFooter) {
        this.isWithFooter = withFooter;
    }
}