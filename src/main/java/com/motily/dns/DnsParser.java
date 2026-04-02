package com.motily.dns;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class DnsParser {
    private static final int DNS_LENGTH = 256;

    public static Map<String, Object> parseDns(String dns) {
        byte[] bytes = Base64.getUrlDecoder().decode(dns);
        Map<String, Object> attributes = new HashMap<>();

        // 解析性格特征 (0-31 bytes)
        attributes.put("personality", parsePersonality(bytes, 0, 32));

        // 解析天赋特征 (32-63 bytes)
        attributes.put("talent", parseTalent(bytes, 32, 64));

        // 解析观念倾向 (64-95 bytes)
        attributes.put("belief", parseBelief(bytes, 64, 96));

        // 解析健康状况 (96-127 bytes)
        attributes.put("health", parseHealth(bytes, 96, 128));

        // 解析社交能力 (128-159 bytes)
        attributes.put("social", parseSocial(bytes, 128, 160));

        // 解析财富潜力 (160-191 bytes)
        attributes.put("wealth", parseWealth(bytes, 160, 192));

        // 解析职业倾向 (192-223 bytes)
        attributes.put("occupation", parseOccupation(bytes, 192, 224));

        // 解析其他特征 (224-255 bytes)
        attributes.put("other", parseOther(bytes, 224, 256));

        return attributes;
    }

    private static Map<String, Double> parsePersonality(byte[] bytes, int start, int end) {
        Map<String, Double> personality = new HashMap<>();
        personality.put("extraversion", normalizeByte(bytes[start]));
        personality.put("neuroticism", normalizeByte(bytes[start + 1]));
        personality.put("openness", normalizeByte(bytes[start + 2]));
        personality.put("agreeableness", normalizeByte(bytes[start + 3]));
        personality.put("conscientiousness", normalizeByte(bytes[start + 4]));
        return personality;
    }

    private static Map<String, Double> parseTalent(byte[] bytes, int start, int end) {
        Map<String, Double> talent = new HashMap<>();
        talent.put("intelligence", normalizeByte(bytes[start]));
        talent.put("creativity", normalizeByte(bytes[start + 1]));
        talent.put("athleticism", normalizeByte(bytes[start + 2]));
        talent.put("artistic", normalizeByte(bytes[start + 3]));
        talent.put("leadership", normalizeByte(bytes[start + 4]));
        return talent;
    }

    private static Map<String, Double> parseBelief(byte[] bytes, int start, int end) {
        Map<String, Double> belief = new HashMap<>();
        belief.put("conservatism", normalizeByte(bytes[start]));
        belief.put("liberalism", normalizeByte(bytes[start + 1]));
        belief.put("religiosity", normalizeByte(bytes[start + 2]));
        belief.put("environmentalism", normalizeByte(bytes[start + 3]));
        belief.put("socialJustice", normalizeByte(bytes[start + 4]));
        return belief;
    }

    private static Map<String, Double> parseHealth(byte[] bytes, int start, int end) {
        Map<String, Double> health = new HashMap<>();
        health.put("physical", normalizeByte(bytes[start]));
        health.put("mental", normalizeByte(bytes[start + 1]));
        health.put("longevity", normalizeByte(bytes[start + 2]));
        return health;
    }

    private static Map<String, Double> parseSocial(byte[] bytes, int start, int end) {
        Map<String, Double> social = new HashMap<>();
        social.put("charisma", normalizeByte(bytes[start]));
        social.put("empathy", normalizeByte(bytes[start + 1]));
        social.put("communication", normalizeByte(bytes[start + 2]));
        social.put("networking", normalizeByte(bytes[start + 3]));
        return social;
    }

    private static Map<String, Double> parseWealth(byte[] bytes, int start, int end) {
        Map<String, Double> wealth = new HashMap<>();
        wealth.put("earningPotential", normalizeByte(bytes[start]));
        wealth.put("savingTendency", normalizeByte(bytes[start + 1]));
        wealth.put("investmentSkill", normalizeByte(bytes[start + 2]));
        return wealth;
    }

    private static Map<String, Double> parseOccupation(byte[] bytes, int start, int end) {
        Map<String, Double> occupation = new HashMap<>();
        occupation.put("technical", normalizeByte(bytes[start]));
        occupation.put("business", normalizeByte(bytes[start + 1]));
        occupation.put("creative", normalizeByte(bytes[start + 2]));
        occupation.put("service", normalizeByte(bytes[start + 3]));
        occupation.put("academic", normalizeByte(bytes[start + 4]));
        return occupation;
    }

    private static Map<String, Double> parseOther(byte[] bytes, int start, int end) {
        Map<String, Double> other = new HashMap<>();
        other.put("luck", normalizeByte(bytes[start]));
        other.put("adaptability", normalizeByte(bytes[start + 1]));
        other.put("resilience", normalizeByte(bytes[start + 2]));
        return other;
    }

    private static double normalizeByte(byte b) {
        return (b + 128.0) / 255.0;
    }
}
