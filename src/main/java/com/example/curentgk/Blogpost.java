package com.example.curentgk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Blogpost extends BaseActivity {
    private static final String JSON_URL = "https://www.googleapis.com/blogger/v2/blogs/6358489634201359297/posts/";

    //listview object
    ListView listView;
    private  String key="AIzaSyBcj47Z7O351gLSd3t2vQPG1iiXQVwvbMU";

    //the hero list where we will store all the hero objects after parsing json
    List<Blog> blogList;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_blogpost);
        Intent iin= getIntent();
        Bundle b = iin.getExtras();
        Toast.makeText(getApplicationContext(),b.getString("postid"), Toast.LENGTH_LONG).show();
     String postid=   b.getString("postid");
        final TextView titlef=findViewById(R.id.blogtitle);
        final WebView contentf=findViewById(R.id.blogcontent);
        WebSettings webSettings = contentf.getSettings();
        webSettings.setJavaScriptEnabled(true);
      if(postid!=null){
          String post_url=JSON_URL+postid+"?key="+key;
          final ProgressBar progressBar = (ProgressBar) findViewById(R.id.prog_one);

          //making the progressbar visible
          progressBar.setVisibility(View.VISIBLE);

          //creating a string request to send request to the url
          StringRequest stringRequest = new StringRequest(Request.Method.GET, post_url,
                  new Response.Listener<String>() {
                      @Override
                      public void onResponse(String response) {
                          //hiding the progressbar after completion
                          progressBar.setVisibility(View.INVISIBLE);


                          try {
                              //getting the whole json object from the response
                              JSONObject obj = new JSONObject(response);

                              //we have the array named hero inside the object
                              //so here we are getting that json array
                              String ptitle=obj.getString("title");
                              String pcontent=obj.getString("content");
                              //titlef.setText(ptitle);
                             // View v = inflater.inflate(R.layout.fragment, container, false);

                              setTitle(ptitle);


                              contentf.loadData(pcontent, "text/html; charset=utf-8", "utf-8");


                          } catch (JSONException e) {
                              e.printStackTrace();
                          }
                      }
                  },
                  new Response.ErrorListener() {
                      @Override
                      public void onErrorResponse(VolleyError error) {
                          //displaying the error in toast if occurrs
                          Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                      }
                  });

          //creating a request queue
          RequestQueue requestQueue = Volley.newRequestQueue(this);

          //adding the string request to request queue
          requestQueue.add(stringRequest);

      }
    }
}
