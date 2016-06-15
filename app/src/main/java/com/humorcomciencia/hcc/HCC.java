package com.humorcomciencia.hcc;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

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
            view.loadUrl(url);
            return true;
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
