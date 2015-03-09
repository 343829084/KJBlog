package org.kymjs.blog.ui.widget.dobmenu;

import org.kymjs.blog.AppContext;
import org.kymjs.blog.ui.widget.dobmenu.CurtainItem.SlidingType;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * 窗帘控件代理类控制器，这个才是控件的核心类
 * 
 * @author kymjs (https://github.com/kymjs)
 * @since 2015-3
 */
public class CurtainViewController {

    public enum SlidingStatus {
        COLLAPSED, EXPANDED, ANIMATING
    }

    public static final float DEFAULT_JUMP_LINE_PERCENTAGE = 0.6f;
    public static final int DEFAULT_INT = -1;

    private final Activity activity;
    private CurtainItem curtainItem;

    public View actionBarView;
    private ViewGroup content;

    private FrameLayout curtainParent;
    private FrameLayout.LayoutParams curtainLayoutParams;
    protected int curtainHeight;

    private float jumpLine;
    private AnimationExecutor animationExecutor;

    public CurtainViewController(Activity activity, CurtainItem slidingItem,
            int actionBarId) {
        super();
        this.activity = activity;
        this.curtainItem = slidingItem;
        init(actionBarId);
    }

    private void init(int actionBarId) {
        curtainParent = new FrameLayout(activity);
        content = (ViewGroup) activity.findViewById(android.R.id.content);
        content.addView(curtainParent);

        actionBarView = activity.findViewById(actionBarId); // 设置ActionBar
        setSlidingType(curtainItem.getSlidingType()); // 设置开关动画模式：卷动或平移
        animationExecutor = new AnimationExecutor(this);
    }

    public void setCurtainView(View curtainView) {
        if (curtainParent.getChildCount() > 0) {
            curtainParent.removeViewAt(0);
        }
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        curtainView.setLayoutParams(params);
        this.curtainParent.addView(curtainView);

        prepareSlidingLayout();
        hideCurtainLayout();
    }

    /**
     * 为控件做一些属性设置
     */
    protected void prepareSlidingLayout() {
        curtainLayoutParams = (FrameLayout.LayoutParams) curtainParent
                .getLayoutParams();
        ViewTreeObserver vto = curtainParent.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            @SuppressLint("NewApi")
            public void onGlobalLayout() {
                hideCurtainLayout();
                ViewTreeObserver obs = curtainParent.getViewTreeObserver();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    obs.removeOnGlobalLayoutListener(this);
                } else {
                    obs.removeGlobalOnLayoutListener(this);
                }
            }

        });

        curtainParent.setOnTouchListener(new OnContentTouchListener());

        curtainParent.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && getSlidingStatus() == SlidingStatus.EXPANDED) {
                    collapse();
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    /**
     * 定义curtainView最初的位置，如果是卷动模式初始值在屏幕顶端，如果是平移模式初始值在负屏幕高度处
     */
    protected void hideCurtainLayout() {
        curtainHeight = content.getHeight();
        jumpLine = curtainHeight * curtainItem.getJumpLinePercentage();

        curtainLayoutParams.height = curtainHeight;
        curtainParent.setLayoutParams(curtainLayoutParams);
        // 不同模式不同设置
        if (curtainItem.getSlidingType() == SlidingType.SIZE) {
            curtainLayoutParams.height = 0;
            curtainLayoutParams.topMargin = 0;
        } else if (curtainItem.getSlidingType() == SlidingType.MOVE) {
            curtainLayoutParams.topMargin = -curtainHeight;
            curtainLayoutParams.height = curtainHeight;
        }
        curtainParent.setLayoutParams(curtainLayoutParams);
    }

    /**
     * 窗帘拉下
     */
    public void expand() {
        animateSliding(0, curtainHeight);
        focusOnSliding();
    }

    /**
     * 窗帘拉起
     */
    public void collapse() {
        animateSliding(curtainHeight, 0);
    }

    public void finish() {
        collapse();
    }

    /**
     * 滑动时的焦点处理
     */
    public void focusOnSliding() {
        curtainParent.setFocusable(true);
        curtainParent.setFocusableInTouchMode(true);
        curtainParent.requestFocus();
    }

    /**
     * 滑动动画
     */
    public void animateSliding(int fromY, int toY) {
        if (curtainItem.isEnabled()) {
            animationExecutor.animateView(fromY, toY);
        }
    }

    public CurtainItem getSlidingItem() {
        return curtainItem;
    }

    public void setSlidingItem(CurtainItem slidingItem) {
        this.curtainItem = slidingItem;
    }

    public void setEnabled(boolean enabled) {
        hideCurtainLayout();
    }

    public SlidingStatus getSlidingStatus() {
        return getSlidingStatus(this);
    }

    public void setViewHeight(int viewHeight) {
        curtainLayoutParams.height = viewHeight;
        curtainParent.setLayoutParams(curtainLayoutParams);
    }

    public int getViewHeight() {
        return curtainLayoutParams.height;
    }

    public void setViewTop(int viewTop) {
        curtainLayoutParams.topMargin = viewTop - curtainHeight;
        curtainParent.setLayoutParams(curtainLayoutParams);
    }

    public int getViewTop() {
        return curtainLayoutParams.topMargin;
    }

    public void setSlidingType(SlidingType slidingType) {
        if (slidingType == SlidingType.SIZE) {
            actionBarView.setOnTouchListener(new OnSizingTouchListener());
        } else if (slidingType == SlidingType.MOVE) {
            actionBarView.setOnTouchListener(new OnMovingTouchListener());
        }
        if (curtainItem.getSlidingView() != null) {
            hideCurtainLayout();
        }
    }

    public FrameLayout getSlidingParent() {
        return curtainParent;
    }

    public int getSlidingHeight() {
        return curtainHeight;
    }

    public float getJumpLine() {
        return jumpLine;
    }

    public static final CurtainViewController.SlidingStatus getSlidingStatus(
            CurtainViewController mCurtainViewController) {

        FrameLayout slidingParent = mCurtainViewController.getSlidingParent();
        FrameLayout.LayoutParams slidingLayoutParams = (FrameLayout.LayoutParams) slidingParent
                .getLayoutParams();

        if (mCurtainViewController.getSlidingItem().getSlidingType() == SlidingType.SIZE) {

            int currentSlidingHeight = slidingParent.getHeight();

            if (currentSlidingHeight == 0) {
                return CurtainViewController.SlidingStatus.COLLAPSED;

            } else if (currentSlidingHeight >= mCurtainViewController
                    .getSlidingHeight()) {
                return CurtainViewController.SlidingStatus.EXPANDED;

            } else {
                return CurtainViewController.SlidingStatus.ANIMATING;
            }

        } else if (mCurtainViewController.getSlidingItem().getSlidingType() == SlidingType.MOVE) {

            int currentSlidingTop = slidingLayoutParams.topMargin;

            if (currentSlidingTop <= -mCurtainViewController.getSlidingHeight()) {
                return CurtainViewController.SlidingStatus.COLLAPSED;

            } else if (currentSlidingTop >= 0) {
                return CurtainViewController.SlidingStatus.EXPANDED;

            } else {
                return CurtainViewController.SlidingStatus.ANIMATING;
            }

        } else {
            return CurtainViewController.SlidingStatus.ANIMATING;
        }
    }

    public static final ImageView initHandle(Context context,
            CurtainViewController slidingMenuController, CurtainItem slidingItem) {
        ImageView handle = new ImageView(context);
        handle.setScaleType(ImageView.ScaleType.CENTER);
        handle.setContentDescription("");

        FrameLayout.LayoutParams handleLayoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM
                        | Gravity.CENTER_HORIZONTAL);
        handle.setLayoutParams(handleLayoutParams);
        return handle;
    }

    /**
     * 缩放模式开窗帘的ActionBar触摸事件监听器
     * 
     * @author kymjs (https://github.com/kymjs)
     * @since 2015-3
     */
    public class OnSizingTouchListener implements OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (getSlidingItem().isEnabled()) {
                float y = event.getY();
                switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    focusOnSliding();
                    if (curtainParent.getHeight() > 0) {
                        return false;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    curtainLayoutParams.height = (int) y;
                    curtainParent.setLayoutParams(curtainLayoutParams);
                    break;
                case MotionEvent.ACTION_UP:
                    if (y > getJumpLine()) {
                        animateSliding((int) y, getSlidingHeight());
                    } else {
                        animateSliding((int) y, 0);
                    }
                    break;
                }
            }
            return true;
        }
    }

    /**
     * 平移模式开窗帘的ActionBar触摸事件监听器
     * 
     * @author kymjs (https://github.com/kymjs)
     * @since 2015-3
     */
    public class OnMovingTouchListener implements OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (getSlidingItem().isEnabled()) {
                float y = event.getY();

                switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    focusOnSliding();
                    if (curtainLayoutParams.bottomMargin > 0) {
                        return false;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    curtainLayoutParams.topMargin = (int) y
                            - curtainParent.getHeight();
                    curtainParent.setLayoutParams(curtainLayoutParams);
                    break;
                case MotionEvent.ACTION_UP:
                    if (y > getJumpLine()) {
                        animateSliding((int) y, curtainParent.getHeight());
                    } else {
                        animateSliding((int) y, 0);
                    }
                    break;
                }
            }
            return true;
        }
    }

    /**
     * 平移模式关窗帘的Content触摸事件监听器（卷动模式的就不写了，需要的时候再说）
     * 
     * @author kymjs (https://github.com/kymjs)
     * @since 2015-3
     */
    public class OnContentTouchListener implements OnTouchListener {
        float downY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            float y = event.getY();
            switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = event.getY();
                focusOnSliding();
                if (curtainLayoutParams.bottomMargin > 0) {
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int move = (int) (downY - y);
                if (move > 0) {
                    curtainLayoutParams.topMargin = -move;
                    curtainParent.setLayoutParams(curtainLayoutParams);
                }
                break;
            case MotionEvent.ACTION_UP:
                int moveY = (int) (downY - y);
                if (moveY < 50) {
                    v.performClick();
                } else if (moveY > AppContext.screenH) {
                    animateSliding(curtainLayoutParams.topMargin
                            + curtainHeight, curtainParent.getHeight());
                } else {
                    animateSliding(curtainLayoutParams.topMargin
                            + curtainHeight, 0);
                }
                break;
            default:
                break;
            }
            return true;
        }
    }

}
