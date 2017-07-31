package query;

import exception.SentenceNotFoundException;
import haha.Util;
import org.apache.log4j.Logger;

/**
 * 诗歌内容处理器，包括上句下句
 */
public class PoemContent {
//定义位置信息
class PosInfo {
   //两个都是闭区间
   int begIndex, endIndex;//部分内容的起始位置
   int begComma, endComma;//整句的起始位置
}

static final Logger logger = Logger.getLogger(PoemContent.class);
static final String sep = "；。？！";//分隔符
static final String halfSep = "，、,";//半句分隔符
static final String allSep = sep + halfSep;//全部分隔符

/**
 * 一首诗的内容有三种形式：<br/>
 * 全部内容<br/>
 * 去除注释括号后的内容<br/>
 * 去除一切非中文字符后的内容<br/>
 */
String wholeContent;//全部内容
String noBrace;//去除括号之后的内容，没有注释
String chinese;//去除一切非中文字符之后的内容
String[] sentences;//句子列表，延迟加载，延迟处理，用到时再初始化这个数组

//获取sentence在所在句中的比例
public double getRatio(String sentence) {
   try {
      PosInfo posInfo = getPosInfo(sentence);
      double ans = (double) (posInfo.endIndex - posInfo.begIndex + 1) / (posInfo.endComma - posInfo.begComma + 1);
      return ans;
   } catch (SentenceNotFoundException e) {
      logger.error("", e);
   }
   return 0;
}
/**判断两个字符串的汉语部分是否完全相等
 * */
boolean chineseEqual(String s, String ss) {
   int i = 0, j = 0;
   while (i < s.length() && j < ss.length()) {
      if (Util.isChinese(s.charAt(i)) == false) i++;
      if (Util.isChinese(ss.charAt(j)) == false) j++;
      if (s.charAt(i) == ss.charAt(j)) {
         i++;
         j++;
      } else {
         return false;
      }
   }
   for (; i < s.length(); i++) {
      if (Util.isChinese(s.charAt(i))) return false;
   }
   for (; i < ss.length(); i++) {
      if (Util.isChinese(ss.charAt(j))) return false;
   }
   return true;
}

//给定句子和相对位置，返回某个句子
String getNearSentence(String sentence, int relativePosition) {
   try {
      PosInfo posInfo = getPosInfo(sentence);
      if (sentences == null) {
         sentences = noBrace.split("[" + sep + "]");
      }
      int begSentence = -1, endSentence = -1;
      int ind = 0;//在全部内容中的下标
      for (int i = 0; i < sentences.length; i++) {
         ind += sentences[i].length() + 1;
         if (begSentence == -1 && ind > posInfo.begIndex) {
            begSentence = i;
         }
         if (ind > posInfo.endIndex) {
            endSentence = i;
            break;
         }
      }
      if (relativePosition == -1) {
         if (begSentence == 0) {
            return null;
         } else {
            return sentences[begSentence - 1];
         }
      } else if (relativePosition == 1) {
         if (endSentence == sentences.length - 1) {
            return null;
         } else {
            return sentences[endSentence + 1];
         }
      } else if (relativePosition == 0) {
         String ans = sentences[begSentence];
         for (int i = begSentence + 1; i <= endSentence; i++) ans += "\n" + sentences[i];
         if (chineseEqual(ans, sentence)) {//如果一样，就返回全文
            return wholeContent;
         } else {
            return ans;
         }
      } else {
         String errInfo = "PoemContent里面的relative position不可能是这个值：" + relativePosition;
         logger.error(errInfo);
      }
   } catch (SentenceNotFoundException e) {
      logger.error("", e);
   }
   return null;
}

//定位sentence在全文中的位置，返回PosInfo类型的对象
PosInfo getPosInfo(String sentence) throws SentenceNotFoundException {
   sentence = sentence.replaceAll("[^\\u4e00-\\u9fa5]", "");
   PosInfo ans = new PosInfo();
   //在纯中文中寻找句子下标
   int beg = chinese.indexOf(sentence);
   if (beg == -1) {
      throw new SentenceNotFoundException(sentence);
   }
   int end = beg + sentence.length() - 1;//因为是闭区间，所以减一
   //汉字的个数
   int chineseCnt = 0;
   ans.begIndex = 0;//nobrace中的起始下标
   while (true) {
      if (Util.isChinese(noBrace.charAt(ans.begIndex))) {
         if (chineseCnt == beg) break;
         chineseCnt++;
      }
      ans.begIndex++;
   }
   ans.endIndex = ans.begIndex;
   while (chineseCnt < end) {
      if (Util.isChinese(noBrace.charAt(ans.endIndex))) {
         chineseCnt++;
      }
      ans.endIndex++;
   }
   ans.begComma = ans.begIndex;//句子在nobrace中的起始位置
   while (ans.begComma > 0 && Util.isChinese(noBrace.charAt(ans.begComma - 1))) {
      ans.begComma--;
   }
   ans.endComma = ans.endIndex;//句子在nobrace中的结束位置
   while (ans.endComma < noBrace.length() - 1 && Util.isChinese(noBrace.charAt(ans.endComma + 1))) {
      ans.endComma++;
   }
   //   System.out.println("查询" + noBrace.substring(ans.begIndex, ans.endIndex + 1));
   //   System.out.println("结果" + noBrace.substring(ans.begComma, ans.endComma + 1));
   return ans;
}

public PoemContent(String content) {
   this.wholeContent = content;
   this.noBrace = content.replaceAll("[\\(（].*?[\\)）]", "").replaceAll("\n", "");
   //去除括号之后的诗歌内容
   //去除非中文字符之后的诗歌内容
   this.chinese = noBrace.replaceAll("[^\\u4e00-\\u9fa5]", "");
}

public static void main(String[] args) {
   String content = "北国风光，千里冰封，万里雪飘。"
           + "\n望长城内外，惟余莽莽；大河上下，顿失滔滔。(余 通：馀)"
           + "\n山舞银蛇，原驰蜡象，欲与天公试比高。(原驰 原作：原驱)"
           + "\n须晴日，看红装素裹，分外妖娆。(红装 一作：银装)"
           + "\n\n江山如此多娇，引无数英雄竞折腰。"
           + "\n惜秦皇汉武，略输文采；唐宗宋祖，稍逊风骚。"
           + "\n一代天骄，成吉思汗，只识弯弓射大雕。"
           + "\n俱往矣，数风流人物，还看今朝。";
   PoemContent p = new PoemContent(content);
   //   System.out.println(p.noBrace);
   String s = p.getNearSentence("大河上下，顿失滔滔", 0);
   System.out.println(s);
}
}
