/**
 * Copyright (c) 2015 FeedHenry Ltd, All Rights Reserved.
 *
 * Please refer to your contract with FeedHenry for the software license agreement.
 * If you do not have a contract, you do not have a license to use this software.
 */
package com.feedhenry.sync.item.gesturehelper;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.feedhenry.sync.item.ShoppingItemAdapter;

import java.util.HashMap;

/**
 * Callback helper for showing/hiding the delete option
 */
public class UncoverDeleteGestureCallback extends ItemTouchHelper.Callback {


    private HashMap<Long, Integer> swipeFlags = new HashMap<>();
    private int swipeDistance = -1;

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    /**
     * By default each item should only swipe from the END.  However if an item's delete icon is visible
     * then it should swipe from the right.  Swipe Flags manages the state of the direction.
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = 0; //no Drag
        ShoppingItemAdapter.ShoppingItemViewHolder shoppingItemsHolder = ((ShoppingItemAdapter.ShoppingItemViewHolder) viewHolder);
        Long itemId = shoppingItemsHolder.getItemId();
        if (!swipeFlags.containsKey(itemId)) {
            swipeFlags.put(itemId, ItemTouchHelper.START);
        }
        return makeMovementFlags(dragFlags, swipeFlags.get(itemId));
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        ShoppingItemAdapter.ShoppingItemViewHolder shoppingItemsHolder = ((ShoppingItemAdapter.ShoppingItemViewHolder) viewHolder);
        Long itemId = shoppingItemsHolder.getItemId();
        if (swipeDistance == -1) {
            calculateSwipeDistance(shoppingItemsHolder);
        }
        if (!swipeFlags.containsKey(itemId)) {
            swipeFlags.put(itemId, ItemTouchHelper.START);
        }
        int flags = swipeFlags.get(itemId);

        switch (flags) {
            case ItemTouchHelper.END:
                shoppingItemsHolder.getTopLayerView().setTranslationX(0);
                swipeFlags.put(itemId, ItemTouchHelper.START);
                break;
            case ItemTouchHelper.START:
                shoppingItemsHolder.getTopLayerView().setTranslationX(-1 * swipeDistance );
                swipeFlags.put(itemId, ItemTouchHelper.END);
                break;
            default:
                break;
        }

    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {


        ShoppingItemAdapter.ShoppingItemViewHolder shoppingItemsHolder = ((ShoppingItemAdapter.ShoppingItemViewHolder) viewHolder);
        Long itemId = shoppingItemsHolder.getItemId();

        if (swipeDistance == -1) {
            calculateSwipeDistance(shoppingItemsHolder);
        }
        if (!swipeFlags.containsKey(itemId)) {
            swipeFlags.put(itemId, ItemTouchHelper.START);
        }

        if (!isCurrentlyActive && Math.abs(shoppingItemsHolder.getTopLayerView().getTranslationX()) >= swipeDistance) {
            return;
        }

        int flags = swipeFlags.get(itemId);
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE ) {
            switch (flags) {
                case ItemTouchHelper.END:
                    if (Math.abs(dX) > swipeDistance ) {
                        shoppingItemsHolder.getTopLayerView().setTranslationX(0);
                    } else if (dX < 0) {
                        shoppingItemsHolder.getTopLayerView().setTranslationX(-1 * swipeDistance);
                    } else {
                        shoppingItemsHolder.getTopLayerView().setTranslationX(-1 * swipeDistance + dX);
                    }
                break;
                case ItemTouchHelper.START:
                    if (Math.abs(dX) < swipeDistance ) {
                        shoppingItemsHolder.getTopLayerView().setTranslationX(dX);
                    } else if (dX < 0) {
                        shoppingItemsHolder.getTopLayerView().setTranslationX(-1 * swipeDistance);
                    }
                break;
                default:
                break;
            }

        }
    }

    private void calculateSwipeDistance(ShoppingItemAdapter.ShoppingItemViewHolder shoppingItemsHolder) {
        swipeDistance = shoppingItemsHolder.getDeleteIconMesauredWidth();;
    }
}
