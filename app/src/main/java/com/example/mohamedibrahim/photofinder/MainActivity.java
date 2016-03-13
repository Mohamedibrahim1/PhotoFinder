package com.example.mohamedibrahim.photofinder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    private MainActivity mInstance = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FetchPhotoTask photoTask = new FetchPhotoTask();
        photoTask.execute();
    }


    public class FetchPhotoTask extends AsyncTask<String, Void, String[]> {


        final String LOG_TAG = MainActivity.class.getSimpleName();

        final String BASE_URL = "https://api.flickr.com/services/rest/?";
        final String KEY_PARAM = "&api_key=";
        final String METHOD_PARAM = "&method=";
        final String FORMAT_PARAM = "&format=";
        final String PER_PAGE_PARAM = "&per_page=";

        final String PHOTO_ID = "id";
        final String PHOTO_OWNER = "owner";
        final String SECRET = "secret";
        final String SERVER = "server";
        final String FARM = "farm";
        final String TITLE = "title";
        final String FARM_PARAM = "farm";
        final ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();
        final String BASE_IMAGE_PARAM = ".static.flickr.com/";
        String key = "61c16fdd001b573394f961883bdf4ba6";
        String method = "flickr.photos.getRecent";
        String format = "json";
        int per_page = 10;

        final String url = BASE_URL + KEY_PARAM + key + METHOD_PARAM + method + FORMAT_PARAM + format + PER_PAGE_PARAM + per_page;

        final ArrayList<String> ownerList = new ArrayList<String>();
        final List<PhotoItem> rowItems = new ArrayList<PhotoItem>();
        final ListView listView = (ListView) findViewById(R.id.list);
        RequestQueue queue = Volley.newRequestQueue(mInstance);


        @Override
        protected String[] doInBackground(String... params) {
            try {
                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {

                                    JSONObject json = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1));

                                    // These are the names of the JSON objects that need to be extracted.
                                    JSONObject photos = json.getJSONObject("photos");
                                    JSONArray photoArray = photos.getJSONArray("photo");
                                    Log.v(LOG_TAG, "DATA3 " + photoArray.toString());

                                    for (int i = 0; i < response.length(); i++) {
                                        JSONObject photo = (JSONObject) photoArray.get(i);
                                        String id = photo.getString(PHOTO_ID);
                                        final String owner = photo.getString(PHOTO_OWNER);
                                        String secret = photo.getString(SECRET);
                                        String server = photo.getString(SERVER);
                                        String farm = photo.getString(FARM);
                                        final String title = photo.getString(TITLE);

                                        ownerList.add(owner); //this adds an element to the list.

                                        String image_url = "http://" + FARM_PARAM + farm + BASE_IMAGE_PARAM + server + "/" + id + "_" + secret + "_m.jpg";
                                        Log.v(LOG_TAG, "image url : " + image_url);

                                        // Retrieves an image specified by the URL, displays it in the UI.
                                        ImageRequest request = new ImageRequest(image_url,
                                                new Response.Listener<Bitmap>() {

                                                    @Override
                                                    public void onResponse(final Bitmap bitmap) {
                                                        PhotoItem item = new PhotoItem(bitmap, title);
                                                        rowItems.add(item);
                                                        MyAdapter adapter = new MyAdapter(mInstance,
                                                                R.layout.photo_list, rowItems);
                                                        listView.setAdapter(adapter);
                                                    }
                                                }, 0, 0, null,
                                                new Response.ErrorListener() {
                                                    public void onErrorResponse(VolleyError error) {
                                                        //mImageView.setImageResource(R.mipmap.ic_launcher);
                                                    }
                                                });
                                        MySingleton.getInstance(mInstance).addToRequestQueue(request);
                                    }

                                } catch (JSONException e) {
                                    Log.v(LOG_TAG, "JSON Error: " + e);
                                }

                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                                        String s = ownerList.get(position);
                                        intent.putExtra("owner", s);
                                        startActivity(intent);
                                    }
                                });
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v(LOG_TAG, "Response Error: " + error);
                    }
                });
                // Add the request to the RequestQueue.
                queue.add(stringRequest);

            } catch (Exception e) {
                Log.e(LOG_TAG, "Error ", e);
            }
            return null;
        }
    }


    public void onClickSearch(View view) {
        //ImageButton searchBtn = (ImageButton) findViewById(R.id.searchBtn);
        TextView searchText = (TextView) findViewById(R.id.searchText);
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("searchText", searchText.getText().toString());
        startActivity(intent);
    }
}