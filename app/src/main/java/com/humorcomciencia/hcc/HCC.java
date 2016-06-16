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
import android.os.AsyncTask;
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

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;

public class HCC extends Activity {

    private boolean firstLoadAccomplished = false;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressBar progress;
    private WebView browser;

    public class SendPostRequest extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return null;
        }

        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String result) {

        }

        public String getPostDataString(JSONObject params) throws Exception {

            StringBuilder result = new StringBuilder();
            boolean first = true;

            Iterator<String> itr = params.keys();

            while(itr.hasNext()){

                String key= itr.next();
                Object value = params.get(key);

                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(value.toString(), "UTF-8"));

            }
            return result.toString();
        }
    }


    /*
    BAGUNÇA
    */



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hcc);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)){
                    String token = intent.getStringExtra("token");



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
            //Verificar o tipo de erro:
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
                /*Uri uri = Uri.parse(url);
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                likeIng.setPackage("com.instagram.android");

                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/xxx")));
                }*/
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
