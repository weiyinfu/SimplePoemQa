package controller;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URI;

/**
 * WeixinClient类，微信客户端，用来向微信服务器发送消息
 * <p>
 * get请求分为：带accessToken和不带accessToken</p>
 * <p>
 * post请求分为：带重试和不带重试,post请求一定是带accessToken的</p>
 * <p>
 * 分带retry和不带retry请求的原因在于：accessToken失效之后需要更换accessToken再进行请求
 * </p>
 */
public class WeixinClient {
static String access_token = getAccessToken();
final static String baseUrl = "https://api.weixin.qq.com/cgi-bin/";
final static Logger logger = Logger.getLogger(WeixinClient.class);

private static String getAccessToken() {
   try {
      URI uri = new URIBuilder(baseUrl + "token")
              .addParameter("grant_type", "client_credential")
              .addParameter("appid", WeixinConfig.appId)
              .addParameter("secret", WeixinConfig.appsecret)
              .build();
      return MyClient.doGetAsJson(uri).getString("access_token");
   } catch (Exception e) {
      logger.error("", e);
   }
   return null;
}

public static JSONObject doPostWithRetry(String url, HttpEntity entity) {
   JSONObject res = doPostWithoutRetry(url, entity);
   Integer errcode = res.getInteger("errcode");
   if (errcode != null && errcode != 0) {
      access_token = getAccessToken();
      res = doPostWithoutRetry(url, entity);
   }
   return res;
}

private static JSONObject doPostWithoutRetry(String url, HttpEntity entity) {
   url = url + (url.contains("?") ? "&" : "?") + "access_token=" + access_token;
   return MyClient.doPostAsJson(url, entity);
}

public static void sendMessage(String openid, JSONObject msgContent, String msgtype) {
   if (MyApplication.developLocal) {
      logger.info("sendMessage");
      logger.info(msgContent);
      return;
   }
   String url = baseUrl + "message/custom/send";
   JSONObject msg = new JSONObject();
   msg.put("touser", openid);
   msg.put("msgtype", msgtype);
   msg.put(msgtype, msgContent);
   doPostWithRetry(url, new StringEntity(msg.toJSONString(), "utf8"));
}

public static void sendTextMessage(String openid, String content) throws IOException {
   JSONObject text = new JSONObject();
   if (content.length() > 500) {
      content = content.substring(0, 400) + "......有点长";
   }
   text.put("content", content);
   sendMessage(openid, text, "text");
}

public static void sendImageMessage(String openid, String mediaId) {
   JSONObject image = new JSONObject();
   image.put("media_id", mediaId);
   sendMessage(openid, image, "image");
}

public static void sendVoiceMessage(String openid, String mediaId) {
   JSONObject voice = new JSONObject();
   voice.put("media_id", mediaId);
   sendMessage(openid, voice, "voice");
}

//image,voice,video,thumb
public static String sendMaterial(byte[] data, String type) {
   String url = baseUrl + "media/upload?type=" + type;
   HttpEntity entity = MultipartEntityBuilder.create().addBinaryBody("media", data, ContentType.MULTIPART_FORM_DATA, "img.jpg").build();
   JSONObject object = doPostWithRetry(url, entity);
   return object.getString("media_id");
}
}
