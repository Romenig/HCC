package com.humorcomciencia.hcc;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.util.List;

public class HCC extends Activity {

    private boolean firstLoadAccomplished = false;
    private ProgressBar progress;
    private  WebView browser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hcc);
        browser = (WebView) findViewById(R.id.webView);
        browser.setBackgroundColor(0x00000000);
        WebSettings webSettings = browser.getSettings();
        webSettings.setJavaScriptEnabled(true);
        browser.setWebViewClient(new MyBrowser());
        browser.setWebChromeClient(new MyChrome());
        browser.loadUrl("http://www.humorcomciencia.com");
        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setMax(100);
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
