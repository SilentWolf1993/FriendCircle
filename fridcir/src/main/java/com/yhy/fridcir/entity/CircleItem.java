package com.yhy.fridcir.entity;

import com.lzy.ninegrid.ImageInfo;

import java.util.Date;
import java.util.List;

/**
 * Created by HongYi Yan on 2017/4/28 0:02.
 */
public class CircleItem {
    public String id;
    public String content;
    public User fromUser;
    public Date createTime;
    public List<ImageInfo> imgList;
    public List<FavorItem> favorList;
    public List<Comment> commentList;
}
