package com.edu.flinnt.adapter;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.edu.flinnt.Flinnt;
import com.edu.flinnt.FlinntApplication;
import com.edu.flinnt.R;
import com.edu.flinnt.protocol.Post;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/**
 * mContentsAdapter class for message list
 */
public class MessageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    private ArrayList<Post> mDataset;
    private ArrayList<Post> filteredDataset;
    OnItemClickListener mItemClickListener;
    OnItemLongClickListener mItemLongClickListener;
    private boolean isSearchMode;
    private boolean isWithFooter;
    private boolean isOfflineSearch;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        // each data item is just a string in this case

        public TextView title;
        public TextView date;
        public TextView messageTo;
        public TextView views;
        public TextView replies;
        public ImageView messageType;
        public ImageView messageUnreadDot;

        public ViewHolder(View v) {
            super(v);

            title = (TextView) v.findViewById(R.id.message_title_text);
            date = (TextView) v.findViewById(R.id.message_date_text);
            messageTo = (TextView) v.findViewById(R.id.message_to_text);
            views = (TextView) v.findViewById(R.id.message_views_text);
            replies = (TextView) v.findViewById(R.id.message_replies_text);
            messageType = (ImageView) v.findViewById(R.id.message_type_image);
            messageUnreadDot = (ImageView) v.findViewById(R.id.message_card_dot);

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

    public void clearAllData() {
        mDataset.clear();
        filteredDataset.clear();
        notifyDataSetChanged();
    }

    /**
     * Update new messages into offline database
     *
     * @param items          list of messages
     * @param offlinePostIDs list of message ids
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
            LogWriter.write("Deleted offlineMessageList size : " + offlinePostList.size());
        for (int i = 0; i < offlinePostIDs.size(); i++) {
            if (LogWriter.isValidLevel(Log.INFO))
                LogWriter.write("Deleted offline Message ID : " + offlinePostIDs.get(i));
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
            notifyItemRemoved(position);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MessageListAdapter(ArrayList<Post> myDataset) {
        filteredDataset = new ArrayList<Post>();
        mDataset = new ArrayList<Post>();

        filteredDataset.addAll(myDataset);
        mDataset.addAll(myDataset);
    }

    public Post getItem(int position) {
        if (position >= 0 && position < filteredDataset.size()) {
            return filteredDataset.get(position);
        } else return null;
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
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_cardview_item, parent, false);
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
            ViewHolder holder = (ViewHolder) viewHolder;
            Post post = filteredDataset.get(position);

            if (null != post) {
                holder.title.setText(post.getTitle());

                if (post.getIsReadInt() == Flinnt.TRUE) {
                    holder.messageUnreadDot.setVisibility(View.INVISIBLE);
                    holder.title.setTypeface(Typeface.DEFAULT);
                } else {
                    holder.messageUnreadDot.setVisibility(View.VISIBLE);
                    holder.title.setTypeface(Typeface.DEFAULT_BOLD);
                }

                //String msgTo = Html.fromHtml("<b>To:</b>") + " " + post.getMessageToUsers();
                holder.messageTo.setText(post.getMessageToUsers());

                holder.views.setText(post.getTotalViews() + " Views");
                holder.replies.setText(post.getTotalComments() + " Replies");
                try {
                    if ((Long.parseLong(post.getPublishDate())) > (System.currentTimeMillis() / 1000)) {
                        holder.date.setText("not yet");
                    } else
                        holder.date.setText(Helper.formateTimeMillis(Long.parseLong(post.getPublishDate())));

                } catch (Exception e) {
                    LogWriter.err(e);
                }


                int drawableID = Helper.getDrawableIdFromType(post.getPostContentTypeInt());
                if (Flinnt.INVALID == drawableID) {
                    holder.messageType.setVisibility(View.GONE);
                } else {
                    holder.messageType.setVisibility(View.VISIBLE);
                    holder.messageType.setImageDrawable(Helper.getDrawable(FlinntApplication.getContext(), drawableID));
                }
            }
        } else if (viewHolder instanceof FooterViewHolder) {
            FooterViewHolder footerHolder = (FooterViewHolder) viewHolder;
            if (isOfflineSearch) {
                footerHolder.readMore.setVisibility(View.GONE);
            } else {
                footerHolder.readMore.setVisibility(isWithFooter ? View.VISIBLE : View.GONE);
            }
            if(LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("\nisOfflineSearch : " + isOfflineSearch + "\nisWithFooter : " + isWithFooter);
        }
    }


    /**
     * Set search results list and display it
     *
     * @param queryText search query
     */
    public void setFilter(String queryText) {
        isOfflineSearch = true;
        filteredDataset = new ArrayList<Post>();
        queryText = queryText.toString().toLowerCase();
        for (Post item : mDataset) {
            if (item.getTitle().toLowerCase().contains(queryText)) {
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
            OnItemLongClickListener mItemLongClickListener) {
        this.mItemLongClickListener = mItemLongClickListener;

    }

    public boolean isWithFooter() {
        return isWithFooter;
    }

    public void setWithFooter(boolean withFooter) {
        this.isWithFooter = withFooter;
    }
}