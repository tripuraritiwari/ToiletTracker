package com.verve.toilettracker.activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
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
import com.verve.toilettracker.Manifest;
import com.verve.toilettracker.R;
import com.verve.toilettracker.URLConfig;
import com.verve.toilettracker.service.GPSTracker;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
public class RequistActivity extends AppCompatActivity {
    private AppCompatButton btnsendrequest;
    private EditText edtfdbck;
    private TextView tv_address_current;
    double latitude;
    double longitude;
    private EditText edt_note;
    private TextView tvwhere;
    private ImageView displayimg;
    private Dialog dialog;
    private WindowManager.LayoutParams lp;
    private RadioButton rbtnboth, rbtnmale, rbtfemale;
    private String gender = "Both", mImageParameter, remarkstr, addresss;
    private GPSTracker gps;
    private double latitude2, longitude2;
    private Bitmap mBitmap;
    private URLConfig urlConfig;
    private Bitmap mbasebitmap;
    private String id;
    private String where;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requist);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbarrequest);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addresss = getIntent().getStringExtra("addressaddrequest");
        tv_address_current = (TextView) findViewById(R.id.tv_address_current);
        tv_address_current.setText(addresss);
        initi();
    }
    private void initi() {
        urlConfig = new URLConfig();
        edt_note = (EditText) findViewById(R.id.edtrequest);
        rbtnboth = (RadioButton) findViewById(R.id.rbtnboth);
        rbtnmale = (RadioButton) findViewById(R.id.rbtnmale);
        rbtfemale = (RadioButton) findViewById(R.id.rbtnfemale);
        rbtnboth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    gender = "Both";
                }
            }
        });
        rbtnmale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    gender = "Male";
                }
            }
        });
        rbtfemale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    gender = "Female";
                }
            }
        });
        edtfdbck = (EditText) findViewById(R.id.edtrequest);
        tvwhere = (TextView) findViewById(R.id.tvwhere);
        tvwhere.setText("Where...");
        tvwhere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(RequistActivity.this);
                dialog.setContentView(R.layout.custom);
                lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                dialog.getWindow().setAttributes(lp);
                dialog.setTitle("Where...");
                dialog.findViewById(R.id.tv1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvwhere.setText("Public Toilet");
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.tv2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvwhere.setText("Hostel");
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.tv3).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvwhere.setText("Restaurant");
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.tv4).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvwhere.setText("Metro Station");
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.tv5).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvwhere.setText("Bus Station");
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.tv6).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvwhere.setText("Train Station");
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.tv7).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvwhere.setText("Hospital");
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.tv8).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvwhere.setText("Shopping Mall");
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.tv9).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvwhere.setText("Petrol Pump");
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.tv10).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvwhere.setText("Other");
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        findViewById(R.id.btnsendrequest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (findlatlong()) {
                    requistservice();
                }
            }
        });
        displayimg = (ImageView) findViewById(R.id.imgvw);
        displayimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
    }
    private void galleryIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 1);
    }
    private void cameraIntent() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, 0);
    }
    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    String s = imageReturnedIntent.getExtras().get("data").toString();
                    Log.i("intentstring", "value are : " + s);
                    Bitmap photo = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    displayimg.setImageBitmap(photo);
                    mBitmap = photo;
                }
                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    String strPicturePath = c.getString(columnIndex);
                    c.close();
                    Bitmap thumbnail = (BitmapFactory.decodeFile(strPicturePath));
                    mBitmap = thumbnail;
                    Log.w("path of image...**.", strPicturePath + "");
                    displayimg.setImageBitmap(thumbnail);
                }
                break;
        }
    }
    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return "data:image/gif;base64," + encodedImage;
    }
    private void requistservice() {
        if (displayimg.getDrawable() != null) {
            mImageParameter = getStringImage(mBitmap);
        }
        remarkstr = "";
        remarkstr = edt_note.getText().toString().trim();
        if (tvwhere.getText().toString().trim().equals("Where...")) {
            where = "Other";
        } else {
            where = tvwhere.getText().toString().trim();
        }
        try {
            urlConfig.showPD(RequistActivity.this);
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLConfig.requestWebService,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("Responce77", response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String success = jsonObject.getString("success");
                                String message = jsonObject.getString("message");
                                if (success.equals("1")) {
                                    RequistActivity.this.finish();
                                    urlConfig.myToast(RequistActivity.this, message);
                                    edtfdbck.setText("");
                                    urlConfig.hidePD();
                                } else {
                                    urlConfig.myToast(RequistActivity.this, message);
                                    urlConfig.hidePD();
                                }
                            } catch (Exception e) {
                                Log.e("MyError82", e.getMessage().toString());
                                urlConfig.hidePD();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("myErrorr", "Error 298: " + error.getMessage().toString());
                    urlConfig.hidePD();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("address", addresss);
                    params.put("latitude", new Double(latitude).toString());
                    params.put("longitude", new Double(longitude).toString());
                    params.put("remark", edtfdbck.getText().toString());
                    ///addingtion parametter'// STOPSHIP: 10/10/17
                    params.put("type", where);
                    params.put("gender", gender);
                    params.put("url", mImageParameter);
                    return params;
                }
            };
            int socketTimeout = 10000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            requestQueue.add(stringRequest);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }
    private boolean findlatlong() {
        boolean result = false;
        GPSTracker gps = new GPSTracker(RequistActivity.this);
        // check if GPS enabled
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            result = true;
        } else {
            gps.showSettingsAlert();
        }
        return result;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        RequistActivity.this.finish();
        return super.onOptionsItemSelected(item);
    }
}
