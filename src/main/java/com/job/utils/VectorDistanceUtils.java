package com.job.utils;

import java.util.List;

public class VectorDistanceUtils {

    /**
     * 计算两个向量的欧氏距离 (支持 List<Double>)
     */
    public static double euclideanDistance(List<Double> v1, List<Double> v2) {
        if (v1.size() != v2.size()) {
            throw new IllegalArgumentException("向量维度不一致: " + v1.size() + " != " + v2.size());
        }

        double sum = 0.0;
        for (int i = 0; i < v1.size(); i++) {
            double diff = v1.get(i) - v2.get(i);
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

    /**
     * 计算两个向量的余弦距离 (支持 List<Double>)
     */
    public static double cosineDistance(List<Double> v1, List<Double> v2) {
        if (v1.size() != v2.size()) {
            throw new IllegalArgumentException("向量维度不一致: " + v1.size() + " != " + v2.size());
        }

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i < v1.size(); i++) {
            dotProduct += v1.get(i) * v2.get(i);
            norm1 += Math.pow(v1.get(i), 2);
            norm2 += Math.pow(v2.get(i), 2);
        }

        if (norm1 == 0 || norm2 == 0) {
            return 1.0; // 其中一个向量为零向量
        }

        double cosineSimilarity = dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
        return 1 - cosineSimilarity; // 余弦距离 = 1 - 余弦相似度
    }

    /**
     * 计算余弦相似度 (0-1之间，越大越相似)
     */
    public static double cosineSimilarity(List<Double> v1, List<Double> v2) {
        if (v1.size() != v2.size()) {
            throw new IllegalArgumentException("向量维度不一致: " + v1.size() + " != " + v2.size());
        }

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i < v1.size(); i++) {
            dotProduct += v1.get(i) * v2.get(i);
            norm1 += Math.pow(v1.get(i), 2);
            norm2 += Math.pow(v2.get(i), 2);
        }

        if (norm1 == 0 || norm2 == 0) {
            return 0.0; // 其中一个向量为零向量
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    // 保留原有的 float[] 方法以兼容旧代码
    public static float euclideanDistance(float[] v1, float[] v2) {
        if (v1.length != v2.length) {
            throw new IllegalArgumentException("向量维度不一致");
        }
        float sum = 0.0f;
        for (int i = 0; i < v1.length; i++) {
            float diff = v1[i] - v2[i];
            sum += diff * diff;
        }
        return (float) Math.sqrt(sum);
    }

    public static float cosineDistance(float[] v1, float[] v2) {
        if (v1.length != v2.length) {
            throw new IllegalArgumentException("向量维度不一致");
        }
        float dotProduct = 0.0f;
        float norm1 = 0.0f;
        float norm2 = 0.0f;
        for (int i = 0; i < v1.length; i++) {
            dotProduct += v1[i] * v2[i];
            norm1 += v1[i] * v1[i];
            norm2 += v2[i] * v2[i];
        }
        if (norm1 == 0 || norm2 == 0) {
            return 1.0f;
        }
        return 1 - (dotProduct / ((float) Math.sqrt(norm1) * (float) Math.sqrt(norm2)));
    }
}