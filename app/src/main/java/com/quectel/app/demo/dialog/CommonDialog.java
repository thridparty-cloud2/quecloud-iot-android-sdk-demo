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

    private TextView yes; // Confirm button
    private View viewLine; // Divider line
    private TextView no; // Cancel button

    private TextView titleTV; // Title text
    private TextView message; // Message text


    private String titleStr; // Title text set externally
    private String messageStr; // Message text set externally
    // Dialog content font size
    private int messageTextSize = 0;
    // Dialog content font color
    private int messageTextColor = 0;
    // Dialog content font bold
    private boolean isBold = false;

    private SpannableString spannableString; // Rich text
    private MovementMethod movementMethod; // Touch event interceptor (customizable)


    // Display text for confirm and cancel buttons
    private String yesStr, noStr;
    private int yesTextColor, noTextColor;

    private int textGravity = 0;

    private onNoOnclickListener noOnclickListener; // Cancel button click listener
    private onYesOnclickListener yesOnclickListener; // Confirm button click listener

    public static final int TITLE_VISIBLE = 0;
    public static final int TITLE_GONE = 1;

    public static final int MESSAGE_VISIBLE = 0;
    public static final int MESSAGE_GONE = 1;

    private boolean isEnableBackPressed = true; // Allow back-press to close dialog by default

    public static void showSimpleInfo(Context context, String title, String content) {
        CommonDialog commonDialog = new CommonDialog(context);
        commonDialog.setTitle(title);
        commonDialog.setMessage(content);
        commonDialog.setOnShowListener(dialog -> commonDialog.setNoBtnVisible(View.GONE));
        commonDialog.show();
    }

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
     * Set cancel button content and listener
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
     * Set confirm button content and listener
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
        // Cannot cancel dialog by tapping outside
        setCanceledOnTouchOutside(false);

        // Initialize UI controls
        initView();
        // Initialize UI data
        initData();
        // Initialize UI control events
        initEvent();
    }


    /**
     * Initialize UI controls
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
     * Initialize display data for UI controls
     */
    private void initData() {
        // If title and message are set by caller
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
        // If button text is set
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
     * Initialize confirm and cancel click listeners
     */
    private void initEvent() {
        // Set confirm button click listener callback
        yes.setOnClickListener(new QuecClickListener() {
            @Override
            public void onViewClick(View v) {
                if (yesOnclickListener != null) {
                    yesOnclickListener.onYesOnclick();
                } else {
                    dismiss();
                }
            }
        });
        // Set cancel button click listener callback
        no.setOnClickListener(new QuecClickListener() {
            @Override
            public void onViewClick(View v) {
                if (noOnclickListener != null) {
                    noOnclickListener.onNoClick();
                } else {
                    dismiss();
                }
            }
        });
    }

    /**
     * Set dialog title from the caller Activity
     *
     * @param title
     */
    public CommonDialog setTitle(String title) {
        titleStr = title;
        return this;
    }

    /**
     * Set dialog content gravity from the caller Activity
     * @param textGravity
     * @return
     */
    public CommonDialog setMessageGravity(int textGravity) {
        this.textGravity = textGravity;
        return this;
    }

    /**
     * Set dialog message from the caller Activity
     *
     * @param message
     */
    public CommonDialog setMessage(String message) {
        messageStr = message;
        return this;
    }

    /**
     * Set dialog message from the caller Activity
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
     * Set rich text and custom touch event
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
     * Set cancel button visibility
     *
     * @param visible Visibility type
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
     * Set confirm button visibility
     *
     * @param visible Visibility type
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
     * Set whether back-press can close the dialog
     * @param isEnabled true = allowed, false = not allowed
     * @return
     */
    public CommonDialog setEnableBackPressed(boolean isEnabled) {
        isEnableBackPressed = isEnabled;
        return this;
    }
}
