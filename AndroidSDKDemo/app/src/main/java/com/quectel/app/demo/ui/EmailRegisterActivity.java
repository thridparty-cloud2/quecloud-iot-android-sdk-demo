package com.quectel.app.demo.ui;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.quectel.app.demo.R;
import com.quectel.app.demo.base.BaseActivity;
import com.quectel.app.demo.utils.MyUtils;
import com.quectel.app.demo.utils.ToastUtils;
import com.quectel.app.quecnetwork.httpservice.IHttpCallBack;
import com.quectel.app.usersdk.userservice.IUserService;
import com.quectel.app.usersdk.utils.UserServiceFactory;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

public class EmailRegisterActivity extends BaseActivity {

    @Override
    protected int getContentLayout() {
        return R.layout.activity_email_register;
    }

    @Override
    protected void addHeadColor() {
        MyUtils.addStatusBarView(this,R.color.gray_bg);
    }

    @BindView(R.id.edit_email)
    EditText edit_email;

    @BindView(R.id.edit_pass)
    EditText edit_pass;

    @BindView(R.id.edit_yanzheng)
    EditText edit_yanzheng;

    @BindView(R.id.bt_getCode)
    Button bt_getCode;


    @Override
    protected void initData() {


    }

    @OnClick({R.id.iv_back, R.id.bt_register,R.id.bt_getCode})
    public void onViewClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.bt_getCode:

                    String mail = MyUtils.getEditTextContent(edit_email);
                    if (TextUtils.isEmpty(mail)) {
                        ToastUtils.showShort(activity, "请输入邮箱");
                        return;
                    }

                    startLoading();
                    //发送邮箱验证码
                    UserServiceFactory.getInstance().getService(IUserService.class).sendEmailRegisterCode(
                            mail,new IHttpCallBack(){
                                @Override
                                public void onSuccess(String result) {
                                   finishLoading();
                                    try {
                                        JSONObject  obj = new JSONObject(result);
                                        if (obj.getInt("code") == 200) {
                                             ToastUtils.showShort(activity,"验证码已发送");
                                        }
                                        else
                                        {
                                            ToastUtils.showShort(activity,obj.getString("msg"));
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                                @Override
                                public void onFail(Throwable e) {
                                      e.printStackTrace();
                                }
                            }

                    );

                break;

            case R.id.iv_back:
                finish();
              break;

            case R.id.bt_register:

                String email = MyUtils.getEditTextContent(edit_email);
                if(TextUtils.isEmpty(email))
                {
                    ToastUtils.showLong(activity,"请输入邮箱");
                    return;
                }
                String pass = MyUtils.getEditTextContent(edit_pass);
                if(TextUtils.isEmpty(pass))
                {
                    ToastUtils.showLong(activity,"请输入密码");
                    return;
                }

                String verifyCode = MyUtils.getEditTextContent(edit_yanzheng);
                if(TextUtils.isEmpty(verifyCode))
                {
                    ToastUtils.showLong(activity,"请输入验证码");
                    return;
                }

                //邮箱注册
                UserServiceFactory.getInstance().getService(IUserService.class).emailPwdRegister(
                        verifyCode,email,pass,0,0,0,
                        new IHttpCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                try {
                                    JSONObject  obj = new JSONObject(result);
                                    if (obj.getInt("code") == 200) {
                                        ToastUtils.showShort(activity,"注册成功");
                                        finish();
                                    }
                                    else
                                    {
                                        ToastUtils.showShort(activity,obj.getString("msg"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onFail(Throwable e) {
                                e.printStackTrace();
                            }
                        }
                );

                break;

        }

    }

}
