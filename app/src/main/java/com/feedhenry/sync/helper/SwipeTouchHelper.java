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
package com.feedhenry.sync.helper;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.feedhenry.sync.adapter.ShoppingItemAdapter;
import com.feedhenry.sync.model.ShoppingItem;

public class SwipeTouchHelper extends ItemTouchHelper.SimpleCallback {

    private final OnItemSwipeListener listener;

    public SwipeTouchHelper(OnItemSwipeListener listener) {
        super(0, ItemTouchHelper.RIGHT);
        this.listener = listener;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView,
                          RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
        ShoppingItemAdapter.ShoppingItemViewHolder v =
                (ShoppingItemAdapter.ShoppingItemViewHolder) viewHolder;
        listener.onItemSwipe(v.getItem());
    }

    public interface OnItemSwipeListener {
        public void onItemSwipe(ShoppingItem item);
    }

}
