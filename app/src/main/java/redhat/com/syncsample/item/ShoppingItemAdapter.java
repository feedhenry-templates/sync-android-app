/**
 * Copyright (c) 2015 FeedHenry Ltd, All Rights Reserved.
 *
 * Please refer to your contract with FeedHenry for the software license agreement.
 * If you do not have a contract, you do not have a license to use this software.
 */
package redhat.com.syncsample.item;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import redhat.com.syncsample.R;

/**
 * Created by summers on 10/13/15.
 */
public class ShoppingItemAdapter extends RecyclerView.Adapter<ShoppingItemAdapter.ViewHolder> {

    private final List<ShoppingItem> items = new ArrayList<>();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View shoppingItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shopping_item, parent, false);

        ViewHolder holder = new ViewHolder(shoppingItemView);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ShoppingItem shoppingItem = getShoppingItem(position);
        holder.itemNameField.setText(shoppingItem.getName());
    }

    public ShoppingItem getShoppingItem(int position) {
        return items.get(position);
    }

    public void clear(){
        items.clear();
    }

    public void add(ShoppingItem item) {
        items.add(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView itemNameField;
        public ViewHolder(View itemView) {
            super(itemView);
            itemNameField = (TextView) itemView.findViewById(R.id.item_name);
        }
    }
}
