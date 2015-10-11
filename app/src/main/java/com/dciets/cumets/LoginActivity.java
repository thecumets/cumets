package com.dciets.cumets;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dciets.cumets.listener.CreateListener;
import com.dciets.cumets.listener.LoginListener;
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


public class LoginActivity extends Activity implements CreateListener, LoginListener {

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

        MainActivity.show(this);
        finish();

        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");
        // Other app specific specialization

        // Callback registration
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Server.createUser(LoginActivity.this, loginResult.getAccessToken().getToken(), loginResult.getAccessToken().getUserId(), Profile.getCurrentProfile().getName(), "", LoginActivity.this);
                    MainActivity.show(LoginActivity.this);
                    finish();
                }

                @Override
                public void onCancel() {
                    Toast.makeText(getApplicationContext(), R.string.com_facebook_loginview_cancel_action, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(FacebookException exception) {
                    Toast.makeText(getApplicationContext(), R.string.com_facebook_image_download_unknown_error, Toast.LENGTH_SHORT).show();
                }
            });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(AccessToken.getCurrentAccessToken() != null) {
            Server.login(this, AccessToken.getCurrentAccessToken().getUserId(), this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreateSuccessful() {
        Server.login(this, AccessToken.getCurrentAccessToken().getUserId(), this);
    }

    @Override
    public void onCreateError() {
        Snackbar.make(findViewById(android.R.id.content), R.string.error, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onLoginSuccessful() {
        MainActivity.show(this);
        finish();
    }

    @Override
    public void onLoginError() {
        Snackbar.make(findViewById(android.R.id.content), R.string.error, Snackbar.LENGTH_SHORT).show();
    }
}
