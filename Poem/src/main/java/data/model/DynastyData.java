package data.model;

import data.mongo.MyMongo;
import data.po.Dynasty;

import static data.index.WordDict.dict;

public class DynastyData {
public static Dynasty query(String q) {
   String id = dict.get(q).dataId;
   if (id == null) return null;
   else {
      Dynasty a = MyMongo.getObjectById("dynasty", id, Dynasty.class);
      return a;
   }
}
}
