package com.motily.dna;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * DNA编码和解码工具类
 * 支持2位/特征的编码规则，将特征值编码为Base64字符串
 */
public class DnaEncoderDecoder {
    
    // 特征强度映射
    private static final Map<Integer, String> INT_TO_BINARY = new HashMap<>();
    private static final Map<String, Integer> BINARY_TO_INT = new HashMap<>();
    
    static {
        // 初始化映射
        INT_TO_BINARY.put(0, "00");
        INT_TO_BINARY.put(1, "01");
        INT_TO_BINARY.put(2, "10");
        INT_TO_BINARY.put(3, "11");
        
        BINARY_TO_INT.put("00", 0);
        BINARY_TO_INT.put("01", 1);
        BINARY_TO_INT.put("10", 2);
        BINARY_TO_INT.put("11", 3);
    }
    
    /**
     * 将特征值数组编码为DNA字符串
     * @param featureValues 特征值数组，长度为128，每个值范围为0-3
     * @return Base64编码的DNA字符串
     */
    public static String encode(int[] featureValues) {
        if (featureValues == null || featureValues.length != 128) {
            throw new IllegalArgumentException("特征值数组长度必须为128");
        }
        
        // 验证特征值范围
        for (int value : featureValues) {
            if (value < 0 || value > 3) {
                throw new IllegalArgumentException("特征值必须在0-3之间");
            }
        }
        
        // 构建二进制字符串
        StringBuilder binaryBuilder = new StringBuilder();
        for (int value : featureValues) {
            binaryBuilder.append(INT_TO_BINARY.get(value));
        }
        
        // 确保长度为256位
        String binaryString = binaryBuilder.toString();
        if (binaryString.length() != 256) {
            throw new IllegalArgumentException("二进制字符串长度必须为256位");
        }
        
        // 将二进制字符串转换为字节数组
        byte[] bytes = binaryStringToBytes(binaryString);
        
        // 编码为Base64字符串
        return Base64.getEncoder().encodeToString(bytes);
    }
    
    /**
     * 将DNA字符串解码为特征值数组
     * @param dnaString Base64编码的DNA字符串
     * @return 特征值数组，长度为128，每个值范围为0-3
     */
    public static int[] decode(String dnaString) {
        if (dnaString == null || dnaString.isEmpty()) {
            throw new IllegalArgumentException("DNA字符串不能为空");
        }
        
        try {
            // 解码Base64字符串为字节数组
            byte[] bytes = Base64.getDecoder().decode(dnaString);
            
            // 将字节数组转换为二进制字符串
            String binaryString = bytesToBinaryString(bytes);
            
            // 确保长度为256位
            if (binaryString.length() != 256) {
                throw new IllegalArgumentException("二进制字符串长度必须为256位");
            }
            
            // 解析特征值
            int[] featureValues = new int[128];
            for (int i = 0; i < 128; i++) {
                int startIndex = i * 2;
                int endIndex = startIndex + 2;
                String binaryValue = binaryString.substring(startIndex, endIndex);
                featureValues[i] = BINARY_TO_INT.get(binaryValue);
            }
            
            return featureValues;
        } catch (Exception e) {
            throw new IllegalArgumentException("无效的DNA字符串", e);
        }
    }
    
    /**
     * 将二进制字符串转换为字节数组
     * @param binaryString 二进制字符串
     * @return 字节数组
     */
    private static byte[] binaryStringToBytes(String binaryString) {
        int length = binaryString.length();
        int byteLength = (length + 7) / 8;
        byte[] bytes = new byte[byteLength];
        
        for (int i = 0; i < length; i += 8) {
            int endIndex = Math.min(i + 8, length);
            String byteString = binaryString.substring(i, endIndex);
            // 补零
            while (byteString.length() < 8) {
                byteString += "0";
            }
            byte b = (byte) Integer.parseInt(byteString, 2);
            bytes[i / 8] = b;
        }
        
        return bytes;
    }
    
    /**
     * 将字节数组转换为二进制字符串
     * @param bytes 字节数组
     * @return 二进制字符串
     */
    private static String bytesToBinaryString(byte[] bytes) {
        StringBuilder binaryBuilder = new StringBuilder();
        
        for (byte b : bytes) {
            // 转换为8位二进制字符串
            String byteString = Integer.toBinaryString(b & 0xFF);
            // 补零
            while (byteString.length() < 8) {
                byteString = "0" + byteString;
            }
            binaryBuilder.append(byteString);
        }
        
        // 截取前256位
        String binaryString = binaryBuilder.toString();
        if (binaryString.length() > 256) {
            binaryString = binaryString.substring(0, 256);
        }
        
        return binaryString;
    }
    
    /**
     * 验证DNA字符串是否有效
     * @param dnaString Base64编码的DNA字符串
     * @return 是否有效
     */
    public static boolean isValidDna(String dnaString) {
        try {
            int[] featureValues = decode(dnaString);
            return featureValues.length == 128;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 生成随机DNA字符串
     * @return 随机生成的DNA字符串
     */
    public static String generateRandomDna() {
        int[] featureValues = new int[128];
        for (int i = 0; i < 128; i++) {
            // 随机生成0-3之间的特征值
            featureValues[i] = (int) (Math.random() * 4);
        }
        return encode(featureValues);
    }
    
    /**
     * 获取指定特征的强度值
     * @param dnaString DNA字符串
     * @param featureId 特征ID
     * @return 特征强度值
     */
    public static int getFeatureValue(String dnaString, int featureId) {
        if (!DnaStructure.isValidFeatureId(featureId)) {
            throw new IllegalArgumentException("无效的特征ID");
        }
        
        int[] featureValues = decode(dnaString);
        return featureValues[featureId];
    }
    
    /**
     * 设置指定特征的强度值
     * @param dnaString DNA字符串
     * @param featureId 特征ID
     * @param value 特征强度值
     * @return 更新后的DNA字符串
     */
    public static String setFeatureValue(String dnaString, int featureId, int value) {
        if (!DnaStructure.isValidFeatureId(featureId)) {
            throw new IllegalArgumentException("无效的特征ID");
        }
        
        if (value < 0 || value > 3) {
            throw new IllegalArgumentException("特征值必须在0-3之间");
        }
        
        int[] featureValues = decode(dnaString);
        featureValues[featureId] = value;
        return encode(featureValues);
    }
}