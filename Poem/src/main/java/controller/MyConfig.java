package controller;

import haha.Util;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**简单的properties文件解析器
 * */
public class MyConfig {
static Logger logger = Logger.getLogger(MyConfig.class);
static Pattern p = Pattern.compile("\\$\\{.+\\}");
static Map<String, String> config = new HashMap<>();

static {
   config.put("user.home", System.getProperty("user.home"));
}

private static String parseValue(String v) {
   Matcher m = p.matcher(v); StringBuilder builder = new StringBuilder(); int ind = 0; while (ind < v.length()) {
      boolean res = m.find(ind); if (res == false) break; builder.append(v.substring(ind, m.start()));
      String k = m.group(); k = k.substring(2, k.length() - 1); builder.append(config.get(k)); ind = m.end();
   } if (ind < v.length()) {
      builder.append(v.substring(ind));
   } return builder.toString();
}

public static void load(InputStream in) {
   try (Scanner cin = new Scanner(in)) {
      while (cin.hasNext()) {
         String line = cin.nextLine(); if (line.startsWith("#")) continue; int eq = line.indexOf('=');
         if (eq == -1) continue; String k = line.substring(0, eq).trim(); String v = line.substring(eq + 1).trim();
         config.put(k, parseValue(v)); System.out.println(k + "=" + parseValue(v));
      }
   } catch (Exception e) {
      logger.error("", e); System.exit(-1);
   }
}

public static String gets(String k) {
   return config.get(k);
}

public static Path getPath(String k) {
   return Paths.get(config.get(k));
}

public static void loadDefault() {
   load(Util.getResource("/myconfig.properties"));
}

public static void main(String[] args) {
   MyConfig.loadDefault();
}
}
