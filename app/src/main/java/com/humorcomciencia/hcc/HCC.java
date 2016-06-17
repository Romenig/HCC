package com.humorcomciencia.hcc;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class HCC extends Activity {

    private boolean firstLoadAccomplished = false;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressBar progress;
    private WebView browser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hcc);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)){
                    String token = intent.getStringExtra("token");
                    OutputStream os = null;
                    HttpURLConnection conn = null;
                    try {
                        URL url = new URL("http://www.humorcomciencia.com/pnfw/register/");

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("token", token);
                        jsonObject.put("os", "Android");
                        String message = jsonObject.toString();

                        conn = (HttpURLConnection) url.openConnection();
                        conn.setReadTimeout( 10000 /*milliseconds*/ );
                        conn.setConnectTimeout( 15000 /* milliseconds */ );
                        conn.setRequestMethod("POST");
                        conn.setDoInput(true);
                        conn.setFixedLengthStreamingMode(message.getBytes().length);

                        //make some HTTP header nicety
                        conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                        conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

                        //open
                        conn.connect();

                        //setup send
                        os = new BufferedOutputStream(conn.getOutputStream());
                        os.write(message.getBytes());
                        //clean up
                        os.flush();

                    } catch(MalformedURLException e){

                    } catch(IOException e) {

                    } catch(JSONException e){

                    } finally {
                        try {
                            os.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        conn.disconnect();
                    }
                }else if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)){

                }else{

                }
            }
        };

        browser = (WebView) findViewById(R.id.webView);
        browser.setBackgroundColor(0x00000000);
        WebSettings webSettings = browser.getSettings();
        webSettings.setJavaScriptEnabled(true);
        browser.setWebViewClient(new MyBrowser());
        browser.setWebChromeClient(new MyChrome());
        browser.loadUrl("http://www.humorcomciencia.com");
        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setMax(100);

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if(ConnectionResult.SUCCESS != resultCode){
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)){
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());
            }else{

            }
        }else{
            Intent intent = new Intent(this, GCMRegistrationIntentService.class);
            startService(intent);
        }

    }

    @Override
    public void onBackPressed() {
        if(browser.canGoBack()){
            browser.goBack();
        }else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w("HCC", "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w("HCC", "onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    private class MyChrome extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            HCC.this.setValue(newProgress);
            super.onProgressChanged(view, newProgress);
        }
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if( url.contains("instagram.com")) {
                Intent insta_intent = getPackageManager().getLaunchIntentForPackage("com.instagram.android");
                insta_intent.setComponent(new ComponentName("com.instagram.android", "com.instagram.android.activity.UrlHandlerActivity"));
                if(url.contains("humor"))
                    insta_intent.setData(Uri.parse("https://www.instagram.com/_u/humorcomciencia/"));
                else
                    insta_intent.setData(Uri.parse(url));
                startActivity(insta_intent);
                return true;
            }else if( url.startsWith("http:") || url.startsWith("https:")  ) {
                view.loadUrl(url);
                return true;
            }
            // Otherwise allow the OS to handle things like tel, mailto, etc.
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity( intent );
            return true;
        }



        private boolean isIntentAvailable(Intent intent) {
            final PackageManager packageManager = getPackageManager();
            List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            return list.size() > 0;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            HCC.this.progress.setProgress(0);
            HCC.this.progress.setVisibility(View.VISIBLE);
            if(!firstLoadAccomplished){
                view.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            HCC.this.progress.setVisibility(View.INVISIBLE);
            if(!firstLoadAccomplished){
                view.setVisibility(View.VISIBLE);
                firstLoadAccomplished = true;
            }
        }
    }

    public void setValue(int progress) {
        this.progress.setProgress(progress);
    }




}
