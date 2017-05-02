package com.yhy.fridcir.entity;

/**
 * Created by HongYi Yan on 2017/4/27 23:59.
 */
public class User {
    public String id;
    public String name;
    public String avatar;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id != null ? id.equals(user.id) : user.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
