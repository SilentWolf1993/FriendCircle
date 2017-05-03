package com.yhy.fridcir.entity;

/**
 * Created by HongYi Yan on 2017/4/28 10:51.
 */
public class FcFavor {

    public String id;
    public FcUser fcUser;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FcFavor fcFavor = (FcFavor) o;

        if (id != null ? !id.equals(fcFavor.id) : fcFavor.id != null) return false;
        return fcUser != null ? fcUser.equals(fcFavor.fcUser) : fcFavor.fcUser == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (fcUser != null ? fcUser.hashCode() : 0);
        return result;
    }
}
