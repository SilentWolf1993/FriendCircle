package com.yhy.friendcircle;

import android.app.Dialog;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.yhy.fridcir.adapter.CircleAdapter;
import com.yhy.fridcir.entity.CircleItem;
import com.yhy.fridcir.entity.Comment;
import com.yhy.fridcir.entity.FavorItem;
import com.yhy.fridcir.entity.User;
import com.yhy.fridcir.widget.CommentListView;
import com.yhy.fridcir.widget.FavorCommentPop;
import com.yhy.fridcir.widget.FavorView;
import com.yhy.fridcir.widget.InputDialogView;
import com.yhy.friendcircle.databinding.MainActivityBinding;
import com.yhy.friendcircle.global.ImgUrls;
import com.yhy.widget.rv.div.RvDivider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private User mCurUser;
    private MainActivityBinding mBinding;
    private List<CircleItem> mCircleList;
    private Random mRand;

    private CircleAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mCurUser = ((App) getApplication()).getUser();

        mBinding.rvCircle.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.RecycledViewPool rvPool = mBinding.rvCircle.getRecycledViewPool();
        if (null != rvPool) {
            rvPool.setMaxRecycledViews(0, 10);
            mBinding.rvCircle.setRecycledViewPool(rvPool);
        }

        mRand = new Random();

        initData();

        mAdapter = new CircleAdapter(this, mCircleList, mCurUser);
        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);

        mAdapter.setOnFavorListener(new CircleAdapter.OnFavorListener() {
            @Override
            public void onFavor(FavorView favorView, CircleItem item, FavorItem favorItem) {
                if (null == favorItem) {
                    //说明未赞过
                    //请求服务器添加点赞数据
                    favorItem = new FavorItem();
                    favorItem.id = UUID.randomUUID().toString();
                    favorItem.user = mCurUser;
                    favorView.addFavor(favorItem);
                } else {
                    //请求服务器删除点赞数据
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

        mBinding.rvCircle.setAdapter(mAdapter);
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

        InputDialogView.showInputView(this, hint, new InputDialogView.OnCommentDialogListener() {
            @Override
            public void onPublish(Dialog dialog, EditText etInput, TextView btn) {
                dialog.dismiss();

                Comment comment = new Comment();
                comment.id = UUID.randomUUID().toString();
                comment.content = etInput.getText().toString();
                comment.fromUser = mCurUser;
                comment.toUser = toUser;

                clvComment.addComment(comment);
            }

            @Override
            public void onShow(int[] ivPosition) {
                // 点击某条评论则这条评论刚好在输入框上面，点击评论按钮则输入框刚好挡住按钮
                Log.i(TAG, "commentBtn : " + coord[1]);
                Log.i(TAG, "inputArea : " + ivPosition[1]);

                int span = alignView.getHeight();
                int dy = coord[1] + span - ivPosition[1];
                mBinding.rvCircle.smoothScrollBy(0, dy, new AccelerateDecelerateInterpolator());
            }

            @Override
            public void onDismiss() {
            }
        });
    }

    private void initData() {
        mCircleList = new ArrayList<>();
        User user = null;
        CircleItem msg = null;
        ImageInfo img = null;
        FavorItem favor = null;
        String imgUrl = null;
        Comment comment = null;
        for (int i = 0; i < 30; i++) {
            user = new User();
            user.id = UUID.randomUUID().toString();
            user.name = "用户 " + i;
            user.avatar = ImgUrls.getAImgUrl();

            msg = new CircleItem();
            msg.id = UUID.randomUUID().toString();
            msg.fromUser = user;
            msg.content = "哈哈哈哈，内容测试！！哈哈哈哈，内容测试！电话号码：13529341623，网址：http://www.baicu.com，内容测试！！哈哈哈哈，内容测试！电话号码：13529341623，网址：http://www.baicu.com";
            msg.createTime = new Date(Calendar.getInstance().getTimeInMillis() - mRand.nextInt(1000) * 10000);

            if (null == msg.imgList) {
                msg.imgList = new ArrayList<>();
            }

            for (int j = 0; j < mRand.nextInt(10); j++) {
                img = new ImageInfo();
                imgUrl = ImgUrls.getAImgUrl();
                img.setThumbnailUrl(imgUrl);
                img.setBigImageUrl(imgUrl);
                msg.imgList.add(img);
            }

            if (null == msg.favorList) {
                msg.favorList = new ArrayList<>();
            }

            for (int j = 0; j < mRand.nextInt(10); j++) {
                user = new User();
                user.id = UUID.randomUUID().toString();
                user.name = "点赞" + j;
                user.avatar = ImgUrls.getAImgUrl();
                favor = new FavorItem();
                favor.id = UUID.randomUUID().toString();
                favor.user = user;
                msg.favorList.add(favor);
            }

            if (null == msg.commentList) {
                msg.commentList = new ArrayList<>();
            }

            for (int j = 0; j < mRand.nextInt(10); j++) {
                user = new User();
                user.id = UUID.randomUUID().toString();
                user.name = "评论" + j;
                user.avatar = ImgUrls.getAImgUrl();

                comment = new Comment();
                comment.id = UUID.randomUUID().toString();
                comment.content = "评论评论评论论评论评论";
                comment.fromUser = user;

                if (j % 2 != 0) {
                    user = new User();
                    user.id = UUID.randomUUID().toString();
                    user.name = "被回复" + j;
                    user.avatar = ImgUrls.getAImgUrl();
                    comment.toUser = user;
                }

                msg.commentList.add(comment);
            }

            mCircleList.add(msg);
        }
    }
}
