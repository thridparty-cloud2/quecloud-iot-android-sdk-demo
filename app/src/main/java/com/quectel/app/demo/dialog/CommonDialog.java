package com.quectel.app.demo.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.quectel.app.demo.R;
import com.quectel.basic.common.interfaces.QuecClickListener;
import com.quectel.basic.common.utils.QuecActivityUtil;
import com.quectel.basic.queclog.QLog;

public class CommonDialog extends Dialog {

    private TextView yes;//确定按钮
    private View viewLine;//分割竖线
    private TextView no;//取消按钮

    private TextView titleTV;//消息标题文本
    private TextView message;//消息提示文本


    private String titleStr;//从外界设置的title文本
    private String messageStr;//从外界设置的消息文本
    //对话框内容字体 大小
    private int messageTextSize = 0;
    //对话框内容字体 颜色
    private int messageTextColor = 0;
    //对话框内容字体 收否加粗
    private boolean isBold = false;

    private SpannableString spannableString;//富文本
    private MovementMethod movementMethod;//拦截事件(可自定义)


    //确定文本和取消文本的显示的内容
    private String yesStr, noStr;
    private int yesTextColor, noTextColor;

    private int textGravity = 0;

    private onNoOnclickListener noOnclickListener;//取消按钮被点击了的监听器
    private onYesOnclickListener yesOnclickListener;//确定按钮被点击了的监听器

    public static final int TITLE_VISIBLE = 0;
    public static final int TITLE_GONE = 1;

    public static final int MESSAGE_VISIBLE = 0;
    public static final int MESSAGE_GONE = 1;

    private boolean isEnableBackPressed = true; //默认允许返回关闭Dialog


    public CommonDialog(@NonNull Context context) {
        super(context, R.style.quec_basic_ui_MyDialog);
        setOwnerActivity(context);
    }

