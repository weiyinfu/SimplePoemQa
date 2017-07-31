package data.model;

import data.mongo.MyMongo;
import data.po.Author;

import static data.index.WordDict.dict;

public class AuthorData {
public static Author query(String q) {
   String id = dict.get(q).dataId;
   if (id == null) return null;
   Author a = MyMongo.getObjectById("author", id, Author.class);
   return a;
}
}
