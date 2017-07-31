package haha;
import com.alibaba.fastjson.JSONObject;
import controller.MyClient;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.util.Arrays;
import java.util.Scanner;

/**
 * 图灵机器人
 */
public class Turing implements Gagger {
public String handleMsg(String msg, String userid) {
   try {
      HttpEntity entity = new UrlEncodedFormEntity(Arrays.asList(
              new BasicNameValuePair("key", "d59c41e816154441ace453269ea08dba"),
              new BasicNameValuePair("info", msg),
              new BasicNameValuePair("userid", userid)
      ), "utf8");
      JSONObject json = MyClient.doPostAsJson("http://www.tuling123.com/openapi/api", entity);
      return json.getString("text");
   } catch (Exception e) {
      e.printStackTrace();
   }
   return "图灵机器人跪了！";
}

public static void main(String[] args) {
   Scanner cin = new Scanner(System.in);
   Turing turing = new Turing();
   while (cin.hasNext()) {
      String s = cin.nextLine();
      System.out.println(turing.handleMsg(s, "123456"));
   }
   cin.close();
}
}
