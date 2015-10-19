/**
 * Copyright (c) 2015 FeedHenry Ltd, All Rights Reserved.
 *
 * Please refer to your contract with FeedHenry for the software license agreement.
 * If you do not have a contract, you do not have a license to use this software.
 */
package com.feedhenry.sync.item;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.feedhenry.sync.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * This class maps a List of Shopping Items into a View in a RecyclerView
 */
public class ShoppingItemAdapter extends RecyclerView.Adapter<ShoppingItemAdapter.ShoppingItemViewHolder> {

    private final List<ShoppingItem> items = new ArrayList<>();
    private ShoppingItemSelectHandler itemSelectHandler = null;

    public ShoppingItemAdapter() {
        super.setHasStableIds(true);
    }

    @Override
    public ShoppingItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View shoppingItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shopping_item, parent, false);

        ShoppingItemViewHolder holder = new ShoppingItemViewHolder(shoppingItemView);

        return holder;
    }

    @Override
    public void onBindViewHolder(ShoppingItemViewHolder holder, int position) {
        ShoppingItem shoppingItem = getShoppingItem(position);
        holder.itemNameField.setText(shoppingItem.getName());
        holder.itemIdField.setText(shoppingItem.getId());
        holder.isNew = true;
        try {
            Long time = Long.parseLong(shoppingItem.getCreated());
            holder.itemDateField.setVisibility(View.VISIBLE);
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);
            Date createdDate = new Date(time);

            if (createdDate.after(today.getTime())) {
                SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.US);
                holder.itemDateField.setText(format.format(createdDate));
            } else {
                SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
                holder.itemDateField.setText(format.format(createdDate));
            }

        } catch (Exception ignore) {
            holder.itemDateField.setVisibility(View.GONE);
        }
    }

    public ShoppingItem getShoppingItem(int position) {
        return items.get(position);
    }

    public void addShoppingItemSelectHandler(ShoppingItemSelectHandler itemSelectHandler) {
        this.itemSelectHandler = itemSelectHandler;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getFHhash();
    }

    /**
     *
     * This method will remove items from the adapters internal storage that are not in itemsToSync
     *
     * @param itemsToSync the currentViewOfTheSyncData
     */
    public void removeMissingItemsFrom(Set<ShoppingItem> itemsToSync) {
        items.retainAll(itemsToSync);
        Collections.sort(items);
    }

    /**
     *
     * This method will add items from the adapters internal storage that are in itemsToSync but not
     * the internal store
     *
     * @param itemsToSync the currentViewOfTheSyncData
     */
    public void addNewItemsFrom(Set<ShoppingItem> itemsToSync) {
        for (ShoppingItem item : itemsToSync) {
            if (!items.contains(item)) {
                items.add(item);
            }
        }
        Collections.sort(items);
    }

    public class ShoppingItemViewHolder extends RecyclerView.ViewHolder {
        protected final TextView itemNameField;
        protected final TextView itemIdField;
        protected final TextView itemDateField;
        protected final ImageButton deleteButton;
        private final View topLayer;
        private final View view;
        private boolean isNew = true;

        public ShoppingItemViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            itemNameField = (TextView) itemView.findViewById(R.id.item_name);
            itemDateField = (TextView) itemView.findViewById(R.id.item_date);
            itemIdField = (TextView) itemView.findViewById(R.id.item_id);
            deleteButton = (ImageButton) view.findViewById(R.id.delete_icon);
            topLayer = view.findViewById(R.id.top_layer);
            topLayer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemSelectHandler != null) {
                        if (getAdapterPosition() >= 0) {
                            itemSelectHandler.shoppingItemSelected(getShoppingItem(getAdapterPosition()));
                        }
                    }
                }
            });
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemSelectHandler != null) {
                        if (getAdapterPosition() >= 0) {
                            itemSelectHandler.shoppingItemLongSelected(getShoppingItem(getAdapterPosition()));
                        }
                    }
                }
            });

        }

        public View getTopLayerView() {
            return topLayer;
        }

        public int getDeleteIconMesauredWidth() {
            return view.findViewById(R.id.delete_icon).getWidth();
        }

        public void setNew(boolean isNew) {
            this.isNew = isNew;
        }

        public boolean isNew() {
            return isNew;
        }
    }
}

