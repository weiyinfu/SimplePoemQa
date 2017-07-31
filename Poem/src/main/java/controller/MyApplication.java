package controller;

import data.index.IndexCreator;
import data.index.ResourceConfig;
import data.index.WordDict;
import data.model.PoemData;
import data.mongo.MyMongo;
import haha.SessionManager;
import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

import java.nio.file.Files;

@SpringBootApplication
public class MyApplication extends SpringBootServletInitializer {
static Logger logger = Logger.getLogger(MyApplication.class);
//表示当前程序是否是作为单独springBoot程序运行，还是放在tomcat中运行
public static boolean developLocal;

public static void init(boolean developLocal) {
   MyApplication.developLocal = developLocal;
   MyConfig.loadDefault();//加载我的配置文件
   logger.info("初始化MongoDB");
   MyMongo.init();
   if (Files.exists(ResourceConfig.poemIndex) == false) {
      logger.info("没有诗歌索引，需要重新建立，比较耗时...");
      IndexCreator.build();
      logger.info("诗歌索引创建完毕");
   }
   logger.info("加载诗歌索引");
   PoemData.loadIndex();
   logger.info("加载词典");
   if (Files.exists(ResourceConfig.dictFile) == false) {
      logger.info("没有词典，需要重新建立，比较耗时...");
      WordDict.build();
      WordDict.save();
      logger.info("词典创建完毕");
   }
   WordDict.load();
   logger.info("加载词典完毕");
   logger.info("初始化Session管理器监听消息队列");
   new Thread(() -> {
      SessionManager.listenBlockingQueue();
   }).start();
   logger.info("初始化Session管理器监听删除session队列");
   new Thread(() -> {
      SessionManager.listenDeleteSession();
   }).start();
}

@Override
protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
   init(false);//以war包部署，非调试模式
   return builder.sources(MyApplication.class);
}

public static void main(String[] args) {
   init(true);//本地运行，调试模式
   SpringApplication.run(MyApplication.class, args);
}
}
