package com.edu.flinnt.gui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edu.flinnt.R;

/**
 * Fragment class for Help options
 */
public class QuizHelpFragment extends Fragment {

	private ImageView screenShot;
	private TextView pageTitle;
	private int imageResourse;
	private String mTitle;
	/*
	public HelpFragment(int res) {
		this.imageResourse = res;
	}
	*/
	// Another constructor function, enable to pass them arguments.
	public static QuizHelpFragment newInstance(int imageResourse, String title) {
		QuizHelpFragment fragment = new QuizHelpFragment();
		
		Bundle args = new Bundle();
		args.putInt("imageResourse", imageResourse);
		args.putString("mResorceTitleTxt", title);
		fragment.setArguments(args);
		
		return fragment;
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            this.imageResourse = getArguments().getInt("imageResourse");
            this.mTitle = getArguments().getString("mResorceTitleTxt");
        }
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v =inflater.inflate(R.layout.quiz_help_fragment, container,false);
		screenShot = (ImageView) v.findViewById(R.id.imageview_screenshot);
		return v;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		screenShot.setImageResource(imageResourse);
	}
}
