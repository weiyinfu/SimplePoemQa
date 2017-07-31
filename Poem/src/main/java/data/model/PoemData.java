package data.model;

import controller.MyApplication;
import data.index.ResourceConfig;
import data.mongo.MyMongo;
import data.po.Poem;
import org.apache.log4j.Logger;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PoemData {
    static IndexSearcher indexSearcher;
    static Logger logger = Logger.getLogger(PoemData.class);

    public static void loadIndex() {
        try {
            logger.info("正在加载poem-index");
            long beg = System.currentTimeMillis();
            IndexReader indexReader = DirectoryReader.open(FSDirectory.open(ResourceConfig.poemIndex));
            indexSearcher = new IndexSearcher(indexReader);
            long end = System.currentTimeMillis();
            logger.info("加载poem-index完成，共用时" + (end - beg) / 1000.0);
        } catch (IOException e) {
            logger.error("", e);
        }
    }


    //返回查询到的诗歌id列表
    public static ScoreDoc[] basicQuery(String author, String title, String sentence, int cnt) {
        try {
            BooleanQuery.Builder builder = new BooleanQuery.Builder();
            if (author != null) {
                builder.add(new TermQuery(new Term("author", author)), BooleanClause.Occur.MUST);
            }
            if (title != null) {
                builder.add(new PhraseQuery("title", title.split("")), BooleanClause.Occur.MUST);
            }
            if (sentence != null) {
                builder.add(new PhraseQuery(0, "content", sentence.split("")), BooleanClause.Occur.MUST);
            }
            Query query = builder.build();
            if (cnt == -1) cnt = 20;
            TopDocs topDocs = indexSearcher.search(query, cnt);
            return topDocs.scoreDocs;
        } catch (Exception e) {
            logger.error("", e);
        }
        return null;
    }

    static List<Poem> scoreDocsToList(ScoreDoc[] docs) {
        List<Poem> poems = new ArrayList<>(docs.length);
        for (int i = 0; i < docs.length; i++) {
            poems.add(docToPoem(docs[i]));
        }
        return poems;
    }

    static Poem docToPoem(ScoreDoc doc) {
        Poem p = null;
        try {
            p = MyMongo.getObjectById("poem", indexSearcher.doc(doc.doc).get("_id"), Poem.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return p;
    }

    public static boolean exists(String author, String title, String sentence) {
        return basicQuery(author, title, sentence, 1).length > 0;
    }

    public static boolean existsTitle(String title) {
        return exists(null, title, null);
    }

    public static boolean existsSentence(String sentence) {
        return exists(null, null, sentence);
    }

    public static Poem queryOne(String author, String title, String sentence) {
        ScoreDoc[] docs = basicQuery(author, title, sentence, 1);
        if (docs.length == 0) return null;
        return docToPoem(docs[0]);
    }

    public static List<Poem> queryList(String author, String title, String sentence) {
        ScoreDoc[] docs = basicQuery(author, title, sentence, 10);
        return scoreDocsToList(docs);
    }

    public static Poem queryByTitle(String q) {
        return queryOne(null, q, null);
    }

    public static List<Poem> queryListByTitle(String q) {
        return queryList(null, q, null);
    }

    public static Poem queryBySentence(String q) {
        return queryOne(null, null, q);
    }

    public static List<Poem> queryListBySentence(String q) {
        return queryList(null, null, q);
    }

    public static void main(String[] args) {
        MyApplication.init(true);
        Scanner cin = new Scanner(System.in);
        while (cin.hasNext()) {
            String q = cin.nextLine();
            Poem p = PoemData.queryByTitle(q);
            System.out.println(p.getContent());
        }
        cin.close();
    }
}
