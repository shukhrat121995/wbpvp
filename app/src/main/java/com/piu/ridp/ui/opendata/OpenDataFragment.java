package com.piu.ridp.ui.opendata;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.piu.ridp.R;

import org.jetbrains.annotations.NotNull;

public class OpenDataFragment extends Fragment {

    public OpenDataFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_open_data, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final WebView webView = view.findViewById(R.id.web_view);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                view.loadUrl("javascript: (function() { " +
                        "document.getElementsByClassName('header-area')[0].style.display='none';" +
                        "document.getElementsByClassName('dento-blog-area')[0].style.display='none';" +
                        "document.getElementsByClassName('dento-service-area')[0].style.display='none';" +
                        "document.getElementsByClassName('dento-about-us-area')[0].style.display='none';" +
                        "document.getElementsByClassName('dento-cta-area')[0].style.display='none';" +
                        "document.getElementsByClassName('footer-area')[0].style.display='none';" +
                        "document.getElementsByClassName('globalClass_191b')[0].style.display='none';" +
                        "" +
                        "})()");
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("jivosite.com")) return false;
                return super.shouldOverrideUrlLoading(view, url);
            }

        });
        webView.loadUrl("http://ridp.uz/" + getString(R.string.app_lang));
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == MotionEvent.ACTION_UP && webView.canGoBack()) {
                    webView.goBack();
                    return true;
                }
                return false;
            }
        });

    }
}
