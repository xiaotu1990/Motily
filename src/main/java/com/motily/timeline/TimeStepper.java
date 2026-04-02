package com.motily.timeline;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TimeStepper {
    public int calculateNextYear(int currentYear, int age) {
        // 根据年龄计算下一个时间步长
        if (age < 30) {
            return currentYear + 1; // 0-30岁：1年/步
        } else if (age < 60) {
            return currentYear + 3; // 30-60岁：3年/步
        } else {
            return currentYear + 5; // 60+岁：5年/步
        }
    }
    
    public int calculateAge(int birthYear, int currentYear) {
        return currentYear - birthYear;
    }
    
    public int calculateStepCount(int startYear, int currentYear, int birthYear) {
        int stepCount = 0;
        int year = startYear;
        while (year < currentYear) {
            int age = calculateAge(birthYear, year);
            year = calculateNextYear(year, age);
            stepCount++;
        }
        return stepCount;
    }
}
