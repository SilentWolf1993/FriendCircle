package com.yhy.fridcir.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yhy.fridcir.R;

/**
 * Created by HongYi Yan on 2017/5/2 15:00.
 */
public class InputDialogView {
    private static Handler handler = new Handler();
    private static Runnable runnable;

    public static void showInputView(Context ctx, String hint, final OnCommentDialogListener listener) {
        final Dialog mDialog = new Dialog(ctx, android.R.style.Theme_Translucent_NoTitleBar);
        mDialog.setContentView(R.layout.widget_input_comment);

        final EditText etInput = (EditText) mDialog.findViewById(R.id.et_input);
        final TextView tvPublish = (TextView) mDialog.findViewById(R.id.tv_publish);

        etInput.setHint(hint);

//        InputUtils.showSoftInput(ctx, etInput);

        mDialog.setCancelable(true);

        mDialog.findViewById(R.id.ll_input_area_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                //取消滚动
                cancelScroll();

                if (listener != null) {
                    listener.onDismiss();
                }
            }
        });

        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvPublish.setEnabled(!TextUtils.isEmpty(s));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        tvPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onPublish(mDialog, etInput, tvPublish);
                }
            }
        });
        mDialog.show();

        runnable = new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    int[] coord = new int[2];
                    mDialog.findViewById(R.id.ll_input_area).getLocationOnScreen(coord);
                    // 传入 输入框距离屏幕顶部（不包括状态栏）的长度
                    listener.onShow(coord);
                }
            }
        };

        handler.postDelayed(runnable, 600);
    }

    public static void cancelScroll() {
        if (null != runnable) {
            handler.removeCallbacks(runnable);
        }
    }

    public interface OnCommentDialogListener {
        void onPublish(Dialog dialog, EditText etInput, TextView btn);

        void onShow(int[] ivPosition);

        void onDismiss();
    }
}
