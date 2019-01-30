package com.edu.flinnt.slidemenu;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.edu.flinnt.R;
import com.edu.flinnt.util.LogWriter;

import java.util.List;

/**
 * Navigation drawer menu items adapter class
 */
public class MenuAdapter extends ArrayAdapter<SlideMenuItem> {

	private Activity activity;
	private List<SlideMenuItem> itemList;
	private SlideMenuItem item;
	private int row;
    private boolean isPrimaryMenu;

	public MenuAdapter(Activity act, int resource, List<SlideMenuItem> arrayList, boolean isPrimaryMenu) {
		super(act, resource, arrayList);
		this.activity = act;
		this.row = resource;
		this.itemList = arrayList;
        this.isPrimaryMenu = isPrimaryMenu;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

        item = itemList.get(position);

        View view = convertView;
        ViewHolder holder;

        if (view == null) {
			LayoutInflater inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(row, null);

			holder = new ViewHolder();
			holder.tvTitle = (TextView) view.findViewById(R.id.menu_title);
            holder.imgView = (ImageView) view.findViewById(R.id.menu_icon); 

            if (!isPrimaryMenu && position > 0) {
                holder.radioUserSelection = (RadioButton) view.findViewById(R.id.radio_user_selection);
                holder.radioUserSelection.setVisibility(View.VISIBLE);
                holder.tvTitle.setText(item.getTitle());
                holder.radioUserSelection.setChecked(item.isSelected());
                holder.imgView.setVisibility(View.GONE);
            } else {
                holder.tvUnread = (TextView) view.findViewById(R.id.unread_count);
                holder.imgView.setVisibility(View.VISIBLE);
            }

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		if ((itemList == null) || ((position + 1) > itemList.size()))
			return view;

        if (isPrimaryMenu || position == 0) {


            try {
                holder.tvTitle.setText(item.getTitle());
                holder.imgView.setImageResource(item.getIconId());

                if (LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("\nitem.getTitle() : " + item.getTitle() + "\nitem.getUnreadCount() : " + item.getUnreadCount());

                if (item.getUnreadCount() > 0 || item.getUnread().equals("99+")) {
                    holder.tvUnread.setVisibility(View.VISIBLE);
                    holder.tvUnread.setText(item.getUnread());

                    if (LogWriter.isValidLevel(Log.DEBUG)) LogWriter.write("\nitem.getUnread() : " + item.getUnread() + "\n holder.tvUnread.getText() : " +  holder.tvUnread.getText());

                    if (item.getIconId() == R.drawable.ic_drawer_alert) {
                        holder.tvUnread.setBackgroundResource(R.drawable.round_unread_count_bg_red);
                    } else {
                        holder.tvUnread.setBackgroundResource(R.drawable.round_unread_count_bg_green);
                    }
                } else {
                    holder.tvUnread.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                LogWriter.write("Exception in MenuAdapter : "+e.toString());
            }


        }

		return view;
	}

	public class ViewHolder {

		public TextView tvTitle;
		public ImageView imgView;
		public TextView tvUnread;
        public RadioButton radioUserSelection;
	}

}
