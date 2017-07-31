package haha;

import data.po.Poem;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 存储上下文，存储会话
 */
public class Session extends HashMap<String, Object> {
    String openid;
    long lastAccessTime;//此会话上次活跃时间，用于判断是否删除此会话
    ConcurrentLinkedQueue<Runnable> tosay = new ConcurrentLinkedQueue<>();
    Poem poem;
    String sentence;
    String authorName;

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public void setPoem(Poem p) {
        poem = p;
    }

    public Poem getPoem() {
        return poem;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String getSentence() {
        return sentence;
    }

    Session(String openid) {
        this.openid = openid;
        lastAccessTime = System.currentTimeMillis();
    }


}
