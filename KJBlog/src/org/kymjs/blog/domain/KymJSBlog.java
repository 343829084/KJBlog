package org.kymjs.blog.domain;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 以后直接从feed获取
 * 
 * @author kymjs
 */
@XStreamAlias("feed")
public class KymJSBlog {
    @XStreamAlias("title")
    private String blogTitle;
    @XStreamAlias("entry")
    private List<KymJSBlogEntity> list;

    public String getBlogTitle() {
        return blogTitle;
    }

    public void setBlogTitle(String blogTitle) {
        this.blogTitle = blogTitle;
    }

    public List<KymJSBlogEntity> getList() {
        return list;
    }

    public void setList(List<KymJSBlogEntity> list) {
        this.list = list;
    }
}
