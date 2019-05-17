package com.topevery.um;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.AnimRes;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.topevery.um.entity.Authorizations;
import com.topevery.um.entity.Head;
import com.topevery.um.entity.Profile;
import com.topevery.um.entity.Settings;
import com.topevery.um.utils.CountClickListener;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * @author wujie
 */
public class LoginActivity extends AppCompatActivity {
	EditText etUser;
    EditText etPwd;
    Button btnLogin;
    View vProgress;
    ImageView ivBg;
    ImageView ivLabel;
    View vLoginFrame;

    private boolean isTest = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTransparent();

        if (isTest) {
            Intent intent = new Intent(LoginActivity.this, WebActivity.class);
//            intent.setData(Uri.parse("file:///android_asset/index.html"));
            startActivity(intent);
            finish();
            return;
        }

        String serverHost = Settings.getServerHost(getApplicationContext());
        if (TextUtils.isEmpty(serverHost)) {
            Intent intent = new Intent(LoginActivity.this, ServerSettingActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        etUser = findViewById(R.id.et_login_user);
        etPwd = findViewById(R.id.et_login_pwd);
        btnLogin = findViewById(R.id.btn_login);
        vProgress = findViewById(R.id.ll_login_progress);
//        ivBg = findViewById(R.id.iv_login_bg);
        ivLabel = findViewById(R.id.iv_login_label);
        vLoginFrame = findViewById(R.id.ll_login_frame);

        init();

        ivLabel.setOnClickListener(new CountClickListener(5) {

            @Override
            public void onCountClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ServerSettingActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 设置状态栏半透明
     */
    private void setStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(0x00000000);
        }
    }

    private void init() {
        Authorizations authorizations = Authorizations.getInstance(getApplicationContext());
        if (authorizations.getProfile() != null) {
//            final String mainUrl = getMainUrl();
//            connectUrlSync(mainUrl).subscribe(new Consumer<Boolean>() {
//                @Override
//                public void accept(Boolean result) throws Exception {
//                    if (result) {
//                        navigateToWebActivity(mainUrl);
//                    }
//                }
//            }, new Consumer<Throwable>() {
//                @Override
//                public void accept(Throwable throwable) throws Exception {
//                }
//            });
            navigateToWebActivity(null);
        } else {
            final String user = authorizations.getUsername();
            final String pwd = authorizations.getPassword();
            if (!TextUtils.isEmpty(user)) {
                etUser.setText(user);
                etUser.setSelection(user.length());
                if (!TextUtils.isEmpty(pwd)) {
                    etPwd.setText(pwd);
                }
            }
//            startAnimation(ivBg, R.anim.splash_bg);
//            startAnimation(ivLabel, R.anim.splash_login_avatar);
//            startAnimation(vLoginFrame, R.anim.splash_login_frame);
        }
    }

    private void startAnimation(View view, @AnimRes int resId) {
        Animation animation = AnimationUtils.loadAnimation(this, resId);
        animation.setFillAfter(true);
        view.startAnimation(animation);
    }

    public void onLogin(View v) {
        String username = etUser.getText().toString().trim();
        String password = etPwd.getText().toString().trim();
        if (check(username, password)) {
            // 隐藏软键盘
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }

            login(username, password);
        }
    }

    public void onPassword(View v) {
        if (v.isSelected()) {
            v.setSelected(false);
            ((ImageView) v).setImageResource(R.mipmap.invisible);
            etPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            v.setSelected(true);
            ((ImageView) v).setImageResource(R.mipmap.visible);
            etPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
    }

    private boolean check(String user, String pwd) {
        if (TextUtils.isEmpty(user)) {
            toast(R.string.toast_empty_user);
            etUser.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(pwd)) {
            toast(R.string.toast_empty_pwd);
            etPwd.requestFocus();
            return false;
        }
        return true;
    }

    private void login(final String user, final String pwd) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        HashMap<String, String> map = new HashMap<>();
        map.put("LoginName", user);
        map.put("LoginPassword", pwd);
        String content = new Gson().toJson(map);
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json"), content);

        String url = getLoginUrl();
        final Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        vProgress.setVisibility(View.VISIBLE);
        btnLogin.setVisibility(View.GONE);

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(final Call call, final IOException e) {
                if (Looper.myLooper() == Looper.getMainLooper()) {
                    vProgress.setVisibility(View.GONE);
                    btnLogin.setVisibility(View.VISIBLE);
                    toast(R.string.login_failure);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onFailure(call, e);
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                if (body == null) {
                    onFailure(call, new IOException());
                    return;
                }
                try {
                    onResponse(call, response, body.string());
                } catch (IOException e) {
                    onFailure(call, e);
                }
            }

            void onResponse(final Call call, final Response response, final String string) {
                if (Looper.myLooper() == Looper.getMainLooper()) {
                    vProgress.setVisibility(View.GONE);
                    btnLogin.setVisibility(View.VISIBLE);

                    boolean isSuccess = false;
                    Head<Profile> result = null;
                    try {
                        Type type = new TypeToken<Head<Profile>>() {}.getType();
                        result = new Gson().fromJson(string, type);
                        isSuccess = result != null && result.isSuccess();
                    } catch (Exception e) {
                        isSuccess = false;
                    }

                    if (!isSuccess) {
                        if (result != null) {
                            toast(getString(R.string.login_failure) + ":" + result.getMessage());
                        } else {
                            toast(R.string.login_failure);
                        }
                    } else {
                        Profile profile = result.getData();
                        if (profile != null) {
                            new Authorizations.Builder(getApplicationContext())
                                    .setUsername(user)
                                    .setPassword(pwd)
                                    .setProfile(profile)
                                    .build();

//                            final String mainUrl = getMainUrl();
//                            connectUrlSync(mainUrl).subscribe(new Consumer<Boolean>() {
//                                @Override
//                                public void accept(Boolean result) throws Exception {
//                                    if (result) {
//                                        navigateToWebActivity(mainUrl);
//                                    } else {
//                                        toast(R.string.failed_to_connect_server);
//                                    }
//                                }
//                            }, new Consumer<Throwable>() {
//                                @Override
//                                public void accept(Throwable throwable) throws Exception {
//                                    toast(R.string.invalid_url);
//                                }
//                            });

                            navigateToWebActivity(null);
                        }
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onResponse(call, response, string);
                        }
                    });
                }
            }
        });
    }

    public void navigateToWebActivity(String url) {
        Intent intent = new Intent(LoginActivity.this, WebActivity.class);
//        intent.setData(Uri.parse(url));
        startActivity(intent);
        finish();
    }

    public static Single<Boolean> connectUrlSync(final String url) {
        return Single.create(new SingleOnSubscribe<Boolean>() {

            @Override
            public void subscribe(SingleEmitter<Boolean> emitter) throws Exception {
                if (!URLUtil.isNetworkUrl(url)) {
                    emitter.onError(new MalformedURLException("Invalid URL"));
                    return;
                }
                HttpURLConnection.setFollowRedirects(false);
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setRequestMethod("HEAD");
                    connection.setConnectTimeout(5000);
                    boolean result = connection.getResponseCode() == HttpURLConnection.HTTP_OK;
                    if (result) {
                        emitter.onSuccess(true);
                    } else {
                        emitter.onSuccess(false);
                    }
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public String getLoginUrl() {
        String serverHost = Settings.getServerHost(getApplicationContext());
        return "http://" + serverHost + "/zhcg.zhzf/api/BasicInfo/AppLogin";
    }

    public String getMainUrl() {
        String serverHost = Settings.getServerHost(getApplicationContext());
        return "http://" + serverHost + "/webwechat/mobile/zhifa/app.html";
    }

    private void toast(CharSequence message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void toast(@StringRes int resId) {
        Toast.makeText(getApplicationContext(), resId, Toast.LENGTH_SHORT).show();
    }
}
