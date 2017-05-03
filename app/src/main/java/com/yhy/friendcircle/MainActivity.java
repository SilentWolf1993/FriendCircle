package com.yhy.friendcircle;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lzy.ninegrid.ImageInfo;
import com.yhy.fridcir.entity.FcCircle;
import com.yhy.fridcir.entity.FcComment;
import com.yhy.fridcir.entity.FcFavor;
import com.yhy.fridcir.entity.FcUser;
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
    private FcUser mCurFcUser;
    private MainActivityBinding mBinding;
    private List<FcCircle> mCircleList;
    private Random mRand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mCurFcUser = ((App) getApplication()).getUser();

        mRand = new Random();

        initData();

        mBinding.cvContent.setDataList(mCircleList, mCurFcUser);

        mBinding.cvContent.setOnFavorListener(new FriendCircleView.OnFavorListener() {
            @Override
            public void onFavor(FcCircle item, FcFavor fcFavor, boolean isCancel) {
                if (isCancel) {
                    ToastUtils.shortToast("取消赞");
                } else {
                    ToastUtils.shortToast("赞");
                }
            }
        });

        mBinding.cvContent.setOnCommentListener(new FriendCircleView.OnCommentListener() {
            @Override
            public void onCommentClick(FcCircle fcCircle, FcComment fcComment) {
                ToastUtils.shortToast(fcComment.content);
            }
        });
    }

    private void initData() {
        mCircleList = new ArrayList<>();
        FcUser fcUser = null;
        FcCircle msg = null;
        ImageInfo img = null;
        FcFavor favor = null;
        String imgUrl = null;
        FcComment fcComment = null;
        for (int i = 0; i < 30; i++) {
            fcUser = new FcUser();
            fcUser.id = UUID.randomUUID().toString();
            fcUser.name = "用户 " + i;
            fcUser.avatar = ImgUrls.getAImgUrl();

            msg = new FcCircle();
            msg.id = UUID.randomUUID().toString();
            msg.fromFcUser = fcUser;
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
                fcUser = new FcUser();
                fcUser.id = UUID.randomUUID().toString();
                fcUser.name = "点赞" + j;
                fcUser.avatar = ImgUrls.getAImgUrl();
                favor = new FcFavor();
                favor.id = UUID.randomUUID().toString();
                favor.fcUser = fcUser;
                msg.favorList.add(favor);
            }

            if (null == msg.fcCommentList) {
                msg.fcCommentList = new ArrayList<>();
            }

            for (int j = 0; j < mRand.nextInt(10); j++) {
                fcUser = new FcUser();
                fcUser.id = UUID.randomUUID().toString();
                fcUser.name = "评论" + j;
                fcUser.avatar = ImgUrls.getAImgUrl();

                fcComment = new FcComment();
                fcComment.id = UUID.randomUUID().toString();
                fcComment.content = "评论评论评论论评论评论";
                fcComment.fromFcUser = fcUser;

                if (j % 2 != 0) {
                    fcUser = new FcUser();
                    fcUser.id = UUID.randomUUID().toString();
                    fcUser.name = "被回复" + j;
                    fcUser.avatar = ImgUrls.getAImgUrl();
                    fcComment.toFcUser = fcUser;
                }

                msg.fcCommentList.add(fcComment);
            }

            mCircleList.add(msg);
        }
    }
}
