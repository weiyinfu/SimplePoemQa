package data.index;

import controller.MyApplication;
import controller.MyConfig;
import data.mongo.MyMongo;

import java.nio.file.Path;

public class ResourceConfig {
    //存储在war包之外的资源
    public static Path poemIndex = MyConfig.getPath("res.poem");
    public static Path dictFile = MyConfig.getPath("res.dict");
    public static Path authorImage = MyConfig.getPath("res.authorImage");

    public static void prepare() {//建立索引，建立词库
        MyApplication.developLocal = true;
        MyMongo.init();
        IndexCreator.build();
        WordDict.build();
        WordDict.save();
    }

    public static void main(String[] args) {
        prepare();
    }
}
