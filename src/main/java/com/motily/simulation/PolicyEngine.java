package com.motily.simulation;

import com.motily.engine.DemographyEngine;
import com.motily.engine.EconomicEngine;
import com.motily.engine.MobilityEngine;
import com.motily.human.HumanService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@ApplicationScoped
public class PolicyEngine {

    private static final Logger log = Logger.getLogger(PolicyEngine.class.getName());

    @Inject
    HumanService humanService;

    @Inject
    MobilityEngine mobilityEngine;

    @Inject
    EconomicEngine economicEngine;

    @Inject
    DemographyEngine demographyEngine;

    public static final String POLICY_TAX_REFORM = "tax_reform";
    public static final String POLICY_EDUCATION = "education_investment";
    public static final String POLICY_HEALTHCARE = "healthcare_coverage";
    public static final String POLICY_MIN_WAGE = "minimum_wage";
    public static final String POLICY_BIRTH_INCENTIVE = "birth_incentive";
    public static final String POLICY_MIGRATION = "migration_policy";

    private final Map<String, Policy> activePolicies = new HashMap<>();

    public static class Policy {
        public String type;
        public Map<String, Object> params;
        public int targetYear;
        public String status;
        public double intensity;
        public String description;

        public Policy() {
            this.params = new HashMap<>();
        }

        public Policy(String type, Map<String, Object> params, int targetYear, String status, double intensity, String description) {
            this.type = type;
            this.params = params != null ? params : new HashMap<>();
            this.targetYear = targetYear;
            this.status = status;
            this.intensity = intensity;
            this.description = description;
        }
    }

    public void applyPolicy(Policy policy, int year) {
        log.info("应用政策: " + policy.type + " 于 " + year + "年, 强度=" + policy.intensity);

        switch (policy.type) {
            case POLICY_TAX_REFORM -> applyTaxReform(policy, year);
            case POLICY_EDUCATION -> applyEducationInvestment(policy, year);
            case POLICY_HEALTHCARE -> applyHealthcareCoverage(policy, year);
            case POLICY_MIN_WAGE -> applyMinimumWage(policy, year);
            case POLICY_BIRTH_INCENTIVE -> applyBirthIncentive(policy, year);
            case POLICY_MIGRATION -> applyMigrationPolicy(policy, year);
            default -> log.warning("未知政策类型: " + policy.type);
        }
    }

    private void applyTaxReform(Policy policy, int year) {
        log.info("[Tax Reform] 税收改革政策生效 - 年份: " + year + ", 强度: " + policy.intensity);
    }

    private void applyEducationInvestment(Policy policy, int year) {
        log.info("[Education Investment] 教育投入政策生效 - 年份: " + year + ", 强度: " + policy.intensity);
    }

    private void applyHealthcareCoverage(Policy policy, int year) {
        log.info("[Healthcare Coverage] 医保覆盖政策生效 - 年份: " + year + ", 强度: " + policy.intensity);
    }

    private void applyMinimumWage(Policy policy, int year) {
        log.info("[Minimum Wage] 最低工资政策生效 - 年份: " + year + ", 强度: " + policy.intensity);
    }

    private void applyBirthIncentive(Policy policy, int year) {
        log.info("[Birth Incentive] 生育激励政策生效 - 年份: " + year + ", 强度: " + policy.intensity);
    }

    private void applyMigrationPolicy(Policy policy, int year) {
        log.info("[Migration Policy] 人口迁移政策生效 - 年份: " + year + ", 强度: " + policy.intensity);
    }

    public double getPolicyModifier(String indicatorType) {
        return 1.0;
    }
}
