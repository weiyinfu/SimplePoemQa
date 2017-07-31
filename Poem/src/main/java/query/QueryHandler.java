package query;

import controller.MyApplication;
import haha.Session;
import haha.SessionManager;
import org.apache.log4j.Logger;

import java.util.Scanner;

public class QueryHandler {
static Logger logger = Logger.getLogger(QueryHandler.class);
//通过threadlocal避免传参复杂
public static ThreadLocal<String> originalQuery = new ThreadLocal<>();
public static ThreadLocal<String> openid = new ThreadLocal<>();
public static ThreadLocal<Session> session = new ThreadLocal<>();

public String getAns(String q, String openid) {
   QueryHandler.originalQuery.set(q);
   QueryHandler.openid.set(openid);
   QueryHandler.session.set(SessionManager.openSession(openid));

   String question = q.replaceAll("[^\\u4e00-\\u9fa5]", "").trim();
   if (question.length() > 16) {
      return "你说的话有点多，我接受不了。你能不能精简一点！（最好别超过16个字）";
   }
   QueryParser parser = new QueryParser(question);
   Query query = parser.parse();
   String ans = query.getAns(openid);
   if (ans.length() > 500) {
      ans = ans.substring(0, 500) + "......\n太长了";
   }
   ans = ans.trim();
   logger.info(openid + ":" + q);
   return ans;
}

public static void main(String[] args) {
   MyApplication.init(true);
   Scanner cin = new Scanner(System.in);
   QueryHandler handler = new QueryHandler();
   while (cin.hasNext()) {
      String q = cin.nextLine();
      String resp = handler.getAns(q, "weidiao");
      System.out.println(resp);
   }
   cin.close();
   System.out.println("this is the end");
}
}
