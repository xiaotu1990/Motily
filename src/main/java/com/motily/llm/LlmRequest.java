package com.motily.llm;

public class LlmRequest {
    public String prompt;
    public int max_tokens;
    public double temperature;
    public String api_key;

    public LlmRequest(String prompt, int max_tokens, double temperature, String api_key) {
        this.prompt = prompt;
        this.max_tokens = max_tokens;
        this.temperature = temperature;
        this.api_key = api_key;
    }
}
