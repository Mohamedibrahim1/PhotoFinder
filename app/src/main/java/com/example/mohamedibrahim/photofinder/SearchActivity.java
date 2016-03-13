package com.example.mohamedibrahim.photofinder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends Activity {
    private SearchActivity SInstance = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        FetchSearchTask SearchTask = new FetchSearchTask();
        SearchTask.execute();
    }


    public class FetchSearchTask extends AsyncTask<String, Void, String[]> {
        Intent intent = getIntent();
        String searchText = intent.getStringExtra("searchText");

        final String LOG_TAG = SearchActivity.class.getSimpleName();

        final String BASE_URL = "https://api.flickr.com/services/rest/?";
        final String KEY_PARAM = "&api_key=";
        final String METHOD_PARAM = "&method=";
        final String FORMAT_PARAM = "&format=";
        final String PER_PAGE_PARAM = "&per_page=";
        final String SEARCH_PARAM = "&text=";

        String key = "61c16fdd001b573394f961883bdf4ba6";
        String method = "flickr.photos.search";
        String format = "json";
        int per_page = 10;

        final String url = BASE_URL + KEY_PARAM + key + METHOD_PARAM + method + FORMAT_PARAM + format + PER_PAGE_PARAM + per_page + SEARCH_PARAM + searchText;
        final List<PhotoItem> rowItems = new ArrayList<PhotoItem>();
        final ListView listView = (ListView) findViewById(R.id.listSearch);
        RequestQueue queue = Volley.newRequestQueue(SInstance);

        @Override
        protected String[] doInBackground(String... params) {

            try {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    final String PHOTO_ID = "id";
                                    final String PHOTO_OWNER = "owner";
                                    final String SECRET = "secret";
                                    final String SERVER = "server";
                                    final String FARM = "farm";
                                    final String TITLE = "title";
                                    final String FARM_PARAM = "farm";
                                    final String BASE_IMAGE_PARAM = ".static.flickr.com/";

                                    JSONObject json = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1));

                                    JSONObject photos = json.getJSONObject("photos");
                                    JSONArray photoArray = photos.getJSONArray("photo");

                                    for (int i = 0; i < response.length(); i++) {

                                        JSONObject photo = (JSONObject) photoArray.get(i);
                                        String id = photo.getString(PHOTO_ID);
                                        final String owner = photo.getString(PHOTO_OWNER);
                                        String secret = photo.getString(SECRET);
                                        String server = photo.getString(SERVER);
                                        String farm = photo.getString(FARM);
                                        final String title = photo.getString(TITLE);


                                        String image_url = "http://" + FARM_PARAM + farm + BASE_IMAGE_PARAM + server + "/" + id + "_" + secret + "_m.jpg";

                                        // Retrieves an image specified by the URL, displays it in the UI.
                                        ImageRequest request = new ImageRequest(image_url,
                                                new Response.Listener<Bitmap>() {

                                                    @Override
                                                    public void onResponse(final Bitmap bitmap) {
                                                        PhotoItem item = new PhotoItem(bitmap, title);
                                                        rowItems.add(item);
                                                        MyAdapter adapter = new MyAdapter(getApplicationContext(),
                                                                R.layout.photo_list, rowItems);
                                                        listView.setAdapter(adapter);
                                                    }
                                                }, 0, 0, null,
                                                new Response.ErrorListener() {
                                                    public void onErrorResponse(VolleyError error) {
                                                        //mImageView.setImageResource(R.mipmap.ic_launcher);
                                                    }
                                                });
                                        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
                                    }

                                } catch (JSONException e) {
                                    Log.v(LOG_TAG, "JSON Error: " + e);
                                }
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
}
