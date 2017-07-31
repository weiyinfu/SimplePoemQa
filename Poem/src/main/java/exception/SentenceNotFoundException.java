package exception;

public class SentenceNotFoundException extends Exception {
    public SentenceNotFoundException(String sentene) {
        super("没有找到“" + sentene + "”这句话");
    }
}
