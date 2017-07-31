package data.model;

import data.mongo.MyMongo;
import data.po.Cipai;

import static data.index.WordDict.dict;

public class CipaiData {
public static Cipai query(String q) {
   String id = dict.get(q).dataId;
   if (id == null) return null;
   Cipai a = MyMongo.getObjectById("cipai", id, Cipai.class);
   return a;
}
}
