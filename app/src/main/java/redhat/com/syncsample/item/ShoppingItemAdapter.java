/**
 * Copyright (c) 2015 FeedHenry Ltd, All Rights Reserved.
 * <p>
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import redhat.com.syncsample.R;

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
        return items.get(position).hashCode();
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
        protected TextView itemNameField;
        private View view;

        public ShoppingItemViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
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

        public TextView getItemNameField() {
            return itemNameField;
        }

        public int getDeleteIconMesauredWidth() {
            return view.findViewById(R.id.delete_icon).getWidth();
        }
    }
}
