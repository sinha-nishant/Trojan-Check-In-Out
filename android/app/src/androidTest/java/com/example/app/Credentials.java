package com.example.app;

import java.util.Random;

public class Credentials {
    public static String email = getEmail();
    public static String id = getId();

    private static String getEmail() {
        byte[] array = new byte[20];
        new Random().nextBytes(array);

        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(20);

        for (int i = 0; i < 20; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString() + "@usc.edu";//generated random string
    }

    public static String getId() {
        long leftLimit = 1000000000L;
        long rightLimit = 9999999999L;
        Long generatedLong = leftLimit + (long) (Math.random() * (rightLimit - leftLimit));
        return generatedLong.toString();
    }
}
