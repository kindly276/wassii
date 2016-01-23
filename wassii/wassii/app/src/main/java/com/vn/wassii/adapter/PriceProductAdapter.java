package com.vn.wassii.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vn.wassii.R;
import com.vn.wassii.activity.MainActivity;
import com.vn.wassii.model.PriceProduct;
import com.vn.wassii.utils.Utils;

import java.util.List;

/**
 * Created by rau muong on 10/01/2016.
 */
public class PriceProductAdapter extends RecyclerView.Adapter {


    private List<PriceProduct> MessageList;
    private MainActivity mainActivity;

    public PriceProductAdapter(List<PriceProduct> Messages, MainActivity mainActivity) {
        this.MessageList = Messages;
        this.mainActivity = mainActivity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_list_price, parent, false);

        vh = new PriceProductViewHolder(v, mainActivity);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PriceProductViewHolder) {

            PriceProduct calendarWassii = (PriceProduct) MessageList.get(position);

            ((PriceProductViewHolder) holder).txtName.setText(calendarWassii.getName());
            ((PriceProductViewHolder) holder).txtPrice.setText(calendarWassii.getPrice() + " " + mainActivity.getResources().getString(R.string.st_money));
            if (!calendarWassii.getLargeImageURL().equals("")) {
                Utils.loadImage(mainActivity, ((PriceProductViewHolder) holder).image_product, calendarWassii.getLargeImageURL());
            }else{
                Utils.loadImage(mainActivity,((PriceProductViewHolder) holder).image_product);
            }
        }
    }


    @Override
    public int getItemCount() {
        if (MessageList != null) {
            return MessageList.size();
        }
        return 0;


    }

    //
    public class PriceProductViewHolder extends RecyclerView.ViewHolder {
        public TextView txtName, txtPrice;
        public PriceProduct priceProduct;
        public ImageView image_product;

        public PriceProductViewHolder(View v, final MainActivity activity) {
            super(v);
            txtName = (TextView) v.findViewById(R.id.name_product);

            txtPrice = (TextView) v.findViewById(R.id.price_product);
            image_product = (ImageView) v.findViewById(R.id.image_product);

        }
    }


}