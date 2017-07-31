package query;

import controller.MyApplication;
import data.index.WordDict;
import data.model.PoemData;
import data.po.Poem;
import haha.Util;
import org.apache.log4j.Logger;

import java.util.Scanner;

public class QueryParser {
String queryString;//当前正在解析的字符串
TermList a[];//a[i]表示s[i]开头的Term
TermList bestPath = null;//最佳答案
QueryClassifier classifier = new QueryClassifier();//查询分类器
TermListJudger judger = new TermListJudger();//评分器
static final Logger logger = Logger.getLogger(QueryParser.class);
int nextBegIndex = 0;//用于解析字符串的全局变量
int nowSentenceEndIndex = -1;//当前最长句子，避免重复解析句子
int nowTitleEndIndex = -1;//当前最长题目，避免重复解析题目
static final int SENTENCE_LEAST_LENGTH = 3;
static final int DICT_WORD_LENGTH = 6;

public QueryParser(String s) {
   this.queryString = s;
   a = new TermList[s.length()];
}

public Query parse() {
   label();
   findBestPath();
   System.out.println("best path : " + bestPath);
   Query ans = classifier.classify(bestPath);
   return ans;
}

void handleTitle(int begIndex, TermList ans) {
   if (begIndex <= nowTitleEndIndex) return;
   int titleEndIndex = begIndex;//闭区间
   while (titleEndIndex < queryString.length()) {
      String now = queryString.substring(begIndex, titleEndIndex + 1);
      if (PoemData.existsTitle(now)) {
         titleEndIndex++;
      } else break;
   }
   titleEndIndex--;
   if (titleEndIndex >= begIndex) {
      String now = queryString.substring(begIndex, titleEndIndex + 1);
      Poem p = PoemData.queryByTitle(now);
      double score = (double) now.length() / p.getTitle().length();
      if (score < 0.51) return;//题目得分0.51,表示字数必须过半才行
      Term titleTerm = new Term(begIndex, titleEndIndex, now, Term.POEM_TITLE);
      titleTerm.setScore(score);
      titleTerm.setPoem(p);
      ans.add(titleTerm);
      nowTitleEndIndex = titleEndIndex;
      if (titleTerm.getScore() == 1) {
         nextBegIndex = Math.min(titleEndIndex + 1, nextBegIndex);
      }
   }
}

void handleSentence(int begIndex, TermList ans) {
   if (begIndex <= nowSentenceEndIndex) return;
   int sentenceEndIndex = begIndex + SENTENCE_LEAST_LENGTH - 1;//句子中至少三个字符
   while (sentenceEndIndex < queryString.length()) {
      String now = queryString.substring(begIndex, sentenceEndIndex + 1);
      if (PoemData.existsSentence(now)) {
         sentenceEndIndex++;
      } else break;
   }
   sentenceEndIndex--;
   if (sentenceEndIndex >= begIndex + SENTENCE_LEAST_LENGTH - 1) {
      String now = queryString.substring(begIndex, sentenceEndIndex + 1);
      Poem p = PoemData.queryBySentence(now);
      PoemContent poemContent = new PoemContent(p.getContent());
      double score = poemContent.getRatio(now);
      if (score < 0.4) return;
      Term sentenceTerm = new Term(begIndex, sentenceEndIndex, now, Term.POEM_SENTENCE);
      sentenceTerm.setScore(score);
      sentenceTerm.setPoem(p);
      ans.add(sentenceTerm);
      nowSentenceEndIndex = sentenceEndIndex;
      if (score == 1.0) {
         nextBegIndex = Math.min(nextBegIndex, sentenceEndIndex + 1);
      }
   }
}

//词性标注
void label() {
   for (int begIndex = 0; begIndex < queryString.length(); begIndex = Math.max(nextBegIndex, begIndex + 1)) {
      TermList ans = new TermList();
      for (int endIndex = begIndex; endIndex < queryString.length() && endIndex < begIndex + DICT_WORD_LENGTH; endIndex++) {
         String now = queryString.substring(begIndex, endIndex + 1);
         WordDict.Word word = WordDict.getWord(now);
         if (word != null) {
            Term term = new Term(begIndex, endIndex, word.word, word.type);
            term.setScore(1);
            ans.add(term);
            nextBegIndex = Math.min(nextBegIndex, endIndex);
         }
      }
      handleSentence(begIndex, ans);
      handleTitle(begIndex, ans);
      a[begIndex] = ans;
   }
}

TermList findBestPath() {
   if (bestPath == null) {
      findBestPath(0, new TermList());
   }
   return bestPath;
}

void findBestPath(int begIndex, TermList termList) {
   if (begIndex >= queryString.length()) {
      double score = judger.judge(termList, queryString.length());
      termList.setScore(score);
      if (bestPath == null || bestPath.getScore() < termList.getScore()) {
         bestPath = new TermList();
         bestPath.addAll(termList);
         bestPath.setScore(termList.getScore());
      }
      if (MyApplication.developLocal) {
         System.out.println(termList + " " + Util.format(termList.getScore()));
      }
      return;
   }
   if (a[begIndex] != null) {
      for (Term i : a[begIndex]) {
         termList.add(i);
         findBestPath(i.endIndex + 1, termList);
         termList.removeLast();
      }
   }
   findBestPath(begIndex + 1, termList);
}

public static void main(String[] args) {
   MyApplication.init(true);
   Scanner cin = new Scanner(System.in);
   while (cin.hasNext()) {
      String s = cin.nextLine();
      QueryParser parse = new QueryParser(s);
      System.out.println("best path\n" + parse.findBestPath());
      System.out.println("over");
   }
   cin.close();
}
}
