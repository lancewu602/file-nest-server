package com.example.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

/**
 * 图片dHash计算工具类
 * 不依赖第三方库，线程安全，提供完善的流处理
 */
public final class DHashUtil {

    // dHash默认尺寸：缩放为9x8像素，便于计算8x8的差异
    private static final int RESIZE_WIDTH = 9;
    private static final int RESIZE_HEIGHT = 8;

    /**
     * 计算图片的dHash值
     * @param imagePath 图片文件
     * @return 图片的dHash值（16位十六进制字符串）
     * @throws IOException 如果读取或处理图片失败
     */
    public static String calculateDHash(Path imagePath) throws IOException {
        // 缩放图片并转为灰度图
        Path newImagePath = MagickUtil.resizeAndGray(imagePath, RESIZE_WIDTH, RESIZE_HEIGHT);
        BufferedImage grayImage = ImageIO.read(newImagePath.toFile());

        // 计算差异哈希
        return computeDHash(grayImage);
    }

    /**
     * 计算灰度图的dHash值
     */
    private static String computeDHash(BufferedImage grayImage) {
        int width = grayImage.getWidth();
        int height = grayImage.getHeight();

        // 计算差异值
        StringBuilder binaryString = new StringBuilder();
        for (int y = 0; y < height; y++) {
            int row = y * width;
            for (int x = 0; x < width - 1; x++) {
                int currentPixel = grayImage.getRGB(x, y) & 0xFF; // 取灰度值
                int nextPixel = grayImage.getRGB(x + 1, y) & 0xFF;

                // 当前像素比下一个亮则为1，否则为0
                binaryString.append(currentPixel > nextPixel ? "1" : "0");
            }
        }

        // 将二进制字符串转换为十六进制字符串
        return binaryToHex(binaryString.toString());
    }

    /**
     * 将二进制字符串转换为十六进制字符串
     */
    private static String binaryToHex(String binary) {
        StringBuilder hex = new StringBuilder();
        // 每4位二进制转换为1位十六进制
        for (int i = 0; i < binary.length(); i += 4) {
            String segment = binary.substring(i, Math.min(i + 4, binary.length()));
            int value = Integer.parseInt(segment, 2);
            hex.append(String.format("%01x", value));
        }
        return hex.toString();
    }

    /**
     * 计算两个dHash值的汉明距离，用于判断图片相似度
     * 距离越小，相似度越高
     * @param hash1 第一个dHash值
     * @param hash2 第二个dHash值
     * @return 汉明距离
     */
    public static int hammingDistance(String hash1, String hash2) {
        if (hash1 == null || hash2 == null || hash1.length() != hash2.length()) {
            throw new IllegalArgumentException("dHash值不能为空且长度必须相同");
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