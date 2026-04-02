package com.motily.llm;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class LlmService {
    @Inject
    @RestClient
    LlmClient llmClient;
    
    @Inject
    @ConfigProperty(name = "motily.llm.api-key")
    String apiKey;
    
    @Inject
    @ConfigProperty(name = "motily.llm.max-tokens")
    int maxTokens;
    
    public String generate(String prompt) {
        LlmRequest request = new LlmRequest(prompt, maxTokens, 0.7, apiKey);
        LlmResponse response = llmClient.generate(request);
        return response.text;
    }
    
    public String generateHumanBehavior(String personality, String talent, String belief, String situation) {
        String prompt = String.format(
            "基于以下特征，生成一个数字人的行为决策：\n" +
            "性格：%s\n" +
            "天赋：%s\n" +
            "观念：%s\n" +
            "情境：%s\n" +
            "请给出具体的行为决策，包括行动和理由。",
            personality, talent, belief, situation
        );
        return generate(prompt);
    }
    
    public String generateSocialTrend(int year, String socialIndicators) {
        String prompt = String.format(
            "基于以下社会指标，预测%d年的社会趋势：\n" +
            "社会指标：%s\n" +
            "请给出具体的社会趋势预测，包括经济、文化、科技等方面。",
            year, socialIndicators
        );
        return generate(prompt);
    }
}
