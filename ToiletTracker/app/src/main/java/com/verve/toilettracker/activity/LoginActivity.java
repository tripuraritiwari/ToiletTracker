package com.verve.toilettracker.activity;
import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.verve.toilettracker.R;
import com.verve.toilettracker.URLConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
public class LoginActivity extends AppCompatActivity {
    private EditText edtemail, edtpassword;
    private SharedPreferences sharedpreferences;
    MainActivity mainActivity;
    private URLConfig urlConfig;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_login);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbarlogin);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initi();
    }
    private void initi() {
        urlConfig = new URLConfig();
        mainActivity = new MainActivity();
        edtemail = (EditText) findViewById(R.id.edtemailid);
        edtpassword = (EditText) findViewById(R.id.edtpass);
        findViewById(R.id.btnlgn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(LoginActivity.this, "Login Successfull" +
//                        "", Toast.LENGTH_SHORT).show();
                if (validation()) {
                    loginrequest();
                }
            }
        });
    }
    private boolean validation() {
        boolean result = false;
        if (edtemail.getText().toString().equals("")) {
            edtemail.setError("Can't remain empty.!");
            result = false;
        } else {
            result = true;
        }
        if (edtpassword.getText().toString().equals("")) {
            edtpassword.setError("Can't remain empty.!");
            result = false;
        } else {
            result = true;
        }
        return result;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LoginActivity.this.finish();
        return super.onOptionsItemSelected(item);
    }
    public void loginrequest() {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            //.............use of volley library for consuming webservice
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLConfig.LoginWebService,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String success = jsonObject.getString("success");
                                String message = jsonObject.getString("message");
                                if (success.equals("1")) {
                                    String value = jsonObject.getString("value");
                                    JSONArray value1 = new JSONArray(value);
                                    JSONObject jresponse = value1.getJSONObject(0);
                                    String id = jresponse.getString("id");
                                    sharedpreferences = getSharedPreferences("loginshare",
                                            Context.MODE_PRIVATE);
                                    urlConfig.myToast(LoginActivity.this, message);
                                    savePreferences(true, id);
                                    LoginActivity.this.finish();
                                } else {
                                    urlConfig.myToast(LoginActivity.this, message);
                                }
                            } catch (Exception e) {
                                Log.e("MyError101", e.getMessage().toString());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Error", "Error: " + error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    //........service call by passing parameter
                    params.put("username", edtemail.getText().toString());
                    params.put("password", edtpassword.getText().toString());
                    return params;
                }
            };
            int socketTimeout = 10000;//30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            requestQueue.add(stringRequest);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }
    private void savePreferences(boolean b, String id) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("loginvalue", b);
        editor.putString("id", id);
        editor.commit();
    }
}
