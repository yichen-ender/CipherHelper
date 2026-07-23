package com.hexihe.cipherhelper;

public class GaloisField {
    private static final int PRIMITIVE_POLY = 0x11d;
    private static final int[] EXP_TABLE = new int[512];
    private static final int[] LOG_TABLE = new int[256];

    static {
        int x = 1;
        for (int i = 0; i < 255; i++) {
            EXP_TABLE[i] = x;
            LOG_TABLE[x] = i;
            x <<= 1;
            if (x >= 256) {
                x ^= PRIMITIVE_POLY;
            }
        }
        for (int i = 255; i < 512; i++) {
            EXP_TABLE[i] = EXP_TABLE[i - 255];
        }
    }

    public static int add(int a, int b) {
        return a ^ b;
    }

    public static int sub(int a, int b) {
        return a ^ b;
    }

    public static int mul(int a, int b) {
        if (a == 0 || b == 0) return 0;
        return EXP_TABLE[(LOG_TABLE[a] + LOG_TABLE[b]) % 255];
    }

    public static int div(int a, int b) {
        if (b == 0) throw new ArithmeticException("Division by zero");
        if (a == 0) return 0;
        return EXP_TABLE[(LOG_TABLE[a] - LOG_TABLE[b] + 255) % 255];
    }

    public static int exp(int power) {
        return EXP_TABLE[power % 255];
    }

    public static int log(int a) {
        if (a == 0) throw new IllegalArgumentException("log(0) is undefined");
        return LOG_TABLE[a];
    }

    public static int[] getExpTable() {
        return EXP_TABLE.clone();
    }

    public static int[] getLogTable() {
        return LOG_TABLE.clone();
    }
}
