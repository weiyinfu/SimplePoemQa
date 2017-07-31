package data.po;

import com.alibaba.fastjson.annotation.JSONField;

public class Poem {
    String author;
    String title;
    String preface;
    String content;
    String description;
    String tags;
    String dynasty;
    Integer dynastyOrder;

    String cipai;

    String _id;
    Integer id;

    @JSONField(name = "id")
    public Integer getId() {
        return id;
    }

    @JSONField(name = "id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JSONField(name = "_id")
    public String get_id() {
        return _id;
    }

    @JSONField(name = "_id")
    public void set_id(String _id) {
        this._id = _id;
    }

    public String getDynasty() {
        return dynasty;
    }

    public void setDynasty(String dynasty) {
        this.dynasty = dynasty;
    }

    public Integer getDynastyOrder() {
        return dynastyOrder;
    }

    public void setDynastyOrder(Integer dynastyOrder) {
        this.dynastyOrder = dynastyOrder;
    }

    public String getCipai() {
        return cipai;
    }

    public void setCipai(String cipai) {
        this.cipai = cipai;
    }

    public String getPreface() {
        return preface;
    }

    public void setPreface(String preface) {
        this.preface = preface;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
