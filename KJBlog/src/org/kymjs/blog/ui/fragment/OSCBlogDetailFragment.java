package org.kymjs.blog.ui.fragment;

import java.util.List;

import org.kymjs.blog.R;
import org.kymjs.blog.domain.CollectData;
import org.kymjs.blog.domain.OSCBlogEntity;
import org.kymjs.blog.ui.SimpleBackActivity;
import org.kymjs.blog.utils.Parser;
import org.kymjs.blog.utils.UIHelper;
import org.kymjs.kjframe.KJDB;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.utils.KJLoger;
import org.kymjs.kjframe.utils.StringUtils;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

/**
 * OSC博客详情界面
 * 
 * @author kymjs (https://github.com/kymjs)
 * 
 */
public class OSCBlogDetailFragment extends TitleBarFragment {

    public static final String TAG = OSCBlogDetailFragment.class
            .getSimpleName();

    @BindView(id = R.id.blogdetail_webview)
    private WebView mWebView;
    @BindView(id = R.id.blogdetail_tv_author)
    private TextView mTvAuthor;
    @BindView(id = R.id.blogdetail_tv_time)
    private TextView mTvTime;
    @BindView(id = R.id.blogdetail_tv_title)
    private TextView mTvBlogTitle;

    private final String OSCBLOG_HOST = "http://www.oschina.net/action/api/blog_detail?id=";
    private int OSCBLOG_ID = 295001;

    private KJHttp kjh;
    private String cacheData;
    private KJDB kjdb;
    private final CollectData data = new CollectData();

    private SimpleBackActivity aty;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container,
            Bundle bundle) {
        aty = (SimpleBackActivity) getActivity();
        View root = View.inflate(aty, R.layout.frag_blog_detail, null);
        return root;
    }

    @Override
    protected void setActionBarRes(ActionBarRes actionBarRes) {
        super.setActionBarRes(actionBarRes);
        actionBarRes.title = getString(R.string.blog_detail);
        actionBarRes.backImageId = R.drawable.titlebar_back;
        List<CollectData> datas = kjdb.findAllByWhere(CollectData.class,
                "url='" + OSCBLOG_HOST + OSCBLOG_ID + "'");
        if (datas != null && datas.size() != 0) {
            actionBarRes.menuImageId = R.drawable.titlebar_star;
            outsideAty.mImgMenu.setTag(Boolean.valueOf(true));
        } else {
            actionBarRes.menuImageId = R.drawable.titlebar_unstar;
            outsideAty.mImgMenu.setTag(Boolean.valueOf(false));
        }

    }

    @Override
    public void onBackClick() {
        super.onBackClick();
        aty.finish();
    }

    @Override
    public void onMenuClick() {
        super.onMenuClick();
        Object tag = outsideAty.mImgMenu.getTag();
        // 如果有tag，且tag为真，则把tag改为false取消收藏
        if (tag != null && tag instanceof Boolean) {
            if ((Boolean) tag) {
                outsideAty.mImgMenu.setTag(Boolean.valueOf(false));
                setMenuImage(R.drawable.titlebar_unstar);
                kjdb.deleteByWhere(CollectData.class, "url='" + OSCBLOG_HOST
                        + OSCBLOG_ID + "'");
                return;
            }
        }
        // 如果没有tag或tag为假，则把tag改为true收藏本链接
        outsideAty.mImgMenu.setTag(Boolean.valueOf(true));
        setMenuImage(R.drawable.titlebar_star);
        data.setName(mWebView.getTitle());
        data.setUrl(OSCBLOG_HOST + OSCBLOG_ID);
        kjdb.save(data);
    }

    @Override
    protected void initData() {
        super.initData();
        kjdb = KJDB.create(outsideAty);
        Bundle outData = aty.getBundleData();
        if (outData != null) {
            OSCBLOG_ID = outData.getInt("oscblog_id", 295001);
        }
        HttpConfig config = new HttpConfig();
        config.cacheTime = 300;
        kjh = new KJHttp(config);
        cacheData = kjh.getStringCache(OSCBLOG_HOST + OSCBLOG_ID);
    }

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
        UIHelper.initWebView(mWebView);

        if (!StringUtils.isEmpty(cacheData)) {
            OSCBlogEntity data = Parser.xmlToBean(OSCBlogEntity.class,
                    cacheData);
            fillUI(data);
        }

        kjh.get(OSCBLOG_HOST + OSCBLOG_ID, new HttpCallBack() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                KJLoger.debug(TAG + "网络请求：" + t);
                if (t != null && !t.equals(cacheData)) {
                    OSCBlogEntity data = Parser.xmlToBean(OSCBlogEntity.class,
                            t);
                    fillUI(data);
                }
            }
        });
    }

    /**
     * 填充UI
     * 
     * @param data
     */
    private void fillUI(OSCBlogEntity data) {
        mTvAuthor.setText(data.getBlog().getAuthorname());
        mTvBlogTitle.setText(data.getBlog().getTitle());
        mTvTime.setText(StringUtils.friendlyTime(data.getBlog().getPubDate()));

        StringBuffer body = new StringBuffer();
        body.append(UIHelper.setHtmlCotentSupportImagePreview(data.getBlog()
                .getBody()));
        body.append(UIHelper.WEB_STYLE).append(UIHelper.WEB_LOAD_IMAGES);
        mWebView.loadDataWithBaseURL(null, body.toString(), "text/html",
                "utf-8", null);
    }
}
