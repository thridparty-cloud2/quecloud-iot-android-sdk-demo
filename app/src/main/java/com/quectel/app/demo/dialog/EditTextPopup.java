package com.quectel.app.demo.dialog;

import android.content.Context;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.quectel.app.demo.R;
import com.quectel.app.demo.databinding.QuecBasicUiEditTextPopBinding;
import com.quectel.basic.common.interfaces.QuecClickListener;

import razerdp.basepopup.BasePopupWindow;

public class EditTextPopup extends BasePopupWindow {

    private QuecBasicUiEditTextPopBinding mBinding;
    private OnEditTextListener mListener;

    public EditTextPopup(Context context) {
        super(context);
        setContentView(R.layout.quec_basic_ui_edit_text_pop);
        setPopupGravity(Gravity.CENTER);
    }


    @Override
    public void onViewCreated(@NonNull View contentView) {
        super.onViewCreated(contentView);
        mBinding = QuecBasicUiEditTextPopBinding.bind(contentView);
        initListener();
    }

    /**
     * 初始化监听
     */
    private void initListener() {
        mBinding.tvSure.setOnClickListener(new QuecClickListener() {
            @Override
            public void onViewClick(@Nullable View view) {
                if (mListener != null) {
                    mListener.changeText(mBinding.editContent.getText().toString());
                }
            }
        });

        mBinding.tvCancel.setOnClickListener(new QuecClickListener() {
            @Override
            public void onViewClick(View v) {
                dismiss();
            }
        });
    }

    public void setEditTextListener(OnEditTextListener l) {
        this.mListener = l;
    }

    public EditTextPopup setContent(String content) {
        mBinding.editContent.setText(content);
        return this;
    }


    public EditTextPopup setTitle(String title) {
        mBinding.tvTitle.setText(title);
        return this;
    }

    public EditTextPopup setHint(String hint) {
        mBinding.editContent.setHint(hint);
        return this;
    }


    public EditTextPopup setCancel(String cancel) {
        mBinding.tvCancel.setText(cancel);
        return this;
    }

    public EditTextPopup setSure(String sure) {
        mBinding.tvSure.setText(sure);
        return this;
    }

    public EditTextPopup setMaxLength(int length){
        mBinding.editContent.setFilters( new InputFilter[]{ new InputFilter.LengthFilter( length )});;
        return this;
    }

    /**
     * 编辑框监听
     */
    public interface OnEditTextListener {
        /**
         * 输入值变化
         *
         * @param content 输入值
         */
        void changeText(String content);

    }

}
