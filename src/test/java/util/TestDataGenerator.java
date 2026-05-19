package util;

import java.util.Random;

public class TestDataGenerator {

    private static final Random random = new Random();
    private static final String ALPHA = "abcdefghijklmnopqrstuvwxyz";

    public static String randomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHA.charAt(random.nextInt(ALPHA.length())));
        }
        return sb.toString();
    }

    public static String randomEmail() {
        return "selenium_" + randomString(8) + "_" + System.currentTimeMillis() + "@example.com";
    }

    public static String randomUsername() {
        return "user_" + randomString(8);
    }

    public static String randomPassword() {
        return "Pwd_" + randomString(10) + "!" + random.nextInt(1000);
    }
}
