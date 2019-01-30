package com.edu.flinnt.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.gui.InAppPreviewActivity;
import com.edu.flinnt.gui.PromoCardInterface;
import com.edu.flinnt.models.PopularStoryDataModel;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.RoundedCornersTransformation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by flinnt-android-3 on 13/1/17.
 */
public class PromoCoursePagerAdapter extends PagerAdapter implements PromoCardInterface {

    Activity mActivity;
    ArrayList<PopularStoryDataModel.Story> promoCourseArraylist;
    private float mBaseElevation;
    private List<CardView> mViews;
    private String mCoursePictureUrl;
    private int pos = 0;
    private String galleryUrl;
    //String URL = "https://flinnt-story-dev.s3.amazonaws.com/";//@chirag:31/08/2018  testing url for story
    String URL = "https://flinnt.s3.amazonaws.com/";//@chirag:31/08/2018  live url for story


    public PromoCoursePagerAdapter(Activity activity, ArrayList<PopularStoryDataModel.Story> promoCourseArraylist) {
        this.mActivity = activity;
        this.promoCourseArraylist = promoCourseArraylist;
        mViews = new ArrayList<>();
    }

    public void setGalleryUrl(String galleryUrl) {
        this.galleryUrl = galleryUrl;
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return promoCourseArraylist.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.card_promo_course, container, false);
        try {
            pos = (position % promoCourseArraylist.size());
            container.addView(view);
            bind(promoCourseArraylist.get(pos), view, pos);
            CardView cardView = (CardView) view.findViewById(R.id.card_view);

            if (mBaseElevation == 0) {
                mBaseElevation = cardView.getCardElevation();
            }
            mViews.add(position, cardView);
        } catch (Exception e) {
            LogWriter.err(e);
        }

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        if (mViews.size() > position) {
            mViews.set(position, null);
        }

    }

    private void bind(final PopularStoryDataModel.Story course, View v, final int position) {

        ImageView courseImage;
        TextView txtCourseName, txtInstituteName, txtCount;
        courseImage = (ImageView) v.findViewById(R.id.course_image);
        txtCourseName = (TextView) v.findViewById(R.id.text_course_name);
        txtInstituteName = (TextView) v.findViewById(R.id.text_institute_name);
        txtCount = (TextView) v.findViewById(R.id.text_promo_count);

        //String url = URL + course.getAttachment();//@chirag:31/08/2018 commented
        String url = galleryUrl + course.getAttachment();//@chirag:31/08/2018 commented
        //Log.d("Add","image : " + url);

        Glide.with(mActivity).load(url).placeholder(R.drawable.default_course_banner).bitmapTransform(new RoundedCornersTransformation(mActivity, 0, 0))
                .into(courseImage);
        txtCourseName.setText(course.getTitle());
        txtInstituteName.setText(course.getCourseName());
        txtCount.setText((position + 1) + "/" + promoCourseArraylist.size());
        courseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helper.isConnected()) {
                    Intent inapp = new Intent(mActivity.getApplicationContext(), InAppPreviewActivity.class);
                    inapp.putExtra(Flinnt.KEY_INAPP_TITLE, promoCourseArraylist.get(position).getCourseName());
                    inapp.putExtra(Flinnt.KEY_INAPP_URL, promoCourseArraylist.get(position).getUrl());
                    inapp.putExtra(Flinnt.KEY_INAPP_BASE, "");
                    mActivity.startActivity(inapp);
                } else {
                    Helper.showNetworkAlertMessage(mActivity);
                }

            }

        });
    }

    public void updateDataSource(ArrayList<PopularStoryDataModel.Story> myCoursePromoList) {

        promoCourseArraylist.clear();
        this.notifyDataSetChanged();
        promoCourseArraylist.addAll(myCoursePromoList);
        this.notifyDataSetChanged();
    }

    @Override
    public float getPageWidth(int position) {
        return 1.0f;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public String getmCoursePictureUrl() {
        return mCoursePictureUrl;
    }

    public void setmCoursePictureUrl(String mCoursePictureUrl) {
        this.mCoursePictureUrl = mCoursePictureUrl;
    }

}