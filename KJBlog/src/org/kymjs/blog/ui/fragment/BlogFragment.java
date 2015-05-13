package org.kymjs.blog.ui.fragment;

import java.util.List;

import org.kymjs.blog.R;
import org.kymjs.blog.adapter.BlogAdapter;
import org.kymjs.blog.domain.Blog;
import org.kymjs.blog.ui.Main;
import org.kymjs.blog.ui.widget.EmptyLayout;
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
public class BlogFragment extends TitleBarFragment {

    public static final String TAG = BlogFragment.class.getSimpleName();

    @BindView(id = R.id.empty_layout)
    private EmptyLayout mEmptyLayout;
    @BindView(id = R.id.blog_swiperefreshlayout)
    private PullToRefreshList mRefreshLayout;
    private ListView mList;

    private Main aty;
    private KJHttp kjh;
    private BlogAdapter adapter;

    private final String MY_BLOG_HOST = "http://www.kymjs.com/api/json_blog_list";
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
        if (hour > 12 && hour < 22) {
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
            List<Blog> datas = Parser.getBlogList(cache);
            adapter = new BlogAdapter(mList, datas, R.layout.item_list_blog);
            mList.setAdapter(adapter);
        } else {
            mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        }
        refresh();
    }

    private void refresh() {
        kjh.get(MY_BLOG_HOST, new HttpCallBack() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if (t != null) {
                    List<Blog> datas = Parser.getBlogList(t);
                    if (adapter == null) {
                        adapter = new BlogAdapter(mList, datas,
                                R.layout.item_list_blog);
                        mList.setAdapter(adapter);
                    } else {
                        adapter.refresh(datas);
                    }
                }
                mRefreshLayout.onPullDownRefreshComplete();
                mRefreshLayout.onPullUpRefreshComplete();
                mEmptyLayout.dismiss();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                mEmptyLayout.setErrorType(EmptyLayout.NODATA);
            }
        });
    }
}
