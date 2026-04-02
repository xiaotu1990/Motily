package com.motily.dns;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;

@ApplicationScoped
public class DnsService {

    public String generateDns() {
        return DnsGenerator.generateDns();
    }

    public String generateDnsFromParents(String fatherDns, String motherDns) {
        return DnsGenerator.generateDnsFromParents(fatherDns, motherDns);
    }

    public Map<String, Object> parseDns(String dns) {
        return DnsParser.parseDns(dns);
    }

    public boolean validateDns(String dns) {
        try {
            byte[] bytes = java.util.Base64.getUrlDecoder().decode(dns);
            return bytes.length == 256;
        } catch (Exception e) {
            return false;
        }
    }
}
