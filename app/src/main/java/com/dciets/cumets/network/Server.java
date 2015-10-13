package com.dciets.cumets.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dciets.cumets.R;
import com.dciets.cumets.listener.CreateListener;
import com.dciets.cumets.listener.MonitorListener;
import com.dciets.cumets.listener.PleasureListener;
import com.dciets.cumets.listener.UpdateListener;
import com.dciets.cumets.model.UpdateInfo;
import com.dciets.cumets.utils.PreferencesController;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by marc-antoinehinse on 2015-10-10.
 */
public class Server {

    private static final String TAG = "Server";
    private static String ADDRESS = "http://api.cumets.com/";

    public static void create(final Context context, final String tokenId, final String facebookId, final String name, final CreateListener listener) {
        AsyncTask<String, Void, String> request = new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... arg0) {
                InstanceID instanceID = InstanceID.getInstance(context);
                try {
                    return instanceID.getToken(context.getResources().getString(R.string.gcm_defaultSenderId),
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if(result == null) {
                    if(listener != null) {
                        listener.onCreateError();
                    }
                } else {
                    createUser(context, tokenId, facebookId, name, result, listener);
                }
            }

        };
        request.execute();
    }

    public static void pushLocation(final Context context, final String longitude, final String latitude) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = ADDRESS + "users/location";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i(TAG, response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("longitude", longitude);
                params.put("latitude", latitude);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();

                headers.put("X-Auth-Token", PreferencesController.getPreference(context, PreferencesController.TOKEN));
                return headers;
            }
        };


        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public static void monitorUser(final Context context, final String facebookId, final MonitorListener listener) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = ADDRESS + "users/monitor/" + facebookId;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i(TAG, response);
                        if(listener != null) {
                            listener.onMonitorSuccessful(facebookId);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                if(listener != null) {
                    listener.onMonitorError();
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();

                headers.put("X-Auth-Token", PreferencesController.getPreference(context, PreferencesController.TOKEN));
                return headers;
            }
        };


        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public static void unmonitorUser(final Context context, final String facebookId, final MonitorListener listener) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = ADDRESS + "users/monitor/" + facebookId;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i(TAG, response);
                        if(listener != null) {
                            listener.onUnmonitorSuccessful(facebookId);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                if(listener != null) {
                    listener.onUnmonitorError();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();

                headers.put("X-Auth-Token", PreferencesController.getPreference(context, PreferencesController.TOKEN));
                return headers;
            }
        };


        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private static void createUser(final Context context, final String tokenId, final String facebookId, final String name, final String gcm, final CreateListener listener) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = ADDRESS + "users/create";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i(TAG, response);
                        try {
                            JSONObject object = new JSONObject(response);
                            PreferencesController.setPreference(context, PreferencesController.TOKEN, object.getString("token"));
                            if(listener != null) {
                                listener.onCreateSuccessful();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.networkResponse.statusCode + "");
                if(error.networkResponse.statusCode == 409) {
                    if(listener != null) {
                        listener.onCreateSuccessful();
                    }
                } else {
                    if(listener != null) {
                        listener.onCreateError();
                    }
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token_id", tokenId);
                params.put("facebook_id", facebookId);
                params.put("name", name);
                params.put("gcm", gcm);
                return params;
            }


        };


        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public static void start(final Context context, final PleasureListener listener) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = ADDRESS + "activity/start";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i(TAG, response);
                        if(listener != null) {
                            listener.onPleasureStartedSuccessful();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                if(listener != null) {
                    listener.onPleasureStartedError();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();

                headers.put("X-Auth-Token", PreferencesController.getPreference(context, PreferencesController.TOKEN));
                return headers;
            }
        };


        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public static void update(final Context context, final UpdateListener listener) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = ADDRESS + "activity/update";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i(TAG, response);
                        try {
                            JSONObject jo = new JSONObject(response);
                            UpdateInfo update = new UpdateInfo(jo.getString("user"), !jo.getString("distance").equals("null") ? jo.getDouble("distance") : Double.MIN_VALUE);
                            if(listener != null) {
                                listener.onNewUpdateInformation(update);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            if(listener != null) {
                                listener.onError();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                if(listener != null) {
                    listener.onError();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();

                headers.put("X-Auth-Token", PreferencesController.getPreference(context, PreferencesController.TOKEN));
                return headers;
            }
        };


        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public static void disrupt(final Context context, final PleasureListener listener) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = ADDRESS + "activity/disrupt";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i(TAG, response);
                        if(listener != null) {
                            listener.onPleasureStoppedSuccessful();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                if(listener != null) {
                    listener.onPleasureStoppedError();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();

                headers.put("X-Auth-Token", PreferencesController.getPreference(context, PreferencesController.TOKEN));
                return headers;
            }
        };


        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public static void stop(final Context context, final PleasureListener listener) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = ADDRESS + "activity/stop";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i(TAG, response);
                        if(listener != null) {
                            listener.onPleasureStoppedSuccessful();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
                if(listener != null) {
                    listener.onPleasureStoppedError();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();

                headers.put("X-Auth-Token", PreferencesController.getPreference(context, PreferencesController.TOKEN));
                return headers;
            }
        };


        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
