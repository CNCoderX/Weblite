package com.topevery.um;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.topevery.um.entity.Settings;

/**
 * @author wujie
 */
public class ServerSettingActivity extends AppCompatActivity {
    TextView tvTitle;
    ImageView ivNav;
    EditText etHttpIp;
    EditText etHttpPort;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_setting);
        tvTitle = findViewById(R.id.tv_title_bar_title);
        ivNav = findViewById(R.id.iv_title_bar_nav_icon);
        etHttpIp = findViewById(R.id.et_setting_http_ip);
        etHttpPort = findViewById(R.id.et_setting_http_port);

        tvTitle.setText(R.string.server_setting);
        ivNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        String serverIp = Settings.getServerIp(getApplicationContext());
        int serverPort = Settings.getServerPort(getApplicationContext());
        if (!TextUtils.isEmpty(serverIp)) {
            etHttpIp.setText(serverIp);
        }
        if (serverPort != 0) {
            etHttpPort.setText(String.valueOf(serverPort));
        }
    }

    @Override
    public void onBackPressed() {
        saveSettings();
        super.onBackPressed();
    }

    private void saveSettings() {
        String serverIp = etHttpIp.getText().toString();
        String serverPort = etHttpPort.getText().toString();
        if (!TextUtils.isEmpty(serverIp)) {
            Settings.setServerIp(getApplicationContext(), serverIp);
        }
        if (TextUtils.isEmpty(serverPort)) {
            Settings.setServerPort(getApplicationContext(), 0);
        } else {
            Settings.setServerPort(getApplicationContext(), Integer.parseInt(serverPort));
        }
    }
}
