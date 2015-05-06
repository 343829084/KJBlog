package org.kymjs.blog.ui.fragment;

import org.kymjs.blog.R;
import org.kymjs.blog.adapter.NewBlogAdapter;
import org.kymjs.blog.domain.KymJSBlog;
import org.kymjs.blog.ui.Main;
import org.kymjs.blog.ui.widget.listview.PullToRefreshBase;
import org.kymjs.blog.ui.widget.listview.PullToRefreshBase.OnRefreshListener;
import org.kymjs.blog.ui.widget.listview.PullToRefreshList;
import org.kymjs.blog.utils.Parser;
import org.kymjs.blog.utils.UIHelper;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;
import org.kymjs.kjframe.ui.BindView;
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
 * 主界面博客模块
 * 
 * @author kymjs (https://github.com/kymjs)
 * @since 2015-3
 */
public class NewBlogFragment extends TitleBarFragment {

    public static final String TAG = NewBlogFragment.class.getSimpleName();

    @BindView(id = R.id.blog_swiperefreshlayout)
    private PullToRefreshList mRefreshLayout;
    private ListView mList;

    private Main aty;
    private KJHttp kjh;
    private NewBlogAdapter adapter;

    private final String MY_BLOG_HOST = "http://blog.kymjs.com/api/bloglist";
    private String cache;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container,
            Bundle bundle) {
        aty = (Main) getActivity();
        return View.inflate(aty, R.layout.frag_blog, null);
    }

    @Override
    protected void setActionBarRes(ActionBarRes actionBarRes) {
        actionBarRes.title = getString(R.string.app_name);
    }

    @Override
    protected void initData() {
        super.initData();
        HttpConfig config = new HttpConfig();
        int hour = StringUtils.toInt(StringUtils.getDataTime("HH"), 0);
        if (hour > 16 && hour < 22) {
            config.cacheTime = 10;
        } else {
            config.cacheTime = 300;
        }
        config.useDelayCache = true;
        kjh = new KJHttp(config);
    }

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
        listViewPreference();
        fillUI();
    }

    /**
     * 初始化ListView样式
     */
    private void listViewPreference() {
        mList = mRefreshLayout.getRefreshView();
        mList.setDivider(new ColorDrawable(android.R.color.transparent));
        mList.setOverscrollFooter(null);
        mList.setOverscrollHeader(null);
        mList.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
        mRefreshLayout.setPullLoadEnabled(true);

        mList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                UIHelper.toBrowser(aty, adapter.getItem(position).getUrl());
            }
        });

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                refresh();
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                refresh();
            }
        });
    }

    private void fillUI() {
        cache = kjh.getStringCache(MY_BLOG_HOST);
        if (!StringUtils.isEmpty(cache)) {
            KymJSBlog dataRes = Parser.xmlToBean(KymJSBlog.class, cache);
            if (dataRes != null) {
                adapter = new NewBlogAdapter(mList, dataRes.getList(),
                        R.layout.item_list_blog);
            } else {
                adapter = new NewBlogAdapter(mList, null,
                        R.layout.item_list_blog);
            }
            mList.setAdapter(adapter);
        }
        refresh();
    }

    private void refresh() {
        kjh.get(MY_BLOG_HOST, new HttpCallBack() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if (t != null) {
                    KymJSBlog dataRes = Parser
                            .xmlToBean(KymJSBlog.class, cache);
                    if (adapter == null) {
                        if (dataRes != null) {
                            adapter = new NewBlogAdapter(mList, dataRes
                                    .getList(), R.layout.item_list_blog);
                        } else {
                            adapter = new NewBlogAdapter(mList, null,
                                    R.layout.item_list_blog);
                        }
                        mList.setAdapter(adapter);
                    } else {
                        if (dataRes != null) {
                            adapter.refresh(dataRes.getList());
                        } else {
                            adapter.refresh(null);
                        }
                    }
                }
                mRefreshLayout.onPullDownRefreshComplete();
                mRefreshLayout.onPullUpRefreshComplete();
            }
        });
    }
}
