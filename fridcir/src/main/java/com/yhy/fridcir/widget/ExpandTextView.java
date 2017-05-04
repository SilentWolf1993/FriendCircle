package com.yhy.fridcir.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yhy.fridcir.R;
import com.yhy.fridcir.spannable.CircleMovementMethod;

public class ExpandTextView extends LinearLayout {
    public static final int DEFAULT_MAX_LINES = 3;
    private TextView tvContent;
    private TextView tvPlus;

    private int showLines;

    private ExpandStatusListener expandStatusListener;
    private boolean isExpand;

    public ExpandTextView(Context context) {
        super(context);
        initView();
    }

    public ExpandTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initView();
    }

    public ExpandTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        initView();
    }

    private void initView() {
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.widget_expand_textview, this);
        tvContent = (TextView) findViewById(R.id.tv_content);
        tvPlus = (TextView) findViewById(R.id.tv_plus);

        if (showLines > 0) {
            tvContent.setMaxLines(showLines);
        }

        tvPlus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String textStr = tvPlus.getText().toString().trim();
                if ("全文".equals(textStr)) {
                    tvContent.setMaxLines(Integer.MAX_VALUE);
                    tvPlus.setText("收起");
                    setExpand(true);
                } else {
                    tvContent.setMaxLines(showLines);
                    tvPlus.setText("全文");
                    setExpand(false);
                }
                //通知外部状态已变更
                if (expandStatusListener != null) {
                    expandStatusListener.statusChange(isExpand());
                }
            }
        });
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.ExpandTextViewAttrs, 0, 0);
        try {
            showLines = typedArray.getInt(R.styleable.ExpandTextViewAttrs_show_lines, DEFAULT_MAX_LINES);
        } finally {
            typedArray.recycle();
        }
    }

    public void setText(final CharSequence content) {
        tvContent.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                // 避免重复监听
                tvContent.getViewTreeObserver().removeOnPreDrawListener(this);

                int linCount = tvContent.getLineCount();
                if (linCount > showLines) {

                    if (isExpand) {
                        tvContent.setMaxLines(Integer.MAX_VALUE);
                        tvPlus.setText("收起");
                    } else {
                        tvContent.setMaxLines(showLines);
                        tvPlus.setText("全文");
                    }
                    tvPlus.setVisibility(View.VISIBLE);
                } else {
                    tvPlus.setVisibility(View.GONE);
                }

                //Log.d("onPreDraw", "onPreDraw...");
                //Log.d("onPreDraw", linCount + "");
                return true;
            }


        });
        tvContent.setText(content);
        tvContent.setMovementMethod(new CircleMovementMethod(getResources().getColor(R.color.name_selector_color)));
    }

    public void setExpand(boolean isExpand) {
        this.isExpand = isExpand;
    }

    public boolean isExpand() {
        return this.isExpand;
    }

    public void setExpandStatusListener(ExpandStatusListener listener) {
        this.expandStatusListener = listener;
    }

    public interface ExpandStatusListener {

        void statusChange(boolean isExpand);
    }
}
