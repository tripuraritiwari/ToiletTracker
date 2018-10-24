package com.verve.toilettracker.activity;
import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import org.json.JSONException;
import org.json.JSONObject;
public class InboxActivity extends AppCompatActivity {
    private LinearLayout parent_layout;
    private URLConfig urlConfig;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        initi();
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbarinbox);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fechmessage();
    }
    private void initi() {
        urlConfig = new URLConfig();
        parent_layout = (LinearLayout) findViewById(R.id.parent_layout);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
    public void fechmessage() {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLConfig.InboxWebService,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String success = jsonObject.getString("success");
                                String message = jsonObject.getString("message");
                                String value = jsonObject.getString("value");
                                if (success.equals("1")) {
                                    try {
                                        JSONArray jsonArray = new JSONArray(value);
                                        //     Toast.makeText(InboxActivity.this, "array " + jsonArray, Toast.LENGTH_SHORT).show();
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                                    1.0f
                                            );
                                            param.setMargins(5, 5, 5, 5);
                                            LinearLayout.LayoutParams viewparam = new LinearLayout.LayoutParams(
                                                    LinearLayout.LayoutParams.MATCH_PARENT, 3,
                                                    1.0f
                                            );
                                            CardView cardView = new CardView(InboxActivity.this);
                                            cardView.setLayoutParams(param);
                                            //cardView.setPadding(5,5,5,5);
                                            LinearLayout linearLayout = new LinearLayout(InboxActivity.this);
                                            cardView.setPadding(5, 5, 5, 5);
                                            linearLayout.setOrientation(LinearLayout.VERTICAL);
                                            linearLayout.setLayoutParams(param);
                                            TextView textViewid = new TextView(InboxActivity.this);
                                            textViewid.setLayoutParams(param);
                                            textViewid.setTextSize(20);
                                            textViewid.setTypeface(null, Typeface.BOLD);
                                            TextView textViewremark = new TextView(InboxActivity.this);
                                            textViewremark.setLayoutParams(param);
                                            textViewremark.setTextSize(20);
                                            textViewremark.setTypeface(null, Typeface.BOLD);
                                            View view = new View(InboxActivity.this);
                                            view.setLayoutParams(viewparam);
                                            view.setBackgroundColor(Color.GREEN);
                                            JSONObject jresponse = jsonArray.getJSONObject(i);
                                            String id = jresponse.getString("toilet_id");
                                            String remark = jresponse.getString("remark");
                                            textViewid.setText("Toilet no ." + id);
                                            textViewremark.setText(remark);
                                            linearLayout.addView(textViewid);
                                            linearLayout.addView(view);
                                            linearLayout.addView(textViewremark);
                                            cardView.addView(linearLayout);
                                            parent_layout.addView(cardView);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        urlConfig.myToast(InboxActivity.this, e.getMessage().toString());
                                    }
                                } else {
                                    urlConfig.myToast(InboxActivity.this, message);
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
            });
            int socketTimeout = 10000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            requestQueue.add(stringRequest);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }
}
