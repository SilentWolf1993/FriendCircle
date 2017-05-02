package com.yhy.fridcir.widget;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yhy.fridcir.R;
import com.yhy.widget.utils.DensityUtils;

/**
 * 朋友圈点赞评论的popupwindow
 */
public class FavorCommentPop extends PopupWindow {
    private Context mCtx;
    private final LinearLayout llFavor;
    private final LinearLayout llComment;
    private TextView tvFavor;
    private TextView tvComment;

    // 实例化一个矩形
    private Rect mRect = new Rect();
    // 坐标的位置（x、y）
    private final int[] mLocation = new int[2];
    // 弹窗子类项选中时的监听
    private OnItemClickListener mItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public FavorCommentPop(Context context) {
        mCtx = context;

        View view = LayoutInflater.from(mCtx).inflate(R.layout.widget_favor_comment_pop, null);

        llFavor = (LinearLayout) view.findViewById(R.id.ll_favor);
        llComment = (LinearLayout) view.findViewById(R.id.ll_comment);
        tvFavor = (TextView) view.findViewById(R.id.tv_favor);
        tvComment = (TextView) view.findViewById(R.id.tv_comment);

        llFavor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (null != mItemClickListener) {
                    mItemClickListener.onFavorClick(v);
                    update();
                }
            }
        });

        llComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (null != mItemClickListener) {
                    mItemClickListener.onCommentClick(v);
                }
            }
        });

        this.setContentView(view);
        this.setWidth(DensityUtils.dp2px(mCtx, 200));
        this.setHeight(DensityUtils.dp2px(mCtx, 36));
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        this.setAnimationStyle(R.style.social_pop_anim);
    }

    public void showPopupWindow(View parent, boolean isFavored) {
        parent.getLocationOnScreen(mLocation);
        // 设置矩形的大小
        mRect.set(mLocation[0], mLocation[1], mLocation[0] + parent.getWidth(), mLocation[1] + parent.getHeight());

        if (!isFavored) {
            //未赞
            tvFavor.setText("赞");
        } else {
            //已赞
            tvFavor.setText("取消");
        }

        if (!isShowing()) {
            showAtLocation(parent, Gravity.NO_GRAVITY, mLocation[0] - this.getWidth(), mLocation[1] - ((this.getHeight() - parent.getHeight()) / 2));
        } else {
            dismiss();
        }
    }

    /**
     * 功能描述：弹窗子类项按钮监听事件
     */
    public interface OnItemClickListener {
        void onFavorClick(View v);

        void onCommentClick(View v);
    }
}
