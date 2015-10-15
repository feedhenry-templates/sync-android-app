/**
 * Copyright (c) 2015 FeedHenry Ltd, All Rights Reserved.
 *
 * Please refer to your contract with FeedHenry for the software license agreement.
 * If you do not have a contract, you do not have a license to use this software.
 */
package redhat.com.syncsample.view;

import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.feedhenry.sdk.FH;
import com.feedhenry.sdk.FHActCallback;
import com.feedhenry.sdk.FHHttpClient;
import com.feedhenry.sdk.FHResponse;
import com.feedhenry.sdk.sync.FHSyncClient;
import com.feedhenry.sdk.sync.FHSyncConfig;
import com.feedhenry.sdk.sync.FHSyncListener;
import com.feedhenry.sdk.sync.NotificationMessage;

import org.json.fh.JSONObject;

import java.util.Iterator;
import java.util.TreeSet;

import cz.msebera.android.httpclient.HttpHost;
import redhat.com.syncsample.R;
import redhat.com.syncsample.item.ShoppingItem;
import redhat.com.syncsample.item.ShoppingItemAdapter;
import redhat.com.syncsample.item.ShoppingItemSelectHandler;
import redhat.com.syncsample.item.gesturehelper.UncoverDeleteGestureCallback;

/**
 * This class sets up synchronization, displays Shopping Items, and sends events to the sync system.
 */
public class ListItemsFragment extends Fragment implements ShoppingItemSelectHandler {


    private static final String TAG = "FHSyncActivity";
    public static final String DATA_ID = "myShoppingList";


    private RecyclerView list;
    private ShoppingItemAdapter adapter = new ShoppingItemAdapter();
    private FHSyncClient syncClient;

    public ListItemsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_main, container, false);
        list = (RecyclerView) contentView.findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        list.setAdapter(adapter);
        adapter.addShoppingItemSelectHandler(this);

        UncoverDeleteGestureCallback callback = new UncoverDeleteGestureCallback();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(list);

        FloatingActionButton fab = (FloatingActionButton) contentView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateItemDialogFragment newFragment = CreateItemDialogFragment.newInstance();
                newFragment.show(getFragmentManager(), "dialog");
                newFragment.setCreateHandler(ListItemsFragment.this);
            }
        });
        return contentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FH.init(getActivity(), new FHActCallback() {
            @Override
            public void success(FHResponse pResponse) {
                Log.d(TAG, "FH.init - success");
                FHHttpClient.setTimeout(40 * 1000);
                //FHHttpClient.setHttpProxy(new HttpHost("10.0.2.2", 8888, "http"));
                fireSync();
            }

            @Override
            public void fail(FHResponse pResponse) {
                Log.d(TAG, "FH.init - fail");
                Log.e(TAG, pResponse.getErrorMessage(), pResponse.getError());
            }
        });
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
        syncClient.init(getActivity().getApplicationContext(), config, new FHSyncListener() {

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
            public void onRemoteUpdateFailed(NotificationMessage pMessage) {
                Log.d(TAG, "syncClient - onRemoteUpdateFailed");

            }

            @Override
            public void onRemoteUpdateApplied(NotificationMessage pMessage) {
                Log.d(TAG, "syncClient - onRemoteUpdateApplied");

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
            public void onCollisionDetected(NotificationMessage pMessage) {
                Log.d(TAG, "syncClient - onCollisionDetected");
            }

            @Override
            public void onClientStorageFailed(NotificationMessage pMessage) {
                Log.d(TAG, "syncClient - onSyncFailed");
            }

        });

        //start the sync process
        try {
            syncClient.manage(DATA_ID, null, new JSONObject());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public void shoppingItemSelected(ShoppingItem shoppingItem) {
        EditDetailsDialogFragment newFragment = EditDetailsDialogFragment.newInstance(shoppingItem);
        newFragment.show(getFragmentManager(), "dialog");
        newFragment.setSaveHandler(this);
    }

    @Override
    public void shoppingItemLongSelected(final ShoppingItem shoppingItem) {
        new AlertDialog.Builder(getActivity()).setTitle(getResources().getString(R.string.delete_dialog_title)).setMessage("Delete Item :" + shoppingItem.getName() + "?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteItem(shoppingItem.getId());
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    public void saveItem(ShoppingItem shoppingItem, String newName, String newCreated) {
        JSONObject updated = new JSONObject();
        updated.put("name", newName);
        updated.put("created", newCreated);
        try {
            if (syncClient != null) {
                syncClient.update(DATA_ID, shoppingItem.getId(), updated);
            }
        } catch (Exception e) {
            Log.e(TAG, "failed to update data: " + updated.toString(), e);
        }
    }

    public void createItem(String newName, String newCreated) {
        JSONObject create = new JSONObject();
        create.put("name", newName);
        create.put("created", newCreated);
        try{
            syncClient.create(DATA_ID, create);
        } catch(Exception e){
            Log.e(TAG, "failed to create data: " + create.toString(), e);
        }
    }

    public void deleteItem(String itemId) {
        try{
            syncClient.delete(DATA_ID, itemId);
        } catch (Exception e) {
            Log.e(TAG, "failed to delete data: " + itemId, e);
        }
    }
}
