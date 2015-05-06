package org.kymjs.blog.adapter;

import java.util.Collection;

import org.kymjs.blog.R;
import org.kymjs.blog.domain.KymJSBlogEntity;
import org.kymjs.blog.utils.UIHelper;
import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.utils.StringUtils;
import org.kymjs.kjframe.widget.AdapterHolder;
import org.kymjs.kjframe.widget.KJAdapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ImageView;

public class NewBlogAdapter extends KJAdapter<KymJSBlogEntity> {

    private final KJBitmap kjb = new KJBitmap();

    public NewBlogAdapter(AbsListView view, Collection<KymJSBlogEntity> mDatas,
            int itemLayoutId) {
        super(view, mDatas, itemLayoutId);
    }

    @Override
    public void convert(AdapterHolder helper, KymJSBlogEntity item,
            boolean isScrolling) {

        helper.getView(R.id.item_blog_tip_recommend).setVisibility(View.GONE);
        helper.getView(R.id.item_blog_tip_tody).setVisibility(View.VISIBLE);
        ImageView image = helper.getView(R.id.item_blog_img);
        String url = item.getImageUrl();
        if (StringUtils.isEmpty(url)) {
            image.setVisibility(View.GONE);
        } else {
            image.setVisibility(View.VISIBLE);
            onPicClick(image, url);
            if (isScrolling) {
                kjb.displayCacheOrDefult(image, url, R.drawable.pic_bg);
            } else {
                kjb.display(image, url, 480, 420, R.drawable.pic_bg);
            }
        }
        helper.setText(R.id.item_blog_tv_title, item.getTitle());
        helper.setText(R.id.item_blog_tv_description, item.getDescription());
        helper.setText(R.id.item_blog_tv_author, "张涛");
        helper.setText(R.id.item_blog_tv_date, item.getPublished());
    }

    private void onPicClick(View view, final String url) {
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.toGallery(v.getContext(), url);
            }
        });
    }
}
