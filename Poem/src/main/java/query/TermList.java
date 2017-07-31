package query;

import java.util.LinkedList;

public class TermList extends LinkedList<Term> {
double score = Integer.MIN_VALUE;

public double getScore() {
   return score;
}

public void setScore(double score) {
   this.score = score;
}

//转换为模板字符串
String toTemplateString() {
   StringBuilder builder = new StringBuilder();
   for (Term i : this) {
      builder.append(i.getType());
   }
   return builder.toString();
}

@Override
public String toString() {
   StringBuilder builder = new StringBuilder();
   this.forEach(i -> builder.append(i));
   return builder.toString();
}

String getAuthor() {
   for (Term i : this) {
      if (i.getType().equals(Term.AUTHOR)) {
         return i.getContent();
      }
   }
   return null;
}

String getSentence() {
   for (Term i : this) {
      if (i.getType().equals(Term.POEM_SENTENCE)) {
         return i.getContent();
      }
   }
   return null;
}

int getTitleCount() {
   int titleCount = 0;
   for (Term i : this) {
      if (i.getType().equals(Term.POEM_TITLE)) {
         titleCount++;
      }
   }
   return titleCount;
}

String getTitle() {
   String cipai = "", title = "";
   for (Term i : this) {
      if (i.getType().equals(Term.POEM_TITLE)) {
         title = i.getContent();
      }
      if (i.getType().equals(Term.CIPAI)) {
         cipai = i.getContent();
      }
   }
   String ans = cipai + title;
   if (ans.length() == 0) return null;
   return ans;
}

String getPoemDetail() {
   for (Term i : this) {
      if (i.getType().equals(Term.POEM_DETAIL)) {
         return i.getContent();
      }
   }
   return null;
}

//获取指代，比如“这首诗”
String getRefer() {
   for (Term i : this) {
      if (i.getType().equals(Term.THIS)) {
         return i.getContent();
      }
   }
   return null;
}

String getHow() {
   for (Term i : this) {
      if (i.getType().equals(Term.HOW)) {
         return i.getContent();
      }
   }
   return null;
}
}
