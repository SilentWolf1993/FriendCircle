package com.yhy.fridcir.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yhy.fridcir.R;
import com.yhy.fridcir.entity.FcComment;
import com.yhy.fridcir.entity.FcUser;
import com.yhy.fridcir.spannable.CircleMovementMethod;
import com.yhy.fridcir.spannable.SpannableClickable;
import com.yhy.fridcir.utils.SpanUrlUtils;

import java.util.ArrayList;
import java.util.List;

public class CommentListView extends LinearLayout {
    private int itemColor;
    private int itemSelectorColor;
    private OnUserClickListener mUserClickListener;
    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;
    private List<FcComment> mCommentList;
    private LayoutInflater layoutInflater;

    public void setOnUserClickListener(OnUserClickListener listener) {
        mUserClickListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener itemLongClickListener) {
        this.mItemLongClickListener = itemLongClickListener;
    }

    public void setCommentList(List<FcComment> datas) {
        mCommentList = datas;
        notifyDataSetChanged();
    }

    public void addComment(FcComment fcComment) {
        if (null == fcComment) {
            return;
        }
        if (null == mCommentList) {
            mCommentList = new ArrayList<>();
        }
        mCommentList.add(fcComment);
        notifyDataSetChanged();
    }

    public void removeComment(FcComment fcComment) {
        if (null == fcComment || null == mCommentList) {
            return;
        }
        mCommentList.remove(fcComment);
        notifyDataSetChanged();
    }

    public List<FcComment> getCommentList() {
        return mCommentList;
    }

    public CommentListView(Context context) {
        super(context);
    }

    public CommentListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
    }

    public CommentListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
    }

    protected void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.FavorViewAttrs, 0, 0);
        try {
            //textview的默认颜色
            itemColor = typedArray.getColor(R.styleable.FavorViewAttrs_item_color, getResources().getColor(R.color.praise_item_default));
            itemSelectorColor = typedArray.getColor(R.styleable.FavorViewAttrs_item_selector_color, getResources().getColor(R.color.praise_item_selector_default));

        } finally {
            typedArray.recycle();
        }
    }

    public void notifyDataSetChanged() {
        removeAllViews();
        if (mCommentList == null || mCommentList.size() == 0) {
            return;
        }
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < mCommentList.size(); i++) {
            final int index = i;
            View view = getView(index);
            if (view == null) {
                throw new NullPointerException("listview item layout is null, please check getView()...");
            }

            addView(view, index, layoutParams);
        }
    }

    private View getView(final int position) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(getContext());
        }
        View convertView = layoutInflater.inflate(R.layout.item_comment, null, false);

        TextView commentTv = (TextView) convertView.findViewById(R.id.commentTv);
        final CircleMovementMethod circleMovementMethod = new CircleMovementMethod(itemSelectorColor, itemSelectorColor);

        final FcComment fcComment = mCommentList.get(position);

        String toReplyName = "";
        if (null != fcComment.toFcUser) {
            toReplyName = fcComment.toFcUser.name;
        }

        SpannableStringBuilder builder = new SpannableStringBuilder();
        if (null != fcComment && null != fcComment.fromFcUser && !TextUtils.isEmpty(fcComment.fromFcUser.name)) {
            builder.append(setClickableSpan(fcComment.fromFcUser));
        }

        if (!TextUtils.isEmpty(toReplyName)) {
            builder.append(" 回复 ");
            builder.append(setClickableSpan(fcComment.toFcUser));
        }
        builder.append(": ");

        //转换表情字符
        String contentBodyStr = fcComment.content;
        builder.append(SpanUrlUtils.formatUrlString(contentBodyStr));
        commentTv.setText(builder);

        commentTv.setMovementMethod(circleMovementMethod);
        commentTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (circleMovementMethod.isPassToTv()) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(v, position);
                    }
                }
            }
        });
        commentTv.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (circleMovementMethod.isPassToTv()) {
                    if (mItemLongClickListener != null) {
                        mItemLongClickListener.onItemLongClick(v, position);
                    }
                    return true;
                }
                return false;
            }
        });

        return convertView;
    }

    @NonNull
    private SpannableString setClickableSpan(final FcUser fcUser) {
        SpannableString subjectSpanText = new SpannableString(fcUser.name);
        subjectSpanText.setSpan(new SpannableClickable(itemColor) {
                                    @Override
                                    public void onClick(View widget) {
                                        if (null != mUserClickListener) {
                                            mUserClickListener.onUserClick(widget, fcUser);
                                        }
                                    }
                                }, 0, subjectSpanText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return subjectSpanText;
    }

    public interface OnUserClickListener {
        void onUserClick(View v, FcUser fcUser);
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View v, int position);
    }
}
