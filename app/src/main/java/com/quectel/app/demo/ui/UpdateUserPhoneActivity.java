package com.quectel.app.demo.ui;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.quectel.app.demo.base.activity.QuecBaseActivity;
import com.quectel.app.demo.databinding.ActivityUpdateUserPhoneBinding;
import com.quectel.app.demo.utils.MyUtils;
import com.quectel.app.usersdk.constant.UserConstant;
import com.quectel.app.usersdk.service.QuecUserService;

public class UpdateUserPhoneActivity extends QuecBaseActivity<ActivityUpdateUserPhoneBinding> {

    @NonNull
    @Override
    public ActivityUpdateUserPhoneBinding getViewBinding() {
        return ActivityUpdateUserPhoneBinding.inflate(getLayoutInflater());
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        binding.btGetCode.setOnClickListener(v -> getCode(true));
        binding.btGetCode2.setOnClickListener(v -> getCode(false));
        binding.ivBack.setOnClickListener(v -> finish());
        binding.btSure.setOnClickListener(v -> confirm());
    }

    private void getCode(boolean isNew) {
        String phone = isNew ? binding.editNewPhone.getText().toString() : binding.editOldPhone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            showMessage("请输入手机号");
            return;
        }
        showOrHideLoading(true);
        QuecUserService.INSTANCE.sendVerifyCodeByPhone(phone, "86",
                isNew ? UserConstant.TYPE_SMS_CODE_REGISTER : UserConstant.TYPE_SMS_CODE_LOGOFF, result -> {
                    showOrHideLoading(false);
                    handlerResult(result);
                });
    }

    private void confirm() {
        String newPhone = MyUtils.getEditTextContent(binding.editNewPhone);
        String oldPhone = MyUtils.getEditTextContent(binding.editOldPhone);
        String newCode = MyUtils.getEditTextContent(binding.editYanzheng);
        String oldCode = MyUtils.getEditTextContent(binding.editYanzheng2);

        if (TextUtils.isEmpty(newPhone) || TextUtils.isEmpty(oldPhone)) {
            showMessage("请输入新手机号");
            return;
        }

        if (TextUtils.isEmpty(newCode) || TextUtils.isEmpty(oldCode)) {
            showMessage("请输入验证码");
            return;
        }

        QuecUserService.INSTANCE.updatePhone(newPhone, "86", newCode, oldPhone, "86", oldCode, result -> {
            handlerResult(result);
            if (result.isSuccess()) {
                finish();
            }
        });
    }

    @Override
    public void initData() {

    }
}
