package query;

import data.po.Poem;

public class Term {
public static final String AUTHOR = "author";
public static final String POEM_TITLE = "title";
public static final String POEM_SENTENCE = "poemSentence";
public static final String DYNASTY = "dynasty";
public static final String CIPAI = "cipai";
public static final String POEM_DETAIL = "poemDetail";
public static final String THIS = "this";
public static final String HOW = "how";

String content;
int begIndex;
int endIndex;
String type;
double score;
Poem poem;


public Poem getPoem() {
   return poem;
}

public void setPoem(Poem poem) {
   this.poem = poem;
}

Term(int fromIndex, int endIndex, String content, String type) {
   this.begIndex = fromIndex;
   this.endIndex = endIndex;
   this.type = type;
   this.content = content;
}

public double getScore() {
   return score;
}

public void setScore(double score) {
   this.score = score;
}

@Override
public String toString() {
   return String.format("<%s,%s,%.2f>", content, type, score);
}

public String getContent() {
   return content;
}

public void setContent(String content) {
   this.content = content;
}

public int getBegIndex() {
   return begIndex;
}

public void setBegIndex(int begIndex) {
   this.begIndex = begIndex;
}

public int getEndIndex() {
   return endIndex;
}

public void setEndIndex(int endIndex) {
   this.endIndex = endIndex;
}

public String getType() {
   return type;
}

public void setType(String type) {
   this.type = type;
}
}
