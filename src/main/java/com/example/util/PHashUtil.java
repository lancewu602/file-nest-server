package com.example.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

/**
 * 感知哈希（Perceptual Hash, PHash）工具类
 * 用于计算图片的哈希值，判断图片内容相似度（对缩放、轻微编辑有一定抗干扰性）
 */
public final class PHashUtil {

    // 缩放尺寸（保留结构特征）
    private static final int RESIZE_WIDTH = 32;
    private static final int RESIZE_HEIGHT = 32;
    // 低频区域尺寸（提取核心特征）
    private static final int LOW_FREQ_SIZE = 8;

    /**
     * 计算图片文件的 PHash 值
     * @param imagePath 图片文件
     * @return 64位哈希字符串（由0和1组成）
     * @throws IOException 图片读取/处理异常
     */
    public static String calculatePHash(Path imagePath) throws IOException {
        // 1. 缩放图片并转为灰度图
        Path newImagePath = MagickUtil.resizeAndGray(imagePath, RESIZE_WIDTH, RESIZE_HEIGHT);
        BufferedImage grayImage = ImageIO.read(newImagePath.toFile());

        // 2. 提取灰度像素值（0-255）
        int[][] pixels = extractGrayPixels(grayImage);

        // 3. 计算二维 DCT（离散余弦变换），获取频率分量
        double[][] dctMatrix = computeDCT(pixels);

        // 4. 提取 8x8 低频区域（核心特征）
        double[][] lowFreqMatrix = extractLowFrequency(dctMatrix);

        // 5. 计算低频区域平均值（排除直流分量）
        double mean = calculateLowFreqMean(lowFreqMatrix);

        // 6. 生成 64位哈希值
        return generateHash(lowFreqMatrix, mean);
    }

    /**
     * 提取灰度图的像素值矩阵（0-255）
     */
    private static int[][] extractGrayPixels(BufferedImage grayImage) {
        int width = grayImage.getWidth();
        int height = grayImage.getHeight();
        int[][] pixels = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // 取灰度值（TYPE_BYTE_GRAY 格式中，RGB值相同，取低8位即可）
                pixels[y][x] = grayImage.getRGB(x, y) & 0xFF;
            }
        }
        return pixels;
    }

    /**
     * 计算二维 DCT（离散余弦变换）
     * 参考公式：DCT(u,v) = α(u)α(v) ΣΣ f(i,j) * cos[(2i+1)uπ/(2N)] * cos[(2j+1)vπ/(2N)]
     */
    private static double[][] computeDCT(int[][] pixels) {
        int n = RESIZE_HEIGHT; // 32
        double[][] dct = new double[n][n];
        double pi = Math.PI;

        for (int u = 0; u < n; u++) {
            for (int v = 0; v < n; v++) {
                // 计算 α 系数（u=0或v=0时特殊处理）
                double alphaU = (u == 0) ? 1 / Math.sqrt(n) : Math.sqrt(2.0 / n);
                double alphaV = (v == 0) ? 1 / Math.sqrt(n) : Math.sqrt(2.0 / n);

                double sum = 0.0;
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        // 计算余弦项
                        double cosI = Math.cos(((2 * i + 1) * u * pi) / (2.0 * n));
                        double cosJ = Math.cos(((2 * j + 1) * v * pi) / (2.0 * n));
                        sum += pixels[i][j] * cosI * cosJ;
                    }
                }
                dct[u][v] = alphaU * alphaV * sum;
            }
        }
        return dct;
    }

    /**
     * 提取 8x8 低频区域（DCT 矩阵的左上角，保留核心结构）
     */
    private static double[][] extractLowFrequency(double[][] dctMatrix) {
        double[][] lowFreq = new double[LOW_FREQ_SIZE][LOW_FREQ_SIZE];
        for (int i = 0; i < LOW_FREQ_SIZE; i++) {
            System.arraycopy(dctMatrix[i], 0, lowFreq[i], 0, LOW_FREQ_SIZE);
        }
        return lowFreq;
    }

    /**
     * 计算低频区域的平均值（排除 (0,0) 直流分量，避免其影响阈值）
     */
    private static double calculateLowFreqMean(double[][] lowFreqMatrix) {
        double sum = 0.0;
        int count = 0;
        for (int i = 0; i < LOW_FREQ_SIZE; i++) {
            for (int j = 0; j < LOW_FREQ_SIZE; j++) {
                if (i == 0 && j == 0) {
                    continue; // 跳过直流分量
                }
                sum += lowFreqMatrix[i][j];
                count++;
            }
        }
        return sum / count;
    }

    /**
     * 生成 64位哈希值（每个低频值与平均值比较，大于为1，小于为0）
     */
    private static String generateHash(double[][] lowFreqMatrix, double mean) {
        StringBuilder hash = new StringBuilder(64);
        for (int i = 0; i < LOW_FREQ_SIZE; i++) {
            for (int j = 0; j < LOW_FREQ_SIZE; j++) {
                hash.append(lowFreqMatrix[i][j] > mean ? '1' : '0');
            }
        }
        return hash.toString();
    }

    /**
     * 计算两个 PHash 的汉明距离（不同位的数量）
     * 距离越小，图片内容越相似（一般 ≤5 可认为相似）
     * @param hash1 第一个哈希值
     * @param hash2 第二个哈希值
     * @return 汉明距离（0-64）
     * @throws IllegalArgumentException 哈希值长度不匹配时抛出
     */
    public static int hammingDistance(String hash1, String hash2) {
        if (hash1 == null || hash2 == null) {
            throw new IllegalArgumentException("哈希值不能为null");
        }
        if (hash1.length() != hash2.length()) {
            throw new IllegalArgumentException("两个哈希值长度必须相同（应为64位）");
        }
        int distance = 0;
        for (int i = 0; i < hash1.length(); i++) {
            if (hash1.charAt(i) != hash2.charAt(i)) {
                distance++;
            }
        }
        return distance;
    }

}