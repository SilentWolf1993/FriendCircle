package com.yhy.friendcircle;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lzy.ninegrid.ImageInfo;
import com.yhy.fridcir.entity.CircleItem;
import com.yhy.fridcir.entity.Comment;
import com.yhy.fridcir.entity.FavorItem;
import com.yhy.fridcir.entity.User;
import com.yhy.fridcir.widget.FriendCircleView;
import com.yhy.friendcircle.databinding.MainActivityBinding;
import com.yhy.friendcircle.global.ImgUrls;
import com.yhy.utils.core.ToastUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private User mCurUser;
    private MainActivityBinding mBinding;
    private List<CircleItem> mCircleList;
    private Random mRand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mCurUser = ((App) getApplication()).getUser();

        mRand = new Random();

        initData();

        mBinding.cvContent.setDataList(mCircleList, mCurUser);

        mBinding.cvContent.setOnFavorListener(new FriendCircleView.OnFavorListener() {
            @Override
            public void onFavor(CircleItem item, FavorItem favorItem) {
                ToastUtils.shortToast("赞");
            }

            @Override
            public void onCancel(CircleItem circleItem, FavorItem favorItem) {
                ToastUtils.shortToast("取消赞");
            }
        });

        mBinding.cvContent.setOnCommentListener(new FriendCircleView.OnCommentListener() {
            @Override
            public void onCommentClick(Comment comment) {
                ToastUtils.shortToast(comment.content);
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
