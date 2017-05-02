package com.yhy.fridcir.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.yhy.fridcir.R;
import com.yhy.fridcir.entity.CircleItem;
import com.yhy.fridcir.entity.FavorItem;
import com.yhy.fridcir.entity.User;
import com.yhy.fridcir.utils.SpanUrlUtils;
import com.yhy.fridcir.widget.CommentListView;
import com.yhy.fridcir.widget.ExpandTextView;
import com.yhy.fridcir.widget.FavorCommentPop;
import com.yhy.fridcir.widget.FavorView;
import com.yhy.utils.core.DateUtils;
import com.yhy.utils.core.ImgUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by HongYi Yan on 2017/5/2 13:16.
 */
public class CircleAdapter extends BaseQuickAdapter<CircleItem, CircleAdapter.CircleViewHolder> {
    private Context mCtx;
    private User mCurrentUser;
    private Map<CircleItem, FavorCommentPop> mPopMap;
    private OnFavorListener mFavorListener;
    private OnCommentClickListener mCommentClickListener;

    public CircleAdapter(Context ctx, @Nullable List<CircleItem> data, User currentUser) {
        super(R.layout.item_circle_msg_rv_list, data);
        mCtx = ctx;
        mCurrentUser = currentUser;
        mPopMap = new HashMap<>();
    }

    @Override
    protected CircleAdapter.CircleViewHolder createBaseViewHolder(View view) {
        return new CircleViewHolder(view);
    }

    @Override
    protected void convert(final CircleAdapter.CircleViewHolder helper, final CircleItem item) {
        ImgUtils.load(helper.civAvatar, item.fromUser.avatar);

        helper.tvUserName.setText(item.fromUser.name);

        helper.etvContent.setText(SpanUrlUtils.formatUrlString(item.content));

        if (null != item.imgList && !item.imgList.isEmpty()) {
            helper.ngvImgs.setVisibility(View.VISIBLE);
            helper.ngvImgs.setAdapter(new NineGridViewClickAdapter(mCtx, item.imgList));
        } else {
            helper.ngvImgs.setVisibility(View.GONE);
        }

        helper.tvTime.setText(DateUtils.friendlyDate(item.createTime));

        if ((null == item.favorList || item.favorList.isEmpty()) && (null == item.commentList || item.commentList.isEmpty())) {
            //隐藏评论区域
            helper.llCommentFavorArea.setVisibility(View.GONE);
        } else {
            helper.llCommentFavorArea.setVisibility(View.VISIBLE);
        }

        if (null != item.favorList && !item.favorList.isEmpty()) {
            helper.fvFavorUsers.setVisibility(View.VISIBLE);
            helper.fvFavorUsers.setFavorList(item.favorList);
        } else {
            helper.fvFavorUsers.setVisibility(View.GONE);
        }

        if (null != item.commentList && !item.commentList.isEmpty()) {
            helper.clvComment.setVisibility(View.VISIBLE);
            helper.clvComment.setCommentList(item.commentList);
        } else {
            helper.clvComment.setVisibility(View.GONE);
        }

        if (null != item.favorList && !item.favorList.isEmpty() && null != item.commentList && !item.commentList.isEmpty()) {
            //显示赞与评论区中间的分割线
            helper.vDiv.setVisibility(View.VISIBLE);
        } else {
            helper.vDiv.setVisibility(View.GONE);
        }

        helper.ivShowPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FavorCommentPop pop;
                if (mPopMap.containsKey(item)) {
                    pop = mPopMap.get(item);
                } else {
                    pop = new FavorCommentPop(mCtx);
                    pop.setOnItemClickListener(new FavorCommentPop.OnItemClickListener() {
                        @Override
                        public void onFavorClick(View v) {
                            if (null != mFavorListener) {
                                mFavorListener.onFavor(helper.fvFavorUsers, item, getCurUserFavor(item));
                            }
                        }

                        @Override
                        public void onCommentClick(View v) {
                            if (null != mCommentClickListener) {
                                if (helper.llCommentFavorArea.getVisibility() == View.VISIBLE) {
                                    //评论区已经显示，就返回评论区的view
                                    mCommentClickListener.onCommentClick(item, null, helper.clvComment, helper.llCommentFavorArea);
                                } else {
                                    //否则返回操作栏的view
                                    mCommentClickListener.onCommentClick(item, null, helper.clvComment, helper.rlOperation);
                                }
                            }
                        }
                    });

                    mPopMap.put(item, pop);
                }

                pop.showPopupWindow(v, null != getCurUserFavor(item));
            }
        });

        helper.clvComment.setOnItemClickListener(new CommentListView.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (null != mCommentClickListener) {
                    mCommentClickListener.onCommentClick(item, item.commentList.get(position).fromUser, helper.clvComment, v);
                }
            }
        });
    }

    private FavorItem getCurUserFavor(CircleItem circleItem) {
        if (null != circleItem && null != circleItem.favorList && !circleItem.favorList.isEmpty()) {
            for (FavorItem fi : circleItem.favorList) {
                if (TextUtils.equals(fi.user.id, mCurrentUser.id)) {
                    return fi;
                }
            }
        }
        return null;
    }

    public static class CircleViewHolder extends BaseViewHolder {
        public CircleImageView civAvatar;
        public TextView tvUserName;
        public ExpandTextView etvContent;
        public NineGridView ngvImgs;
        public RelativeLayout rlOperation;
        public TextView tvTime;
        public ImageView ivShowPop;
        public LinearLayout llCommentFavorArea;
        public FavorView fvFavorUsers;
        public View vDiv;
        public CommentListView clvComment;

        public CircleViewHolder(View view) {
            super(view);

            civAvatar = (CircleImageView) view.findViewById(R.id.civ_avatar);
            tvUserName = (TextView) view.findViewById(R.id.tv_user_name);
            etvContent = (ExpandTextView) view.findViewById(R.id.etv_content);
            ngvImgs = (NineGridView) view.findViewById(R.id.ngv_imgs);
            rlOperation = (RelativeLayout) view.findViewById(R.id.rl_operation);
            tvTime = (TextView) view.findViewById(R.id.tv_time);
            ivShowPop = (ImageView) view.findViewById(R.id.iv_show_pop);
            llCommentFavorArea = (LinearLayout) view.findViewById(R.id.ll_favor_comment_area);
            fvFavorUsers = (FavorView) view.findViewById(R.id.fv_favor_users);
            vDiv = view.findViewById(R.id.v_div);
            clvComment = (CommentListView) view.findViewById(R.id.clv_comment);
        }
    }

    public void setOnFavorListener(OnFavorListener listener) {
        mFavorListener = listener;
    }

    public void setOnCommentClickListener(OnCommentClickListener listener) {
        mCommentClickListener = listener;
    }

    public interface OnFavorListener {
        void onFavor(FavorView favorView, CircleItem item, FavorItem favorItem);
    }

    public interface OnCommentClickListener {
        void onCommentClick(CircleItem item, User toUser, CommentListView clvComment, View alignView);
    }
}
