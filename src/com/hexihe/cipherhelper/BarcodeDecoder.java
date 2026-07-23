package com.hexihe.cipherhelper;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BarcodeDecoder {

    public static String decode(Bitmap bitmap) {
        if (bitmap == null) return null;
        String qr = decodeQR(bitmap);
        if (qr != null) return qr;
        String code39 = decodeCode39(bitmap);
        if (code39 != null) return code39;
        String code128 = decodeCode128(bitmap);
        if (code128 != null) return code128;
        return null;
    }

    public static Bitmap yuvToBitmap(byte[] data, int width, int height) {
        try {
            YuvImage yuv = new YuvImage(data, ImageFormat.NV21, width, height, null);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            yuv.compressToJpeg(new Rect(0, 0, width, height), 80, out);
            byte[] bytes = out.toByteArray();
            return android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Exception e) {
            return null;
        }
    }

    private static String decodeQR(Bitmap bitmap) {
        try {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            int[] gray = new int[w * h];
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int p = bitmap.getPixel(x, y);
                    int r = (p >> 16) & 0xFF;
                    int g = (p >> 8) & 0xFF;
                    int b = p & 0xFF;
                    gray[y * w + x] = (r + g + b) / 3;
                }
            }

            int threshold = computeThreshold(gray);
            boolean[] bin = new boolean[w * h];
            for (int i = 0; i < gray.length; i++) {
                bin[i] = gray[i] < threshold;
            }

            List<int[]> finders = findFinderPatterns(bin, w, h);
            if (finders.size() < 3) return null;

            int[] tl = finders.get(0);
            int[] tr = finders.get(1);
            int[] bl = finders.get(2);

            int dim = estimateDimension(tl, tr, bl);
            if (dim < 21 || dim > 177) return null;

            int moduleSize = (int) Math.round(
                (distance(tl[0], tl[1], tr[0], tr[1]) +
                 distance(tl[0], tl[1], bl[0], bl[1])) / 2.0 / (dim - 7));
            if (moduleSize < 1) moduleSize = 1;

            int startX = tl[0] - 3 * moduleSize;
            int startY = tl[1] - 3 * moduleSize;

            boolean[][] modules = new boolean[dim][dim];
            for (int r = 0; r < dim; r++) {
                for (int c = 0; c < dim; c++) {
                    int px = startX + c * moduleSize + moduleSize / 2;
                    int py = startY + r * moduleSize + moduleSize / 2;
                    if (px >= 0 && px < w && py >= 0 && py < h) {
                        modules[r][c] = bin[py * w + px];
                    }
                }
            }

            int formatInfo = readFormatInfo(modules, dim);
            if (formatInfo < 0) return null;
            int maskPattern = (formatInfo >> 3) & 0x07;

            unmaskData(modules, dim, maskPattern);

            int[] data = readDataBits(modules, dim);
            if (data == null || data.length < 1) return null;

            int mode = (data[0] >> 4) & 0x0F;
            if (mode != 4) return null;

            int countBits = dim <= 9 ? 8 : 16;
            int count = 0;
            int bitIdx = 4;
            for (int i = 0; i < countBits; i++) {
                int byteIdx = bitIdx / 8;
                int bitPos = 7 - (bitIdx % 8);
                if (byteIdx < data.length) {
                    count = (count << 1) | ((data[byteIdx] >> bitPos) & 1);
                }
                bitIdx++;
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < count; i++) {
                int b = 0;
                for (int j = 0; j < 8; j++) {
                    int byteIdx = bitIdx / 8;
                    int bitPos = 7 - (bitIdx % 8);
                    if (byteIdx < data.length) {
                        b = (b << 1) | ((data[byteIdx] >> bitPos) & 1);
                    }
                    bitIdx++;
                }
                sb.append((char) (b & 0xFF));
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    private static int computeThreshold(int[] gray) {
        int min = 255, max = 0;
        for (int g : gray) {
            if (g < min) min = g;
            if (g > max) max = g;
        }
        return (min + max) / 2;
    }

    private static List<int[]> findFinderPatterns(boolean[] bin, int w, int h) {
        List<int[]> results = new ArrayList<>();
        for (int y = h / 4; y < 3 * h / 4; y += 3) {
            int[] state = new int[5];
            int count = 0;
            for (int x = 0; x < w; x++) {
                boolean pixel = bin[y * w + x];
                if (x > 0 && pixel != bin[y * w + x - 1]) {
                    if (count == 5) {
                        if (checkRatio(state)) {
                            int cx = x - state[4] - state[3] - state[2] / 2;
                            boolean found = false;
                            for (int[] r : results) {
                                if (Math.abs(r[0] - cx) < 10 && Math.abs(r[1] - y) < 10) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                results.add(new int[]{cx, y});
                            }
                        }
                    }
                    for (int i = 0; i < 4; i++) state[i] = state[i + 1];
                    state[4] = 1;
                } else {
                    state[4]++;
                }
            }
        }
        if (results.size() >= 3) {
            Collections.sort(results, new Comparator<int[]>() {
                @Override
                public int compare(int[] a, int[] b) {
                    return Integer.compare(a[0], b[0]);
                }
            });
        }
        return results;
    }

    private static boolean checkRatio(int[] state) {
        int total = 0;
        for (int s : state) total += s;
        if (total < 20) return false;
        int unit = total / 7;
        if (unit < 2) return false;
        int[] expected = {1, 1, 3, 1, 1};
        for (int i = 0; i < 5; i++) {
            int diff = Math.abs(state[i] - expected[i] * unit);
            if (diff > unit) return false;
        }
        return true;
    }

    private static int estimateDimension(int[] tl, int[] tr, int[] bl) {
        double dTop = distance(tl[0], tl[1], tr[0], tr[1]);
        double dLeft = distance(tl[0], tl[1], bl[0], bl[1]);
        double avg = (dTop + dLeft) / 2.0;
        return (int) Math.round(avg / 3.5) + 7;
    }

    private static double distance(int x1, int y1, int x2, int y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    private static int readFormatInfo(boolean[][] modules, int dim) {
        int info = 0;
        for (int i = 0; i < 6; i++) {
            info = (info << 1) | (modules[8][i] ? 1 : 0);
        }
        info = (info << 1) | (modules[8][7] ? 1 : 0);
        info = (info << 1) | (modules[8][8] ? 1 : 0);
        info = (info << 1) | (modules[7][8] ? 1 : 0);
        for (int i = 5; i >= 0; i--) {
            info = (info << 1) | (modules[i][8] ? 1 : 0);
        }
        return info ^ 0x5412;
    }

    private static void unmaskData(boolean[][] modules, int dim, int mask) {
        for (int r = 0; r < dim; r++) {
            for (int c = 0; c < dim; c++) {
                if (isFunctionModule(r, c, dim)) continue;
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
                if (condition) modules[r][c] = !modules[r][c];
            }
        }
    }

    private static boolean isFunctionModule(int r, int c, int dim) {
        if (r <= 8 && c <= 8) return true;
        if (r <= 8 && c >= dim - 8) return true;
        if (r >= dim - 8 && c <= 8) return true;
        if (r == 6 || c == 6) return true;
        if (dim > 21 && r >= dim - 11 && c <= 5) return true;
        if (dim > 21 && r <= 5 && c >= dim - 11) return true;
        return false;
    }

    private static int[] readDataBits(boolean[][] modules, int dim) {
        List<Integer> bits = new ArrayList<>();
        int dir = -1;
        int col = dim - 1;
        while (col > 0) {
            if (col == 6) col--;
            for (int rowPass = 0; rowPass < dim; rowPass++) {
                int row = (dir == -1) ? (dim - 1 - rowPass) : rowPass;
                for (int c = 0; c < 2; c++) {
                    int cCol = col - c;
                    if (cCol < 0) continue;
                    if (!isFunctionModule(row, cCol, dim)) {
                        bits.add(modules[row][cCol] ? 1 : 0);
                    }
                }
            }
            dir = -dir;
            col -= 2;
        }
        int numBytes = (bits.size() + 7) / 8;
        int[] result = new int[numBytes];
        for (int i = 0; i < numBytes; i++) {
            int val = 0;
            for (int j = 0; j < 8; j++) {
                int idx = i * 8 + j;
                if (idx < bits.size()) {
                    val = (val << 1) | bits.get(idx);
                }
            }
            result[i] = val;
        }
        return result;
    }

    private static String decodeCode39(Bitmap bitmap) {
        try {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            int y = h / 2;
            int[] line = new int[w];
            for (int x = 0; x < w; x++) {
                int p = bitmap.getPixel(x, y);
                line[x] = ((p >> 16) & 0xFF + (p >> 8) & 0xFF + p & 0xFF) / 3;
            }
            int threshold = 0;
            for (int v : line) threshold += v;
            threshold /= w;
            boolean[] bin = new boolean[w];
            for (int i = 0; i < w; i++) bin[i] = line[i] < threshold;

            List<Integer> widths = new ArrayList<>();
            int count = 1;
            for (int i = 1; i < w; i++) {
                if (bin[i] == bin[i - 1]) {
                    count++;
                } else {
                    widths.add(count);
                    count = 1;
                }
            }
            widths.add(count);

            if (widths.size() < 19) return null;
            int start = 0;
            while (start < widths.size() - 9) {
                int sum = 0;
                for (int i = start; i < start + 9; i++) sum += widths.get(i);
                int narrow = sum / 12;
                int[] pattern = new int[9];
                for (int i = 0; i < 9; i++) {
                    pattern[i] = Math.round(widths.get(start + i) / (float) narrow);
                    if (pattern[i] < 1 || pattern[i] > 3) break;
                }
                start += 9;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private static String decodeCode128(Bitmap bitmap) {
        try {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            int y = h / 2;
            int[] line = new int[w];
            for (int x = 0; x < w; x++) {
                int p = bitmap.getPixel(x, y);
                line[x] = ((p >> 16) & 0xFF + (p >> 8) & 0xFF + p & 0xFF) / 3;
            }
            int threshold = 0;
            for (int v : line) threshold += v;
            threshold /= w;
            boolean[] bin = new boolean[w];
            for (int i = 0; i < w; i++) bin[i] = line[i] < threshold;
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
