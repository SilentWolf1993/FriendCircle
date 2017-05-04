package com.yhy.fridcir.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yhy.fridcir.R;
import com.yhy.fridcir.adapter.CircleAdapter;
import com.yhy.fridcir.entity.FcCircle;
import com.yhy.fridcir.entity.FcComment;
import com.yhy.fridcir.entity.FcFavor;
import com.yhy.fridcir.entity.FcUser;
import com.yhy.widget.rv.div.RvDivider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HongYi Yan on 2017/5/3 9:31.
 */
public class FriendCircleView extends LinearLayout {
    private static final String TAG = "CircleView";
    private Context mCtx;
    private RecyclerView rvCircle;
    private FcUser mFcUser;
    private CircleAdapter mAdapter;
    private List<FcCircle> mCircleList;

    private OnFavorListener mOnFavorListener;
    private OnCommentListener mOnCommentListener;
    private boolean mClickUserComment;
    private OnCommentItemLongClickListener mOnCommentItemLongClickListener;
    private OnUserClickListener mOnUserClickListener;

    public FriendCircleView(Context context) {
        this(context, null);
    }

    public FriendCircleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FriendCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCtx = context;
        mCircleList = new ArrayList<>();

        init(attrs);
    }

    private void init(AttributeSet attrs) {
        initView();

        initAttrs(attrs);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray ta = mCtx.obtainStyledAttributes(attrs, R.styleable.FriendCircleViewAttrs);
        mClickUserComment = ta.getBoolean(R.styleable.FriendCircleViewAttrs_click_user_comment, true);
        ta.recycle();
    }

    private void initView() {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.widget_circle_view, this);
        rvCircle = (RecyclerView) view.findViewById(R.id.rv_circle);
        rvCircle.setLayoutManager(new LinearLayoutManager(mCtx));
        RecyclerView.RecycledViewPool rvPool = rvCircle.getRecycledViewPool();
        if (null != rvPool) {
            rvPool.setMaxRecycledViews(0, 10);
            rvCircle.setRecycledViewPool(rvPool);
        }
    }

    public void setAdapter(CircleAdapter adapter) {
        mAdapter = adapter;
        rvCircle.setAdapter(mAdapter);
        rvCircle.addItemDecoration(new RvDivider(mCtx));
        initListener();
    }

    public void setDataList(List<FcCircle> circleList, FcUser fcUser) {
        mCircleList.clear();
        mCircleList.addAll(circleList);

        mFcUser = fcUser;

        if (null == mAdapter) {
            mAdapter = new CircleAdapter(mCtx, mCircleList, mFcUser, mClickUserComment);
            mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
            rvCircle.setAdapter(mAdapter);
            rvCircle.addItemDecoration(new RvDivider(mCtx));
            initListener();
        } else {
            mAdapter.setNewData(mCircleList);
        }
    }

    public void addData(FcCircle circle) {
        if (null == mAdapter) {
            mCircleList.add(circle);
            mAdapter = new CircleAdapter(mCtx, mCircleList, mFcUser, mClickUserComment);
            mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
            rvCircle.setAdapter(mAdapter);
            rvCircle.addItemDecoration(new RvDivider(mCtx));
            initListener();
        } else {
            mAdapter.addData(circle);
        }
    }

    private void initListener() {
        if (null == mAdapter) {
            return;
        }

        mAdapter.setOnFavorListener(new CircleAdapter.OnFavorListener() {
            @Override
            public void onFavor(FcCircle fcCircle, FcFavor fcFavor, FavorView favorView, int itemPosition) {
                if (null == fcFavor) {
                    //说明未赞过，添加赞
                    //回调
                    if (null != mOnFavorListener) {
                        mOnFavorListener.onFavor(favorView, fcCircle, itemPosition);
                    }
                } else {
                    //已经赞过，需要取消赞
                    //回调
                    if (null != mOnFavorListener) {
                        mOnFavorListener.onCancel(favorView, fcCircle, fcFavor, itemPosition);
                    }
                }
            }
        });

        mAdapter.setOnCommentListener(new CircleAdapter.OnCommentListener() {
            @Override
            public void onComment(FcCircle fcCircle, FcUser toFcUser, View alignView, CommentListView clvComment, int itemPosition) {
                //请求服务器添加评论
                commentAndReply(fcCircle, toFcUser, clvComment, alignView, itemPosition);
            }
        });

        mAdapter.setOnCommentItemLongClickListener(new CircleAdapter.OnCommentItemLongClickListener() {
            @Override
            public void onItemLongClick(FcCircle fcCircle, FcComment fcComment, CommentListView clvComment, int itemPosition) {
                if (null != mOnCommentItemLongClickListener) {
                    mOnCommentItemLongClickListener.onItemLongClick(fcCircle, fcComment, clvComment, itemPosition);
                }
            }
        });

        mAdapter.setOnUserClickListener(new CircleAdapter.OnUserClickListener() {
            @Override
            public void onUserClcik(View v, FcCircle fcCircle, FcUser fcUser) {
                if (null != mOnUserClickListener) {
                    mOnUserClickListener.onUserClcik(v, fcCircle, fcUser);
                }
            }
        });
    }

    public void addFavor(FavorView favorView, FcFavor fcFavor, int itemPosition) {
        if (null != favorView && null != fcFavor) {
            favorView.addFavor(fcFavor);
            refreshUi(itemPosition);
        }
    }

    public void removeFavor(FavorView favorView, FcFavor fcFavor, int itemPosition) {
        if (null != favorView && null != fcFavor) {
            favorView.removeFavor(fcFavor);
            refreshUi(itemPosition);
        }
    }

    public void addComment(CommentListView clvComment, FcComment fcComment, int itemPosition) {
        if (null != clvComment && null != fcComment) {
            clvComment.addComment(fcComment);
            refreshUi(itemPosition);
        }
    }

    public void removeComment(CommentListView clvComment, FcComment fcComment, int itemPosition) {
        if (null != clvComment && null != fcComment) {
            clvComment.removeComment(fcComment);
            refreshUi(itemPosition);
        }
    }

    private void refreshUi(int itemPosition) {
        mAdapter.notifyItemChanged(itemPosition);
    }

    private void commentAndReply(final FcCircle fcCircle, final FcUser toFcUser, final CommentListView clvComment, final View alignView, final int itemPosition) {
        final int[] coord = new int[2];
        if (null != alignView) {
            alignView.getLocationOnScreen(coord);
        }
        String hint = "说点什么吧...";
        if (null != toFcUser) {
            hint = "回复" + toFcUser.name + ":";
        }

        InputDialogView.showInputView(mCtx, hint, new InputDialogView.OnCommentDialogListener() {
            @Override
            public void onPublish(Dialog dialog, EditText etInput, TextView btn) {
                dialog.dismiss();

                FcComment fcComment = new FcComment();
//                fcComment.id = UUID.randomUUID().toString();
                fcComment.content = etInput.getText().toString();
                fcComment.fromFcUser = mFcUser;
                fcComment.toFcUser = toFcUser;

                //回调
                if (null != mOnFavorListener) {
                    mOnCommentListener.onComment(fcCircle, fcComment, clvComment, itemPosition);
                }
            }

            @Override
            public void onShow(int[] ivPosition) {
                // 点击某条评论则这条评论刚好在输入框上面，点击评论按钮则输入框刚好挡住按钮
                int span = alignView.getHeight();
                int dy = coord[1] + span - ivPosition[1];
                rvCircle.smoothScrollBy(0, dy, new AccelerateDecelerateInterpolator());
            }

            @Override
            public void onDismiss() {
            }
        });
    }

    public void setOnFavorListener(OnFavorListener listener) {
        mOnFavorListener = listener;
    }

    public void setOnCommentListener(OnCommentListener listener) {
        mOnCommentListener = listener;
    }

    public void setOnCommentItemLongClickListener(OnCommentItemLongClickListener listener) {
        mOnCommentItemLongClickListener = listener;
    }

    public void setOnUserClickListener(OnUserClickListener listener) {
        mOnUserClickListener = listener;
    }

    public interface OnFavorListener {
        void onFavor(FavorView favorView, FcCircle fcCircle, int itemPosition);

        void onCancel(FavorView favorView, FcCircle fcCircle, FcFavor fcFavor, int itemPosition);
    }

    public interface OnCommentListener {
        void onComment(FcCircle fcCircle, FcComment fcComment, CommentListView clvComment, int itemPosition);
    }

    public interface OnCommentItemLongClickListener {
        void onItemLongClick(FcCircle fcCircle, FcComment fcComment, CommentListView clvComment, int itemPosition);
    }

    public interface OnUserClickListener {
        void onUserClcik(View v, FcCircle fcCircle, FcUser fcUser);
    }
}
