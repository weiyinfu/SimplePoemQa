package data.index;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import data.mongo.MyMongo;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
/**创建索引
 * */
public class IndexCreator {
    public static Analyzer analyzer = new StandardAnalyzer(); // new SmartChineseAnalyzer(true);
    static Logger logger = Logger.getLogger(IndexCreator.class);

    private static void buildPoemIndex() {
        logger.info("start indexing poem");
        long startTime = System.currentTimeMillis();
        try {
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            Directory directory = FSDirectory.open(ResourceConfig.poemIndex);
            IndexWriter writer = new IndexWriter(directory, config);
            FindIterable<org.bson.Document> res = MyMongo.getCollection("poem").find();
            MongoCursor<org.bson.Document> it = res.iterator();
            int cnt = 0;
            while (it.hasNext()) {
                cnt += 1;
                if (cnt % 1000 == 0) System.out.println("已经处理" + cnt + "首诗");
                org.bson.Document document = it.next();
                Document doc = new Document();
                doc.add(new StoredField("_id", document.getObjectId("_id").toString()));
                doc.add(new TextField("title", document.getString("title"), Field.Store.NO));
                doc.add(new StringField("author", document.getString("author"), Field.Store.NO));
                doc.add(new TextField("content", document.getString("content"), Field.Store.NO));
                writer.addDocument(doc);
            }
            it.close();
            writer.close();
        } catch (Exception e) {
            logger.error("", e);
        }
        long endTime = System.currentTimeMillis();
        logger.info("建立索引用时 " + (endTime - startTime) / 1000.0 + " 秒");
    }

    public static void build() {
        buildPoemIndex();
    }

    public static void main(String[] args) {
        build();
    }
}
