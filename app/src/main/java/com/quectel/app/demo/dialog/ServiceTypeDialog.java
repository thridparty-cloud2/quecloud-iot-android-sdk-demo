package com.quectel.app.demo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.quectel.app.demo.R;
import com.quectel.app.demo.databinding.QuecServiceTypeDialogBinding;
import com.quectel.sdk.iot.QuecCloudServiceType;
import com.quectel.sdk.iot.QuecIotAppSdk;


public class ServiceTypeDialog extends Dialog {

    private Context context;
    private QuecServiceTypeDialogBinding binding;
    private OnConfirmClickListener onConfirmClickListener;


    public ServiceTypeDialog(@NonNull Context context) {
        this(context, R.style.quec_smart_config_BottomDialog);
        this.context = context;
    }

    public ServiceTypeDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ServiceTypeDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setWindowAnimations(R.style.quec_smart_config_BottomAnim);

        binding = QuecServiceTypeDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //空白处不能取消动画
        initLayout();
        initEvent();
    }


    private void initLayout() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = wm.getDefaultDisplay().getWidth();
        getWindow().setAttributes(params);
    }

    public void setOnConfirmClickListener(OnConfirmClickListener confirmClickListener) {
        if (confirmClickListener != null) {
            this.onConfirmClickListener = confirmClickListener;
        }
    }

    /**
     * 初始化界面的确定和取消监听
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        binding.btnCancel.setOnClickListener(view -> {
            if (isShowing()) {
                cancel();
            }
        });


        binding.tvChina.setOnClickListener(view -> {
            cancel();
            if(onConfirmClickListener!=null){
                onConfirmClickListener.onConfirm(QuecCloudServiceType.QuecCloudServiceTypeChina);
            }
        });

        binding.tvEurope.setOnClickListener(view -> {
            cancel();
            if(onConfirmClickListener!=null){
                onConfirmClickListener.onConfirm(QuecCloudServiceType.QuecCloudServiceTypeEurope);
            }
        });

        binding.tvNorthAmerica.setOnClickListener(view -> {
            cancel();
            if(onConfirmClickListener!=null){
                onConfirmClickListener.onConfirm(QuecCloudServiceType.QuecCloudServiceTypeNorthAmerica);
            }
        });

    }





    public interface OnConfirmClickListener {
        void onConfirm(QuecCloudServiceType type );
    }

}
