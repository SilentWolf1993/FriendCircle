package com.yhy.fridcir.entity;

import java.util.Date;

/**
 * Created by HongYi Yan on 2017/4/27 23:59.
 */
public class FcComment {
    public String id;
    public String content;
    public Date createTime;
    public FcUser fromFcUser;
    public FcUser toFcUser;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FcComment fcComment = (FcComment) o;

        return id != null ? id.equals(fcComment.id) : fcComment.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
