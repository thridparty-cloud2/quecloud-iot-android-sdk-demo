package com.quectel.app.demo.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.quectel.app.demo.R;
import com.quectel.app.demo.bean.EditListBean;
import com.quectel.app.demo.databinding.QuecBasicUiEditSceneTextPopBinding;
import com.quectel.basic.common.interfaces.QuecClickListener;
import com.quectel.app.demo.adapter.EditListAdapter;

import java.util.ArrayList;
import java.util.List;

import razerdp.basepopup.BasePopupWindow;

public class EditListTextPopup extends BasePopupWindow {

    private QuecBasicUiEditSceneTextPopBinding mBinding;
    private OnEditTextListener mListener;
    private EditListAdapter editListAdapter;

    public EditListTextPopup(Context context) {
        super(context);
        setContentView(R.layout.quec_basic_ui_edit_scene_text_pop);
        setPopupGravity(Gravity.CENTER);
    }


    @Override
    public void onViewCreated(@NonNull View contentView) {
        super.onViewCreated(contentView);
        mBinding = QuecBasicUiEditSceneTextPopBinding.bind(contentView);
        initView();
        initListener();
    }

    private void initView() {
        editListAdapter = new EditListAdapter(getContext(), null);
        mBinding.rvList.setAdapter(editListAdapter);
        mBinding.rvList.setLayoutManager(new GridLayoutManager(getContext(), 2));
    }

    /**
     * 初始化监听
     */
    private void initListener() {
        mBinding.tvSure.setOnClickListener(new QuecClickListener() {
            @Override
            public void onViewClick(@Nullable View view) {
                if (mListener != null) {
                    List<EditListBean> allContent = editListAdapter.getAllContent();
                    mListener.changeText(allContent);
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

    public EditListTextPopup setDataList(ArrayList<EditListBean> dataList) {
        editListAdapter.setNewInstance(dataList);
        return this;
    }

    public EditListTextPopup setCancel(String cancel) {
        mBinding.tvCancel.setText(cancel);
        return this;
    }

    public EditListTextPopup setSure(String sure) {
        mBinding.tvSure.setText(sure);
        return this;
    }

    /**
     * 编辑框监听
     */
    public interface OnEditTextListener {
        /**
         * 输入值结果
         */
        void changeText(List<EditListBean> dataList);
    }

}
