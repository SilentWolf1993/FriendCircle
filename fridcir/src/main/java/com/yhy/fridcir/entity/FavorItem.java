package com.yhy.fridcir.entity;

/**
 * Created by HongYi Yan on 2017/4/28 10:51.
 */
public class FavorItem {

    public String id;
    public User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FavorItem favorItem = (FavorItem) o;

        if (id != null ? !id.equals(favorItem.id) : favorItem.id != null) return false;
        return user != null ? user.equals(favorItem.user) : favorItem.user == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }
}
