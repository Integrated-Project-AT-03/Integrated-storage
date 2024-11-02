package com.example.integratedstorage.utils;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

public class CustomNanoId {
    public static String generate(int size) {
        char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        return NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, alphabet, size);
    }
}
