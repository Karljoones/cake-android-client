package com.waracle.androidtest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.waracle.androidtest.fragments.CakeListFragment;
import com.waracle.androidtest.fragments.LoadingFragment;
import com.waracle.androidtest.templates.Cake;
import com.waracle.androidtest.utils.ImageLoader;
import com.waracle.androidtest.utils.StreamUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static String JSON_URL = "https://gist.githubusercontent.com/hart88/198f29ec5114a3ec3460/" +
            "raw/8dd19a88f9b8d24c23d9960f3300d0c917a4f07c/cake.json";

    private ArrayList<Cake> cakes;

    /**
     * Returns the charset specified in the Content-Type of this header,
     * or the HTTP default (ISO-8859-1) if none can be found.
     */
    public static String parseCharset(String contentType) {
        if (contentType != null) {
            String[] params = contentType.split(",");
            for (int i = 1; i < params.length; i++) {
                String[] pair = params[i].trim().split("=");
                if (pair.length == 2) {
                    if (pair[0].equals("charset")) {
                        return pair[1];
                    }
                }
            }
        }
        return "UTF-8";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cakes = new ArrayList<>();

        if (savedInstanceState == null) {
            // Download the data for the application on a separate thread.
            new DownloadData().execute();
        } else {
            // This will solve the issue with app rotation.
            // If there is a saved instance, such as the items, then reinsert these into the list without reloading them with another call.
            cakes = (ArrayList<Cake>) savedInstanceState.getSerializable("items");
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new CakeListFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the items into the Bundle, this prevents reloading of the information.
        outState.putSerializable("items", cakes);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                new DownloadData().execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This is used by the fragments to get access to the list of items that are stored in the main activity.
     * @return list of Cake items.
     */
    public ArrayList<Cake> getCakes() {
        return cakes;
    }

    private JSONArray loadData() throws IOException, JSONException {
        URL url = new URL(JSON_URL);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            byte[] bytes = StreamUtils.readUnknownFully(in);

            // Read in charset of HTTP content.
            String charset = parseCharset(urlConnection.getRequestProperty("Content-Type"));

            // Convert byte array to appropriate encoded string.
            String jsonText = new String(bytes, charset);

            // Read string as JSON.
            return new JSONArray(jsonText);
        } finally {
            urlConnection.disconnect();
        }
    }

    private class DownloadData extends AsyncTask<Void, Void, ArrayList<Cake>> {
        ImageLoader mImageLoader = new ImageLoader();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            getSupportFragmentManager().beginTransaction().replace(R.id.container, new LoadingFragment()).commit();
        }

        @Override
        protected ArrayList<Cake> doInBackground(Void... voids) {

            // Load data from net.
            try {
                JSONArray array = loadData();
                ArrayList<Cake> cakes = new ArrayList<>();

                // Get the title, description and imageData from the object in the background thread, this ensures that not all the information is processed on the UI thread.
                for (int i = 0; i < array.length(); i++) {
                    Cake cake = new Cake();
                    JSONObject obj = array.getJSONObject(i);
                    cake.setTitle(obj.getString("title"));
                    cake.setDesc(obj.getString("desc"));

                    try {
                        cake.setImageData(mImageLoader.loadImageData(obj.getString("image")));
                    } catch (IOException e) {
                        Log.e(getClass().getSimpleName(), "Error loading image data");
                    }

                    // Add it to the list of cakes.
                    cakes.add(cake);
                }

                return cakes;
            } catch (IOException | JSONException e) {
                Log.e(getClass().getSimpleName(), e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Cake> array) {
            cakes = array;
            // Show the cake list fragment.
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new CakeListFragment()).commit();
        }
    }
}
