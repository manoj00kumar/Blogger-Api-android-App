package com.example.curentgk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import static com.example.curentgk.NetworkUtil.getConnectivityStatusString;

public class MainActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 1000;
    private static final String EMAIL = "email";
   // private CallbackManager callbackManager;
    private TextView textView;
    private  TextView emailText;
    private ImageView userImage;
    private  LoginButton loginButton;
    private Button goBtn;
    CallbackManager  callbackManager = CallbackManager.Factory.create();
    private AccessToken mAccessToken;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String FName = "fnameKey";
    public static final String LName = "lnameKey";
    public static final String IMg = "imgKey";
    public static final String Email = "emailKey";
    public static final String ISLogin = "islogin";
    private Fbdata fbdata;
    SharedPreferences sharedpreferences;
    private BroadcastReceiver MyReciever = null;
    AlertDialog.Builder builder;
    @Override




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        NetworkUtil netu = new NetworkUtil();
        String xs = netu.getConnectivityStatusString(this);

            if(xs==null){
                showAlert();
            }






        /**/



        Toast.makeText(this, xs, Toast.LENGTH_LONG).show();

textView=findViewById(R.id.textUsername);
emailText=findViewById(R.id.textEmail);
userImage=findViewById(R.id.userImg);
goBtn=findViewById(R.id.gotoBtn);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(EMAIL));
        // If you are using in a fragment, call loginButton.setFragment(this);
             /* init data */
        sharedpreferences= getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String restoredText = sharedpreferences.getString("islogin", null);

            String fname = sharedpreferences.getString("fnameKey", "No name defined");//"No name defined" is the default value.
            String lname = sharedpreferences.getString("lnameKey", "No name defined");
            String myimg = sharedpreferences.getString("imgKey", "No name defined");
            String myemail = sharedpreferences.getString("emailKey", "No name defined");
            if(restoredText!=null) {
                goBtn.setVisibility(View.VISIBLE);
                textView.setText(fname + " " + lname);
                emailText.setText(myemail);
                Picasso.with(MainActivity.this).load(myimg).into(userImage);
            }

        //broadcastIntent();


        /*end*/

        Profile fbProfile = Profile.getCurrentProfile();
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                   // Log.d(TAG, "onLogout catched");
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.clear();
                    editor.commit();
                    finish();
                }
            }
        };

goBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent blogint=new Intent(MainActivity.this,BlogList.class);
        startActivity(blogint);
    }
});

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                mAccessToken = loginResult.getAccessToken();
                getUserProfile(mAccessToken);
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        accessTokenTracker.startTracking();
/*************/



    }

    private void getUserProfile(AccessToken currentAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                currentAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("TAG", object.toString());
                        try {
                           // sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            String first_name = object.getString("first_name");
                            String last_name = object.getString("last_name");
                            String email = object.getString("email");
                            String id = object.getString("id");
                            String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";
                            editor.putString(FName, first_name);
                            editor.putString(LName, last_name);
                            editor.putString(Email, email);
                            editor.putString(IMg, image_url);
                            editor.putString(ISLogin,"1");
                            editor.commit();
                            fbdata=new Fbdata(first_name, last_name, email, 1);
                            textView.setText("First Name: " + first_name + "\nLast Name: " + last_name);
                            emailText.setText(email);
                            Picasso.with(MainActivity.this).load(image_url).into(userImage);
                            goBtn.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();

    }





    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode,  data);
    }

    //chck net
      public  void showAlert()
      {
          builder = new AlertDialog.Builder(this);
          /** show alert*/

          builder.setMessage("Please enable internet").setTitle("No internet");

          //Setting message manually and performing action on button click
          builder.setMessage("Please enable internet to access app")
                  .setCancelable(false)
                  .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                      public void onClick(DialogInterface dialog, int id) {
                          finish();

                      }
                  })
                  .setNegativeButton("No", new DialogInterface.OnClickListener() {
                      public void onClick(DialogInterface dialog, int id) {
                          //  Action for 'NO' Button
                          dialog.cancel();

                      }
                  });
          //Creating dialog box
          AlertDialog alert = builder.create();
          //Setting the title manually

          alert.show();
      }



}
