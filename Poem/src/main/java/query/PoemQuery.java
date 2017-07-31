package query;

public class PoemQuery implements Query {
String title;
String author;
String sentence;
String detail;

public String getTitle() {
   return title;
}

public void setTitle(String title) {
   this.title = title;
}

public String getAuthor() {
   return author;
}

public void setAuthor(String author) {
   this.author = author;
}

public String getSentence() {
   return sentence;
}

public void setSentence(String sentence) {
   this.sentence = sentence;
}

public String getDetail() {
   return detail;
}

public void setDetail(String detail) {
   this.detail = detail;
}

@Override
public String getAns(String openid) {
   PoemQueryHandler handler = new PoemQueryHandler();
   return handler.getAns(openid, this);
}
}
