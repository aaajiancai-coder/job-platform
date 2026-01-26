package com.job.ai;

import com.job.utils.VectorDistanceUtils;
import org.junit.jupiter.api.Test;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class EmbeddingModelTests {

    @Autowired
    private EmbeddingModel embeddingModel;

    /**
     * 基础向量化测试
     */
    @Test
    void testBasicEmbedding() {
        System.out.println("测试基础向量化...");
        float[] embedding = embeddingModel.embed("合肥工业大学是大专");
        System.out.println("向量维度: " + embedding.length);
        System.out.println("前10个值: " + Arrays.toString(Arrays.copyOf(embedding, 10)));
    }

    /**
     * 测试向量距离计算
     */
    @Test
    public void testVectorDistances() {
        try {
            // 1.测试数据
            String query = "global conflicts";
            String[] texts = new String[]{
                    "哈马斯称加沙下阶段停火谈判仍在进行 以方尚未做出承诺",
                    "土耳其、芬兰、瑞典与北约代表将继续就瑞典入约问题进行谈判",
                    "日本航空基地水井中检测出有机氟化物超标",
                    "国家游泳中心恢复游泳嬉水乐园等水上项目运营",
                    "我国首次在空间站开展舱外辐射生物学暴露实验",
            };

            // 2.向量化
            System.out.println("开始向量化查询文本...");
            float[] queryVector = embeddingModel.embed(query);
            System.out.println("查询文本向量维度: " + queryVector.length);

            System.out.println("开始向量化比较文本...");
            List<float[]> textVectors = embeddingModel.embed(Arrays.asList(texts));

            // 3.比较欧氏距离（越小越相似）
            System.out.println("\n=== 欧氏距离比较（越小越相似）===");
            for (int i = 0; i < textVectors.size(); i++) {
                float distance = VectorDistanceUtils.euclideanDistance(queryVector, textVectors.get(i));
                System.out.printf("文本%d: %.6f - %s\n", i+1, distance, texts[i]);
            }

            // 4.比较余弦距离（越小越相似）
            System.out.println("\n=== 余弦距离比较（越小越相似）===");
            for (int i = 0; i < textVectors.size(); i++) {
                float distance = VectorDistanceUtils.cosineDistance(queryVector, textVectors.get(i));
                System.out.printf("文本%d: %.6f - %s\n", i+1, distance, texts[i]);
            }

        } catch (Exception e) {
            System.err.println("测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试相似文本识别
     */
    @Test
    public void testSimilarityDetection() {
        try {
            // 相似文本测试
            String[] similarTexts = {
                    "我喜欢吃苹果",
                    "我爱吃苹果",
                    "苹果是我最喜欢的水果",
                    "今天天气很好",
                    "编程很有趣"
            };

            String query = "苹果很好吃";

            float[] queryVector = embeddingModel.embed(query);
            List<float[]> textVectors = embeddingModel.embed(Arrays.asList(similarTexts));

            System.out.println("=== 相似文本识别测试 ===");
            System.out.println("查询文本: " + query);
            System.out.println();

            for (int i = 0; i < similarTexts.length; i++) {
                // 使用余弦相似度（越大越相似）
                float similarity = 1 - VectorDistanceUtils.cosineDistance(queryVector, textVectors.get(i));
                System.out.printf("相似度: %.4f - 文本: %s\n", similarity, similarTexts[i]);
            }

        } catch (Exception e) {
            System.err.println("测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 性能测试：批量向量化
     */
    @Test
    public void testBatchEmbedding() {
        try {
            long startTime = System.currentTimeMillis();

            // 生成测试文本
            String[] testTexts = new String[10];
            for (int i = 0; i < 10; i++) {
                testTexts[i] = "这是第" + (i+1) + "个测试文本，用于验证批量向量化性能";
            }

            List<float[]> embeddings = embeddingModel.embed(Arrays.asList(testTexts));

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            System.out.println("批量处理 " + testTexts.length + " 个文本");
            System.out.println("总耗时: " + duration + "ms");
            System.out.println("平均每个文本: " + (double)duration/testTexts.length + "ms");
            System.out.println("向量维度: " + embeddings.get(0).length);

        } catch (Exception e) {
            System.err.println("性能测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 简单测试 Ollama 是否工作
     */
    @Test
    public void testOllamaSimple() {
        try {
            System.out.println("测试 Ollama 嵌入模型...");

            float[] embedding = embeddingModel.embed("Hello, world!");
            System.out.println("✅ 嵌入模型工作正常");
            System.out.println("向量维度: " + embedding.length);
            System.out.println("前5个值: " + Arrays.toString(Arrays.copyOf(embedding, 5)));

        } catch (Exception e) {
            System.err.println("❌ 嵌入模型错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}