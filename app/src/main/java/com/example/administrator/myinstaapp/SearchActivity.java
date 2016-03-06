package com.example.administrator.myinstaapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.administrator.myinstaapp.Pojo.Picture;
import com.example.administrator.myinstaapp.Services.ServiceManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    private MenuItem mSearchMenuItem;
    private SearchView mSearchView;
    EditText mText;
    String mAuthToken;
    private String mQuery;

    List<Picture> mPictures;
    private String mMaxId, mMinId;
    ImageAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();
        mAuthToken = intent.getStringExtra("AUTH_TOKEN");

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(manager);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) { // only when scrolling up

                    final int visibleThreshold = 2;

                    GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
                    int lastItem = layoutManager.findLastCompletelyVisibleItemPosition();
                    int currentTotalCount = layoutManager.getItemCount();

                    if (currentTotalCount <= lastItem + visibleThreshold) {
                        //show your loading view
                        // load content in background

                        getTagResults(mQuery, "", "");

                    }
                }

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);

//
        mSearchMenuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchMenuItem);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mQuery = query;
                getTagResults(query, "", "");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }



    private void getTagResults(String query, String minId, String maxId) {
        showPD();
        Call<ResponseBody> response = ServiceManager.createService().getResponse(query, "631477962.1fb234f.f7c5cda97c7f4df983b1c764f066ed37", minId, maxId);
        response.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccess()) {
                    hidePD();
                    mPictures = new ArrayList<Picture>();
                    //mEditText.setText("secces");
                    StringBuilder sb = new StringBuilder();
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream()));
                        String line;

                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }

                        JSONObject tagResponse = new JSONObject(sb.toString());

                        for (int i = 0; i < tagResponse.length() - 2; i++) {
                            JSONObject pagination = tagResponse.getJSONObject("pagination");

                            mMaxId = pagination.getString("next_max_id");
                            mMinId = pagination.getString("next_min_id");

                            JSONObject meta = tagResponse.getJSONObject("meta");
                            JSONArray data = tagResponse.getJSONArray("data");

                            for (int j = 0; j < data.length(); j++) {

                                JSONArray tags = data.getJSONObject(j).getJSONArray("tags");


                                JSONObject images = data.getJSONObject(j).getJSONObject("images").getJSONObject("low_resolution");


                                Picture picture = new Picture();
                                picture.setURL(images.getString("url"));
                                mPictures.add(picture);

                            }

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }

                mAdapter = new ImageAdapter(mPictures);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                t.printStackTrace();
            }

        });


    }

    private void showPD(){

        if(mProgressDialog==null){
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
        }
    }

    private void hidePD(){
        if(mProgressDialog!= null){
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }

    }
}
