/**
 * Copyright (c) 2015 FeedHenry Ltd, All Rights Reserved.
 *
 * Please refer to your contract with FeedHenry for the software license agreement.
 * If you do not have a contract, you do not have a license to use this software.
 */
package redhat.com.syncsample;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.feedhenry.sdk.FH;
import com.feedhenry.sdk.FHActCallback;
import com.feedhenry.sdk.FHResponse;
import com.feedhenry.sdk.sync.FHSyncClient;
import com.feedhenry.sdk.sync.FHSyncConfig;
import com.feedhenry.sdk.sync.FHSyncListener;
import com.feedhenry.sdk.sync.NotificationMessage;

import org.json.fh.JSONObject;

import java.util.Iterator;

import redhat.com.syncsample.items.ShoppingItem;
import redhat.com.syncsample.items.ShoppingItemAdapter;

public class MainActivityFragment extends Fragment {


    private static final String TAG = "FHSyncActivity";
    public static final String DATA_ID = "myShoppingList";


    private RecyclerView list;
    private ShoppingItemAdapter adapter = new ShoppingItemAdapter();

    public MainActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_main, container, false);
        list = (RecyclerView) contentView.findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        list.setAdapter(adapter);
        return contentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FH.init(getActivity(), new FHActCallback() {
            @Override
            public void success(FHResponse pResponse) {
                Log.d(TAG, "FH.init - success");
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

        final FHSyncClient syncClient = FHSyncClient.getInstance();

        //create a new instance of sync config
        FHSyncConfig config = new FHSyncConfig();
        config.setNotifySyncStarted(true);
        config.setAutoSyncLocalUpdates(true);
        config.setNotifyDeltaReceived(true);
        config.setNotifySyncComplete(true);
        config.setUseCustomSync(false);

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

                adapter.clear();

                JSONObject allData = syncClient.list(DATA_ID);
                Iterator<String> it = allData.keys();
                while (it.hasNext()) {
                    String key = it.next();
                    JSONObject data = allData.getJSONObject(key);
                    JSONObject dataObj = data.getJSONObject("data");
                    String name = dataObj.optString("name", "NO name");
                    String created = dataObj.optString("created", "no date");
                    ShoppingItem item = new ShoppingItem(key, name, created);
                    adapter.add(item);
                }
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
            }

            @Override
            public void onDeltaReceived(NotificationMessage pMessage) {
                Log.d(TAG, "syncClient - onDeltaReceived");
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

}
