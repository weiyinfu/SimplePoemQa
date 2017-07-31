package data.mongo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import controller.MyConfig;
import org.apache.log4j.Logger;
import org.bson.BsonDocument;
import org.bson.BsonObjectId;
import org.bson.Document;
import org.bson.types.ObjectId;

public class MyMongo {
    static MongoDatabase db;
    static Logger logger = Logger.getLogger(MyMongo.class);

    public static void init() {
        logger.info("正在初始化MyMongo");
        MongoClientOptions.Builder builder =
                new MongoClientOptions.Builder()
                        .connectionsPerHost(100)// 与目标数据库可以建立的最大链接数
                        .connectTimeout(1000 * 60 * 20)// 与数据库建立链接的超时时间
                        .maxWaitTime(1000 * 60 * 20)// 一个线程成功获取到一个可用数据库之前的最大等待时间
                        .threadsAllowedToBlockForConnectionMultiplier(100)
                        .maxConnectionIdleTime(0)
                        .maxConnectionLifeTime(0)
                        .socketTimeout(1000 * 60)
                        .socketKeepAlive(false);

        MongoClient mongoClient = new MongoClient(new MongoClientURI(MyConfig.gets("mongo.url"), builder));
        db = mongoClient.getDatabase("poem");
        logger.info("获取mongo结束");
    }

    public static MongoCollection<Document> getCollection(String collectionName) {
        return db.getCollection(collectionName);
    }

    public static <T> T getObjectById(String collection, String id, Class<T> type) {
        return JSON.parseObject(gets(collection, id), type);
    }

    public static JSONObject getJSONById(String collection, String id) {
        return JSON.parseObject(gets(collection, id));
    }

    static String gets(String collection, String id) {
        FindIterable<Document> res = MyMongo.getCollection(collection).find(new BsonDocument("_id", new BsonObjectId(new ObjectId(id))));
        Document doc = res.first();
        doc.put("_id", doc.get("_id").toString());
        if (doc == null) return null;
        String json = JSON.toJSONString(doc);
        return json;
    }
}
