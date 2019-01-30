package com.edu.flinnt.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edu.flinnt.R;

/**
 * Created by mgod on 5/27/15.
 *
 * Simple custom view example to show how to get selected events from the token
 * view. See ContactsCompletionView and contact_token.xml for usage
 */
public class TagTokenLayout extends LinearLayout {

    public TagTokenLayout(Context context) {
        super(context);
    }

    public TagTokenLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void setSelected(boolean selected) {
       // super.setSelected(selected);
        super.setSelected(true);

        TextView v = (TextView)findViewById(R.id.name);
        v.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_close, 0);
        /*if (selected) {
            v.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_close, 0);
        } else {
        	v.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
        }*/
    }
}
