package haha;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Util {
    static Logger logger = Logger.getLogger(Util.class);

    public static String toHexString(byte[] data) {
        StringBuilder builder = new StringBuilder();
        for (byte b : data) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
    public static String tos(InputStream cin) {
        StringBuilder builder = new StringBuilder();
        Scanner scanner = new Scanner(cin);
        while (scanner.hasNext()) {
            builder.append(scanner.nextLine());
        }
        return builder.toString();
    }

    public static String getRootClassPath() {
        String p = Util.class.getResource("/").getPath();
        if (p.contains(":")) p = p.substring(1);
        return p;
    }

    public static InputStream getResource(String path) {
        return Util.class.getResourceAsStream(path);
    }

    public static void pause() {
        try {
            System.in.read();
        } catch (IOException e) {
            logger.error("", e);
        }
    }

    public static boolean isChinese(char c) {
        return c >= 0x4e00 && c <= 0x9fa5;
    }

    public static String format(double x) {
        return String.format("%.2f", x);
    }

    static class Se {
        long t;

        Se(long x) {
            t = x;
        }
    }

    public static void main(String[] args) throws IOException {
        PriorityQueue<Se> q = new PriorityQueue<>(new Comparator<Se>() {
            @Override
            public int compare(Se m, Se n) {
                return (int) (m.t - n.t);
            }
        });
        Se one = new Se(1);
        Se two = new Se(2);
        q.add(one);
        q.add(two);
//        two.t=-1;
        while (q.isEmpty() == false) {
            Se now = q.poll();
            System.out.println(now.t);
        }
    }
}
