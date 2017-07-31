package data.index;

import com.mongodb.client.MongoCursor;
import controller.MyApplication;
import data.mongo.MyMongo;
import haha.Util;
import org.apache.log4j.Logger;
import org.bson.Document;
import query.Term;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
/**词典<br/>
 * 每行一项，每一项包括：word，type，synonym，dataId
 * */
public class WordDict {
public static class Word {
   public String word;
   public String type;//词语类型
   public String synonym;//词语同义词
   public String dataId;//对应数据库中的实体

   Word(String word, String type, String synonym, String dataId) {
      this.word = word;
      this.type = type;
      this.synonym = synonym;
      this.dataId = dataId;
   }

   @Override
   public String toString() {
      return word + " " + synonym + " " + type;
   }
}

public static final ConcurrentHashMap<String, Word> dict = new ConcurrentHashMap<>();
static final Logger logger = Logger.getLogger(WordDict.class);

//读取mongodb
static void build(String collection, String termType) {
   long begTime = System.currentTimeMillis();
   logger.info(collection + " is building ");
   MongoCursor<Document> it = MyMongo.getCollection(collection).find().iterator();
   while (it.hasNext()) {
      Document doc = it.next();
      String word = doc.getString("name");
      String id = doc.getObjectId("_id").toString();
      dict.put(word, new Word(word, termType, word, id));
   }
   it.close();
   logger.info("创建" + collection + "索引花费时间" + (System.currentTimeMillis() - begTime) / 1000 + " 秒");
}

//加载用户词典
static void loadUserDict() {
   Scanner cin = new Scanner(Util.getResource("/细节.txt"));
   String nowWord = null, nowType = null;
   while (cin.hasNext()) {
      String line = cin.nextLine().trim();
      if (line.length() == 0) continue;
      if (line.startsWith("##")) {
         String[] wordType = line.replace("#", "").split("\\s+");
         nowWord = wordType[0];
         nowType = wordType[1];
         dict.put(nowWord, new Word(nowWord, nowType, nowWord, null));
      } else {
         dict.put(line, new Word(line, nowType, nowWord, null));
      }
   }
   cin.close();
}

public static void build() {
   build("author", Term.AUTHOR);
   build("cipai", Term.CIPAI);
   build("dynasty", Term.DYNASTY);
}

public static void load() {
   try (Scanner dictCin = new Scanner(Files.newBufferedReader(ResourceConfig.dictFile));) {
      while (dictCin.hasNext()) {
         String word = dictCin.next();
         dict.put(word, new Word(word, dictCin.next(), dictCin.next(), dictCin.next()));
      }
      loadUserDict();
   } catch (Exception e) {
      logger.error("", e);
   }
}

public static void save() {
   try (PrintWriter cout = new PrintWriter(Files.newBufferedWriter(ResourceConfig.dictFile))) {
      dict.values().forEach(x -> {
         cout.write(x.word + " ");
         cout.write(x.type + " ");
         cout.write(x.synonym + " ");
         cout.write(x.dataId + " ");
         cout.write("\n");
      });
   } catch (Exception e) {
      logger.error("", e);
   }
}

public static Word getWord(String wordString) {
   while (true) {
      Word word = dict.get(wordString);
      if (word == null) return null;
      if (word.word.equals(word.synonym)) return word;
      else wordString = word.synonym;
   }
}

static void debug() {
   load();
   Scanner cin = new Scanner(System.in);
   while (cin.hasNext()) {
      String line = cin.nextLine();
      System.out.println(dict.get(line));
   }
}

public static void main(String[] args) throws IOException {
   MyApplication.init(true);
   //   build();
   //   save();
   load();
   debug();
}
}
