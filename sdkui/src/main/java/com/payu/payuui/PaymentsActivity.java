package com.payu.payuui;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.http.util.EncodingUtils;

import com.payu.india.Model.PayuConfig;
import com.payu.india.Payu.PayuConstants;

public class PaymentsActivity extends Activity {

    Bundle bundle;
    WebView mWebView;
    String url;
    boolean cancelTransaction = false;

    PayuConfig payuConfig;
    private BroadcastReceiver mReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * when the device runing out of memory we dont want the user to restart the payment. rather we close it and redirect them to previous activity.
         */

        if(savedInstanceState!=null){
            super.onCreate(null);
            finish();//call activity u want to as activity is being destroyed it is restarted
        }else {
            super.onCreate(savedInstanceState);
        }
        setContentView(R.layout.activity_payments);

        //region Replace the whole code by the commented code if you are NOT using custombrowser
        // Replace the whole code by the commented code if you are NOT using custombrowser.

        /*bundle = getIntent().getExtras();
        payuConfig = bundle.getParcelable(PayuConstants.PAYU_CONFIG);

        mWebView = (WebView) findViewById(R.id.webview);

        url = payuConfig.getEnvironment() == PayuConstants.PRODUCTION_ENV?  PayuConstants.PRODUCTION_PAYMENT_URL : PayuConstants.MOBILE_TEST_PAYMENT_URL ;

        byte[] encodedData = EncodingUtils.getBytes(payuConfig.getData(), "base64");
        mWebView.postUrl(url, encodedData);


        mWebView.getSettings().setSupportMultipleWindows(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient() {});
        mWebView.setWebViewClient(new WebViewClient() {});*/
        //endregion

        bundle = getIntent().getExtras();
        payuConfig = bundle.getParcelable(PayuConstants.PAYU_CONFIG);

        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

            mWebView.getSettings().setSupportMultipleWindows(true);
            mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            mWebView.addJavascriptInterface(new Object() {
                @JavascriptInterface
                public void onSuccess() {
                    onSuccess("");
                }

                @JavascriptInterface
                public void onSuccess(final String result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent();
                            intent.putExtra("result", result);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
//                }
                    });
                }

                @JavascriptInterface
                public void onFailure() {
                    onFailure("");
                }

                @JavascriptInterface
                public void onFailure(final String result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent();
                            intent.putExtra("result", result);
                            setResult(RESULT_CANCELED, intent);
                            finish();
                        }
                    });
                }
            }, "PayU");

            mWebView.setWebChromeClient(new WebChromeClient() {

            });
            mWebView.setWebViewClient(new WebViewClient());

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        url = payuConfig.getEnvironment() == PayuConstants.PRODUCTION_ENV?  PayuConstants.PRODUCTION_PAYMENT_URL : PayuConstants.MOBILE_TEST_PAYMENT_URL ;
        mWebView.postUrl(url, EncodingUtils.getBytes(payuConfig.getData(), "base64"));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_payments, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        if(cancelTransaction){
            cancelTransaction = false;
            Intent intent = new Intent();
            intent.putExtra("result", "Transaction canceled due to back pressed!");
            setResult(RESULT_CANCELED, intent);
            super.onBackPressed();
            return;
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);;
        alertDialog.setCancelable(false);
        alertDialog.setMessage("Do you really want to cancel the transaction ?");
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancelTransaction = true;
                dialog.dismiss();
                onBackPressed();
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
