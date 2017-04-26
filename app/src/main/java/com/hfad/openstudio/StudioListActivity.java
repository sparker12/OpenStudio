package com.hfad.openstudio;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StudioListActivity extends ListActivity {

    private ProgressDialog progressDialog;
    private static String GET_ALL_STUDIOS_PATH = "get_all_studios.php";
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_STUDIOS = "studios";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_TYPE = "type";

    HttpRequestHandler httpRequestHandler = new HttpRequestHandler();

    ArrayList<HashMap<String,String>> studioList;

    JSONArray studiosArray = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studio_list);

        studioList = new ArrayList<HashMap<String, String>>();
        new LoadAllStudios().execute();
    }
    //If a product has been changed the page will be reloaded.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == 100){
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }

    class LoadAllStudios extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(StudioListActivity.this);
            progressDialog.setMessage("Loading Studios. Please wait...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        protected String doInBackground(String...args) {
            Map<String, String> params = new HashMap<>();

            JSONObject json = httpRequestHandler.makeHttpRequest(GET_ALL_STUDIOS_PATH, "GET", params);
            Log.d("All Studios:", json.toString());

            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    studiosArray = json.getJSONArray(TAG_STUDIOS);

                    for (int i = 0; i < studiosArray.length(); i++) {
                        JSONObject jsonObject = studiosArray.getJSONObject(i);

                        String id = jsonObject.getString(TAG_ID);
                        String name = jsonObject.getString(TAG_NAME);
                        String type = jsonObject.getString(TAG_TYPE);

                        HashMap<String, String> hashMap = new HashMap<String, String>();
                        hashMap.put(TAG_ID, id);
                        hashMap.put(TAG_NAME, name);
                        hashMap.put(TAG_TYPE,type);
                        studioList.add(hashMap);
                    }
                }else {
                    Intent intent = new Intent(getApplicationContext(), AddStudioActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url){
            progressDialog.dismiss();
            ListAdapter adapter = new SimpleAdapter(
                    StudioListActivity.this, studioList,
                    R.layout.single_studio_list, new String[]{
                    TAG_ID, TAG_NAME,TAG_TYPE},
                    new int[]{R.id.id, R.id.name, R.id.type});
            setListAdapter(adapter);
        }

    }

}




