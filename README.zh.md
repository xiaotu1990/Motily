# Motily

🌍 大模型驱动的数字人类社会演化可扩展平台

> **注意：** 该项目目前处于想法验证的早期开发阶段，请勿在生产环境中使用。


![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)



![Tech Stack](https://img.shields.io/badge/Stack-JBoot%20%2B%20MySQL%20%2B%20LLM-green.svg)



![Stars](https://img.shields.io/github/stars/xiaotu1990/Motily.svg?style=social)



***

## 语言

- [English](README.md)
- [中文](README.zh.md)

***

## 项目介绍

Motily 是一个基于 **JBoot 3.x + MySQL 8.0** 构建的企业级轻量级开源框架，专注于 **数字人类全生命周期社会演化的可扩展模拟**。

其核心创新集成了四大关键能力：*数字 DNS 遗传编码*、*代际社会传承*、*大模型轨迹驱动*和*时间轴可控模拟*。支持数字人类从童年到老年的自主成长、家族谱系延续、社会阶层流动和规则涌现，为社会科学研究、多智能体 AI 系统和数字孪生开发提供高性能、可扩展的开源解决方案。



***

## 核心特性

### 🔬 数字 DNS 遗传



* 64 位唯一 DNS 编码，定义数字人先天性格、天赋、观念倾向

* 支持父母 DNS 杂交与随机变异，保证社会多样性

* 基因编码与社会属性深度绑定，构建差异化个体基础

### 👨‍👩‍👧‍👦 代际传承机制



* 继承父母财富、社会阶层、观念与社会资本

* 基于「父 45%+ 母 45%+ 社会环境 5%+ 变异 5%」的科学继承公式

* 支持家族族谱构建与多代际延续

### 🤖 大模型轨迹驱动



* 群体聚合推理策略，10 万人仅需≤300 次 LLM 调用 / 轮

* 严格规则约束（年龄、资源、性格一致性），避免逻辑矛盾

* 适配本地 LLM（Qwen3.5-0.8B）与商用 API，灵活部署

### ⏳ 时间轴可控模拟



* 动态时间粒度（0-30 岁 1 年 / 步，30-60 岁 3 年 / 步，60 + 岁 5 年 / 步）

* 支持输入模拟年限，自动推进社会演进

* 可暂停、可回溯、可复现，适配研究场景需求

### 📊 社会演化涌现



* 自动计算阶层分布、职业结构、观念传播等宏观指标

* 支持社会规则与文化自发涌现，无需人工干预

* 提供可视化社会状态输出，直观呈现演进结果

### ⚡ 高性能架构



* 支持 10 万数字人稳定仿真，单步模拟耗时 < 30 秒

* 基于 JBoot 缓存与 MySQL 批量操作优化，普通服务器即可运行

* 原生支持分布式扩展，适配更大规模仿真需求



***

## 技术栈



| 类别      | 技术选型                                          |
| ------- | --------------------------------------------- |
| 核心框架    | JBoot 3.12.0 (内置 Undertow + MyBatisPlus)      |
| 数据存储    | MySQL 8.0 + JBoot Cache (Caffeine)            |
| 大模型集成   | JBoot Http + 本地 LLM (Qwen3.5-0.8B) / 商用 API |
| 开发语言    | Java 23                                      |
| 构建工具    | Maven                                         |
| 可视化（可选） | Vue3 + Element-Plus / JBoot + Freemarker      |
| 适配系统    | Windows/macOS/Linux                           |



***

## 快速开始

### 1. 环境准备



* JDK 23

* MySQL 8.0

* Maven 3.6+

* （可选）本地 LLM（推荐 Qwen3.5-0.8B）

### 2. 项目部署



```bash
# 1. 克隆仓库
git clone https://github.com/xiaotu1990/Motily.git

cd Motily

# 2. 配置数据库（修改jboot.properties）
vim src/main/resources/jboot.properties

# 配置：jboot.datasource.url=jdbc:mysql://localhost:3306/motily
#       jboot.datasource.user=root
#       jboot.datasource.password=your-password

# 3. 初始化数据库（执行SQL脚本）
mysql -u root -p motily_init.sql

# 4. 编译打包
mvn clean package -Dmaven.test.skip=true

# 5. 启动项目
java -jar target/motily-1.0.0.jar
```

### 3. 首次使用



1. 访问后台面板：[http://localhost:8080](http://localhost:8080)

2. 生成初始 10 万数字人 DNS 编码

3. 输入模拟年限（如 50 年），启动社会演进仿真

4. 查看数字人人生轨迹与社会宏观状态报告

***

## 许可证

本项目基于 **Apache License 2.0** 开源协议，详情见 [LICENSE](LICENSE) 文件。

核心条款说明：



* ✅ 允许商业使用、二次开发与私有部署

* ✅ 必须保留版权声明与许可证信息

* ✅ 修改代码需标注修改记录

* ✅ 专利保护：贡献者授予全球免费专利许可

***

## 联系我们

欢迎点亮 ⭐ 关注数字人类社会演化的技术突破！