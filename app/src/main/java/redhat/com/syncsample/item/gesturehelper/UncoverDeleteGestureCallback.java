package redhat.com.syncsample.item.gesturehelper;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.SparseArray;

import java.util.HashMap;

import redhat.com.syncsample.item.ShoppingItem;
import redhat.com.syncsample.item.ShoppingItemAdapter;

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
                Log.d("END", "Flipping to start"+ "name:" + shoppingItemsHolder.getItemNameField().getText());
                shoppingItemsHolder.getItemNameField().setTranslationX(0);
                swipeFlags.put(itemId, ItemTouchHelper.START);
                break;
            case ItemTouchHelper.START:
                Log.d("START", "Flipping to end"+ "name:" + shoppingItemsHolder.getItemNameField().getText());
                shoppingItemsHolder.getItemNameField().setTranslationX(-1 * swipeDistance );
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

        if (!isCurrentlyActive && Math.abs(shoppingItemsHolder.getItemNameField().getTranslationX()) >= swipeDistance) {
            return;
        }

        int flags = swipeFlags.get(itemId);
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE ) {
            switch (flags) {
                case ItemTouchHelper.END:
                    Log.d("END", dX+ ":iscurrentlyActive:" + isCurrentlyActive + "name:" + shoppingItemsHolder.getItemNameField().getText());
                    if (Math.abs(dX) > swipeDistance ) {
                        shoppingItemsHolder.getItemNameField().setTranslationX(0);
                    } else if (dX < 0) {
                        shoppingItemsHolder.getItemNameField().setTranslationX(-1 * swipeDistance );
                    } else {
                        shoppingItemsHolder.getItemNameField().setTranslationX(-1 * swipeDistance  + dX);
                    }
                break;
                case ItemTouchHelper.START:
                    Log.d("START", dX+ ":iscurrentlyActive:" + isCurrentlyActive+ "name:" + shoppingItemsHolder.getItemNameField().getText());
                    if (Math.abs(dX) < swipeDistance ) {
                        shoppingItemsHolder.getItemNameField().setTranslationX(dX);
                    } else if (dX < 0) {
                        shoppingItemsHolder.getItemNameField().setTranslationX(-1 * swipeDistance );
                    }
                break;
                default:
                break;
            }

        }
    }

    private void calculateSwipeDistance(ShoppingItemAdapter.ShoppingItemViewHolder shoppingItemsHolder) {
        swipeDistance = 120;
    }
}
