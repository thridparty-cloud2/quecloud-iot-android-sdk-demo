package com.quectel.app.demo.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.quectel.app.demo.R;
import com.quectel.app.demo.bean.LanVO;
import java.util.List;

public class LanAdapter extends BaseQuickAdapter<LanVO, BaseViewHolder> {

    private Context mContext;
    int picWidth  = 0;
    int picHeight  = 0;
    LayoutInflater inflater  = null;
    public LanAdapter(Context context, List data) {
        super(R.layout.lan_item, data);
        this.mContext  = context;
    }
    @Override
    protected void convert(BaseViewHolder helper, final LanVO item) {
        helper.setText(R.id.tv_text, item.getVal());


    }

}
