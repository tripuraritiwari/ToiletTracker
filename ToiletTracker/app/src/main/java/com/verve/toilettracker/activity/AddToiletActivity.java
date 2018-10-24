package com.verve.toilettracker.activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
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

import com.android.volley.AuthFailureError;
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
import com.verve.toilettracker.service.GPSTracker;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
public class AddToiletActivity extends AppCompatActivity {
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_toilet);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
    }
    private void init() {
        displayimg=(ImageView)findViewById(R.id.imgvw1);
        displayimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        getsharepreferences();
        urlConfig = new URLConfig();
        gps = new GPSTracker(AddToiletActivity.this);
        addresss = getIntent().getStringExtra("addressaddtoilet");
        Log.i("address : ", "address " + addresss);
        ((TextView) findViewById(R.id.tv_address)).setText(addresss);
        edt_note = (EditText) findViewById(R.id.edt_notes);
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
        findViewById(R.id.btntagsubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gps.canGetLocation()) {
                    latitude2 = gps.getLatitude();
                    longitude2 = gps.getLongitude();
                    if (validation()) {
                        urlConfig.showPD(AddToiletActivity.this);
                        tagrequest();
                    }
                } else {
                    gps.showSettingsAlert();
                    urlConfig.hidePD();
                }
            }
        });
        tvwhere = (TextView) findViewById(R.id.tvwhere);
        tvwhere.setText("Where...");
        tvwhere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(AddToiletActivity.this);
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

       //  setdefaultpicture_inimageview();
    }
    private void setdefaultpicture_inimageview() {
        Drawable drawable = this.getResources().getDrawable(R.mipmap.ic_userid);
        mbasebitmap = ((BitmapDrawable) drawable).getBitmap();
        mBitmap = mbasebitmap;
        displayimg.setImageBitmap(mbasebitmap);
    }
    public void multipartuploadfile() {
        // String path = getPath(filePath);
        try {
            String uploadId = UUID.randomUUID().toString();
            //Creating a multi part request
            new MultipartUploadRequest(this, uploadId, URLConfig.tagtoiletWebService)
                    //    .addFileToUpload(path, "image") //Adding file
                    //     .addParameter("name", name) //Adding text parameter to the request
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload(); //Starting the upload
        } catch (Exception exc) {
            urlConfig.myToast(AddToiletActivity.this, exc.getMessage());
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AddToiletActivity.this.finish();
        return super.onOptionsItemSelected(item);
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
    private void galleryIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 1);
    }
    private void cameraIntent() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, 0);
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
////                    displayimg.setImageURI(selectedImage);
////                    mBitmap = (Bitmap) imageReturnedIntent.getExtras().get("data");
//                    ///
                    //                   Uri selectedImage = imageReturnedIntent.getData();
//                    InputStream image_stream = null;
//                    try {
//                        image_stream = getContentResolver().openInputStream(selectedImage);
//                        Bitmap bitmap = BitmapFactory.decodeStream(image_stream);
//                        displayimg.setImageBitmap(bitmap);
//                        mBitmap = bitmap;
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
                    ////
//                    String s = imageReturnedIntent.getExtras().get("data").toString();
//                    Log.i("intentstring", "value are : " + s);
//                    Bitmap photo = (Bitmap) imageReturnedIntent.getExtras().get("data");
//                    displayimg.setImageBitmap(photo);
//                    mBitmap = photo;
                    /////
                    // Get the Image from data
//                    Uri selectedImage = imageReturnedIntent.getData();
//                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
//                    // Get the cursor
//                    Cursor cursor = this.getContentResolver().query(selectedImage,
//                            filePathColumn, null, null, null);
//                    // Move to first row
//                    cursor.moveToFirst();
//                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                    String picturePath = cursor.getString(columnIndex);
//                    cursor.close();
//                    // Set the Image in ImageView after decoding the String
//                    Bitmap bitThumbnail = BitmapFactory.decodeFile(picturePath);
//                    Log.w("Bitmap thumbnail", String.valueOf(bitThumbnail));
//                    // insertDialogData(bitThumbnail, picturePath);
//                    displayimg.setImageBitmap(bitThumbnail);
//                    mBitmap = bitThumbnail;
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
    private boolean validation() {
        boolean result = false;
        if (tvwhere.getText().toString().equals("Where...")) {
            tvwhere.setError("Can't remain empty.!");
            result = false;
        } else {
            result = true;
        }
        return result;
    }
    public void tagrequest() {
        if (displayimg.getDrawable() != null) {
            mImageParameter = getStringImage(mBitmap);
        }
        remarkstr = "";
        remarkstr = edt_note.getText().toString().trim();
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLConfig.tagtoiletWebService,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("myresponce: ", "responce : " + response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String success = jsonObject.getString("success");
                                String message = jsonObject.getString("message");
                                if (success.equals("1")) {
                                    AddToiletActivity.this.finish();
                                    urlConfig.myToast(AddToiletActivity.this, message);
                                    clearalldata();
                                    urlConfig.hidePD();
                                } else {
                                    urlConfig.myToast(AddToiletActivity.this, message);
                                    urlConfig.hidePD();
                                }
                            } catch (Exception e) {
                                Log.e("MyError381", e.getMessage().toString());
                                urlConfig.hidePD();
                                urlConfig.myToast(AddToiletActivity.this, e.getMessage());
                                urlConfig.myToast(AddToiletActivity.this, "Error in Tagging Toilet.\nTry Agin With Different image");
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    VolleyLog.d("Error", "Error: " + e.getMessage());
                    urlConfig.myToast(AddToiletActivity.this, e.getMessage().toString());
                    urlConfig.hidePD();
                    Log.e("MyError382", e.getMessage().toString());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("latitude", String.valueOf(latitude2));
                    params.put("longitude", String.valueOf(longitude2));
                    params.put("type", tvwhere.getText().toString().trim());
                    params.put("user_id", id);
                    params.put("date", "");
                    params.put("pincode", "");
                    params.put("address", addresss);
                    params.put("name", "");
                    params.put("gender", gender);
                    params.put("remark", remarkstr);
                    params.put("url", mImageParameter);
                    Log.i("imagevalue", "value : " + mImageParameter);
                    return params;
                }
            };
            int socketTimeout = 10000;//30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            requestQueue.add(stringRequest);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            urlConfig.hidePD();
            Log.e("MyError405", ex.getMessage().toString());
        }
        /////////json responce
    }
    private void clearalldata() {
        tvwhere.setText("Where...");
        rbtnboth.setChecked(true);
        rbtnmale.setChecked(false);
        rbtfemale.setChecked(false);
        gender = "Both";
        edt_note.setText("");
        displayimg.setImageResource(R.drawable.shape_edittext);
        ////
    }
    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return "data:image/gif;base64," + encodedImage;
    }
    private void getsharepreferences() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        id = sharedPreferences.getString("id", "");
    }
}
