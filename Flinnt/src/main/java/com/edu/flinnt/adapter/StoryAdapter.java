package com.edu.flinnt.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.gui.InAppPreviewActivity;
import com.edu.flinnt.models.StoryDataModel;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ssl.SSLSocketFactory;
import cz.msebera.android.httpclient.entity.StringEntity;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.MyViewHolder> {

    private List<StoryDataModel.Story> moviesList = new ArrayList<>();
    //String url = "https://flinnt-story-dev.s3.amazonaws.com/";//@chirag:31/08/2018  testing url for story
    String url = "https://flinnt.s3.amazonaws.com/";//@chirag:31/08/2018  live url for story
    String galleryUrl;
    public AsyncHttpClient client;
    String story_id, like_status;
    Context mContext;

    public void setData(List<StoryDataModel.Story> mlist) {
        moviesList.clear();
        moviesList.addAll(mlist);
        notifyDataSetChanged();
        //Log.d("filterstory", String.valueOf(moviesList.size()));
    }
    public void clearData() {
        moviesList.clear();
        notifyDataSetChanged();
        //Log.d("filterstory", String.valueOf(moviesList.size()));
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_coursename, txt_date, txt_desc, total_likes_txt, txt_title, description_read_more_txt, txt_play;
        public LinearLayout like_linear, share_linear;
        ImageView media_thumbnail_img, like_img;
        LinearLayout lin_fullview;

        public MyViewHolder(View view) {
            super(view);
            txt_date = (TextView) view.findViewById(R.id.txt_date);
            lin_fullview = (LinearLayout) view.findViewById(R.id.lin_fullview);
            txt_coursename = (TextView) view.findViewById(R.id.txt_coursename);
            txt_desc = (TextView) view.findViewById(R.id.txt_desc);
            total_likes_txt = (TextView) view.findViewById(R.id.total_likes_txt);
            txt_title = (TextView) view.findViewById(R.id.txt_title);
            description_read_more_txt = (TextView) view.findViewById(R.id.description_read_more_txt);
            txt_play = (TextView) view.findViewById(R.id.txt_play);
            media_thumbnail_img = (ImageView) view.findViewById(R.id.media_thumbnail_img);
            like_img = (ImageView) view.findViewById(R.id.like_img);
            like_linear = (LinearLayout) view.findViewById(R.id.like_linear);
            share_linear = (LinearLayout) view.findViewById(R.id.share_linear);
        }
    }


    public StoryAdapter(List<StoryDataModel.Story> moviesList, Context mContext) {
        this.moviesList.addAll(moviesList);
        this.mContext = mContext;
    }

    public void setGalleryUrl(String galleryUrl) {
        this.galleryUrl = galleryUrl;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_stories_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final StoryDataModel.Story story = moviesList.get(position);

        String dateString = Helper.formateDateDetails(story.getPublishDate());
        StringBuilder sb = new StringBuilder(dateString);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        holder.txt_date.setText(sb.toString());

        holder.txt_coursename.setText(story.getCourseName());
        holder.txt_title.setText(story.getTitle());
        holder.txt_desc.setText(story.getDescription());
        //holder.txt_desc.setMaxLines(2);
        //Glide.with(mContext).load(url + story.getAttachment()).thumbnail(0.5f).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.media_thumbnail_img); //@chirag:31/08/218 commented
        Glide.with(mContext)
                .load(galleryUrl + story.getAttachment())  //@Chirag:31/08/2018 replaced galleryUrl from url
                .thumbnail(0.5f).crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.media_thumbnail_img);

        holder.txt_desc.post(new Runnable() {
            @Override
            public void run() {
                int moreLine = holder.txt_desc.getLineCount();
                if (moreLine > 2) {
                    //post.setShowMoreLess(Flinnt.SHOWMORE);
                    holder.txt_desc.setMaxLines(2);
                    holder.description_read_more_txt.setVisibility(View.VISIBLE);
                    holder.description_read_more_txt.setText("...Read More");
                } else {
                    holder.description_read_more_txt.setVisibility(View.GONE);
                }
            }
        });

        holder.description_read_more_txt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (holder.txt_desc.getMaxLines() == 2) {
                    holder.txt_desc.setMaxLines(Integer.MAX_VALUE);
                    holder.description_read_more_txt.setText("Read Less");
                } else if (holder.txt_desc.getMaxLines() == Integer.MAX_VALUE) {
                    holder.txt_desc.setMaxLines(2);
                    holder.description_read_more_txt.setText("...Read More");
                }
            }
        });

        holder.txt_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helper.isConnected()) {
                    String url = story.getUrl();
                    String coursename = story.getCourseName();
                    Intent inapp = new Intent(mContext, InAppPreviewActivity.class);
                    inapp.putExtra(Flinnt.KEY_INAPP_TITLE, coursename);
                    inapp.putExtra(Flinnt.KEY_INAPP_URL, url);
                    inapp.putExtra(Flinnt.KEY_INAPP_BASE, "");
                    mContext.startActivity(inapp);
                } else {
                    Helper.showNetworkAlertMessage(mContext);
                }

            }
        });

        if (story.getLikeStatus().equals("1")) {
            holder.like_img.setImageResource(R.drawable.ic_like_blue);
            holder.total_likes_txt.setTextColor(mContext.getResources().getColor(R.color.ColorPrimary));
        } else {
            holder.like_img.setImageResource(R.drawable.ic_like_grey);
            holder.total_likes_txt.setTextColor(mContext.getResources().getColor(R.color.timeline_text_color));
        }

        holder.like_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helper.isConnected()) {
                    story_id = story.getId();
                    if (story.getLikeStatus().equals("1")) {
                        like_status = "0";
                    } else {
                        like_status = "1";
                    }
                    if (story.getLikeStatus().equals("1")) {
                        holder.like_img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_like_blue));
                        holder.total_likes_txt.setTextColor(mContext.getResources().getColor(R.color.ColorPrimary));
                        story.setLikeStatus("0");
                        WebService_LikeDisLike();
                        notifyDataSetChanged();
                    } else {
                        holder.like_img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_like_grey));
                        holder.total_likes_txt.setTextColor(mContext.getResources().getColor(R.color.timeline_text_color));
                        story.setLikeStatus("1");
                        WebService_LikeDisLike();
                        notifyDataSetChanged();
                    }
                } else {
                    Helper.showNetworkAlertMessage(mContext);
                }


            }
        });
        holder.lin_fullview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Helper.isConnected()) {
                    String url = story.getUrl();
                    String coursename = story.getCourseName();
                    Intent inapp = new Intent(mContext, InAppPreviewActivity.class);
                    inapp.putExtra(Flinnt.KEY_INAPP_TITLE, coursename);
                    inapp.putExtra(Flinnt.KEY_INAPP_URL, url);
                    inapp.putExtra(Flinnt.KEY_INAPP_BASE, "");
                    mContext.startActivity(inapp);
                } else {
                    Helper.showNetworkAlertMessage(mContext);
                }
            }
        });
        holder.share_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helper.isConnected()) {
                    String text = story.getTitle();
                    String url = story.getUrl();
                    share(text, url);
                } else {
                    Helper.showNetworkAlertMessage(mContext);
                }

            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helper.isConnected()) {
//                    String url = story.getUrl();
//                    String coursename = story.getCourseName();
//                    Intent intent = new Intent(mContext, StoryDetails.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("URL", url + "?view=course" + "&userid=" + Config.getStringValue(Config.USER_ID));
//                    //bundle.putString("URL","http://pnl.citytadka.com/story/65");
//                    bundle.putString("Coursename", coursename);
//                    intent.putExtras(bundle);
//                    mContext.startActivity(intent);
                } else {
                    Helper.showNetworkAlertMessage(mContext);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    public SSLContext getSslContext() {

        TrustManager[] byPassTrustManagers = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }
        }};

        SSLContext sslContext = null;

        try {
            sslContext = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sslContext.init(null, byPassTrustManagers, new SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return sslContext;
    }

    public void WebService_LikeDisLike() {
        try {
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("user_id", Config.getStringValue(Config.USER_ID));
            jsonParams.put("story_id", story_id);
            jsonParams.put("like_status", like_status);
            StringEntity entity = new StringEntity(jsonParams.toString());
            client = new AsyncHttpClient();
            client.setSSLSocketFactory(new SSLSocketFactory(getSslContext(), SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER));
            client.post(mContext, Flinnt.API_URL + Flinnt.URL_LIKE_DISLIKE_STORY, entity, "application/json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.d(String.valueOf(mContext), "onSuccess() called with: statusCode = [" + statusCode + "], headers = [" + headers + "], response = [" + response + "]");

                    try {
                        if (response.getString("status").equals("1")) {

                            notifyDataSetChanged();
                        } else {
                            Toast.makeText(mContext, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    //Log.e("TAG", "onSuccess() called with: statusCode = [" + statusCode + "], headers = [" + headers + "], response = [" + errorResponse + "]");
                }
            });
        } catch (UnsupportedEncodingException e) {

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void share(String text, String url) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text + " " + url);
        mContext.startActivity(Intent.createChooser(shareIntent, "Share"));
    }
}