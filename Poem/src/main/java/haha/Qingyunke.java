package haha;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import controller.MyClient;
import org.apache.http.HttpEntity;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;

import java.util.Scanner;

public class Qingyunke implements Gagger {
public String handleMsg(String msg, String userid) {
   try {
      HttpEntity resp = MyClient.doGet(new URIBuilder("http://api.qingyunke.com/api.php")
              .addParameter("key", "free")
              .addParameter("msg", msg)
              .addParameter("appid", "0").build());
      JSONObject json = JSON.parseObject(EntityUtils.toString(resp));
      return json.getString("content");
   } catch (Exception e) {
      e.printStackTrace();
   }
   return "青云客机器人跪了！";
}

public static void main(String[] args) {
   Scanner cin = new Scanner(System.in);
   Qingyunke robot = new Qingyunke();
   while (cin.hasNext()) {
      String s = cin.nextLine();
      System.out.println(robot.handleMsg(s, "0"));
   }
   cin.close();
}

}
