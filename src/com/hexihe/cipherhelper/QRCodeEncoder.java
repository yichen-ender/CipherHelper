package com.hexihe.cipherhelper;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class QRCodeEncoder {

    // Byte mode max capacity for versions 1-10 (L, M, Q, H)
    private static final int[][] BYTE_CAPACITY = {
        {},
        {17, 14, 11, 7},
        {32, 26, 20, 14},
        {53, 42, 32, 24},
        {78, 62, 46, 34},
        {106, 84, 60, 44},
        {134, 108, 74, 58},
        {154, 122, 87, 64},
        {192, 152, 109, 78},
        {230, 180, 130, 97},
        {271, 213, 151, 111}
    };

    // Total codewords per version
    private static final int[] TOTAL_CODEWORDS = {
        0, 26, 44, 70, 100, 134, 172, 196, 242, 292, 346
    };

    // Data codewords per version/level (including header)
    private static final int[][] DATA_CODEWORDS = {
        {},
        {19, 16, 13, 9},
        {34, 28, 22, 16},
        {55, 44, 34, 26},
        {80, 64, 48, 36},
        {108, 86, 62, 46},
        {136, 108, 76, 60},
        {156, 124, 88, 66},
        {194, 154, 110, 82},
        {232, 182, 132, 100},
        {274, 216, 154, 114}
    };

    // Error correction blocks: {numBlocks, dataCodewords, ecCodewords}
    // or {numBlocks1, data1, ec1, numBlocks2, data2, ec2} for split blocks
    private static final int[][][] EC_BLOCKS = {
        {},
        {{1, 19, 7}, {1, 16, 10}, {1, 13, 13}, {1, 9, 17}},
        {{1, 34, 10}, {1, 28, 16}, {1, 22, 22}, {1, 16, 28}},
        {{1, 55, 15}, {1, 44, 26}, {2, 18, 17}, {2, 13, 22}},
        {{1, 80, 20}, {2, 32, 18}, {2, 24, 26}, {4, 9, 16}},
        {{1, 108, 26}, {2, 43, 24}, {2, 39, 28}, {2, 31, 36}},
        {{2, 68, 18}, {4, 27, 16}, {4, 19, 26}, {4, 15, 28}},
        {{2, 78, 20}, {4, 31, 18}, {2, 44, 24}, {4, 21, 26}},
        {{2, 97, 24}, {2, 38, 22}, {4, 24, 22}, {4, 22, 26}},
        {{2, 116, 30}, {3, 36, 22}, {4, 30, 22}, {4, 24, 28}},
        {{2, 137, 36}, {4, 54, 26}, {4, 39, 22}, {4, 29, 30}}
    };

    // Alignment pattern center positions for versions 2-10
    private static final int[][] ALIGNMENT_CENTERS = {
        {},
        {},           // V1: none
        {6, 18},      // V2
        {6, 22},      // V3
        {6, 26},      // V4
        {6, 30},      // V5
        {6, 34},      // V6
        {6, 22, 38},  // V7
        {6, 24, 42},  // V8
        {6, 26, 46},  // V9
        {6, 28, 50}   // V10
    };

    // Pre-masked format info: [ecLevel][maskPattern]
    private static final int[][] FORMAT_INFO_MASKED = {
        {0x77c4, 0x72f3, 0x7daa, 0x789d, 0x662f, 0x6318, 0x6c41, 0x6976},
        {0x5412, 0x5125, 0x5e7c, 0x5b4b, 0x45f9, 0x40ce, 0x4f97, 0x4aa0},
        {0x355f, 0x3068, 0x3f31, 0x3a06, 0x24b4, 0x2183, 0x2eda, 0x2bed},
        {0x1689, 0x13be, 0x1ce7, 0x19d0, 0x0762, 0x0255, 0x0d0c, 0x083b}
    };

    public static Bitmap encode(String text, int size) {
        try {
            byte[] data = text.getBytes("UTF-8");
            int dataLen = data.length;

            int version = selectVersion(dataLen);
            if (version < 0) return null;

            int ecLevel = 0;
            int[] capacity = BYTE_CAPACITY[version];
            for (int i = 0; i < 4; i++) {
                if (dataLen <= capacity[i]) {
                    ecLevel = i;
                    break;
                }
            }

            int dimension = 17 + version * 4;
            boolean[][] moduleMatrix = new boolean[dimension][dimension];
            boolean[][] isFunction = new boolean[dimension][dimension];

            int numDataCodewords = DATA_CODEWORDS[version][ecLevel];
            int totalCodewords = TOTAL_CODEWORDS[version];
            int numECCodewords = totalCodewords - numDataCodewords;

            // Build bit stream
            List<Integer> bits = new ArrayList<>();
            appendBits(bits, 4, 4); // Byte mode indicator
            int charCountBits = version <= 9 ? 8 : 16;
            appendBits(bits, dataLen, charCountBits);
            for (byte b : data) {
                appendBits(bits, b & 0xFF, 8);
            }

            // Pad with terminator (0000) and fill remaining
            int requiredBits = numDataCodewords * 8;
            int[] padBytes = {0xEC, 0x11};
            int padIdx = 0;
            while (bits.size() < requiredBits) {
                if (bits.size() + 4 <= requiredBits) {
                    appendBits(bits, 0, 4);
                }
                while (bits.size() < requiredBits && bits.size() % 8 != 0) {
                    bits.add(0);
                }
                while (bits.size() < requiredBits) {
                    appendBits(bits, padBytes[padIdx % 2], 8);
                    padIdx++;
                }
            }

            // Convert bits to codewords
            int[] dataCodewords = new int[numDataCodewords];
            for (int i = 0; i < numDataCodewords; i++) {
                int val = 0;
                for (int j = 0; j < 8; j++) {
                    val = (val << 1) | (bits.get(i * 8 + j) != 0 ? 1 : 0);
                }
                dataCodewords[i] = val;
            }

            // Generate error correction
            int[] ecBlockDef = EC_BLOCKS[version][ecLevel];
            int numBlocks1 = ecBlockDef[0];
            int blockDataLen1 = ecBlockDef[1];
            int blockECLen1 = ecBlockDef[2];
            int numBlocks2 = 0;
            int blockDataLen2 = 0;
            int blockECLen2 = 0;

            if (ecBlockDef.length > 3) {
                numBlocks2 = ecBlockDef[3];
                blockDataLen2 = ecBlockDef[4];
                blockECLen2 = ecBlockDef[5];
            }

            int totalBlocks = numBlocks1 + numBlocks2;
            int[][] allBlocks = new int[totalBlocks][];
            int[][] allEC = new int[totalBlocks][];

            int dataIdx = 0;
            for (int i = 0; i < numBlocks1; i++) {
                allBlocks[i] = new int[blockDataLen1];
                System.arraycopy(dataCodewords, dataIdx, allBlocks[i], 0, blockDataLen1);
                allEC[i] = ReedSolomon.generateECC(allBlocks[i], blockECLen1);
                dataIdx += blockDataLen1;
            }
            for (int i = 0; i < numBlocks2; i++) {
                allBlocks[numBlocks1 + i] = new int[blockDataLen2];
                System.arraycopy(dataCodewords, dataIdx, allBlocks[numBlocks1 + i], 0, blockDataLen2);
                allEC[numBlocks1 + i] = ReedSolomon.generateECC(allBlocks[numBlocks1 + i], blockECLen2);
                dataIdx += blockDataLen2;
            }

            // Interleave data and EC codewords
            int[] finalCodewords = new int[totalCodewords];
            int idx = 0;
            int maxDataLen = Math.max(blockDataLen1, blockDataLen2);
            for (int j = 0; j < maxDataLen; j++) {
                for (int i = 0; i < totalBlocks; i++) {
                    if (j < allBlocks[i].length) {
                        finalCodewords[idx++] = allBlocks[i][j];
                    }
                }
            }
            int maxECLen = Math.max(blockECLen1, blockECLen2);
            for (int j = 0; j < maxECLen; j++) {
                for (int i = 0; i < totalBlocks; i++) {
                    if (j < allEC[i].length) {
                        finalCodewords[idx++] = allEC[i][j];
                    }
                }
            }

            // Place function patterns
            placeFinderPatterns(moduleMatrix, isFunction, dimension);
            placeSeparators(moduleMatrix, isFunction, dimension);
            placeTimingPatterns(moduleMatrix, isFunction, dimension);
            placeAlignmentPatterns(moduleMatrix, isFunction, dimension, version);
            placeDarkModule(moduleMatrix, isFunction, version);

            // Place data
            placeData(moduleMatrix, isFunction, finalCodewords, dimension);

            // Apply best mask
            int bestMask = selectBestMask(moduleMatrix, isFunction, dimension);
            applyMask(moduleMatrix, isFunction, dimension, bestMask);

            // Place format and version info
            placeFormatInfo(moduleMatrix, dimension, ecLevel, bestMask);
            if (version >= 7) {
                placeVersionInfo(moduleMatrix, dimension, version);
            }

            return renderBitmap(moduleMatrix, dimension, size);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static int selectVersion(int dataLen) {
        for (int v = 1; v <= 10; v++) {
            if (dataLen <= BYTE_CAPACITY[v][0]) {
                return v;
            }
        }
        return -1;
    }

    private static void appendBits(List<Integer> bits, int value, int numBits) {
        for (int i = numBits - 1; i >= 0; i--) {
            bits.add((value >> i) & 1);
        }
    }

    private static void placeFinderPatterns(boolean[][] matrix, boolean[][] isFunction, int dim) {
        int[][] positions = {{0, 0}, {0, dim - 7}, {dim - 7, 0}};
        for (int[] pos : positions) {
            int row = pos[0], col = pos[1];
            for (int r = 0; r < 7; r++) {
                for (int c = 0; c < 7; c++) {
                    boolean black = (r == 0 || r == 6 || c == 0 || c == 6) ||
                                    (r >= 2 && r <= 4 && c >= 2 && c <= 4);
                    matrix[row + r][col + c] = black;
                    isFunction[row + r][col + c] = true;
                }
            }
        }
    }

    private static void placeSeparators(boolean[][] matrix, boolean[][] isFunction, int dim) {
        // Top-left: row 7 (cols 0-7), col 7 (rows 0-6)
        for (int i = 0; i < 8; i++) {
            matrix[7][i] = false;
            isFunction[7][i] = true;
        }
        for (int i = 0; i < 7; i++) {
            matrix[i][7] = false;
            isFunction[i][7] = true;
        }
        // Top-right: row 7 (cols dim-8 to dim-1), col dim-8 (rows 0-6)
        for (int i = 0; i < 8; i++) {
            matrix[7][dim - 8 + i] = false;
            isFunction[7][dim - 8 + i] = true;
        }
        for (int i = 0; i < 7; i++) {
            matrix[i][dim - 8] = false;
            isFunction[i][dim - 8] = true;
        }
        // Bottom-left: row dim-8 (cols 0-7), col 7 (rows dim-7 to dim-1)
        for (int i = 0; i < 8; i++) {
            matrix[dim - 8][i] = false;
            isFunction[dim - 8][i] = true;
        }
        for (int i = 0; i < 7; i++) {
            matrix[dim - 7 + i][7] = false;
            isFunction[dim - 7 + i][7] = true;
        }
    }

    private static void placeTimingPatterns(boolean[][] matrix, boolean[][] isFunction, int dim) {
        for (int i = 8; i < dim - 8; i++) {
            boolean bit = (i % 2) == 0;
            matrix[6][i] = bit;
            matrix[i][6] = bit;
            isFunction[6][i] = true;
            isFunction[i][6] = true;
        }
    }

    private static void placeAlignmentPatterns(boolean[][] matrix, boolean[][] isFunction, int dim, int version) {
        if (version == 1) return;
        int[] centers = ALIGNMENT_CENTERS[version];
        for (int r : centers) {
            for (int c : centers) {
                if (isFunction[r][c]) continue;
                if ((r < 9 && c < 9) || (r < 9 && c > dim - 10) || (r > dim - 10 && c < 9)) continue;
                for (int dr = -2; dr <= 2; dr++) {
                    for (int dc = -2; dc <= 2; dc++) {
                        boolean black = (Math.abs(dr) == 2 || Math.abs(dc) == 2) || (dr == 0 && dc == 0);
                        matrix[r + dr][c + dc] = black;
                        isFunction[r + dr][c + dc] = true;
                    }
                }
            }
        }
    }

    private static void placeDarkModule(boolean[][] matrix, boolean[][] isFunction, int version) {
        int row = 4 * version + 9;
        int col = 8;
        matrix[row][col] = true;
        isFunction[row][col] = true;
    }

    private static void placeData(boolean[][] matrix, boolean[][] isFunction, int[] codewords, int dim) {
        int bitIndex = 0;
        int totalBits = codewords.length * 8;
        int dir = -1;
        int col = dim - 1;

        while (col > 0) {
            if (col == 6) col--; // Skip timing column
            for (int rowPass = 0; rowPass < dim; rowPass++) {
                int row = (dir == -1) ? (dim - 1 - rowPass) : rowPass;
                for (int c = 0; c < 2; c++) {
                    int cCol = col - c;
                    if (isFunction[row][cCol]) continue;
                    if (bitIndex < totalBits) {
                        int codewordIdx = bitIndex / 8;
                        int bitPos = 7 - (bitIndex % 8);
                        boolean bit = ((codewords[codewordIdx] >> bitPos) & 1) == 1;
                        matrix[row][cCol] = bit;
                    } else {
                        matrix[row][cCol] = false;
                    }
                    bitIndex++;
                }
            }
            dir = -dir;
            col -= 2;
        }
    }

    private static int selectBestMask(boolean[][] matrix, boolean[][] isFunction, int dim) {
        int bestMask = 0;
        int bestScore = Integer.MAX_VALUE;
        boolean[][] temp = new boolean[dim][dim];

        for (int mask = 0; mask < 8; mask++) {
            for (int r = 0; r < dim; r++) {
                System.arraycopy(matrix[r], 0, temp[r], 0, dim);
            }
            applyMask(temp, isFunction, dim, mask);
            int score = evaluateMask(temp, dim);
            if (score < bestScore) {
                bestScore = score;
                bestMask = mask;
            }
        }
        return bestMask;
    }

    private static void applyMask(boolean[][] matrix, boolean[][] isFunction, int dim, int mask) {
        for (int r = 0; r < dim; r++) {
            for (int c = 0; c < dim; c++) {
                if (isFunction[r][c]) continue;
                boolean condition = false;
                switch (mask) {
                    case 0: condition = (r + c) % 2 == 0; break;
                    case 1: condition = r % 2 == 0; break;
                    case 2: condition = c % 3 == 0; break;
                    case 3: condition = (r + c) % 3 == 0; break;
                    case 4: condition = (r / 2 + c / 3) % 2 == 0; break;
                    case 5: condition = (r * c) % 2 + (r * c) % 3 == 0; break;
                    case 6: condition = ((r * c) % 2 + (r * c) % 3) % 2 == 0; break;
                    case 7: condition = ((r + c) % 2 + (r * c) % 3) % 2 == 0; break;
                }
                if (condition) matrix[r][c] = !matrix[r][c];
            }
        }
    }

    private static int evaluateMask(boolean[][] matrix, int dim) {
        int score = 0;

        // Rule 1: Adjacent modules in row/column
        for (int r = 0; r < dim; r++) {
            int count = 1;
            for (int c = 1; c < dim; c++) {
                if (matrix[r][c] == matrix[r][c - 1]) count++;
                else {
                    if (count >= 5) score += count - 2;
                    count = 1;
                }
            }
            if (count >= 5) score += count - 2;
        }
        for (int c = 0; c < dim; c++) {
            int count = 1;
            for (int r = 1; r < dim; r++) {
                if (matrix[r][c] == matrix[r - 1][c]) count++;
                else {
                    if (count >= 5) score += count - 2;
                    count = 1;
                }
            }
            if (count >= 5) score += count - 2;
        }

        // Rule 2: Block of modules
        for (int r = 0; r < dim - 1; r++) {
            for (int c = 0; c < dim - 1; c++) {
                boolean b = matrix[r][c];
                if (matrix[r][c + 1] == b && matrix[r + 1][c] == b && matrix[r + 1][c + 1] == b) {
                    score += 3;
                }
            }
        }

        // Rule 3: Patterns like finder
        for (int r = 0; r < dim; r++) {
            for (int c = 0; c < dim - 10; c++) {
                if (!matrix[r][c] && matrix[r][c + 1] && !matrix[r][c + 2] && !matrix[r][c + 3] &&
                    !matrix[r][c + 4] && matrix[r][c + 5] && !matrix[r][c + 6] &&
                    ((c + 10 < dim && matrix[r][c + 7] && matrix[r][c + 8] && matrix[r][c + 9] && matrix[r][c + 10]) ||
                     (c >= 4 && matrix[r][c - 1] && matrix[r][c - 2] && matrix[r][c - 3] && matrix[r][c - 4]))) {
                    score += 40;
                }
            }
        }
        for (int c = 0; c < dim; c++) {
            for (int r = 0; r < dim - 10; r++) {
                if (!matrix[r][c] && matrix[r + 1][c] && !matrix[r + 2][c] && !matrix[r + 3][c] &&
                    !matrix[r + 4][c] && matrix[r + 5][c] && !matrix[r + 6][c] &&
                    ((r + 10 < dim && matrix[r + 7][c] && matrix[r + 8][c] && matrix[r + 9][c] && matrix[r + 10][c]) ||
                     (r >= 4 && matrix[r - 1][c] && matrix[r - 2][c] && matrix[r - 3][c] && matrix[r - 4][c]))) {
                    score += 40;
                }
            }
        }

        // Rule 4: Dark ratio
        int dark = 0;
        for (int r = 0; r < dim; r++) {
            for (int c = 0; c < dim; c++) {
                if (matrix[r][c]) dark++;
            }
        }
        int percent = dark * 100 / (dim * dim);
        int prev = Math.abs(percent - 50) / 5;
        score += prev * 10;

        return score;
    }

    private static void placeFormatInfo(boolean[][] matrix, int dim, int ecLevel, int mask) {
        int formatInfo = FORMAT_INFO_MASKED[ecLevel][mask];

        // Top-left
        for (int i = 0; i < 6; i++) {
            matrix[8][i] = ((formatInfo >> i) & 1) == 1;
        }
        matrix[8][7] = ((formatInfo >> 6) & 1) == 1;
        matrix[8][8] = ((formatInfo >> 7) & 1) == 1;
        matrix[7][8] = ((formatInfo >> 8) & 1) == 1;
        for (int i = 9; i < 15; i++) {
            matrix[14 - i][8] = ((formatInfo >> i) & 1) == 1;
        }

        // Top-right and bottom-left
        for (int i = 0; i < 8; i++) {
            matrix[dim - 1 - i][8] = ((formatInfo >> i) & 1) == 1;
        }
        for (int i = 8; i < 15; i++) {
            matrix[8][dim - 15 + i] = ((formatInfo >> i) & 1) == 1;
        }
    }

    private static void placeVersionInfo(boolean[][] matrix, int dim, int version) {
        int versionInfo = getVersionInfo(version);
        for (int i = 0; i < 18; i++) {
            boolean bit = ((versionInfo >> i) & 1) == 1;
            int r = i / 3;
            int c = i % 3;
            matrix[dim - 11 + c][r] = bit;
            matrix[r][dim - 11 + c] = bit;
        }
    }

    private static int getVersionInfo(int version) {
        int remainder = version << 12;
        int divisor = 0x1f25;
        for (int i = 17; i >= 12; i--) {
            if (((remainder >> i) & 1) == 1) {
                remainder ^= divisor << (i - 12);
            }
        }
        return (version << 12) | remainder;
    }

    private static Bitmap renderBitmap(boolean[][] matrix, int dimension, int size) {
        int scale = Math.max(1, size / dimension);
        int bmpSize = dimension * scale;
        Bitmap bmp = Bitmap.createBitmap(bmpSize, bmpSize, Bitmap.Config.ARGB_8888);
        for (int r = 0; r < dimension; r++) {
            for (int c = 0; c < dimension; c++) {
                int color = matrix[r][c] ? Color.BLACK : Color.WHITE;
                for (int sr = 0; sr < scale; sr++) {
                    for (int sc = 0; sc < scale; sc++) {
                        bmp.setPixel(c * scale + sc, r * scale + sr, color);
                    }
                }
            }
        }
        return bmp;
    }
}
