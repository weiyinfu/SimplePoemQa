package query;

import com.alibaba.fastjson.JSONObject;
import controller.WeixinClient;
import data.model.PoemData;
import data.mongo.MyMongo;
import data.po.Poem;
import haha.Session;
import haha.SessionManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

public class PoemQueryHandler {
static Logger logger = Logger.getLogger(PoemQueryHandler.class);
String openid;
Session session;
PoemQuery q;

void explain(Poem p) {
   SessionManager.submit(openid, () -> {
      JSONObject json = MyMongo.getJSONById("poem", p.get_id());
      json.keySet().stream().filter(k -> !"_id id dynasty author preface content title related dynasty_order".contains(k)).forEach(k -> {
         try {
            String s = json.getString(k);
            if (k.equals("description")) k = "注释及译文";
            if (k.equals("cipai")) k = "词牌名";
            WeixinClient.sendTextMessage(openid, k + "\n" + s);
         } catch (IOException e) {
            logger.error("", e);
         }
      });
   });
}

String handleMisc(Poem p, String type) {
   JSONObject json = MyMongo.getJSONById("poem", p.get_id());
   String ans = "我也不知道";
   for (String i : json.keySet()) {
      if (i.equals(type)) {
         ans = json.getString(i);
      }
      if (ans.equals("我也不知道") && i.contains(type)) {
         ans = json.getString(i);
      }
   }
   return ans;
}

String handleDetail(Poem p, String sentence) {
   PoemContent poemContent = null;
   if (sentence != null && p != null) {
      poemContent = new PoemContent(p.getContent());
   }
   switch (q.getDetail()) {
      case "上句":
         if (poemContent == null) return "哪句诗的上句？";
         else {
            String prevSentence = poemContent.getNearSentence(sentence, -1);
            if (prevSentence == null) {
               return "这已经是第一句了";
            } else {
               session.setSentence(prevSentence);
               return prevSentence;
            }
         }
      case "下句":
         if (poemContent == null) return "哪句诗的下句";
         else {
            String nextSentence = poemContent.getNearSentence(sentence, 1);
            if (nextSentence == null) {
               return "这已经是最后一句了";
            } else {
               session.setSentence(nextSentence);
               return nextSentence;
            }
         }
      case "全文":
         if (p == null) return "哪首诗的全文?";
         else {
            StringBuilder builder = new StringBuilder();
            builder.append(String.format("《%s》", p.getTitle()));
            if (p.getDynasty() != null && p.getDynasty().isEmpty() == false) {
               builder.append(String.format("【%s】", p.getDynasty()));
            }
            builder.append(p.getAuthor())
                    .append("\n")
                    .append(p.getContent());
            return builder.toString();
         }
      case "题目":
         if (p == null) return "哪首诗的题目?";
         else return p.getTitle();
      case "作者": {
         if (p == null) return "哪首诗的作者";
         AuthorQuery authorQuery = new AuthorQuery();
         authorQuery.setName(p.getAuthor());
         return authorQuery.getAns(openid);
      }
      case "解释": {
         if (p == null) return "解释哪首诗?";
         explain(p);
         return String.format("《%s》作者%s", p.getTitle(), p.getAuthor());
      }
      case "词牌": {
         if (p == null) return "哪首诗的词牌?";
         if (p.getCipai() == null || p.getCipai().isEmpty()) return "这首诗没有词牌";
         else return p.getCipai();
      }
      case "背景": {
         if (p == null) return "哪首诗的背景？";
         return handleMisc(p, "背景");
      }
      case "注释": {
         if (p == null) return "哪首诗的注释？";
         return handleMisc(p, "注释");
      }
      case "译文": {
         if (p == null) return "哪首诗的译文？";
         return handleMisc(p, "译文");
      }
   }
   return "I'm over ! Where's the developer!";
}

String handleSentence(Poem p) {
   if (p == null) return "没有这首诗";
   session.setPoem(p);
   if (q.getSentence() != null) {
      session.setSentence(q.getSentence());
   }
   if (q.getDetail() != null) {
      return handleDetail(p, q.getSentence());
   } else {
      PoemContent poemContent = new PoemContent(p.getContent());
      return poemContent.getNearSentence(q.getSentence(), 0);
   }
}

public String getAns(String openid, PoemQuery q) {
   this.openid = openid;
   this.session = QueryHandler.session.get();
   this.q = q;
   //如果三项都为null，那么肯定是细节了
   if (q.getAuthor() == null && q.getSentence() == null && q.getTitle() == null) {
      if (q.getDetail() == null) {
         return GagHandler.handleMsg(QueryHandler.originalQuery.get(), QueryHandler.openid.get());
      }
      return handleDetail(session.getPoem(), session.getSentence());
   }
   //查询题目
   if (q.getTitle() != null) {
      Poem p = PoemData.queryOne(q.getAuthor(), q.getTitle(), q.getSentence());
      if (p == null) return "没有这首诗";
      session.setPoem(p);
      session.setSentence(q.getSentence());
      if (q.getDetail() == null) {
         q.setDetail("全文");
      }
      return handleDetail(p, null);
   }
   if (q.getAuthor() == null && q.getTitle() == null && q.getSentence() != null) {
      List<Poem> poems = PoemData.queryListBySentence(q.getSentence());
      session.setSentence(q.getSentence());
      session.setPoem(null);
      if (poems.size() == 0) {
         session.setPoem(null);
         return "没有包含“" + q.getSentence() + "”的诗";
      } else if (poems.size() == 1) {
         return handleSentence(poems.get(0));
      } else if (poems.size() > 1) {
         HashSet<String> authors = new HashSet<>(poems.size());
         for (int i = 0; i < poems.size(); i++) {
            String author = poems.get(i).getAuthor();
            if (authors.contains(author)) {
               poems.set(i, null);
            } else {
               authors.add(author);
            }
         }
         if (authors.size() == 1) {
            Poem p = PoemData.queryOne(q.getAuthor(), q.getTitle(), q.getSentence());
            return handleSentence(p);
         } else {
            int earliest = 0;
            System.out.println("========" + poems.get(0).getTitle() + " " + poems.get(0).getDynasty() + " " + poems.get(0).getDynastyOrder());
            for (int i = 1; i < poems.size(); i++) {
               if (poems.get(i) == null) continue;
               System.out.println("========" + poems.get(i).getTitle() + " " + poems.get(i).getDynasty() + " " + poems.get(i).getDynastyOrder());
               if (poems.get(i).getDynastyOrder() < poems.get(earliest).getDynastyOrder()) {
                  earliest = i;
               }
            }
            Poem earliestPoem = poems.get(earliest);
            poems.set(earliest, null);
            StringBuilder builder = new StringBuilder();
            builder.append(String.format("这句话最早见于%s《%s》\n%s\n",
                    earliestPoem.getAuthor(),
                    earliestPoem.getTitle(),
                    handleSentence(earliestPoem).trim())
            );
            builder.append("\n下面诗中也含有这句话\n");
            poems.stream().filter(p -> p != null)
                    .forEach(p -> builder.append(String.format("《%s》%s\n", p.getTitle(), p.getAuthor())));
            builder.append("\n不知你说的是哪一首？查询“诗句作者名”试试。");
            return builder.toString();
         }
      }
      return "我跪了，快去找程序员";
   }
   if (q.getSentence() != null) {
      Poem p = PoemData.queryOne(q.getAuthor(), q.getTitle(), q.getSentence());
      return handleSentence(p);
   }
   return GagHandler.handleMsg(QueryHandler.originalQuery.get(), QueryHandler.openid.get());
}
}
