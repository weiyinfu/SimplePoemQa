package query;

public class QueryClassifier {
public Query classify(TermList termList) {
   if (termList.size() == 1) {
      return oneTermQuery(termList.get(0));
   } else {
      PoemQuery poemQuery = new PoemQuery();
      poemQuery.setAuthor(termList.getAuthor());
      poemQuery.setDetail(termList.getPoemDetail());
      poemQuery.setSentence(termList.getSentence());
      poemQuery.setTitle(termList.getTitle());
      return poemQuery;
   }
}

Query oneTermQuery(Term term) {
   switch (term.getType()) {
      case Term.AUTHOR:
         AuthorQuery authorQuery = new AuthorQuery();
         authorQuery.setName(term.getContent());
         return authorQuery;
      case Term.CIPAI:
         CipaiQuery cipaiQuery = new CipaiQuery();
         cipaiQuery.setName(term.getContent());
         return cipaiQuery;
      case Term.POEM_DETAIL: {
         PoemQuery poemQuery = new PoemQuery();
         poemQuery.setDetail(term.getContent());
         return poemQuery;
      }
      case Term.DYNASTY:
         DynastyQuery dynastyQuery = new DynastyQuery();
         dynastyQuery.setName(term.getContent());
         return dynastyQuery;
      case Term.POEM_TITLE: {
         PoemQuery poemQuery = new PoemQuery();
         poemQuery.setTitle(term.getContent());
         return poemQuery;
      }
      case Term.POEM_SENTENCE: {
         PoemQuery poemQuery = new PoemQuery();
         poemQuery.setSentence(term.getContent());
         return poemQuery;
      }
      case Term.HOW: {
         HowQuery howQuery = new HowQuery();
         return howQuery;
      }
   }
   return null;
}
}
