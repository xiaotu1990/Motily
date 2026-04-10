# Motily

🌍 An LLM-Powered Scalable Platform for Digital Human Society Evolution Simulation

> **Note:** This project is currently in early development for proof-of-concept purposes. Please do not use it in production.


![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)



![Tech Stack](https://img.shields.io/badge/Stack-Quarkus%20%2B%20MySQL%20%2B%20LLM-green.svg)



![Stars](https://img.shields.io/github/stars/xiaotu1990/Motily.svg?style=social)



***

## Language

- [English](README.md)
- [中文](README.zh.md)

***

## Project Introduction

Motily is an enterprise-grade lightweight open-source framework built on **Quarkus 3.x + MySQL 8.0**, focusing on **scalable simulation of digital humans' full-life-cycle social evolution**.

Its core innovation integrates four key capabilities: *digital DNS genetic coding*, *intergenerational social inheritance*, *LLM-powered life trajectory modeling*, and *timeline-driven simulation*. It supports digital humans' autonomous growth from childhood to old age, family lineage continuity, social class mobility, and rule emergence, providing a high-performance, extensible open-source solution for social science research, multi-agent AI systems, and digital twin development.



***

## Core Features

### 🔬 Digital DNS Heredity



* 64-bit unique DNS encoding, defining digital humans' innate personality, talents, and ideological tendencies

* Supports parental DNS hybridization and random mutation to ensure social diversity

* Deep integration of genetic coding with social attributes to build differentiated individual foundations

### 👨‍👩‍👧‍👦 Intergenerational Inheritance



* Inherits parents' wealth, social class, ideas, and social capital

* Based on the scientific inheritance formula of "Father 45% + Mother 45% + Social Environment 5% + Mutation 5%"

* Supports family tree construction and multi-generational continuity

### 🤖 LLM-Powered Trajectory



* Group aggregation reasoning strategy, only ≤300 LLM calls per round for 100,000 people

* Strict rule constraints (age, resources, personality consistency) to avoid logical contradictions

* Adapts to local LLMs (Qwen3.5-0.8B, Qwen-7B, Llama3) and commercial APIs for flexible deployment

### ⏳ Timeline-Controlled Simulation



* Dynamic time granularity (0-30 years: 1 year/step, 30-60 years: 3 years/step, 60+ years: 5 years/step)

* Supports input simulation years to automatically advance social evolution

* Pausable, traceable, and reproducible, suitable for research scenarios

### 📊 Social Evolution & Emergence



* Automatically calculates macro indicators such as class distribution, occupational structure, and idea propagation

* Supports spontaneous emergence of social rules and culture without manual intervention

* Provides visualized social state output to intuitively present evolution results

### ⚡ High-Performance Architecture



* Supports stable simulation of 100,000 digital humans, with single-step simulation time < 30 seconds

* Based on JBoot cache and MySQL batch operation optimization, runs on ordinary servers

* Natively supports distributed expansion for larger-scale simulation needs



***

## Technology Stack



| Category | Technology Selection |
| ------- | ------------------ |
| Core Framework | Quarkus 3.13+ (built-in Undertow + Hibernate ORM) |
| Data Storage | MySQL 8.0 + Quarkus Cache (Caffeine) + JBoot Cache |
| LLM Integration | Quarkus HTTP Client + Local LLM (Qwen3.5-0.8B) / Commercial API |
| Development Language | Java 23 |
| Build Tool | Maven/Gradle |
| Visualization (Optional) | Vue3 + Element-Plus / Quarkus + Qute |
| Compatible Systems | Windows/macOS/Linux |



***

## Quick Start

### 1. Environment Preparation



* JDK 23

* MySQL 8.0

* Maven 3.6+

* (Optional) Local LLM (recommended: Qwen3.5-0.8B)

### 2. Deployment



```bash
# 1. Clone the repository
git clone https://github.com/xiaotu1990/Motily.git

cd Motily

# 2. Configure database (modify application.properties)
vim src/main/resources/application.properties

# Configuration: quarkus.datasource.jdbc.url=jdbc:mysql://localhost:3306/motily
#                quarkus.datasource.username=root
#                quarkus.datasource.password=your-password

# 3. Initialize database (execute SQL script)
mysql -u root -p < src/main/resources/import.sql

# 4. Compile and package
mvn clean package -Dmaven.test.skip=true

# 5. Start the project
java -jar target/motily-1.0.0.jar
```

### 3. First Use



1. Access the backend panel: [http://localhost:8080](http://localhost:8080)

2. Generate initial 100,000 digital human DNS codes

3. Input simulation years (e.g., 50 years) and start social evolution simulation

4. View digital human life trajectories and social macro state reports

***

## License

This project is licensed under the **Apache License 2.0** - see the [LICENSE](LICENSE) file for details.

Key terms:



* ✅ Permits commercial use, secondary development, and private deployment

* ✅ Must retain copyright notices and license information

* ✅ Modified code must be marked with modification records

* ✅ Patent protection: contributors grant worldwide free patent licenses

***

## Contact Us

Welcome to star 🌟 this project and follow the technical breakthroughs of digital human social evolution!