package query;
import haha.Util;

import java.util.Scanner;

public class HowQuery implements Query {
public static String howToUse;

static {
   Scanner cin = new Scanner(Util.getResource("/how.txt"));
   StringBuilder builder = new StringBuilder();
   while (cin.hasNext()) {
      builder.append(cin.nextLine() + "\n");
   }
   howToUse = builder.toString();
   cin.close();
}

@Override
public String getAns(String openid) {
   return howToUse;
}
}
