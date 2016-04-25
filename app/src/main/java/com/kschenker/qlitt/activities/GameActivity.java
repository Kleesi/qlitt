package com.kschenker.qlitt.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.kschenker.qlitt.R;

public class GameActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__game);
        addGlobalLayoutListener();
    }

    private void addGlobalLayoutListener()
    {
        View contentView = findViewById(android.R.id.content);
        if (contentView != null)
        {
            ViewTreeObserver viewTreeObserver = contentView.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new GlobalLayoutObserver());
        }
    }

    private class GlobalLayoutObserver implements ViewTreeObserver.OnGlobalLayoutListener
    {
        @Override
        public void onGlobalLayout()
        {
            printViewSize(findViewById(android.R.id.content), "content");
        }

        private void printViewSize(View view, String name)
        {
            if (view == null)
            {
                return;
            }

            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            float widthInDip = view.getWidth() / displayMetrics.density;
            float heightInDip = view.getHeight() / displayMetrics.density;

            String sizeMessageFormat = "Size of %s is (%.2fdp, %.2fdp)";
            Log.i("Display", String.format(sizeMessageFormat, name, widthInDip, heightInDip));
        }
    }

}
