package com.quectel.app.demo.ui;

import android.Manifest;

import com.quectel.app.demo.R;
import com.quectel.app.demo.base.BaseActivity;
import com.quectel.app.demo.fragment.MainFragment;
import com.quectel.app.demo.utils.MyUtils;
import com.quectel.app.quecnetwork.logservice.ILogService;
import com.quectel.app.quecnetwork.utils.LogService;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

public class HomeActivity  extends BaseActivity {

    @Override
    protected int getContentLayout() {
        return R.layout.activity_home;
    }

    @Override
    protected void addHeadColor() {
        MyUtils.addStatusBarView(this,R.color.gray_bg);
    }

    @Override
    protected void initData() {

        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .subscribe(new Consumer<Boolean>() {
                               @Override
                               public void accept(Boolean grant) throws Exception {
                               }
                           }

                );


        if (findFragment(MainFragment.class) == null) {
            loadRootFragment(R.id.fl_container, MainFragment.newInstance());
        }

    }

    @Override
    public void onBackPressedSupport() {
        super.onBackPressedSupport();
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultHorizontalAnimator();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogService.get(ILogService.class).closeLog();

    }





}
