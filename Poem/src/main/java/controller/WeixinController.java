package controller;

import haha.SessionManager;
import haha.Util;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import query.HowQuery;
import query.QueryHandler;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.stream.Collectors;

@Controller
public class WeixinController {
private final Logger logger = Logger.getLogger(this.getClass());

@ResponseBody
@RequestMapping(value = "check", method = RequestMethod.GET)
String get(String signature, String timestamp, String nonce, String echostr) throws Exception {
   return signature.equals(
           Util.toHexString(MessageDigest.getInstance("SHA1").digest(Arrays.asList(timestamp, nonce, WeixinConfig.token).stream().sorted().collect(Collectors.joining()).getBytes("utf8")))) ? echostr : null;
}

@RequestMapping(value = "check", method = RequestMethod.POST)
String post(HttpServletRequest req, ModelMap model) throws Exception {
   Document doc = new SAXReader().read(req.getReader());
   String FromUserName = doc.selectSingleNode("//FromUserName").getText();
   String ToUserName = doc.selectSingleNode("//ToUserName").getText();
   String CreateTime = doc.selectSingleNode("//CreateTime").getText();
   String MsgType = doc.selectSingleNode("//MsgType").getText();
   model.addAttribute("ToUserName", FromUserName);
   model.addAttribute("FromUserName", ToUserName);
   model.addAttribute("CreateTime", System.currentTimeMillis());
   switch (MsgType) {
      case "text": {
         String Content = doc.selectSingleNode("//Content").getText();
         model.addAttribute("Content", reply(Content, FromUserName));
         return "text";
      }
      case "event": {
         model.addAttribute("Content", HowQuery.howToUse);
         return "text";
      }
      default: {
         model.addAttribute("Content", "一生醉心文字，不懂其它");
         SessionManager.submit(FromUserName, new Runnable() {
            @Override
            public void run() {
               try {
                  WeixinClient.sendTextMessage(FromUserName, HowQuery.howToUse);
               } catch (IOException e) {
                  e.printStackTrace();
               }
            }
         });
      }
      return "text";
   }

}

String reply(String q, String openid) {
   QueryHandler queryHandler = new QueryHandler();
   String s = queryHandler.getAns(q, openid);
   return s;
}

//用于网页请求
@RequestMapping("haha")
@ResponseBody
String haha(String q) {
   return reply(q, "wyf");
}
}
