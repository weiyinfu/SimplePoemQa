package query;

import controller.WeixinClient;
import data.index.ResourceConfig;
import data.model.AuthorData;
import data.po.Author;
import haha.SessionManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;

public class AuthorQuery implements Query {
Logger logger = Logger.getLogger(AuthorQuery.class);
String name;

public String getName() {
   return name;
}

public void setName(String name) {
   this.name = name;
}

@Override
public String getAns(String openid) {
   System.out.println(name+"authorquery.getAns()");
   Author author = AuthorData.query(name);
   if (author == null) {
      return "没有收录" + name + "的信息";
   }
   String media = author.getImg();
   if (media != null) {
      SessionManager.submit(openid, () -> {
         String mediaId = null;
         try {
            mediaId = WeixinClient.sendMaterial(Files.readAllBytes(ResourceConfig.authorImage.resolve(media)), "image");
         } catch (IOException e) {
            logger.error("发送诗人头像失败", e);
            e.printStackTrace();
         }
         WeixinClient.sendImageMessage(openid, mediaId);
      });
   }
   return author.getDescription();
}
}
