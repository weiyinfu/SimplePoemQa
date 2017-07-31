package query;
import haha.Gagger;
import haha.Qingyunke;
import haha.Turing;

public class GagHandler {
//因为有上下文的存在，所以必须让不同的机器人处理不同的用户
static Gagger[] gaggers = {new Qingyunke(), new Turing()};


static String handleMsg(String msg, String userid) {
   if (Math.random() < 0.1) {
      return HowQuery.howToUse;
   }
   String ans=gaggers[1].handleMsg(msg, userid);
   ans=ans.replaceAll("图灵机器人","诗词小强");
   return ans;
}
}
