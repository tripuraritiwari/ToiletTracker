package com.verve.toilettracker;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;
public class URLConfig {
    public ProgressDialog progressbar;
    public Context context;
    // public static String LoginWebService = "http://avrom.intellectdigital.com/roopeshg_toilet/login.php";
// public static String InboxWebService = "http://avrom.intellectdigital.com/roopeshg_toilet/get_inbox.php";
// public static String requestWebService = "http://avrom.intellectdigital.com/roopeshg_toilet/set_request.php";
// public static String feedbackWebService = "http://avrom.intellectdigital.com/roopeshg_toilet/set_feedback.php";
// public static String alldetailWebService = "http://avrom.intellectdigital.com/roopeshg_toilet/get_all_details.php";
// public static String tagtoiletWebService = "http://avrom.intellectdigital.com/roopeshg_toilet/set_details.php";
    ///update url's
    public static String LoginWebService = "http://avrom.intellectdigital.com/toilet_locator/webservice/login.php";
    public static String InboxWebService = "http://avrom.intellectdigital.com/toilet_locator/webservice/get_inbox.php";
    public static String requestWebService = "http://avrom.intellectdigital.com/toilet_locator/webservice/set_request.php";
    public static String feedbackWebService = "http://avrom.intellectdigital.com/toilet_locator/webservice/set_feedback.php";
    public static String alldetailWebService = "http://avrom.intellectdigital.com/toilet_locator/webservice/get_all_details.php";
    public static String tagtoiletWebService = "http://avrom.intellectdigital.com/toilet_locator/webservice/set_details.php";
    public void showPD(Context context) {
        this.context = context;
        progressbar = new ProgressDialog(context);
        progressbar.setMessage("Please Wait...");
        progressbar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressbar.setCancelable(false);
        progressbar.show();
    }
    public void hidePD() {
        if (progressbar.isShowing()) {
            progressbar.cancel();
        }
    }
    public void myToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
