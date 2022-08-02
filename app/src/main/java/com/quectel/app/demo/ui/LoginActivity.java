package com.quectel.app.demo.ui;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.quectel.app.demo.R;
import com.quectel.app.demo.base.BaseActivity;
import com.quectel.app.demo.utils.MyUtils;
import com.quectel.app.demo.utils.ToastUtils;
import com.quectel.app.quecnetwork.httpservice.IHttpCallBack;
import com.quectel.app.quecnetwork.httpservice.IResponseCallBack;
import com.quectel.app.usersdk.userservice.IUserService;
import com.quectel.app.usersdk.utils.UserServiceFactory;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity  extends BaseActivity {

    @Override
    protected int getContentLayout() {
        return R.layout.activity_login;
    }

    @Override
    protected void addHeadColor() {
        MyUtils.addStatusBarView(this,R.color.gray_bg);
    }

    @BindView(R.id.edit_phone)
    EditText edit_phone;

    @BindView(R.id.edit_pass)
    EditText edit_pass;

    @BindView(R.id.rl_two)
    RelativeLayout rl_two;

    @BindView(R.id.rl_three)
    RelativeLayout rl_three;

    boolean loginStyle = false;

    @BindView(R.id.bt_style)
    Button bt_style;

    @BindView(R.id.edit_yanzheng)
    EditText edit_yanzheng;

    @BindView(R.id.bt_getCode)
    Button bt_getCode;

    Handler handler;
    int countNum = 0;
    private static final int INTERVAL = 2000;
    private long mExitTime;

    @Override
    protected void initData() {

        bt_style.setText("验证码登录");
        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                int  dCount = msg.arg1;
                if (dCount == 0)
                {
                    bt_getCode.setText("获取验证码");
                    bt_getCode.setEnabled(true);
                }
                else
                {
                    bt_getCode.setEnabled(false);
                    bt_getCode.setText("(" + dCount + ")秒");
                }
            }
        };

//        RxPermissions rxPermissions = new RxPermissions(this);
//        rxPermissions
//                .request(
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.READ_EXTERNAL_STORAGE
//                )
//                .subscribe(new Consumer<Boolean>() {
//                               @Override
//                               public void accept(Boolean grant) throws Exception {
//                                   if(grant)
//                                   {
//
//                                   }
//                                   else
//                                   {
//                                      // ToastUtils.showLong(activity,"权限禁止拍照相册可能无法使用");
//
//                                   }
//
//                               }
//                           }
//
//                );



    }

    private Runnable dRunnable = new Runnable() {
        @Override
        public void run() {
            countNum = countNum - 1;
            Message msg = new Message();
            msg.arg1 = countNum;
            handler.sendMessage(msg);
            if(countNum==0)
            {
                handler.removeCallbacks(dRunnable);
                return;
            }
            handler.postDelayed(dRunnable,1000);
        }
    };


    @OnClick({R.id.iv_back, R.id.bt_login,R.id.tv_register,R.id.bt_style,R.id.bt_getCode})
    public void onViewClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.bt_getCode:
                if (System.currentTimeMillis() - mExitTime > INTERVAL)
                {
                    String phoneContent = MyUtils.getEditTextContent(edit_phone);
                    if (TextUtils.isEmpty(phoneContent)) {
                        ToastUtils.showShort(activity, "请输入手机号码");
                        return;
                    }

                    UserServiceFactory.getInstance().getService(IUserService.class).sendPhoneSmsCode(
                            "86",phoneContent,3,"","",new IHttpCallBack(){
                                @Override
                                public void onSuccess(String result) {
                                    System.out.println("sendPhoneSmsCode onSuccess-:"+result);

                                    try {
                                        JSONObject  obj = new JSONObject(result);
                                        if (obj.getInt("code") == 200) {
                                            countNum = 60;
                                            handler.postDelayed(dRunnable,1000);
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
                    mExitTime = System.currentTimeMillis();
                }
                 break;

            case R.id.bt_style:

                if(!loginStyle)
                {
                    bt_style.setText("密码登录");
                    loginStyle = true;

                    rl_three.setVisibility(View.VISIBLE);
                    rl_two.setVisibility(View.GONE);

                }
                else
                {
                    bt_style.setText("验证码登录");
                    loginStyle = false;
                    rl_three.setVisibility(View.GONE);
                    rl_two.setVisibility(View.VISIBLE);
                }

                break;

            case R.id.iv_back:
                finish();
              break;
            case R.id.tv_register:
                System.out.println("tv_register");
                 intent = new Intent(activity, RegisterActivity.class);
                 startActivity(intent);

                break;

            case R.id.bt_login:
                System.out.println("bt_login");
                String phone = MyUtils.getEditTextContent(edit_phone);
                String pass = MyUtils.getEditTextContent(edit_pass);
                System.out.println("str1-:"+phone);
                System.out.println("pass-:"+pass);
                if(TextUtils.isEmpty(phone))
                {
                    ToastUtils.showLong(activity,"请输入手机号码");
                    return;
                }


                if(!loginStyle)
                {
                    if(TextUtils.isEmpty(pass))
                    {
                        ToastUtils.showLong(activity,"请输入密码");
                        return;
                    }
                    //internationalCode 默认不传或传"" 默认国内
                    //手机号密码登录


                    UserServiceFactory.getInstance().getService(IUserService.class).phonePwdLogin(
                            phone, pass, "", new IResponseCallBack() {
                                @Override
                                public void onSuccess() {
                                    System.out.println("--onSuccess-phoneLogin-");
                                    ToastUtils.showShort(activity,"登录成功");
                                    edit_phone.setText("");
                                    startActivity(new Intent(activity,HomeActivity.class));

                                    finish();
                                }

                                @Override
                                public void onFail(Throwable e) {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onError(int code,String errorMsg) {
                                    ToastUtils.showShort(activity,errorMsg);
                                }
                            }
                    );
                }
                else
                {

                    String verifyCode = MyUtils.getEditTextContent(edit_yanzheng);
                    System.out.println("verifyCode--:"+verifyCode);
                    if(TextUtils.isEmpty(verifyCode))
                    {
                        ToastUtils.showLong(activity,"请输入验证码");
                        return;
                    }

                    UserServiceFactory.getInstance().getService(IUserService.class).phoneSmsCodeLogin(phone,
                            verifyCode, "", new IResponseCallBack() {
                                @Override
                                public void onSuccess() {
                                    System.out.println("login success Sms");
                                    ToastUtils.showShort(activity,"登录成功");
                                    edit_phone.setText("");
                                    startActivity(new Intent(activity,HomeActivity.class));
                                    finish();
                                }
                                @Override
                                public void onFail(Throwable e) {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onError(int code,String errorMsg) {
                                    ToastUtils.showShort(activity,errorMsg);
                                }
                            }

                    );

                }

                break;

        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(handler!=null)
        {
            handler.removeCallbacks(dRunnable);
        }

    }

}
