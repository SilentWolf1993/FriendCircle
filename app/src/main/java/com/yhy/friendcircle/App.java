package com.yhy.friendcircle;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.lzy.ninegrid.NineGridView;
import com.orhanobut.logger.Logger;
import com.yhy.fridcir.entity.User;
import com.yhy.fridcir.helper.FcHepler;
import com.yhy.fridcir.utils.ImgUtils;
import com.yhy.fridcir.utils.ToastUtils;
import com.yhy.friendcircle.global.ImgUrls;

import java.util.UUID;

/**
 * Created by HongYi Yan on 2017/4/28 1:17.
 */
public class App extends Application {
    private User mUser;

    @Override
    public void onCreate() {
        super.onCreate();

        init();
    }

    private void init() {
        mUser = new User();
        mUser.id = UUID.randomUUID().toString();
        mUser.name = "天狼";
        mUser.avatar = ImgUrls.getAImgUrl();

        ToastUtils.init(this);

        FcHepler.getInstance().init(this);

        ImgUtils.init(new ImgUtils.ImgLoader() {
            @Override
            public <T> void load(Context ctx, final ImageView iv, T model) {
                loadImg(ctx, iv, model);
            }
        });

        NineGridView.setImageLoader(new NineGridView.ImageLoader() {
            @Override
            public void onDisplayImage(Context context, ImageView imageView, String url) {
                loadImg(context, imageView, url);
            }

            @Override
            public Bitmap getCacheImage(String url) {
                return null;
            }
        });
    }

    private <T> void loadImg(Context ctx, final ImageView iv, T model) {
        //解决CircleImageView加载图片时只显示占位图和RecycleView网络加载图片时显示变形的bug，将图片装换为Bitmap即可
        Glide.with(ctx)
                .load(model)
                .asBitmap()
                .placeholder(R.drawable.ic_default_image)
                .error(R.drawable.ic_default_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)//使用磁盘缓存
                .skipMemoryCache(true)//跳过内存缓存
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                        iv.setImageBitmap(bitmap);
                    }
                });
//                .into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
//                        Drawable drawable = new BitmapDrawable(bitmap);
//                        iv.setImageDrawable(drawable);
//                    }
//                });

        Logger.i("model : " + model);
    }

    public User getUser() {
        return mUser;
    }
}
