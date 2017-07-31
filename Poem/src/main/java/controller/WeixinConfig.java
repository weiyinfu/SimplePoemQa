package controller;

/**
 * 设置微信
 */
public class WeixinConfig {
    final static String appId = MyConfig.gets("weixin.appId");
    final static String appsecret = MyConfig.gets("weixin.appsecret");
    final static String token = MyConfig.gets("weixin.token");
}
