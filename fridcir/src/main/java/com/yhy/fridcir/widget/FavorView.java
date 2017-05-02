package com.yhy.fridcir.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.view.View;

import com.yhy.fridcir.R;
import com.yhy.fridcir.entity.FavorItem;
import com.yhy.fridcir.helper.FcHepler;
import com.yhy.fridcir.spannable.CircleMovementMethod;
import com.yhy.fridcir.spannable.ImgSpan;
import com.yhy.fridcir.spannable.SpannableClickable;

import java.util.List;

public class FavorView extends AppCompatTextView {
    private int itemColor;
    private int itemSelectorColor;
    private List<FavorItem> mFavorList;
    private OnItemClickListener onItemClickListener;

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public FavorView(Context context) {
        super(context);
    }

    public FavorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
    }

    public FavorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.FavorViewAttrs, 0, 0);
        try {
            //textview的默认颜色
            itemColor = typedArray.getColor(R.styleable.FavorViewAttrs_item_color, getResources().getColor(R.color.praise_item_default));
            itemSelectorColor = typedArray.getColor(R.styleable.FavorViewAttrs_item_selector_color, getResources().getColor(R.color.praise_item_selector_default));
        } finally {
            typedArray.recycle();
        }
    }

    public List<FavorItem> getFavorList() {
        return mFavorList;
    }

    public void setFavorList(List<FavorItem> favorList) {
        this.mFavorList = favorList;
        notifyDataSetChanged();
    }

    public void addFavor(FavorItem favor) {
        mFavorList.add(favor);
        notifyDataSetChanged();
    }

    public void removeFavor(FavorItem favor) {
        mFavorList.remove(favor);
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        if (mFavorList != null && mFavorList.size() > 0) {
            //添加点赞图标
            builder.append(setImageSpan());
            FavorItem item = null;
            for (int i = 0; i < mFavorList.size(); i++) {
                item = mFavorList.get(i);
                if (item != null) {
                    builder.append(setClickableSpan(item.user.name, i));
                    if (i != mFavorList.size() - 1) {
                        builder.append("、");
                    }
                }
            }
        }

        setText(builder);
        setMovementMethod(new CircleMovementMethod(itemSelectorColor));
    }

    private SpannableString setImageSpan() {
        String text = "  ";
        SpannableString imgSpanText = new SpannableString(text);
        imgSpanText.setSpan(new ImgSpan(FcHepler.getInstance().getApplication(), R.mipmap.ic_favor_list),
                0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return imgSpanText;
    }

    @NonNull
    private SpannableString setClickableSpan(String textStr, final int position) {
        SpannableString subjectSpanText = new SpannableString(textStr);
        subjectSpanText.setSpan(new SpannableClickable(itemColor) {
                                    @Override
                                    public void onClick(View widget) {
                                        if (onItemClickListener != null) {
                                            onItemClickListener.onClick(position);
                                        }
                                    }
                                }, 0, subjectSpanText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return subjectSpanText;
    }


    public interface OnItemClickListener {
        void onClick(int position);
    }
}
