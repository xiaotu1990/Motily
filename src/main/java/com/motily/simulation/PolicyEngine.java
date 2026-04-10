package com.motily.simulation;

import com.motily.human.Human;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@ApplicationScoped
public class PolicyEngine {

    private static final String[] POLICY_TYPES = {"税收改革", "教育投入", "医疗改革", "社保政策", "就业政策"};
    private double[] policyEffects = new double[POLICY_TYPES.length];

    public PolicyEngine() {
        // 初始化政策效果
        for (int i = 0; i < POLICY_TYPES.length; i++) {
            policyEffects[i] = 0.0;
        }
    }

    @Transactional
    public void processWeeklyPolicy(int currentYear, int currentWeek, Random rng) {
        updatePolicyEffects(rng);
        applyPolicyEffects(currentYear);
    }

    protected void updatePolicyEffects(Random rng) {
        for (int i = 0; i < POLICY_TYPES.length; i++) {
            if (rng.nextDouble() < 0.01) {
                policyEffects[i] = (rng.nextDouble() * 0.2 - 0.1); // -0.1 到 0.1
            }
        }
    }

    @Transactional
    protected void applyPolicyEffects(int currentYear) {
        List<Human> humans = Human.findAll().list();
        
        for (Human human : humans) {
            if (human.deathYear != null) {
                continue;
            }
            
            applyTaxPolicy(human);
            applyEducationPolicy(human);
            applyHealthcarePolicy(human);
            applySocialSecurityPolicy(human);
            applyEmploymentPolicy(human);
        }
    }

    @Transactional
    protected void applyTaxPolicy(Human human) {
        double taxRate = calculateTaxRate(human.wealth);
        double taxAmount = human.wealth * taxRate / 52.0;
        if (taxAmount > 0 && human.wealth > taxAmount) {
            human.wealth -= taxAmount;
            human.updatedAt = LocalDateTime.now();
            human.persist();
        }
    }

    private double calculateTaxRate(double wealth) {
        if (wealth > 1000000) {
            return 0.3;
        } else if (wealth > 500000) {
            return 0.2;
        } else if (wealth > 200000) {
            return 0.15;
        } else if (wealth > 100000) {
            return 0.1;
        } else {
            return 0.05;
        }
    }

    @Transactional
    protected void applyEducationPolicy(Human human) {
        int age = 2026 - human.birthYear;
        if (age >= 6 && age <= 18) {
            double educationSubsidy = 100.0;
            human.wealth += educationSubsidy / 52.0;
            human.updatedAt = LocalDateTime.now();
            human.persist();
        }
    }

    @Transactional
    protected void applyHealthcarePolicy(Human human) {
        if (human.healthStatus.equals("疾病") || human.healthStatus.equals("重疾")) {
            double healthcareSubsidy = human.wealth * 0.01;
            human.wealth += healthcareSubsidy;
            human.updatedAt = LocalDateTime.now();
            human.persist();
        }
    }

    @Transactional
    protected void applySocialSecurityPolicy(Human human) {
        int age = 2026 - human.birthYear;
        if (age >= 65) {
            double pension = 2000.0;
            human.wealth += pension / 52.0;
            human.updatedAt = LocalDateTime.now();
            human.persist();
        }
    }

    @Transactional
    protected void applyEmploymentPolicy(Human human) {
        if (human.occupation == null) {
            double unemploymentBenefit = 1000.0;
            human.wealth += unemploymentBenefit / 52.0;
            human.updatedAt = LocalDateTime.now();
            human.persist();
        }
    }

    public void implementPolicy(String policyType, double intensity) {
        int policyIndex = getPolicyIndex(policyType);
        if (policyIndex != -1) {
            policyEffects[policyIndex] = intensity;
        }
    }

    private int getPolicyIndex(String policyType) {
        for (int i = 0; i < POLICY_TYPES.length; i++) {
            if (POLICY_TYPES[i].equals(policyType)) {
                return i;
            }
        }
        return -1;
    }

    public double getPolicyEffect(String policyType) {
        int policyIndex = getPolicyIndex(policyType);
        return policyIndex != -1 ? policyEffects[policyIndex] : 0.0;
    }

    public String getMostEffectivePolicy() {
        double maxEffect = -1;
        int bestPolicyIndex = 0;
        
        for (int i = 0; i < POLICY_TYPES.length; i++) {
            if (Math.abs(policyEffects[i]) > maxEffect) {
                maxEffect = Math.abs(policyEffects[i]);
                bestPolicyIndex = i;
            }
        }
        
        return POLICY_TYPES[bestPolicyIndex];
    }

    public double calculatePolicyImpact(String policyType, Human human) {
        int policyIndex = getPolicyIndex(policyType);
        if (policyIndex == -1) {
            return 0.0;
        }
        
        double baseImpact = policyEffects[policyIndex];
        double wealthFactor = 1.0;
        
        switch (policyType) {
            case "税收改革":
                wealthFactor = human.wealth / 100000.0;
                break;
            case "教育投入":
                int age = 2026 - human.birthYear;
                wealthFactor = (age >= 6 && age <= 18) ? 1.0 : 0.0;
                break;
            case "医疗改革":
                wealthFactor = (human.healthStatus.equals("疾病") || human.healthStatus.equals("重疾")) ? 1.0 : 0.0;
                break;
            case "社保政策":
                age = 2026 - human.birthYear;
                wealthFactor = (age >= 65) ? 1.0 : 0.0;
                break;
            case "就业政策":
                wealthFactor = (human.occupation == null) ? 1.0 : 0.0;
                break;
        }
        
        return baseImpact * wealthFactor;
    }

    public void adjustPolicy(String policyType, double adjustment) {
        int policyIndex = getPolicyIndex(policyType);
        if (policyIndex != -1) {
            policyEffects[policyIndex] = Math.max(-0.2, Math.min(0.2, policyEffects[policyIndex] + adjustment));
        }
    }

    public void resetPolicies() {
        for (int i = 0; i < POLICY_TYPES.length; i++) {
            policyEffects[i] = 0.0;
        }
    }
}