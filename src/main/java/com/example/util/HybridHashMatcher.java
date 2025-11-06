package com.example.util;

import java.io.IOException;
import java.nio.file.Path;

/**
 * pHash + DHash 双重校验工具类（优化版：pHash优先，不满足时再计算dHash）
 */
public class HybridHashMatcher {

    // 高可信度阈值（仅pHash即可判定）
    private static final int HIGH_P_MAX = 3;

    // 中可信度阈值（pHash不达标时，需dHash辅助）
    private static final int MID_P_MAX = 5;
    private static final int MID_D_MAX = 4;
    private static final double MID_AVG_MAX = 3.5;

    // 低可信度阈值（pHash和dHash联合判断）
    private static final int LOW_P_MAX = 8;
    private static final int LOW_D_MAX = 6;
    private static final int LOW_MIN_ONE_MAX = 5;

    /**
     * 双重校验图片相似度（pHash优先，不满足时再计算dHash）
     * @param img1Path 第一张图片路径
     * @param img2Path 第二张图片路径
     * @return 匹配等级（HIGH/MID/LOW/NONE）
     */
    public static MatchLevel match(Path img1Path, Path img2Path) throws IOException {
        // 1. 先计算pHash并判断高可信度场景
        String pHash1 = PHashUtil.calculatePHash(img1Path);
        String pHash2 = PHashUtil.calculatePHash(img2Path);
        int pDistance = PHashUtil.hammingDistance(pHash1, pHash2);

        // 高可信度：pHash达标则直接返回，无需计算dHash
        if (pDistance <= HIGH_P_MAX) {
            return MatchLevel.HIGH;
        }

        // 2. pHash未达高可信度，计算dHash进行二次校验
        String dHash1 = DHashUtil.calculateDHash(img1Path);
        String dHash2 = DHashUtil.calculateDHash(img2Path);
        int dDistance = DHashUtil.hammingDistance(dHash1, dHash2);

        // 3. 结合pHash和dHash判断中/低可信度
        if ((pDistance <= MID_P_MAX && dDistance <= MID_D_MAX)
            || ((pDistance + dDistance) / 2.0 <= MID_AVG_MAX)) {
            return MatchLevel.MID;
        } else if (pDistance <= LOW_P_MAX && dDistance <= LOW_D_MAX
            && (pDistance <= LOW_MIN_ONE_MAX || dDistance <= LOW_MIN_ONE_MAX)) {
            return MatchLevel.LOW;
        } else {
            return MatchLevel.NONE;
        }
    }

    /**
     * 匹配等级枚举
     */
    public enum MatchLevel {
        HIGH("高度相似（可信度95%+）"),
        MID("相似（可信度70%-95%）"),
        LOW("可能相似（可信度50%-70%）"),
        NONE("不相似（可信度<50%）");

        public final String desc;

        MatchLevel(String desc) {
            this.desc = desc;
        }
    }
}