package com.quectel.app.demo.dialog;

import android.content.Context;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.quectel.app.demo.R;
import com.quectel.app.demo.databinding.QuecBasicUiEditDoubleTextPopBinding;
import com.quectel.app.demo.databinding.QuecBasicUiEditSceneTextPopBinding;
import com.quectel.basic.common.interfaces.QuecClickListener;

import razerdp.basepopup.BasePopupWindow;

public class EditSceneTextPopup extends BasePopupWindow {

    private QuecBasicUiEditSceneTextPopBinding mBinding;
    private OnEditTextListener mListener;

    public EditSceneTextPopup(Context context) {
        super(context);
        setContentView(R.layout.quec_basic_ui_edit_scene_text_pop);
        setPopupGravity(Gravity.CENTER);
    }


    @Override
    public void onViewCreated(@NonNull View contentView) {
        super.onViewCreated(contentView);
        mBinding = QuecBasicUiEditSceneTextPopBinding.bind(contentView);
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
                    mListener.changeText(mBinding.editContent1.getText().toString(), mBinding.editContent2.getText().toString(),
                            mBinding.editContent3.getText().toString(), mBinding.editContent4.getText().toString(),
                            mBinding.editContent5.getText().toString(), mBinding.editContent6.getText().toString(),
                            mBinding.editContent7.getText().toString(), mBinding.editContent8.getText().toString(),
                            mBinding.editContent9.getText().toString(), mBinding.editContent10.getText().toString(),
                            mBinding.editContent11.getText().toString(), mBinding.editContent12.getText().toString(),
                            mBinding.editContent13.getText().toString()
                    );
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

    public EditSceneTextPopup setContent1(String content) {
        mBinding.editContent1.setText(content);
        return this;
    }

    public EditSceneTextPopup setContent2(String content) {
        mBinding.editContent2.setText(content);
        return this;
    }

    public EditSceneTextPopup setContent3(String content) {
        mBinding.editContent3.setText(content);
        return this;
    }

    public EditSceneTextPopup setContent4(String content) {
        mBinding.editContent4.setText(content);
        return this;
    }

    public EditSceneTextPopup setContent5(String content) {
        mBinding.editContent5.setText(content);
        return this;
    }

    public EditSceneTextPopup setContent6(String content) {
        mBinding.editContent6.setText(content);
        return this;
    }

    public EditSceneTextPopup setContent7(String content) {
        mBinding.editContent7.setText(content);
        return this;
    }

    public EditSceneTextPopup setContent8(String content) {
        mBinding.editContent8.setText(content);
        return this;
    }

    public EditSceneTextPopup setContent9(String content) {
        mBinding.editContent9.setText(content);
        return this;
    }

    public EditSceneTextPopup setContent10(String content) {
        mBinding.editContent10.setText(content);
        return this;
    }

    public EditSceneTextPopup setContent11(String content) {
        mBinding.editContent11.setText(content);
        return this;
    }

    public EditSceneTextPopup setContent12(String content) {
        mBinding.editContent12.setText(content);
        return this;
    }

    public EditSceneTextPopup setContent13(String content) {
        mBinding.editContent13.setText(content);
        return this;
    }


    public EditSceneTextPopup setTitle(String title) {
        mBinding.tvTitle.setText(title);
        return this;
    }

    public EditSceneTextPopup setHint1(String hint) {
        mBinding.editContent1.setHint(hint);
        return this;
    }

    public EditSceneTextPopup setHint2(String hint) {
        mBinding.editContent2.setHint(hint);
        return this;
    }

    public EditSceneTextPopup setHint3(String hint) {
        mBinding.editContent3.setHint(hint);
        return this;
    }

    public EditSceneTextPopup setHint4(String hint) {
        mBinding.editContent4.setHint(hint);
        return this;
    }

    public EditSceneTextPopup setHint5(String hint) {
        mBinding.editContent5.setHint(hint);
        return this;
    }

    public EditSceneTextPopup setHint6(String hint) {
        mBinding.editContent6.setHint(hint);
        return this;
    }

    public EditSceneTextPopup setHint7(String hint) {
        mBinding.editContent7.setHint(hint);
        return this;
    }

    public EditSceneTextPopup setHint8(String hint) {
        mBinding.editContent8.setHint(hint);
        return this;
    }

    public EditSceneTextPopup setHint9(String hint) {
        mBinding.editContent9.setHint(hint);
        return this;
    }

    public EditSceneTextPopup setHint10(String hint) {
        mBinding.editContent10.setHint(hint);
        return this;
    }

    public EditSceneTextPopup setHint11(String hint) {
        mBinding.editContent11.setHint(hint);
        return this;
    }

    public EditSceneTextPopup setHint12(String hint) {
        mBinding.editContent12.setHint(hint);
        return this;
    }

    public EditSceneTextPopup setHint13(String hint) {
        mBinding.editContent13.setHint(hint);
        return this;
    }


    public EditSceneTextPopup setCancel(String cancel) {
        mBinding.tvCancel.setText(cancel);
        return this;
    }

    public EditSceneTextPopup setSure(String sure) {
        mBinding.tvSure.setText(sure);
        return this;
    }

    public EditSceneTextPopup setMaxLength(int length) {
        mBinding.editContent1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
        return this;
    }

    /**
     * 编辑框监听
     */
    public interface OnEditTextListener {
        /**
         * 输入值变化
         * @param sceneInfoModelName 场景名称
         * @param sceneInfoModelIcon 场景图标
         * @param actionModelCode 物模型标志符
         * @param actionModelDataType 物模型数据类型
         * @param actionModelId 物模型功能ID
         * @param actionModelName 物模型功能名称
         * @param actionModelSubName 物模型值subName
         * @param actionModelSubType 物模型subType
         * @param actionModelType 物模型功能类型
         * @param actionModelValue 物模型值
         * @param metaDataModelProductKey 设备pk
         * @param metaDataModelDeviceKey 设备dk
         * @param metaDataModelDeviceType 物模型数据类型
         */
        void changeText(String sceneInfoModelName, String sceneInfoModelIcon, String actionModelCode, String actionModelDataType,
                        String actionModelId, String actionModelName, String actionModelSubName, String actionModelSubType,
                        String actionModelType, String actionModelValue, String metaDataModelProductKey, String metaDataModelDeviceKey, String metaDataModelDeviceType);

    }

}
