package com.cncoderx.photopicker.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.cncoderx.photopicker.R;
import com.cncoderx.photopicker.adapter.PreviewAdapter;
import com.cncoderx.photopicker.widget.CirclePageIndicator;

/**
 * @author wujie
 */
public class PreviewActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(0x00000000);
        }

        setContentView(R.layout.activity_preview);

        Intent intent = getIntent();
        final int current = intent.getIntExtra("current", 0);
        final String[] urls = intent.getStringArrayExtra("urls");

        if (urls == null || urls.length == 0) {
            Toast.makeText(getApplicationContext(), "请至少传入一张图片", Toast.LENGTH_SHORT).show();
            return;
        }

        PreviewAdapter adapter = new PreviewAdapter(this);
        adapter.addAll(urls);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(current);

        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.pager_indicator);
        indicator.setPageColor(0xff808080);
        indicator.setFillColor(0xffffffff);
        indicator.setCentered(true);
        indicator.setRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                3f, getResources().getDisplayMetrics()));
        indicator.setViewPager(viewPager, current);

        if (urls.length == 1) {
            indicator.setVisibility(View.INVISIBLE);
        }
    }
}
