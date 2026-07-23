package com.hexihe.cipherhelper;

public class ReedSolomon {

    public static int[] generateECC(int[] data, int numECCodewords) {
        int[] generator = buildGenerator(numECCodewords);
        int[] remainder = new int[numECCodewords];

        for (int value : data) {
            int factor = value ^ remainder[0];
            System.arraycopy(remainder, 1, remainder, 0, numECCodewords - 1);
            remainder[numECCodewords - 1] = 0;

            if (factor != 0) {
                for (int i = 0; i < numECCodewords; i++) {
                    remainder[i] ^= GaloisField.mul(generator[i], factor);
                }
            }
        }

        return remainder;
    }

    private static int[] buildGenerator(int degree) {
        int[] generator = new int[degree];
        generator[0] = 1;

        for (int i = 0; i < degree; i++) {
            int alpha = GaloisField.exp(i);
            for (int j = degree - 1; j > 0; j--) {
                if (generator[j] != 0) {
                    generator[j] = generator[j - 1] ^ GaloisField.mul(generator[j], alpha);
                } else {
                    generator[j] = generator[j - 1];
                }
            }
            generator[0] = GaloisField.mul(generator[0], alpha);
        }

        return generator;
    }
}
