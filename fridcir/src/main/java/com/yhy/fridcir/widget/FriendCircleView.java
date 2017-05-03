package com.yhy.fridcir.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yhy.fridcir.R;
import com.yhy.fridcir.adapter.CircleAdapter;
import com.yhy.fridcir.entity.CircleItem;
import com.yhy.fridcir.entity.Comment;
import com.yhy.fridcir.entity.FavorItem;
import com.yhy.fridcir.entity.User;
import com.yhy.widget.rv.div.RvDivider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by HongYi Yan on 2017/5/3 9:31.
 */
public class FriendCircleView extends LinearLayout {
    private static final String TAG = "CircleView";
    private Context mCtx;
    private RecyclerView rvCircle;
    private User mUser;
    private CircleAdapter mAdapter;
    private List<CircleItem> mCircleList;

    private OnFavorListener mOnFavorListener;
    private OnCommentListener mOnCommentListener;

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

    public void setDataList(List<CircleItem> circleList, User user) {
        mCircleList.clear();
        mCircleList.addAll(circleList);

        mUser = user;

        if (null == mAdapter) {
            mAdapter = new CircleAdapter(mCtx, mCircleList, mUser);
            mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
            rvCircle.setAdapter(mAdapter);
            rvCircle.addItemDecoration(new RvDivider(mCtx));
            initListener();
        } else {
            mAdapter.setNewData(mCircleList);
        }
    }

    public void addData(CircleItem circle) {
        if (null == mAdapter) {
            mCircleList.add(circle);
            mAdapter = new CircleAdapter(mCtx, mCircleList, mUser);
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
            public void onFavor(FavorView favorView, CircleItem item, FavorItem favorItem) {
                if (null == favorItem) {
                    //说明未赞过
                    favorItem = new FavorItem();
                    favorItem.id = UUID.randomUUID().toString();
                    favorItem.user = mUser;
                    //回调
                    if (null != mOnFavorListener) {
                        mOnFavorListener.onFavor(item, favorItem);
                    }
                    favorView.addFavor(favorItem);
                } else {
                    //回调
                    if (null != mOnFavorListener) {
                        mOnFavorListener.onCancel(item, favorItem);
                    }
                    favorView.removeFavor(favorItem);
                }
            }
        });

        mAdapter.setOnCommentClickListener(new CircleAdapter.OnCommentClickListener() {
            @Override
            public void onCommentClick(CircleItem item, User toUser, CommentListView clvComment, View alignView) {
                //请求服务器添加评论
                commentAndReply(toUser, clvComment, alignView);
            }
        });
    }

    private void commentAndReply(final User toUser, final CommentListView clvComment, final View alignView) {
        final int[] coord = new int[2];
        if (null != alignView) {
            alignView.getLocationOnScreen(coord);
        }
        String hint = "说点什么吧...";
        if (null != toUser) {
            hint = "回复" + toUser.name + ":";
        }

        InputDialogView.showInputView(mCtx, hint, new InputDialogView.OnCommentDialogListener() {
            @Override
            public void onPublish(Dialog dialog, EditText etInput, TextView btn) {
                dialog.dismiss();

                Comment comment = new Comment();
                comment.id = UUID.randomUUID().toString();
                comment.content = etInput.getText().toString();
                comment.fromUser = mUser;
                comment.toUser = toUser;

                //回调
                if (null != mOnFavorListener) {
                    mOnCommentListener.onCommentClick(comment);
                }

                //添加评论到界面
                clvComment.addComment(comment);
            }

            @Override
            public void onShow(int[] ivPosition) {
                // 点击某条评论则这条评论刚好在输入框上面，点击评论按钮则输入框刚好挡住按钮
//                Log.i(TAG, "commentBtn : " + coord[1]);
//                Log.i(TAG, "inputArea : " + ivPosition[1]);

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

    public interface OnFavorListener {
        void onFavor(CircleItem item, FavorItem favorItem);

        void onCancel(CircleItem circleItem, FavorItem favorItem);
    }

    public interface OnCommentListener {
        void onCommentClick(Comment comment);
    }
}
