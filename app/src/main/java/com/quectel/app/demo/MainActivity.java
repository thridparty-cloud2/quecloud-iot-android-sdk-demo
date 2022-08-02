package com.quectel.app.demo;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.quectel.app.common.tools.utils.QuecCommonManager;
import com.quectel.app.demo.ui.HomeActivity;
import com.quectel.app.demo.ui.LoginActivity;
import com.quectel.app.demo.ui.SelectLoginActivity;
import com.quectel.app.demo.utils.ToastUtils;
import com.quectel.app.quecnetwork.httpservice.IHttpCallBack;
import com.quectel.app.usersdk.userservice.IUserService;
import com.quectel.app.usersdk.utils.UserServiceFactory;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UserServiceFactory.getInstance().getService(IUserService.class).queryUserInfo(new IHttpCallBack() {
            @Override
            public void onSuccess(String result) {
                String token =   UserServiceFactory.getInstance().getService(IUserService.class).getToken();
                if(!TextUtils.isEmpty(token))
                {
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Intent intent = new Intent(MainActivity.this, SelectLoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
            @Override
            public void onFail(Throwable e) {
                  e.printStackTrace();
            }
        });
    }

}