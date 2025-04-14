package com.quectel.app.demo.dialog;

import android.content.Context;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.quectel.app.demo.R;
import com.quectel.app.demo.databinding.QuecBasicUiEditDoubleTextPopBinding;
import com.quectel.basic.common.interfaces.QuecClickListener;

import razerdp.basepopup.BasePopupWindow;

public class EditDoubleTextPopup extends BasePopupWindow {

    private QuecBasicUiEditDoubleTextPopBinding mBinding;
    private OnEditTextListener mListener;

    public EditDoubleTextPopup(Context context) {
        super(context);
        setContentView(R.layout.quec_basic_ui_edit_double_text_pop);
        setPopupGravity(Gravity.CENTER);
    }


    @Override
    public void onViewCreated(@NonNull View contentView) {
        super.onViewCreated(contentView);
        mBinding = QuecBasicUiEditDoubleTextPopBinding.bind(contentView);
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
                    mListener.changeText(mBinding.editContent.getText().toString(), mBinding.editContent2.getText().toString());
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

    public EditDoubleTextPopup setContent1(String content) {
        mBinding.editContent.setText(content);
        return this;
    }

    public EditDoubleTextPopup setContent2(String content) {
        mBinding.editContent2.setText(content);
        return this;
    }


    public EditDoubleTextPopup setTitle(String title) {
        mBinding.tvTitle.setText(title);
        return this;
    }

    public EditDoubleTextPopup setHint1(String hint) {
        mBinding.editContent.setHint(hint);
        return this;
    }

    public EditDoubleTextPopup setHint2(String hint) {
        mBinding.editContent2.setHint(hint);
        return this;
    }

    public EditDoubleTextPopup setCancel(String cancel) {
        mBinding.tvCancel.setText(cancel);
        return this;
    }

    public EditDoubleTextPopup setSure(String sure) {
        mBinding.tvSure.setText(sure);
        return this;
    }

    public EditDoubleTextPopup setMaxLength(int length) {
        mBinding.editContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
        ;
        return this;
    }

    /**
     * 编辑框监听
     */
    public interface OnEditTextListener {
        /**
         * 输入值变化
         *
         * @param content1 输入值1
         * @param content2 输入值2
         */
        void changeText(String content1, String content2);

    }

}
