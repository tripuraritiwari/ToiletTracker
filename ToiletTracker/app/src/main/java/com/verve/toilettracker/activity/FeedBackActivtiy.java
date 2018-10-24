package com.verve.toilettracker.activity;
import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
public class FeedBackActivtiy extends AppCompatActivity {
    private TextView tvfdbck;
    private EditText edtfdbck;
    private ImageView img1, img2, img3;
    String Address = "";
    private TextView tvaddress;
    private String toiletid;
    private String id;
    private URLConfig urlConfig;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_marker_remark);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbarfeedback);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        inti();
        listener();
    }
    private void inti() {
        urlConfig = new URLConfig();
        // Address = getIntent().getStringExtra("address");
        toiletid = getIntent().getStringExtra("id");
        Address = getIntent().getStringExtra("toiletaddress");
        tvaddress = (TextView) findViewById(R.id.tvaddress);
        img1 = (ImageView) findViewById(R.id.imgone);
        img2 = (ImageView) findViewById(R.id.imgtow);
        img3 = (ImageView) findViewById(R.id.imgthree);
        tvfdbck = (TextView) findViewById(R.id.tvfdbck);
        edtfdbck = (EditText) findViewById(R.id.edtfdbck);
        tvaddress.setText(Address);
        getsharepreferences();
    }
    private void listener() {
        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvfdbck.setText("Good. Nice Condition");
            }
        });
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvfdbck.setText("Average. Sufficient Condition");
            }
        });
        img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvfdbck.setText("Poor. Need to Clean");
            }
        });
        findViewById(R.id.btnsend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation()) {
                    loginrequest();
                }
            }
        });
    }
    private void getsharepreferences() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        id = sharedPreferences.getString("id", "");
    }
    private boolean validation() {
        boolean result = false;
        if (tvfdbck.getText().toString().equals("")) {
            tvfdbck.setError("Can't remain empty.!");
            result = false;
        } else {
            result = true;
        }
        return result;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
    public void loginrequest() {
        // Toast.makeText(FeedBackActivtiy.this, "id : " + id, Toast.LENGTH_SHORT).show();
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLConfig.feedbackWebService,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String success = jsonObject.getString("success");
                                String message = jsonObject.getString("message");
                                if (success.equals("1")) {
                                    FeedBackActivtiy.this.finish();
                                    urlConfig.myToast(FeedBackActivtiy.this, message);
                                } else {
                                    urlConfig.myToast(FeedBackActivtiy.this, message);
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
                    params.put("toilet_id", toiletid);
                    params.put("status", tvfdbck.getText().toString());
                    params.put("remark", edtfdbck.getText().toString());
                    params.put("user_id", id);
                    Log.i("parameterfeedback", "toiletid :" + toiletid + " tvfdbck:" + tvfdbck.getText().toString() + " edtfdbck:" + edtfdbck.getText().toString() + " userid :" + id);
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
}
