package org.kymjs.blog.ui.fragment;

import java.util.List;

import org.kymjs.blog.R;
import org.kymjs.blog.adapter.CollectAdapter;
import org.kymjs.blog.domain.CollectData;
import org.kymjs.blog.ui.widget.EmptyLayout;
import org.kymjs.blog.ui.widget.listview.PullToRefreshBase;
import org.kymjs.blog.ui.widget.listview.PullToRefreshBase.OnRefreshListener;
import org.kymjs.blog.ui.widget.listview.PullToRefreshList;
import org.kymjs.blog.utils.UIHelper;
import org.kymjs.kjframe.KJDB;
import org.kymjs.kjframe.ui.BindView;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MyCollectFragment extends TitleBarFragment {

    public static final String TAG = MyCollectFragment.class.getSimpleName();

    @BindView(id = R.id.empty_layout)
    private EmptyLayout mEmptyLayout;
    @BindView(id = R.id.listview)
    private PullToRefreshList mRefreshLayout;
    private ListView mListView;

    private CollectAdapter adapter;

    private KJDB kjdb;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container,
            Bundle bundle) {
        View root = View.inflate(outsideAty,
                R.layout.frag_pull_refresh_listview, null);
        return root;
    }

    @Override
    protected void initData() {
        super.initData();
        kjdb = KJDB.create(outsideAty);
        mEmptyLayout.dismiss();
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
                if (parent.getAdapter() instanceof CollectAdapter) {
                    CollectData data = (CollectData) parent.getAdapter()
                            .getItem(position);
                    UIHelper.toBrowser(outsideAty, data.getUrl());
                }
            }
        });
        mRefreshLayout.setPullLoadEnabled(false);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                adapter.refresh(kjdb.findAll(CollectData.class));
                mRefreshLayout.onPullDownRefreshComplete();
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {}
        });
        fillUI();
    }

    private void fillUI() {
        List<CollectData> datas = kjdb.findAll(CollectData.class);
        adapter = new CollectAdapter(mListView, datas,
                R.layout.item_list_collect);
        mListView.setAdapter(adapter);
    }

    /**
     * 将在onResume方法中调用
     */
    @Override
    protected void setActionBarRes(ActionBarRes actionBarRes) {
        super.setActionBarRes(actionBarRes);
        actionBarRes.title = getString(R.string.str_collect);
        actionBarRes.backImageId = R.drawable.titlebar_back;
    }

    @Override
    public void onBackClick() {
        super.onBackClick();
        outsideAty.finish();
    }
}
