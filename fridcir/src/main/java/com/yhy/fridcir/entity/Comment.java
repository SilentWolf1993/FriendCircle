package com.yhy.fridcir.entity;

import java.util.Date;

/**
 * Created by HongYi Yan on 2017/4/27 23:59.
 */
public class Comment {
    public String id;
    public String content;
    public Date createTime;
    public User fromUser;
    public User toUser;
}
