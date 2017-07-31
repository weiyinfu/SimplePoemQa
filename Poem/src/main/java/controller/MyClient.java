package controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URI;

/**
 * MyClient，我的客户端，用来发起HTTP请求
 */
public class MyClient {
static Logger logger = Logger.getLogger(MyClient.class);
final static PoolingHttpClientConnectionManager httpClientConnectionManager = new PoolingHttpClientConnectionManager();

static HttpClient getClient() {
   return HttpClients
           .custom()
           .setConnectionManager(httpClientConnectionManager)
           .build();
}

public static HttpEntity doGet(URI uri) {
   try {
      HttpResponse resp = getClient().execute(new HttpGet(uri));
      return resp.getEntity();
   } catch (Exception e) {
      logger.error("", e);
   }
   return null;
}

public static HttpEntity doPost(String url, HttpEntity entity) {
   try {
      HttpPost post = new HttpPost(url);
      post.setEntity(entity);
      HttpResponse resp = getClient().execute(post);
      return resp.getEntity();
   } catch (IOException e) {
      logger.error("", e);
   }
   return null;
}

static JSONObject asJSON(HttpEntity entity) {
   try {
      return JSON.parseObject(EntityUtils.toString(entity, "utf8"));
   } catch (IOException e) {
      logger.error("", e);
   }
   return null;
}

public static JSONObject doGetAsJson(URI url) {
   return asJSON(doGet(url));
}

public static JSONObject doPostAsJson(String url, HttpEntity entity) {
   return asJSON(doPost(url, entity));
}

public static byte[] doGetAsByteArray(URI url) {
   try {
      HttpEntity entity = doGet(url);
      byte[] content = EntityUtils.toByteArray(entity);
      return content;
   } catch (Exception e) {
      logger.error("", e);
   }
   return null;
}
}
