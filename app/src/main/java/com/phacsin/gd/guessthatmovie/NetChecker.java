package com.phacsin.gd.guessthatmovie;

import java.net.InetAddress;

public  class NetChecker {

    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }

    }
}