package haha;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PropertyTest {
    static Logger logger = Logger.getLogger(PropertyTest.class);

    public static void main(String[] args) throws IOException {
        Files.list(Paths.get("${user.home}")).forEach(i -> {
            System.out.println(i);
        });
    }
}
