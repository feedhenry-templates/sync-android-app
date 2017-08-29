/**
 * Copyright 2015 Red Hat, Inc., and individual contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.feedhenry.sync.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.feedhenry.sync.R;
import com.feedhenry.sync.model.CollisionItem;

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
public class CollisionItemAdapter
        extends RecyclerView.Adapter<CollisionItemAdapter.CollisionItemViewHolder> {

    private final List<CollisionItem> items = new ArrayList<CollisionItem>();

    public CollisionItemAdapter() {
        super.setHasStableIds(true);
    }

    @Override
    public CollisionItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View collisionItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.collision_item, parent, false);

        return new CollisionItemViewHolder(collisionItemView);

    }

    @Override
    public void onBindViewHolder(CollisionItemViewHolder holder, int position) {

        CollisionItem collisionItem = getItem(position);

        holder.item = collisionItem;

        holder.itemIdField.setText(collisionItem.getId());

        try {

            Date createdDate = new Date(Long.parseLong(collisionItem.getDate()));

            SimpleDateFormat format;
            if (createdDate.after(today().getTime())) {
                format = new SimpleDateFormat("hh:mm a", Locale.US);
            } else {
                format = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
            }

            holder.itemDateField.setText(format.format(createdDate));
            holder.itemDateField.setVisibility(View.VISIBLE);

        } catch (Exception ignore) {
            holder.itemDateField.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getFHhash();
    }

    public CollisionItem getItem(int position) {
        return items.get(position);
    }

    private Calendar today() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        return today;
    }

    /**
     * This method will add items from the adapters internal storage that are in itemsToSync but not
     * the internal store
     *
     * @param itemsToSync the currentViewOfTheSyncData
     */
    public void addNewItemsFrom(Set<CollisionItem> itemsToSync) {
        for (CollisionItem item : itemsToSync) {
            if (!items.contains(item)) {
                items.add(item);
            }
        }
        Collections.sort(items);
    }

    /**
     * This method will remove items from the adapters internal storage that are not in itemsToSync
     *
     * @param itemsToSync the currentViewOfTheSyncData
     */
    public void removeMissingItemsFrom(Set<CollisionItem> itemsToSync) {
        items.retainAll(itemsToSync);
        Collections.sort(items);
    }

    public class CollisionItemViewHolder extends RecyclerView.ViewHolder {
        protected final TextView itemIdField;
        protected final TextView itemNameField;
        protected final TextView itemDateField;

        protected CollisionItem item;

        public CollisionItemViewHolder(View itemView) {
            super(itemView);
            itemNameField = (TextView) itemView.findViewById(R.id.item_name);
            itemDateField = (TextView) itemView.findViewById(R.id.item_date);
            itemIdField = (TextView) itemView.findViewById(R.id.item_id);
        }

        public CollisionItem getItem() {
            return item;
        }
    }

}

