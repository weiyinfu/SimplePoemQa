package query;

import controller.MyApplication;
import data.model.PoemData;
import data.po.Poem;
import haha.Util;
import org.apache.log4j.Logger;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

//TermList打分器
public class TermListJudger {
static final Logger logger = Logger.getLogger(TermListJudger.class);
static List<String> reList = new ArrayList<>();//正则表达式列表
TermList termList;
String termListStr;
double queryLength;

static {
   try {
      Scanner cin = new Scanner(new InputStreamReader(Util.getResource("/re.txt")));
      while (cin.hasNext()) {
         reList.add(cin.nextLine());
      }
      cin.close();
   } catch (Exception e) {
      logger.error("", e);
   }
}

double termScore() {//每一项的平均得分
   double score = 0;
   for (Term i : termList) {
      score += i.getScore();
   }
   return score / termList.size();
}

double lengthScore() {//长度越长越好，也就是查询串的利用率
   double score = 0;
   for (Term i : termList) {
      score += i.getEndIndex() - i.getBegIndex() + 1;
   }
   return score / queryLength;
}

double poemSquare() {//说的是不是同一首诗，求诗的方差
   HashSet<String> poems = new HashSet<>();
   int cnt = 0;
   for (Term i : termList) {
      if (i.getType().equals(Term.POEM_TITLE) || i.getType().equals(Term.POEM_SENTENCE)) {
         if (i.getPoem() != null) {
            poems.add(i.getPoem().get_id());
            cnt++;
         }
      }
      if (i.getType().equals(Term.THIS)) {
         poems.add(QueryHandler.session.get().getPoem().get_id());
         cnt++;
      }
   }
   if (cnt == 0) return 0;
   return cnt / (poems.size() * poems.size());
}

double existPoem() {
   int cnt = 0;
   if (termList.getAuthor() != null) cnt++;
   if (termList.getTitle() != null) cnt++;
   if (termList.getSentence() != null) cnt++;
   Poem p = PoemData.queryOne(termList.getAuthor(), termList.getTitle(), termList.getSentence());
   if (cnt > 1) {
      if (p != null) return 1;
      else return -1;
   }
   return 0;
}

double judge(TermList termList, int queryLength) {
   if (termList.size() == 0) return 0;
   this.termList = termList;
   this.queryLength = queryLength;
   this.termListStr = termList.toTemplateString();
   double score = 0;
   double scoreLength = lengthScore();
   if (scoreLength > 0.8) {
      if (
              termList.size() == 1
                      && (termList.get(0).getType().equals(Term.POEM_DETAIL)
                      || termList.get(0).getType().equals(Term.CIPAI)
                      || termList.get(0).getType().equals(Term.AUTHOR)
                      || termList.get(0).getType().equals(Term.DYNASTY)
                      || termList.get(0).getType().equals(Term.HOW)
              )) {
         score += 5;//最高优先级
      }
      if (termListStr.equals("作者题目") || termListStr.equals("题目作者")) {
         score += 0.2;
      }
      if (termList.equals("题目")) {
         score += 0.2;
      }
   }
   score += scoreLength*2;//给scoreLength权重大一些
   double scoreTerm = termScore();
   score += scoreTerm;
   double scorePoemSquare = poemSquare();
   score += scorePoemSquare;
   double scoreAuthorSquare = existPoem();
   score += scoreAuthorSquare;
   if (MyApplication.developLocal) {
      System.out.println("各项得分=" +
              "termScore:" + Util.format(scoreTerm)
              + " lengthScore:" + Util.format(scoreLength)
              + " 诗的个数越少越好：" + Util.format(scorePoemSquare)
              + " 同一首诗：" + Util.format(scoreAuthorSquare)
      );
   }
   return score;
}

public static void main(String[] args) {
   for (String i : reList) {
      System.out.println(i);
   }
}
}
