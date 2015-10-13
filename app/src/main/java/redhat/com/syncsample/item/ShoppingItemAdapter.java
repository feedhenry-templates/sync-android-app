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
 * This class maps a List of Shopping Items into a View in a RecyclerView
 */
public class ShoppingItemAdapter extends RecyclerView.Adapter<ShoppingItemAdapter.ViewHolder> {

    private final List<ShoppingItem> items = new ArrayList<>();
    private ShoppingItemSelectHandler itemSelectHandler = null;

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

    public void addShoppingItemSelectHandler(ShoppingItemSelectHandler itemSelectHandler) {
        this.itemSelectHandler = itemSelectHandler;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView itemNameField;
        public ViewHolder(View itemView) {
            super(itemView);
            itemNameField = (TextView) itemView.findViewById(R.id.item_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemSelectHandler != null) {
                        itemSelectHandler.shoppingItemSelected(getShoppingItem(getAdapterPosition()));
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (itemSelectHandler != null) {
                        itemSelectHandler.shoppingItemLongSelected(getShoppingItem(getAdapterPosition()));
                    }
                    return true;
                }
            });
        }
    }
}
