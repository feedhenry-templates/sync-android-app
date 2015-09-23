package redhat.com.syncsample;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import java.util.Date;
import java.util.Iterator;
import org.json.fh.JSONObject;

public class MainActivity extends ListActivity {
  private static final String TAG = "FHSyncActivity";
  public static final String DATAID = "myShoppingList";
  ArrayAdapter<ShoppingItem> adapter = null;
  FHSyncClient syncClient = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    adapter = new ArrayAdapter<ShoppingItem>(this,R.layout.activity_sync);
    getListView().setAdapter(adapter);
    final Context that = this;
    FH.init(this, new FHActCallback() {
      @Override public void success(FHResponse pResponse) {
        syncClient = FHSyncClient.getInstance();

        //create a new instance of sync config
        FHSyncConfig config = new FHSyncConfig();
        config.setNotifySyncStarted(true);
        config.setAutoSyncLocalUpdates(true);
        config.setNotifyDeltaReceived(true);
        config.setNotifySyncComplete(true);

        //initialize the sync client
        syncClient.init(that, config, new FHSyncListener() {

          @Override
          public void onUpdateOffline(NotificationMessage pMessage) {
            // TODO Auto-generated method stub

          }

          @Override
          public void onSyncStarted(NotificationMessage pMessage) {
            // TODO Auto-generated method stub
            Log.d(TAG, "SYNC LOOP STARTED!!");
          }

          @Override
          public void onSyncFailed(NotificationMessage pMessage) {
            // TODO Auto-generated method stub

          }

          @Override
          //On sync complete, list all the data and update the adapter
          public void onSyncCompleted(NotificationMessage pMessage) {
            Log.d(TAG, "Sync complete: " + pMessage.getMessage());
            JSONObject alldata = syncClient.list(DATAID);
            adapter.clear();
            Iterator<String> it = alldata.keys();
            while(it.hasNext()){
              String key = it.next();
              JSONObject data = alldata.getJSONObject(key);
              JSONObject dataObj = data.getJSONObject("data");
              String name = dataObj.optString("name", "NO name");
              String created = dataObj.optString("created", "no date");
              ShoppingItem item = new ShoppingItem(key, name, created);
              adapter.add(item);
            }
          }

          @Override
          public void onRemoteUpdateFailed(NotificationMessage pMessage) {
            // TODO Auto-generated method stub

          }

          @Override
          public void onRemoteUpdateApplied(NotificationMessage pMessage) {
            // TODO Auto-generated method stub

          }

          @Override
          public void onLocalUpdateApplied(NotificationMessage pMessage) {
            // TODO Auto-generated method stub

          }

          @Override
          public void onDeltaReceived(NotificationMessage pMessage) {
            // TODO Auto-generated method stub

          }

          @Override
          public void onCollisionDetected(NotificationMessage pMessage) {
            // TODO Auto-generated method stub

          }

          @Override
          public void onClientStorageFailed(NotificationMessage pMessage) {
            // TODO Auto-generated method stub

          }

        });

        //start the sync process
        try {
          syncClient.manage(DATAID, null, new JSONObject());
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      @Override public void fail(FHResponse pResponse) {

      }
    });

  }

  public boolean onCreateOptionsMenu(Menu menu){

    return true;
  }



  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if(1 == requestCode || 2 == requestCode){
      if(RESULT_OK == resultCode){
        String action = data.getStringExtra("com.feedhenry.fhandroidexampleapp.action");

        if("save".equalsIgnoreCase(action)){
          String name = data.getStringExtra("com.feedhenry.fhandroidexampleapp.name");
          if(1 == requestCode){
            String uid = data.getStringExtra("com.feedhenry.fhandroidexampleapp.uid");
            String created = data.getStringExtra("com.feedhenry.fhandroidexampleapp.created");
            JSONObject updated = new JSONObject();
            updated.put("name", name);
            updated.put("created", created);
            try {
              syncClient.update(DATAID, uid, updated);
            } catch (Exception e) {
              Log.e(TAG, "failed to update data: " + updated.toString(), e);
            }
          } else if(2 == requestCode){
            JSONObject create = new JSONObject();
            create.put("name", name);
            create.put("created", new Date().getTime());
            try{
              syncClient.create(DATAID, create);
            } catch(Exception e){
              Log.e(TAG, "failed to create data: " + create.toString(), e);
            }
          }

        } else if("delete".equalsIgnoreCase(action)){
          String uid = data.getStringExtra("com.feedhenry.fhandroidexampleapp.uid");
          try{
            syncClient.delete(DATAID, uid);
          } catch (Exception e) {
            Log.e(TAG, "failed to delete data: " + uid, e);
          }
        }
      }
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

    public ShoppingItem(String pId, String pName, String pCreated){
      itemId = pId;
      itemName = pName;
      itemCreated = pCreated;
    }

    public String getId(){
      return itemId;
    }

    public String getName(){
      return itemName;
    }

    public String getCreated(){
      return itemCreated;
    }

    public String toString(){
      return itemName;
    }
  }
}
