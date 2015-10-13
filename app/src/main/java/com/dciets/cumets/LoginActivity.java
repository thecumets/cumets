package com.dciets.cumets;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.dciets.cumets.listener.CreateListener;
import com.dciets.cumets.network.Server;
import com.dciets.cumets.utils.DatabaseHelper;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;


public class LoginActivity extends Activity implements CreateListener {

    private static final int REQUEST_PERMISSION = 9000;
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    public static void show(final Context context) {
        Intent i = new Intent(context, LoginActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        DatabaseHelper.init(getApplicationContext());

        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");
        // Other app specific specialization

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCB(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(AccessToken.getCurrentAccessToken() != null) {
            Server.create(this, AccessToken.getCurrentAccessToken().getToken(), AccessToken.getCurrentAccessToken().getUserId(), "", this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreateSuccessful() {
        if(Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);
        } else {
            MainActivity.show(this);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_PERMISSION) {
            boolean allEnabled = true;
            for(int i = 0; i<grantResults.length; i++) {
                Log.i("MainActivity", grantResults[i] + "");
                if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    allEnabled = false;
                }
            }

            if(allEnabled) {
                MainActivity.show(this);
                finish();
            } else {
                Snackbar.make(findViewById(android.R.id.content), R.string.location_error, Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onCreateError() {
        Snackbar.make(findViewById(android.R.id.content), R.string.error, Snackbar.LENGTH_SHORT).show();
    }

    private class FacebookCB implements FacebookCallback<LoginResult> {
        final LoginActivity loginActivity;

        public FacebookCB(final LoginActivity loginActivity) {
            this.loginActivity = loginActivity;
        }

        @Override
        public void onSuccess(LoginResult loginResult) {
            Server.create(loginActivity, loginResult.getAccessToken().getToken(), loginResult.getAccessToken().getUserId(), "", loginActivity);
        }

        @Override
        public void onCancel() {
            Toast.makeText(loginActivity, R.string.cancel, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(FacebookException exception) {
            Toast.makeText(loginActivity, R.string.error, Toast.LENGTH_SHORT).show();
        }
    }
}