    public CommonDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        setOwnerActivity(context);
    }

    protected CommonDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        setOwnerActivity(context);
    }

    private void setOwnerActivity(@NonNull Context context) {
        if (context instanceof Activity) {
            super.setOwnerActivity((Activity) context);
        } else {
            QLog.e("QuecDialog needs context as Activity!!!");
        }
    }


    /**
     * 设置取消按钮的显示内容和监听
     *
     * @param str
     * @param onNoOnclickListener
     */
    public CommonDialog setNoOnclickListener(String str, onNoOnclickListener onNoOnclickListener) {
        if (str != null) {
            noStr = str;
        }
        this.noOnclickListener = onNoOnclickListener;
        return this;
    }


    public CommonDialog setNoOnclickListener(String str, int textColor, onNoOnclickListener onNoOnclickListener) {
        if (str != null) {
            noStr = str;
        }
        if (textColor != 0) {
            this.noTextColor = textColor;
        }
        this.noOnclickListener = onNoOnclickListener;
        return this;
    }


    /**
     * 设置确定按钮的显示内容和监听
     *
     * @param str
     * @param yesOnclickListener
     */
    public CommonDialog setYesOnclickListener(String str, onYesOnclickListener yesOnclickListener) {
        if (str != null) {
            yesStr = str;
        }
        this.yesOnclickListener = yesOnclickListener;
        return this;
    }


    public CommonDialog setYesOnclickListener(String str, int textColor, onYesOnclickListener yesOnclickListener) {
        if (str != null) {
            yesStr = str;
        }
        if (textColor != 0) {
            this.yesTextColor = textColor;
        }
        this.yesOnclickListener = yesOnclickListener;
        return this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quec_basic_ui_dialog);
        //空白处不能取消动画
        setCanceledOnTouchOutside(false);

        //初始化界面控件
        initView();
        //初始化界面数据
        initData();
        //初始化界面控件的事件
        initEvent();
    }


    /**
     * 初始化界面控件
     */
    private void initView() {
        yes = findViewById(R.id.yes);
        viewLine = findViewById(R.id.viewLine);
        no = findViewById(R.id.no);
        titleTV = findViewById(R.id.title);
        message = findViewById(R.id.message);

        message.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    /**
     * 初始化界面控件的显示数据
     */
    private void initData() {
        //如果用户自定了title和message
        if (titleStr != null) {
            titleTV.setText(titleStr);
        }
        if (messageStr != null) {
            message.setText(messageStr);
            if (textGravity != 0) {
                message.setGravity(textGravity);
            }
            if (messageTextSize != 0) {
                message.setTextSize(messageTextSize);
            }
            if (messageTextColor != 0) {
                message.setTextColor(messageTextColor);
            }
            if (isBold) {
                message.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }
        } else {
            if (spannableString != null) {
                message.setText(spannableString);
            }
            if (movementMethod != null) {
                message.setMovementMethod(movementMethod);
            }
        }
        //如果设置按钮文字
        if (yesStr != null) {
            yes.setText(yesStr);
        }
        if (noStr != null) {
            no.setText(noStr);
        }
        if (yesTextColor != 0) {
            yes.setTextColor(yesTextColor);
        }
        if (noTextColor != 0) {
            no.setTextColor(noTextColor);
        }
    }

    /**
     * 初始化界面的确定和取消监听
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        yes.setOnClickListener(new QuecClickListener() {
            @Override
            public void onViewClick(View v) {
                if (yesOnclickListener != null) {
                    yesOnclickListener.onYesOnclick();
                }
            }
        });
        //设置取消按钮被点击后，向外界提供监听
        no.setOnClickListener(new QuecClickListener() {
            @Override
            public void onViewClick(View v) {
                if (noOnclickListener != null) {
                    noOnclickListener.onNoClick();
                }
            }
        });
    }

    /**
     * 从外界Activity为Dialog设置标题
     *
     * @param title
     */
    public CommonDialog setTitle(String title) {
        titleStr = title;
        return this;
    }

    /**
     * 从外界Activity为Dialog设置内容的位置
     * @param textGravity
     * @return
     */
    public CommonDialog setMessageGravity(int textGravity) {
        this.textGravity = textGravity;
        return this;
    }

    /**
     * 从外界Activity为Dialog设置message
     *
     * @param message
     */
    public CommonDialog setMessage(String message) {
        messageStr = message;
        return this;
    }

    /**
     * 从外界Activity为Dialog设置message
     *
     * @param message
     */
    public CommonDialog setMessageStyle(String message, int messageTextSize, int messageTextColor, boolean isBold) {
        messageStr = message;
        this.messageTextSize = messageTextSize;
        this.messageTextColor = messageTextColor;
        this.isBold = isBold;
        return this;
    }

    public void setTitleVisibleOrGone(int type) {
        if (titleTV != null) {
            if (type == TITLE_VISIBLE) {
                titleTV.setVisibility(View.VISIBLE);
            } else if (type == TITLE_GONE) {
                titleTV.setVisibility(View.GONE);
            }
        }
    }

    public void setMessageVisibleOrGone(int type) {
        if (message != null) {
            if (type == MESSAGE_VISIBLE) {
                message.setVisibility(View.VISIBLE);
            } else if (type == MESSAGE_GONE) {
                message.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 设置富文本和自定义事件
     *
     * @param spannableString
     * @param movementMethod
     * @return
     */
    public CommonDialog setMessage(SpannableString spannableString, MovementMethod movementMethod) {
        this.spannableString = spannableString;
        this.movementMethod = movementMethod;
        return this;
    }

    /**
     * 设置取消按钮的是否可见
     *
     * @param visible 可见类型
     * @return QuecDialog
     */
    public CommonDialog setNoBtnVisible(int visible) {
        this.no.setVisibility(visible);
        this.viewLine.setVisibility(View.GONE);
        return this;
    }

    @Override
    public void show() {
        if (QuecActivityUtil.isAvailable(this.getOwnerActivity())) {
            super.show();
        }
    }

    @Override
    public void dismiss() {
        if (QuecActivityUtil.isAvailable(this.getOwnerActivity())) {
            super.dismiss();
        }
    }

    /**
     * 设置确认按钮的是否可见
     *
     * @param visible 可见类型
     * @return QuecDialog
     */
    public CommonDialog setYesBtnVisible(int visible) {
        this.yes.setVisibility(visible);
        this.viewLine.setVisibility(View.GONE);
        return this;
    }

    public interface onNoOnclickListener {
        void onNoClick();
    }

    public interface onYesOnclickListener {
        void onYesOnclick();
    }

    @Override
    public void onBackPressed() {
        if (isEnableBackPressed) {
            super.onBackPressed();
        }
    }

    /**
     * 设置是否可以返回关闭窗口
     * @param isEnabled true 可以 false 不可以
     * @return
     */
    public CommonDialog setEnableBackPressed(boolean isEnabled) {
        isEnableBackPressed = isEnabled;
        return this;
    }
}
