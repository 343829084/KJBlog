package org.kymjs.blog.ui.fragment;

import org.kymjs.blog.R;
import org.kymjs.blog.adapter.OSCBlogAdapter;
import org.kymjs.blog.domain.OSCBlog;
import org.kymjs.blog.domain.OSCBlogList;
import org.kymjs.blog.domain.SimpleBackPage;
import org.kymjs.blog.ui.SimpleBackActivity;
import org.kymjs.blog.ui.widget.EmptyLayout;
import org.kymjs.blog.ui.widget.listview.FooterLoadingLayout;
import org.kymjs.blog.ui.widget.listview.PullToRefreshBase;
import org.kymjs.blog.ui.widget.listview.PullToRefreshBase.OnRefreshListener;
import org.kymjs.blog.ui.widget.listview.PullToRefreshList;
import org.kymjs.blog.utils.Parser;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.utils.KJLoger;
import org.kymjs.kjframe.utils.StringUtils;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * 第三方博客列表
 * 
 * @author kymjs
 */
public class OSCBlogListFragment extends TitleBarFragment {

    public static final String TAG = OSCBlogListFragment.class.getSimpleName();

    @BindView(id = R.id.empty_layout)
    private EmptyLayout mEmptyLayout;
    @BindView(id = R.id.listview)
    private PullToRefreshList mRefreshLayout;
    private ListView mListView;

    private OSCBlogAdapter adapter;

    private KJHttp kjh;

    private final String OSCBLOG_HOST = "http://www.oschina.net/action/api/userblog_list?authoruid=";
    private int BLOGLIST_ID = 1428332;
    private String cache;
    private SimpleBackActivity aty;
    private String titleBarName;

    public static String BLOGLIST_KEY = "osc_blog_id_key";

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container,
            Bundle bundle) {
        View root = View.inflate(outsideAty,
                R.layout.frag_pull_refresh_listview, null);
        aty = (SimpleBackActivity) getActivity();
        return root;
    }

    @Override
    protected void initData() {
        super.initData();
        HttpConfig config = new HttpConfig();
        int hour = StringUtils.toInt(StringUtils.getDataTime("HH"), 0);
        if (hour > 7 && hour < 10) { // 如果是在早上7点到10点，就缓存的时间短一点
            config.cacheTime = 10;
        } else {
            config.cacheTime = 300;
        }
        config.useDelayCache = true;
        kjh = new KJHttp(config);

        Bundle bundle = aty.getBundleData();
        String name = null;
        if (bundle != null) {
            BLOGLIST_ID = bundle.getInt(BLOGLIST_KEY, 1428332);
            name = bundle.getString(BlogAuthorFragment.AUTHOR_NAME_KEY);
        }
        if (StringUtils.isEmpty(name)) {
            titleBarName = getString(R.string.osc_joke);
        } else {
            titleBarName = name + "的博客";
        }
    }

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
        mListView = mRefreshLayout.getRefreshView();
        mListView.setDivider(new ColorDrawable(android.R.color.transparent));
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                if (parent.getAdapter() instanceof OSCBlogAdapter) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("oscblog_id", ((OSCBlog) parent.getAdapter()
                            .getItem(position)).getId());
                    SimpleBackActivity.postShowWith(outsideAty,
                            SimpleBackPage.OSC_BLOG_DETAIL, bundle);
                }
            }
        });
        mRefreshLayout.setPullLoadEnabled(true);
        ((FooterLoadingLayout) mRefreshLayout.getFooterLoadingLayout())
                .setNoMoreDataText("太旧的文章就不要看了吧~");
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                refresh();
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                mRefreshLayout.setHasMoreData(false);
            }
        });

        fillUI();
    }

    private void fillUI() {
        cache = kjh.getStringCache(OSCBLOG_HOST + BLOGLIST_ID);
        if (!StringUtils.isEmpty(cache)) {
            OSCBlogList dataRes = Parser.xmlToBean(OSCBlogList.class, cache);
            if (adapter == null) {
                adapter = new OSCBlogAdapter(mListView, dataRes.getBloglist(),
                        R.layout.item_list_blog);
                mListView.setAdapter(adapter);
            } else {
                adapter.refresh(dataRes.getBloglist());
            }
            mEmptyLayout.dismiss();
        }
        refresh();
    }

    private void refresh() {
        kjh.get(OSCBLOG_HOST + BLOGLIST_ID, new HttpCallBack() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                KJLoger.debug(TAG + "网络请求：" + t);
                mRefreshLayout.onPullDownRefreshComplete();
                if (t != null && !t.equals(cache)) {
                    OSCBlogList dataRes = Parser
                            .xmlToBean(OSCBlogList.class, t);
                    if (adapter == null) {
                        adapter = new OSCBlogAdapter(mListView, dataRes
                                .getBloglist(), R.layout.item_list_blog);
                        mListView.setAdapter(adapter);
                    } else {
                        adapter.refresh(dataRes.getBloglist());
                    }
                }
                mEmptyLayout.dismiss();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                mEmptyLayout.setErrorType(EmptyLayout.NODATA);
            }
        });
    }

    /**
     * 将在onResume方法中调用
     */
    @Override
    protected void setActionBarRes(ActionBarRes actionBarRes) {
        super.setActionBarRes(actionBarRes);
        actionBarRes.title = titleBarName;
        actionBarRes.backImageId = R.drawable.titlebar_back;
    }

    @Override
    public void onBackClick() {
        super.onBackClick();
        outsideAty.finish();
    }

}
