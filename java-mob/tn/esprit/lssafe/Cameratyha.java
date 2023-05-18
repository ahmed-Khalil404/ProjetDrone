package tn.esprit.lssafe;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class Cameratyha extends AppCompatActivity {


    ProgressBar superProgressBar;
    ImageView superImageView;
    WebView superWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cameratyha);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        superProgressBar=findViewById(R.id.myProgressBar);
        superImageView=findViewById(R.id.myImageView);
        superWebView=findViewById(R.id.myWebView);


        superProgressBar.setMax(100);
        superWebView.loadUrl("http://172.20.10.5:8000");
        superWebView.getSettings().setJavaScriptEnabled(true);

        superWebView.getSettings().setLoadWithOverviewMode(true);
        superWebView.getSettings().setUseWideViewPort(true);
        //superWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        //superWebView.setScrollbarFadingEnabled(true);

        superWebView.setWebViewClient(new WebViewClient());
        superWebView.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public void onProgressChanged(WebView view, int newProgress)
            {
                super.onProgressChanged(view, newProgress);
                superProgressBar.setProgress(newProgress);
            }
/*
            @Override
            public void onReceivedTitle(WebView view, String title)
            {
                super.onReceivedTitle(view, title);
                getSupportActionBar().setTitle(title);
            }
*/
            @Override
            public void onReceivedIcon(WebView view, Bitmap icon)
            {
                super.onReceivedIcon(view, icon);
                superImageView.setImageBitmap(icon);
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        if(superWebView.canGoBack())
        {
            superWebView.goBack();
        }
        else        {
            finish();
        }
    }
}

