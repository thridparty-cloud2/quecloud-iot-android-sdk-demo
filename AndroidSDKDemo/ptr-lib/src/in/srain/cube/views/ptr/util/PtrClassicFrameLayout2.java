package in.srain.cube.views.ptr.util;

import android.content.Context;
import android.util.AttributeSet;

import in.srain.cube.views.ptr.PtrClassicDefaultHeader;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class PtrClassicFrameLayout2 extends PtrFrameLayout {

    private PtrClassicDefaultHeader mPtrClassicHeader;

    public PtrClassicFrameLayout2(Context context) {
        super(context);
        initViews();
    }

    public PtrClassicFrameLayout2(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public PtrClassicFrameLayout2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews();
    }

    private void initViews() {
        mPtrClassicHeader = new PtrClassicDefaultHeader(getContext());
        setHeaderView(mPtrClassicHeader);
        addPtrUIHandler(mPtrClassicHeader);
    }

    public PtrClassicDefaultHeader getHeader() {
        return mPtrClassicHeader;
    }

    /**
     * Specify the last update time by this key string
     *
     * @param key
     */
    public void setLastUpdateTimeKey(String key) {
        if (mPtrClassicHeader != null) {
            mPtrClassicHeader.setLastUpdateTimeKey(key);
        }
    }

    /**
     * Using an object to specify the last update time.
     *
     * @param object
     */
    public void setLastUpdateTimeRelateObject(Object object) {
        if (mPtrClassicHeader != null) {
            mPtrClassicHeader.setLastUpdateTimeRelateObject(object);
        }
    }
}
