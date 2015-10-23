/**
 * Copyright (c) 2015 FeedHenry Ltd, All Rights Reserved.
 *
 * Please refer to your contract with FeedHenry for the software license agreement.
 * If you do not have a contract, you do not have a license to use this software.
 */
package com.feedhenry.sync.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.feedhenry.sdk.sync.FHSyncClient;
import com.feedhenry.sdk.sync.FHSyncConfig;
import com.feedhenry.sdk.sync.FHSyncListener;
import com.feedhenry.sdk.sync.NotificationMessage;
import com.feedhenry.sync.R;
import com.feedhenry.sync.helper.SwipeTouchHelper;
import com.feedhenry.sync.listener.RecyclerItemClickListener;
import com.feedhenry.sync.model.ShoppingItem;
import com.feedhenry.sync.adapter.ShoppingItemAdapter;

import org.json.fh.JSONObject;

import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;

public class ListOfItemsActivity extends AppCompatActivity {

    private static final String TAG = "FHSyncActivity";
    private static final String DATA_ID = "myShoppingList";

    private ShoppingItemAdapter adapter = new ShoppingItemAdapter();

    private RecyclerView list;
    private FHSyncClient syncClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_of_items_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        list = (RecyclerView) findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
        list.addOnItemTouchListener(new RecyclerItemClickListener(
                getApplicationContext(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        showPopup(adapter.getItem(position));
                    }
                }
        ));

        SwipeTouchHelper callback = new SwipeTouchHelper(new SwipeTouchHelper.OnItemSwipeListener() {
            @Override
            public void onItemSwipe(ShoppingItem item) {
                deleteItem(item);
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(list);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(new ShoppingItem());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        fireSync();
    }

    private void fireSync() {

        this.syncClient = FHSyncClient.getInstance();

        //create a new instance of sync config
        FHSyncConfig config = new FHSyncConfig();
        config.setNotifySyncStarted(true);
        config.setNotifyLocalUpdateApplied(true);
        config.setAutoSyncLocalUpdates(true);
        config.setNotifyDeltaReceived(true);
        config.setNotifySyncComplete(true);
        config.setUseCustomSync(false);
        config.setSyncFrequency(10);

        //initialize the sync client
        syncClient.init(getApplicationContext(), config, new FHSyncListener() {

            @Override
            //On sync complete, list all the data and update the adapter
            public void onSyncCompleted(NotificationMessage pMessage) {
                Log.d(TAG, "syncClient - onSyncCompleted");
                Log.d(TAG, "Sync message: " + pMessage.getMessage());

                JSONObject allData = syncClient.list(DATA_ID);
                Iterator<String> it = allData.keys();
                TreeSet<ShoppingItem> itemsToSync = new TreeSet<ShoppingItem>();

                while (it.hasNext()) {
                    String key = it.next();
                    JSONObject data = allData.getJSONObject(key);
                    JSONObject dataObj = data.getJSONObject("data");
                    String name = dataObj.optString("name", "NO name");
                    if (name.startsWith("N")) {
                        Log.d(TAG, "Sync Complete Name : " + name);
                    }
                    String created = dataObj.optString("created", "no date");
                    ShoppingItem item = new ShoppingItem(key, name, created);
                    itemsToSync.add(item);
                }

                adapter.removeMissingItemsFrom(itemsToSync);
                adapter.addNewItemsFrom(itemsToSync);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLocalUpdateApplied(NotificationMessage pMessage) {
                Log.d(TAG, "syncClient - onLocalUpdateApplied");

                JSONObject allData = syncClient.list(DATA_ID);

                Iterator<String> it = allData.keys();
                TreeSet<ShoppingItem> itemsToSync = new TreeSet<ShoppingItem>();

                while (it.hasNext()) {
                    String key = it.next();
                    JSONObject data = allData.getJSONObject(key);
                    JSONObject dataObj = data.getJSONObject("data");
                    String name = dataObj.optString("name", "NO name");
                    if (name.startsWith("N")) {
                        Log.d(TAG, "Local Name : " + name);
                    }
                    String created = dataObj.optString("created", "no date");
                    ShoppingItem item = new ShoppingItem(key, name, created);
                    itemsToSync.add(item);
                }

                adapter.removeMissingItemsFrom(itemsToSync);
                adapter.addNewItemsFrom(itemsToSync);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onDeltaReceived(NotificationMessage pMessage) {
                Log.d(TAG, "syncClient - onDeltaReceived");
                Log.d(TAG, "syncClient : " + pMessage.toString());
            }

            @Override
            public void onUpdateOffline(NotificationMessage pMessage) {
                Log.d(TAG, "syncClient - onUpdateOffline");
            }

            @Override
            public void onSyncStarted(NotificationMessage pMessage) {
                Log.d(TAG, "syncClient - onSyncStarted");
            }

            @Override
            public void onSyncFailed(NotificationMessage pMessage) {
                Log.d(TAG, "syncClient - onSyncFailed");
            }

            @Override
            public void onRemoteUpdateFailed(NotificationMessage pMessage) {
                Log.d(TAG, "syncClient - onRemoteUpdateFailed");

            }

            @Override
            public void onRemoteUpdateApplied(NotificationMessage pMessage) {
                Log.d(TAG, "syncClient - onRemoteUpdateApplied");

            }

            @Override
            public void onCollisionDetected(NotificationMessage pMessage) {
                Log.d(TAG, "syncClient - onCollisionDetected");
            }

            @Override
            public void onClientStorageFailed(NotificationMessage pMessage) {
                Log.d(TAG, "syncClient - onSyncFailed");
            }

        });

        // Start the sync process
        try {
            syncClient.manage(DATA_ID, null, new JSONObject());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

    }

    private void showPopup(final ShoppingItem item) {
        final View customView = View.inflate(getApplicationContext(), R.layout.form_item_dialog, null);
        final EditText name = (EditText) customView.findViewById(R.id.name);
        name.setText(item.getName());

        new MaterialDialog.Builder(this)
                .title(R.string.shopping_item)
                .customView(customView, false)
                .positiveText(R.string.save)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        item.setName(name.getText().toString());
                        saveItem(item);
                    }
                })
                .negativeText(R.string.cancel)
                .show();
    }

    private void saveItem(ShoppingItem item) {

        JSONObject data = new JSONObject();
        data.put("name", item.getName());

        try {

            if (item.getId() == null) {
                data.put("created", String.valueOf(new Date().getTime()));
                syncClient.create(DATA_ID, data);
            } else {
                data.put("created", item.getCreated());
                syncClient.update(DATA_ID, item.getId(), data);
            }
        } catch (Exception e) {
            Log.e(TAG, "failed to data data: " + data.toString(), e);
        }

    }

    private void deleteItem(ShoppingItem item) {
        try {
            syncClient.delete(DATA_ID, item.getId());
        } catch (Exception e) {
            Log.e(TAG, "failed to delete data: " + item.getId(), e);
        }
    }

}
