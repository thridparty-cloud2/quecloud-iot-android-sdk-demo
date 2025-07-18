package com.quectel.app.demo;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.quectel.app.demo.ui.HomeActivity;
import com.quectel.app.demo.ui.SelectLoginActivity;
import com.quectel.app.quecnetwork.httpservice.IHttpCallBack;
import com.quectel.app.usersdk.userservice.IUserService;
import com.quectel.app.usersdk.utils.UserServiceFactory;
import com.quectel.basic.queclog.QLog;

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
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    setLoginRet(jsonObject.getInt("code") == 200);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFail(Throwable e) {
                QLog.e(e);
                setLoginRet(false);
            }
        });
    }

    private void setLoginRet(boolean isSuccess) {
        QLog.i("setLoginRet", "isSuccess:" + isSuccess);
        Intent intent = new Intent(MainActivity.this, isSuccess ? HomeActivity.class : SelectLoginActivity.class);
        startActivity(intent);
        finish();
    }
}