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
import com.yhy.fridcir.entity.FcCircle;
import com.yhy.fridcir.entity.FcComment;
import com.yhy.fridcir.entity.FcFavor;
import com.yhy.fridcir.entity.FcUser;
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
public class CircleAdapter extends BaseQuickAdapter<FcCircle, CircleAdapter.CircleViewHolder> {
    private Context mCtx;
    private FcUser mCurrentFcUser;
    private boolean mClickUserComment;
    private Map<FcCircle, FavorCommentPop> mPopMap;
    private OnFavorListener mFavorListener;
    private OnCommentListener mCommentClickListener;
    private OnUserClickListener mOnUserClickListener;
    private OnCommentItemLongClickListener mOnCommentItemLongClickListener;

    public CircleAdapter(Context ctx, @Nullable List<FcCircle> data, FcUser currentFcUser, boolean clickUserComment) {
        super(R.layout.item_circle_rv_list, data);
        mCtx = ctx;
        mCurrentFcUser = currentFcUser;
        mClickUserComment = clickUserComment;
        mPopMap = new HashMap<>();
    }

    @Override
    protected CircleAdapter.CircleViewHolder createBaseViewHolder(View view) {
        return new CircleViewHolder(view);
    }

    @Override
    protected void convert(final CircleAdapter.CircleViewHolder helper, final FcCircle item) {
        ImgUtils.load(helper.civAvatar, item.fromFcUser.avatar);

        helper.tvUserName.setText(item.fromFcUser.name);

        helper.etvContent.setText(SpanUrlUtils.formatUrlString(item.content));

        if (null != item.imgList && !item.imgList.isEmpty()) {
            helper.ngvImgs.setVisibility(View.VISIBLE);
            helper.ngvImgs.setAdapter(new NineGridViewClickAdapter(mCtx, item.imgList));
        } else {
            helper.ngvImgs.setVisibility(View.GONE);
        }

        helper.tvTime.setText(DateUtils.friendlyDate(item.createTime));

        showOrHiddenFavorComment(item, helper);

        helper.fvFavorUsers.setFavorList(item.favorList);

        helper.clvComment.setCommentList(item.fcCommentList);

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
                                mFavorListener.onFavor(item, getCurUserFavor(item), helper.fvFavorUsers, getPositionByItem(item));
                            }
                        }

                        @Override
                        public void onCommentClick(View v) {
                            if (null != mCommentClickListener) {
                                if (helper.llCommentFavorArea.getVisibility() == View.VISIBLE) {
                                    //评论区已经显示，就返回评论区的view
                                    mCommentClickListener.onComment(item, null, helper.llCommentFavorArea, helper.clvComment, getPositionByItem(item));
                                } else {
                                    //否则返回操作栏的view
                                    mCommentClickListener.onComment(item, null, helper.rlOperation, helper.clvComment, getPositionByItem(item));
                                }
                            }
                        }
                    });

                    mPopMap.put(item, pop);
                }

                pop.showPopupWindow(v, null != getCurUserFavor(item));
            }
        });

        helper.fvFavorUsers.setOnItemClickListener(new FavorView.OnItemClickListener() {
            @Override
            public void onClick(View v, FcUser fcUser, int position) {
                if (mClickUserComment && null != mCommentClickListener) {
                    mCommentClickListener.onComment(item, fcUser, v, helper.clvComment, getPositionByItem(item));
                }

                if (null != mOnUserClickListener) {
                    mOnUserClickListener.onUserClcik(v, item, fcUser);
                }
            }
        });

        helper.clvComment.setOnItemClickListener(new CommentListView.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (null != mCommentClickListener) {
                    mCommentClickListener.onComment(item, item.fcCommentList.get(position).fromFcUser, v, helper.clvComment, getPositionByItem(item));
                }
            }
        });

        helper.clvComment.setOnUserClickListener(new CommentListView.OnUserClickListener() {
            @Override
            public void onUserClick(View v, FcUser fcUser) {
                if (mClickUserComment && null != mCommentClickListener) {
                    mCommentClickListener.onComment(item, fcUser, v, helper.clvComment, getPositionByItem(item));
                }

                if (null != mOnUserClickListener) {
                    mOnUserClickListener.onUserClcik(v, item, fcUser);
                }
            }
        });

        helper.clvComment.setOnItemLongClickListener(new CommentListView.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View v, int position) {
                if (null != mOnCommentItemLongClickListener) {
                    mOnCommentItemLongClickListener.onItemLongClick(item, item.fcCommentList.get(position), helper.clvComment, getPositionByItem(item));
                }
            }
        });
    }

    private void showOrHiddenFavorComment(FcCircle item, CircleViewHolder helper) {
        //整个点赞和评论区域
        if ((null == item.favorList || item.favorList.isEmpty()) && (null == item.fcCommentList || item.fcCommentList.isEmpty())) {
            //隐藏评论区域
            helper.llCommentFavorArea.setVisibility(View.GONE);
        } else {
            helper.llCommentFavorArea.setVisibility(View.VISIBLE);
        }

        //点赞控件
        if (null != item.favorList && !item.favorList.isEmpty()) {
            helper.fvFavorUsers.setVisibility(View.VISIBLE);
        } else {
            helper.fvFavorUsers.setVisibility(View.GONE);
        }

        //分割线
        if (null != item.favorList && !item.favorList.isEmpty() && null != item.fcCommentList && !item.fcCommentList.isEmpty()) {
            //显示赞与评论区中间的分割线
            helper.vDiv.setVisibility(View.VISIBLE);
        } else {
            helper.vDiv.setVisibility(View.GONE);
        }

        //评论列表控件
        if (null != item.fcCommentList && !item.fcCommentList.isEmpty()) {
            helper.clvComment.setVisibility(View.VISIBLE);
        } else {
            helper.clvComment.setVisibility(View.GONE);
        }
    }

    private FcFavor getCurUserFavor(FcCircle fcCircle) {
        if (null != fcCircle && null != fcCircle.favorList && !fcCircle.favorList.isEmpty()) {
            for (FcFavor fi : fcCircle.favorList) {
                if (TextUtils.equals(fi.fcUser.id, mCurrentFcUser.id)) {
                    return fi;
                }
            }
        }
        return null;
    }

    public int getPositionByItem(FcCircle item) {
        if (null == item || null == mData) {
            return -1;
        }

        return mData.indexOf(item);
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

    public void setOnCommentListener(OnCommentListener listener) {
        mCommentClickListener = listener;
    }

    public void setOnCommentItemLongClickListener(OnCommentItemLongClickListener listener) {
        mOnCommentItemLongClickListener = listener;
    }

    public void setOnUserClickListener(OnUserClickListener listener) {
        mOnUserClickListener = listener;
    }

    public interface OnFavorListener {
        void onFavor(FcCircle fcCircle, FcFavor fcFavor, FavorView favorView, int itemPosition);
    }

    public interface OnCommentListener {
        void onComment(FcCircle fcCircle, FcUser toFcUser, View alignView, CommentListView clvComment, int itemPosition);
    }

    public interface OnCommentItemLongClickListener {
        void onItemLongClick(FcCircle fcCircle, FcComment fcComment, CommentListView clvComment, int itemPosition);
    }

    public interface OnUserClickListener {
        void onUserClcik(View v, FcCircle fcCircle, FcUser fcUser);
    }
}
