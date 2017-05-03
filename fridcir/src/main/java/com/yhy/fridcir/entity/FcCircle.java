package com.yhy.fridcir.entity;

import com.lzy.ninegrid.ImageInfo;

import java.util.Date;
import java.util.List;

/**
 * Created by HongYi Yan on 2017/4/28 0:02.
 */
public class FcCircle {
    public String id;
    public String content;
    public FcUser fromFcUser;
    public Date createTime;
    public List<ImageInfo> imgList;
    public List<FcFavor> favorList;
    public List<FcComment> fcCommentList;
}
