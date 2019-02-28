package com.edu.flinnt.adapter.store;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.edu.flinnt.Flinnt;
import com.edu.flinnt.R;
import com.edu.flinnt.core.Requester;
import com.edu.flinnt.core.store.BrowseCoursesNew;
import com.edu.flinnt.core.store.CartItems;
import com.edu.flinnt.core.store.CartListItemResponse;
import com.edu.flinnt.customviews.store.QuantityView;
import com.edu.flinnt.fragments.store.BrowseCoursesFragmentNew;
import com.edu.flinnt.gui.store.ShoppingCartActivity;
import com.edu.flinnt.models.store.StoreModelResponse;
import com.edu.flinnt.util.Config;
import com.edu.flinnt.util.Helper;
import com.edu.flinnt.util.LogWriter;
import com.edu.flinnt.util.SelectableRoundedCourseImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Checkout_CartListItemAdapter extends RecyclerView.Adapter<Checkout_CartListItemAdapter.CustomViewHolder> {

    private List<CartListItemResponse.CartItem> valueList = new ArrayList<CartListItemResponse.CartItem>();
    private Context context;
    private ImageLoader mImageLoader;
    private int totalCount;
    private CartItems cartItems;
    private Handler mHandler;
    private int cart_empty = 200;
    private onCartEmptyListner onCartEmptyListner;
    private BrowseCoursesNew mBrowseCourses;
    private StoreModelResponse storeModelResponse;
    private HashMap<Integer,String> qtyItems = new HashMap<>();
    private ProgressDialog mProgressDialog;



    public interface onCartEmptyListner{
        void onCartEmpty();
    }

    public Checkout_CartListItemAdapter(final Context context, List<CartListItemResponse.CartItem> valueList,onCartEmptyListner onCartEmptyListner) {
        this.valueList = valueList;
        this.context = context;
        mImageLoader = Requester.getInstance().getImageLoader();
        this.onCartEmptyListner = onCartEmptyListner;

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                stopProgressDialog();
                switch (msg.what) {
                    case Flinnt.SUCCESS:
                        if(msg.arg1 == 1){
                            //BrowseCoursesFragmentNew.cart_count--;
//                            startProgressDialog();
//                            mBrowseCourses = new BrowseCoursesNew(mHandler);
//                            mBrowseCourses.setSearchString("");
//                            mBrowseCourses.sendBrowseCoursesRequest();

                            Snackbar.make(((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content),"Item removed from cart",Snackbar.LENGTH_LONG).show();
                        }else if(msg.arg1 == 2){
                            //update success
                            startProgressDialog();
                            mBrowseCourses = new BrowseCoursesNew(mHandler);
                            mBrowseCourses.setSearchString("");
                            mBrowseCourses.sendBrowseCoursesRequest();

                        }
                        else if(msg.obj instanceof StoreModelResponse){
                            storeModelResponse = (StoreModelResponse)msg.obj;
                            stopProgressDialog();
                            updateCartCount(storeModelResponse);

                        }
                        break;
                    case Flinnt.FAILURE:

                        Snackbar.make(((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content),"Something wrong",Snackbar.LENGTH_LONG).show();

                        break;
                }
            }


        };
    }

    @Override
    public Checkout_CartListItemAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.checkout_cart_list_items, null);
        Checkout_CartListItemAdapter.CustomViewHolder viewHolder = new Checkout_CartListItemAdapter.CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final Checkout_CartListItemAdapter.CustomViewHolder customViewHolder, final int i) {
        final CartListItemResponse.CartItem item  = valueList.get(i);
        try{
            String url  = item.getThumbnailPath();
            customViewHolder.iv_product.setImageUrl(url,mImageLoader);
        }catch (Exception ex){
            ex.printStackTrace();
        }

        customViewHolder.txt_productname.setText(item.getName());
        customViewHolder.newPriceTxt.setText(context.getString(R.string.currency)+""+String.valueOf(item.getPrice()));

        customViewHolder.old_price_txt.setVisibility(View.GONE);
        customViewHolder.old_price_txt.setText(context.getString(R.string.currency)+""+String.valueOf(0));
        customViewHolder.old_price_txt.setPaintFlags(customViewHolder.old_price_txt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        customViewHolder.review_count_text.setText("88");
        customViewHolder.ratingbar.setRating((float)3.5);

        totalCount +=  item.getPrice() * Integer.parseInt(item.getQty());

        // customViewHolder.txt_vendorname.setText(item.getVendorName());
        customViewHolder.txt_vendorname.setText(item.getVendorName());

        customViewHolder.quantityView.setQuantity(Integer.parseInt(item.getQty()));
        customViewHolder.tv_qty.setText(String.valueOf(item.getQty()));
        customViewHolder.quantityView.setOnQuantityChangeListener(new QuantityView.OnQuantityChangeListener() {
            @Override
            public void onQuantityChanged(int oldQuantity,int newQuantity,boolean programmatically) {
                startProgressDialog();
                valueList.get(i).setQty(String.valueOf(newQuantity));
                BrowseCoursesFragmentNew.cart_count = newQuantity;
                refreshTotalPrice();
                cartItems = new CartItems(mHandler,Config.getStringValue(Config.USER_ID),String.valueOf(item.getRowId()),String.valueOf(newQuantity));
                cartItems.updateCartItemRequest();
            }
            @Override
            public void onLimitReached() {
            }
        });

        qtyItems.put(Integer.parseInt(item.getQty()),String.valueOf(item.getQty()));

        for(int count = 0;count < 10 ;count++){
            qtyItems.put((count+1),String.valueOf(count+1));
        }


        ArrayList<String> qtyItems2 = new ArrayList<>();

        for(int countItem = 0;countItem<qtyItems.size();countItem++){
            qtyItems2.add(qtyItems.get(countItem+1));
        }

        customViewHolder.btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startProgressDialog();
                valueList.remove(i);
                refreshTotalPrice();
                notifyItemRemoved(i);
                cartItems = new CartItems(mHandler,Config.getStringValue(Config.USER_ID),String.valueOf(item.getRowId()));
                cartItems.removeCartItemRequest();

                if(valueList.size() <= 0){
                    onCartEmptyListner.onCartEmpty();
                    BrowseCoursesFragmentNew.cart_count = 0;
                    Message message = new Message();
                    message.what = Flinnt.SUCCESS;
                    message.arg1 = cart_empty;
                    mHandler.sendMessage(message);
                }
            }
        });

    }

    private void refreshTotalPrice(){
        totalCount = 0;
        for(int count = 0;count<valueList.size();count++){
            totalCount += valueList.get(count).getPrice() * Integer.parseInt(valueList.get(count).getQty());

        }
        ((ShoppingCartActivity)context).onQuntyChange(totalCount);
    }

    public int getItemCount() {
        return (null != valueList ? valueList.size() : 0);
    }


    public void clearData(){
        valueList.clear();
        notifyDataSetChanged();
    }

    public void updateCartCount(StoreModelResponse mBrowseCoursesResponse) {
        try {
            //coursePictureURLstatic = mBrowseCoursesResponse.getPictureUrl();
            BrowseCoursesFragmentNew.cart_count = mBrowseCoursesResponse.getData().get(0).getCartTotal();
        } catch (Exception e) {
            LogWriter.err(e);
        }
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        protected TextView txt_productname,newPriceTxt,old_price_txt,review_count_text,txt_vendorname,tv_qty;
        private RatingBar ratingbar;
        private Button btn_remove;
        private SelectableRoundedCourseImageView iv_product;
        private QuantityView quantityView;

        public CustomViewHolder(View itemView) {
            super(itemView);
            txt_productname = (TextView) itemView.findViewById(R.id.txt_productname);
            newPriceTxt = (TextView) itemView.findViewById(R.id.newPriceTxt);
            old_price_txt = (TextView) itemView.findViewById(R.id.old_price_txt);
            review_count_text = (TextView) itemView.findViewById(R.id.review_count_text);
            ratingbar = (RatingBar)itemView.findViewById(R.id.ratingbar);
            btn_remove = (Button) itemView.findViewById(R.id.btn_remove);
            iv_product = (SelectableRoundedCourseImageView)itemView.findViewById(R.id.iv_product);
            txt_vendorname = (TextView) itemView.findViewById(R.id.txt_vendorname);
            quantityView = (QuantityView)itemView.findViewById(R.id.quantityView_default);
            tv_qty = (TextView)itemView.findViewById(R.id.tv_qty);

        }
    }

    public void startProgressDialog() {
        if (!Helper.isFinishingOrIsDestroyed(((Activity)context))) {
            mProgressDialog = Helper.getProgressDialog(context, "", "", Helper.PROGRESS_DIALOG_TRANSPERENT);
            if ( mProgressDialog != null && !Helper.isFinishingOrIsDestroyed(((Activity)context)))
                mProgressDialog.show();
        }
    }
    private void stopProgressDialog(){
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.hide();
            }
        }
        catch (Exception e) {
            LogWriter.err(e);
        }
        finally {
            mProgressDialog = null;
        }
    }

}
