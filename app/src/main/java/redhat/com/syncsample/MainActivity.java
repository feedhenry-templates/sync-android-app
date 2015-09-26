package redhat.com.syncsample;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.feedhenry.sdk.FH;
import com.feedhenry.sdk.FHActCallback;
import com.feedhenry.sdk.FHResponse;
import com.feedhenry.sdk.sync.FHSyncClient;
import com.feedhenry.sdk.sync.FHSyncConfig;
import com.feedhenry.sdk.sync.FHSyncListener;
import com.feedhenry.sdk.sync.NotificationMessage;

import org.json.fh.JSONObject;

import java.util.Iterator;

public class MainActivity extends ListActivity {

    private static final String TAG = "FHSyncActivity";
    public static final String DATA_ID = "myShoppingList";

    private ArrayAdapter<ShoppingItem> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new ArrayAdapter<ShoppingItem>(this, R.layout.activity_sync);
        getListView().setAdapter(adapter);

        FH.init(this, new FHActCallback() {
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
        config.setUseCustomSync(true);

        //initialize the sync client
        syncClient.init(getApplicationContext(), config, new FHSyncListener() {

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

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        ShoppingItem item = adapter.getItem(position);
        Log.d(TAG, "Selected item: " + item);
    }

    private class ShoppingItem {
        private String itemId;
        private String itemName;
        private String itemCreated;

        public ShoppingItem(String pId, String pName, String pCreated) {
            itemId = pId;
            itemName = pName;
            itemCreated = pCreated;
        }

        public String getId() {
            return itemId;
        }

        public String getName() {
            return itemName;
        }

        public String getCreated() {
            return itemCreated;
        }

        public String toString() {
            return itemName;
        }
    }
}
