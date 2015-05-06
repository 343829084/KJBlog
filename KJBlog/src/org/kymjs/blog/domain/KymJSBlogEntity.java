package org.kymjs.blog.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 博客实体类
 * 
 * @author kymjs
 * 
 */
@XStreamAlias("entry")
public class KymJSBlogEntity {

    @XStreamAlias("title")
    private String title;
    @XStreamAlias("url")
    private String url;
    @XStreamAlias("published")
    private String published;
    @XStreamAlias("author")
    private String author;
    @XStreamAlias("description")
    private String description;

    private String imageUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = "http://blog.kymjs.com/" + url;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

}
