<template>
  <div class="dashboard">
    <!-- 加载状态 -->
    <div v-if="loading && !initialLoaded" class="loading-overlay">
      <div class="loading-spinner"></div>
      <p>正在加载实时数据...</p>
    </div>

    <!-- 错误提示 -->
    <div v-if="error" class="message message-error">
      {{ error }}
      <button @click="error = null" class="close-btn">×</button>
    </div>

    <!-- 页面头部：白色背景 + 阴影 -->
    <div class="dashboard-header">
      <div class="header-left">
        <h1 class="dashboard-title">数字社会实时监控</h1>
        <p class="dashboard-subtitle">数字人类社会演化模拟系统 - 实时数据可视化平台</p>
      </div>
      <div class="header-right">
        <!-- 快捷操作按钮组 -->
        <div class="quick-actions">
          <button class="quick-btn batch-btn" @click="showBatchDialog = true" title="批量添加人口">
            <span class="quick-icon">👥</span>
            <span class="quick-label">批量添加</span>
          </button>
          <button class="quick-btn" :class="{ 'auto-run-active': autoRunEnabled }" @click="toggleAutoRun" title="自动运行">
            <span class="quick-icon">{{ autoRunEnabled ? '⏸️' : '▶️' }}</span>
            <span class="quick-label">{{ autoRunEnabled ? '暂停' : '自动运行' }}</span>
          </button>
        </div>
        <div class="sim-time" v-if="simulationTime">
          <span class="sim-label">模拟时间</span>
          <span class="sim-value" :class="{ 'time-change': timeChangeFlag }">第{{ simulationTime.year }}年 第{{ simulationTime.week }}周</span>
        </div>
      </div>
    </div>

    <!-- 主内容区域：左侧指标+图表 | 右侧动态栏 -->
    <div class="main-layout" v-show="initialLoaded">

      <!-- 左侧主内容：三组分类指标 + 内嵌图表 -->
      <div class="cards-main">

        <!-- 📊 人口统计 section（含内嵌图表） -->
        <section class="metrics-section population-section">
          <div class="section-header">
            <h2 class="section-title"><span class="section-emoji">📊</span>人口统计</h2>
            <span class="section-badge" style="background:#667eea">4 项指标</span>
          </div>

          <!-- 指标卡片网格 -->
          <div class="metrics-grid">
            <div class="metric-card population">
              <div class="mini-label">总人口数</div>
              <div class="mini-value primary-color">{{ formatNumber(stats.totalPopulation) }}</div>
              <div class="mini-trend" :class="stats.populationTrend >= 0 ? 'up' : 'down'" v-if="stats.populationTrend !== null">
                {{ stats.populationTrend >= 0 ? '+' : '' }}{{ stats.populationTrend }}%
              </div>
            </div>
            <div class="metric-card population">
              <div class="mini-label">性别比例</div>
              <div class="gender-bar">
                <div class="gender-male" :style="{ width: stats.maleRatio + '%' }">
                  <span class="gender-text">男 {{ stats.maleRatio }}%</span>
                </div>
                <div class="gender-female" :style="{ width: stats.femaleRatio + '%' }">
                  <span class="gender-text">女 {{ stats.femaleRatio }}%</span>
                </div>
              </div>
            </div>
            <div class="metric-card metric-card-dual population">
              <div class="dual-item">
                <div class="mini-label-sm">平均年龄</div>
                <div class="mini-val-sm">{{ stats.avgAge }} 岁</div>
              </div>
              <div class="dual-divider"></div>
              <div class="dual-item">
                <div class="mini-label-sm">中位年龄</div>
                <div class="mini-val-sm">{{ stats.medianAge }} 岁</div>
              </div>
            </div>
            <div class="metric-card metric-card-dual population">
              <div class="dual-item">
                <div class="mini-label-sm">出生率</div>
                <div class="mini-val-sm green">{{ stats.birthRate }}‰</div>
              </div>
              <div class="dual-divider"></div>
              <div class="dual-item">
                <div class="mini-label-sm">死亡率</div>
                <div class="mini-val-sm red">{{ stats.deathRate }}‰</div>
              </div>
            </div>
          </div>

          <!-- 内嵌图表区域：阶层环形图 + 人口趋势折线图 -->
          <div class="section-charts">
            <div class="chart-box">
              <h4>阶层结构分布</h4>
              <div class="donut-chart-container">
                <div ref="socialClassChart" class="donut-chart"></div>
                <div class="donut-center">
                  <div class="donut-center-value">{{ socialClassData.total || 0 }}</div>
                  <div class="donut-center-label">总人口</div>
                </div>
              </div>
              <div class="chart-legend">
                <div
                  v-for="(item, index) in socialClassData.distribution"
                  :key="index"
                  class="legend-item"
                >
                  <span class="legend-color" :style="{ backgroundColor: chartColors[index % chartColors.length] }"></span>
                  <span class="legend-label">{{ item.category }}</span>
                  <span class="legend-value">{{ (item.value || 0).toLocaleString() }}</span>
                  <span class="legend-percentage">{{ item.percentage || 0 }}%</span>
                </div>
              </div>
            </div>
            <div class="chart-box">
              <h4>每年新增人口趋势</h4>
              <div class="trend-tabs-compact">
                <button
                  v-for="period in ['周', '年', '全部']"
                  :key="period"
                  :class="['tab-btn-sm', { active: populationPeriod === period }]"
                  @click="populationPeriod = period"
                >{{ period }}</button>
              </div>
              <div ref="populationTrendChart" class="chart-canvas"></div>
            </div>
          </div>
        </section>

        <!-- 💰 经济就业 section（含内嵌图表） -->
        <section class="metrics-section economy-section">
          <div class="section-header">
            <h2 class="section-title"><span class="section-emoji">💰</span>经济就业</h2>
            <span class="section-badge" style="background:#764ba2">4 项指标</span>
          </div>

          <!-- 指标卡片网格 -->
          <div class="metrics-grid">
            <div class="metric-card economy">
              <div class="mini-label">总财富</div>
              <div class="mini-value purple-color">{{ formatCurrency(stats.totalWealth) }}</div>
              <div class="mini-trend" :class="stats.wealthTrend >= 0 ? 'up' : 'down'" v-if="stats.wealthTrend !== null">
                {{ stats.wealthTrend >= 0 ? '+' : '' }}{{ stats.wealthTrend }}%
              </div>
            </div>
            <div class="metric-card economy">
              <div class="mini-label">人均财富</div>
              <div class="mini-value purple-color">{{ formatCurrency(stats.perCapitaWealth) }}</div>
            </div>
            <div class="metric-card metric-card-dual economy">
              <div class="dual-item">
                <div class="mini-label-sm">就业率</div>
                <div class="mini-val-sm green">{{ stats.employmentRate }}%</div>
              </div>
              <div class="dual-divider"></div>
              <div class="dual-item">
                <div class="mini-label-sm">失业率</div>
                <div class="mini-val-sm red">{{ (100 - stats.employmentRate).toFixed(1) }}%</div>
              </div>
            </div>
            <div class="metric-card economy">
              <div class="mini-label">基尼系数</div>
              <div class="gini-display">
                <span class="gini-value">{{ stats.giniIndex.toFixed(2) }}</span>
                <div class="gini-bar">
                  <div class="gini-fill" :style="{ width: (stats.giniIndex * 100) + '%' }"></div>
                </div>
                <span class="gini-level" :class="getGiniLevel(stats.giniIndex)">{{ getGiniLabel(stats.giniIndex) }}</span>
              </div>
            </div>
          </div>

          <!-- 内嵌图表区域：职业柱状图(全宽) + 财富分布/财富趋势(两列) -->
          <div class="section-charts economy-charts">
            <div class="chart-box full-width">
              <h4>职业结构分析</h4>
              <div ref="occupationChart" class="chart-canvas chart-canvas-lg"></div>
            </div>
            <div class="chart-charts-row">
              <div class="chart-box">
                <h4>财富区间分布</h4>
                <div ref="wealthChart" class="chart-canvas"></div>
                <div class="wealth-summary">
                  <div class="wealth-item">
                    <span class="wealth-label">平均财富</span>
                    <span class="wealth-value">{{ formatCurrency(averageWealth) }}</span>
                  </div>
                  <div class="wealth-item">
                    <span class="wealth-label">中位数</span>
                    <span class="wealth-value">{{ formatCurrency(medianWealth) }}</span>
                  </div>
                </div>
              </div>
              <div class="chart-box">
                <h4>财富增长趋势</h4>
                <div class="trend-tabs-compact">
                  <button
                    v-for="period in ['周', '年', '全部']"
                    :key="period"
                    :class="['tab-btn-sm', { active: wealthPeriod === period }]"
                    @click="wealthPeriod = period"
                  >{{ period }}</button>
                </div>
                <div ref="wealthTrendChart" class="chart-canvas"></div>
              </div>
            </div>
          </div>
        </section>

        <!-- ⚖️ 社会稳定 section（仅指标卡片） -->
        <section class="metrics-section stability-section">
          <div class="section-header">
            <h2 class="section-title"><span class="section-emoji">⚖️</span>社会稳定</h2>
            <span class="section-badge" style="background:#28a745">3 项指标</span>
          </div>

          <div class="metrics-grid metrics-grid-3">
            <div class="metric-card stability">
              <div class="mini-label">社会稳定度</div>
              <div class="stability-big">
                <span class="stability-num green-color">{{ stats.stability }}%</span>
                <div class="stability-bar-lg">
                  <div class="stability-fill-lg" :style="{ width: stats.stability + '%' }"></div>
                </div>
              </div>
            </div>
            <div class="metric-card stability">
              <div class="mini-label">阶层分布均衡度</div>
              <div class="mini-value green-color">{{ getClassBalance() }}%</div>
              <div class="mini-hint">基于阶层方差计算</div>
            </div>
            <div class="metric-card metric-card-dual stability">
              <div class="dual-item">
                <div class="mini-label-sm">幸福感指数</div>
                <div class="mini-val-sm green">{{ stats.happinessIndex }}</div>
              </div>
              <div class="dual-divider"></div>
              <div class="dual-item">
                <div class="mini-label-sm">满意度</div>
                <div class="mini-val-sm">{{ Math.round(stats.happinessIndex * 0.92) }}</div>
              </div>
            </div>
          </div>
        </section>

        <!-- 🗺️ 地理分布 section -->
        <section class="metrics-section region-section">
          <div class="section-header">
            <h2 class="section-title"><span class="section-emoji">🗺️</span>地理分布</h2>
            <span class="section-badge" style="background:#17a2b8">{{ regionData.distribution.length }} 个区域</span>
          </div>
          <div class="section-charts">
            <div class="chart-box full-width">
              <h4>省市自治区人口分布 Top 15</h4>
              <div ref="regionChart" class="chart-canvas chart-canvas-lg"></div>
            </div>
          </div>
        </section>

      </div>

      <!-- 右侧：实时动态侧边栏 -->
      <aside class="sidebar-events">
        <div class="sidebar-header">
          <h3 class="sidebar-title">📋 实时动态</h3>
          <router-link to="/event" class="view-all-link">查看全部 →</router-link>
        </div>
        <div class="events-scroll">
          <div
            v-for="(evt, idx) in recentEvents"
            :key="idx"
            class="sidebar-event"
          >
            <span class="event-dot" :class="'dot-' + evt.type"></span>
            <div class="event-info">
              <div class="event-name">{{ evt.title }}</div>
              <div class="event-rel-time">{{ formatRelativeTime(evt.time) }}</div>
            </div>
          </div>
          <div v-if="recentEvents.length === 0" class="empty-events">暂无动态</div>
        </div>
      </aside>

    </div>

    <!-- 批量添加人口弹窗 -->
    <div v-if="showBatchDialog" class="modal-overlay" @click.self="showBatchDialog = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3>👥 批量添加数字人</h3>
          <button @click="showBatchDialog = false" class="modal-close">×</button>
        </div>
        <div class="modal-body">
          <p class="modal-desc">按比例扩大全社会人口,自动分配职业、区域、财富、基因等属性。</p>
          <div class="form-group">
            <label>添加数量</label>
            <input type="number" v-model.number="batchCount" min="100" max="50000" step="1000" placeholder="输入数量 (100-50000)" />
          </div>
          <div class="batch-presets">
            <button v-for="n in [1000, 5000, 10000, 20000]" :key="n"
                    @click="batchCount = n" :class="{ active: batchCount === n }"
                    class="preset-btn">{{ n.toLocaleString() }}人</button>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="showBatchDialog = false" class="btn-cancel">取消</button>
          <button @click="executeBatchCreate" :disabled="batchCreating" class="btn-confirm">
            {{ batchCreating ? '创建中...' : '确认创建' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import * as echarts from 'echarts'

export default {
  name: 'Dashboard',
  data() {
    return {
      loading: false,
      initialLoaded: false,
      error: null,
      currentTime: '',
      lastUpdateTime: '-',
      autoRefreshInterval: 30000,
      refreshTimer: null,
      timeUpdateTimer: null,
      timeUpdateInterval: 5000,
      timeChangeFlag: false,
      autoRunEnabled: false,
      autoRunTimer: null,
      autoRunInterval: 10000,

      stats: {
        totalPopulation: 0,
        totalWealth: 0,
        economicIndex: 0,
        stability: 0,
        populationTrend: null,
        wealthTrend: null,
        economicTrend: null,
        maleRatio: 0,
        femaleRatio: 0,
        avgAge: 0,
        medianAge: 0,
        birthRate: 0,
        deathRate: 0,
        perCapitaWealth: 50000,
        employmentRate: 94.2,
        giniIndex: 0.38,
        happinessIndex: 72
      },

      socialClassData: {
        distribution: [],
        total: 0
      },
      occupationData: {
        distribution: [],
        total: 0
      },
      wealthData: {
        distribution: [],
        total: 0
      },
      trendData: [],
      populationPeriod: '年',
      wealthPeriod: '年',

      recentEvents: [],

      showBatchDialog: false,
      batchCount: 1000,
      batchCreating: false,
      derivedStats: null,
      regionData: { distribution: [], total: 0 },
      simulationTime: { year: 2024, week: 1, simulationId: null },

      charts: {},
      chartColors: ['#667eea', '#764ba2', '#f093fb', '#f5576c', '#4facfe', '#00f2fe']
    }
  },
  computed: {
    averageWealth() {
      if (!this.stats.totalPopulation || this.stats.totalPopulation === 0) return 0
      return this.stats.totalWealth / this.stats.totalPopulation
    },
    medianWealth() {
      if (!this.wealthData.distribution || this.wealthData.distribution.length === 0) return 0
      const sorted = [...this.wealthData.distribution].sort((a, b) => a.value - b.value)
      const mid = Math.floor(sorted.length / 2)
      return sorted.length % 2 !== 0 ? sorted[mid].value : (sorted[mid - 1].value + sorted[mid].value) / 2
    }
  },
  mounted() {
    this.updateCurrentTime()
    setInterval(this.updateCurrentTime, 1000)
    this.loadAllData()
    this.startAutoRefresh()
    this.startTimeUpdate()
  },
  beforeUnmount() {
    this.stopAutoRefresh()
    this.stopTimeUpdate()
    this.stopAutoRun()
    Object.values(this.charts).forEach(chart => chart?.dispose())
  },
  methods: {
    updateCurrentTime() {
      const now = new Date()
      this.currentTime = now.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      })
    },

    async loadAllData() {
      this.loading = true
      this.error = null

      try {
        const [
          humanStatsRes,
          socialClassRes,
          occupationRes,
          wealthRes,
          trendRes,
          derivedStatsRes,
          regionRes,
          simulationTimeRes,
          eventsRes
        ] = await Promise.all([
          axios.get('/api/human/stats'),
          axios.get('/api/human/distribution/social-class'),
          axios.get('/api/human/distribution/occupation'),
          axios.get('/api/human/distribution/wealth'),
          axios.get('/api/indicator/trend', { params: { startYear: 2000, endYear: new Date().getFullYear() } }),
          axios.get('/api/human/derived-stats'),
          axios.get('/api/human/distribution/region'),
          axios.get('/api/simulation/time'),
          axios.get('/api/simulation/events')
        ])

        const humanStats = humanStatsRes.data?.data || {}
        this.stats.totalPopulation = humanStats.totalPopulation || 0
        this.stats.totalWealth = humanStats.totalWealth || 0
        this.stats.economicIndex = this.stats.totalPopulation > 0
          ? Math.floor((this.stats.totalWealth / this.stats.totalPopulation) * 10)
          : 0

        this.socialClassData = socialClassRes.data?.data || { distribution: [], total: 0 }
        this.occupationData = occupationRes.data?.data || { distribution: [], total: 0 }
        this.wealthData = wealthRes.data?.data || { distribution: [], total: 0 }

        const derivedStats = derivedStatsRes.data?.data || null
        this.calculateDerivedMetrics(derivedStats)

        this.regionData = regionRes.data?.data || { distribution: [], total: 0 }

        // 更新模拟时间
        const simulationTimeData = simulationTimeRes.data?.data || { year: 2024, week: 1, simulationId: null }
        this.updateSimulationTime(simulationTimeData)

        // 处理事件数据
        const eventsData = eventsRes.data?.data || []
        this.recentEvents = eventsData.map(event => {
          let type = 'social'
          if (event.eventType.includes('出生')) type = 'birth'
          else if (event.eventType.includes('死亡')) type = 'death'
          else if (event.eventType.includes('结婚')) type = 'marriage'
          else if (event.eventType.includes('经济')) type = 'economic'
          return {
            type,
            title: event.description,
            time: event.createdAt
          }
        }).slice(0, 12) // 只取最近的12个事件

        this.calculateStability()

        this.trendData = Array.isArray(trendRes.data?.data) ? trendRes.data.data : []
        this.calculateTrends()
        // 不再使用模拟事件，使用真实事件
        // this.generateMockEvents()

        this.lastUpdateTime = new Date().toLocaleTimeString('zh-CN')
        this.initialLoaded = true

        this.$nextTick(() => {
          this.renderAllCharts()
        })

      } catch (err) {
        console.error('加载数据失败:', err)
        this.error = `加载失败：${err.message || '未知错误'}`
        this.generateMockEvents()
        this.initialLoaded = true
        this.$nextTick(() => {
          this.renderAllCharts()
        })
      } finally {
        this.loading = false
      }
    },

    async updateSimulationTime(newTime) {
      const oldYear = this.simulationTime.year
      const oldWeek = this.simulationTime.week
      
      this.simulationTime = newTime
      
      // 检测时间变化
      if (oldYear !== newTime.year || oldWeek !== newTime.week) {
        this.timeChangeFlag = true
        console.log('模拟时间更新:', oldYear, '年', oldWeek, '周 →', newTime.year, '年', newTime.week, '周')
        
        // 时间变化时，触发数据刷新
        setTimeout(() => {
          this.timeChangeFlag = false
        }, 1000)
      }
    },

    async fetchSimulationTime() {
      try {
        const response = await axios.get('/api/simulation/time')
        const simulationTimeData = response.data?.data || { year: 2024, week: 1, simulationId: null }
        this.updateSimulationTime(simulationTimeData)
      } catch (err) {
        console.error('获取模拟时间失败:', err)
      }
    },

    startTimeUpdate() {
      this.timeUpdateTimer = setInterval(() => {
        this.fetchSimulationTime()
      }, this.timeUpdateInterval)
    },

    stopTimeUpdate() {
      if (this.timeUpdateTimer) {
        clearInterval(this.timeUpdateTimer)
        this.timeUpdateTimer = null
      }
    },

    calculateDerivedMetrics(derivedStats = null) {
      if (this.stats.totalPopulation > 0) {
        this.stats.perCapitaWealth = Math.round(this.stats.totalWealth / this.stats.totalPopulation)
      }

      if (derivedStats) {
        if (derivedStats.maleRatio !== undefined) {
          this.stats.maleRatio = derivedStats.maleRatio
          this.stats.femaleRatio = derivedStats.femaleRatio || (100 - derivedStats.maleRatio)
        }
        this.stats.birthRate = derivedStats.birthRate || this.stats.birthRate
        this.stats.deathRate = derivedStats.deathRate || this.stats.deathRate
        this.stats.giniIndex = derivedStats.giniCoefficient !== undefined ? derivedStats.giniCoefficient : this.stats.giniIndex
        this.stats.avgAge = derivedStats.ageDistribution ? this.calculateAvgAge(derivedStats.ageDistribution) : this.stats.avgAge
        this.stats.medianAge = this.stats.avgAge > 0 ? Math.max(0, this.stats.avgAge - 2) : 0
        this.stats.happinessIndex = this.stats.giniIndex > 0 ? Math.round(100 - (this.stats.giniIndex * 100)) : this.stats.happinessIndex
        this.derivedStats = derivedStats
      }
    },

    calculateAvgAge(ageDistribution) {
      if (!ageDistribution) return 35
      const ranges = Object.keys(ageDistribution)
      let totalPeople = 0
      let weightedSum = 0
      ranges.forEach(range => {
        const count = ageDistribution[range] || 0
        totalPeople += count
        let midAge = 0
        if (range === '0-18') midAge = 9
        else if (range === '19-35') midAge = 27
        else if (range === '36-55') midAge = 45.5
        else if (range === '56+') midAge = 65
        weightedSum += count * midAge
      })
      return totalPeople > 0 ? Math.round(weightedSum / totalPeople) : 35
    },

    generateMockEvents() {
      const types = ['birth', 'death', 'marriage', 'economic', 'social']
      const titles = {
        birth: ['新生儿诞生', '新居民加入社会', '移民迁入'],
        death: ['居民离世', '自然死亡事件', '意外事故'],
        marriage: ['新人结为伴侣', '家庭组建完成', '婚姻登记'],
        economic: ['企业创立', '投资项目启动', '市场交易活跃'],
        social: ['社区活动举办', '公共设施建成', '政策调整发布']
      }
      const now = Date.now()
      this.recentEvents = Array.from({ length: 12 }, (_, i) => {
        const type = types[i % types.length]
        const typeTitles = titles[type]
        return {
          type,
          title: typeTitles[Math.floor(Math.random() * typeTitles.length)],
          time: new Date(now - i * (Math.floor(Math.random() * 10) + 1) * 60000)
        }
      })
    },

    formatRelativeTime(date) {
      if (!date) return ''
      const diff = Date.now() - new Date(date).getTime()
      const mins = Math.floor(diff / 60000)
      if (mins < 1) return '刚刚'
      if (mins < 60) return `${mins}分钟前`
      const hours = Math.floor(mins / 60)
      if (hours < 24) return `${hours}小时前`
      const days = Math.floor(hours / 24)
      return `${days}天前`
    },

    getGiniLevel(val) {
      if (val < 0.3) return 'level-good'
      if (val < 0.45) return 'level-warn'
      return 'level-danger'
    },

    getGiniLabel(val) {
      if (val < 0.3) return '均衡'
      if (val < 0.45) return '一般'
      return '差距大'
    },

    getClassBalance() {
      if (!this.socialClassData.distribution || this.socialClassData.distribution.length === 0) return 78
      const values = this.socialClassData.distribution.map(d => d.value || 0)
      const mean = values.reduce((a, b) => a + b, 0) / values.length
      const variance = values.reduce((sum, val) => sum + Math.pow(val - mean, 2), 0) / values.length
      const cv = mean > 0 ? (Math.sqrt(variance) / mean) * 100 : 0
      return Math.max(0, Math.min(100, Math.round(100 - cv)))
    },

    calculateStability() {
      if (!this.socialClassData.distribution || this.socialClassData.distribution.length === 0) {
        this.stats.stability = 75
        return
      }
      const values = this.socialClassData.distribution.map(d => d.value || 0)
      const mean = values.reduce((a, b) => a + b, 0) / values.length
      const variance = values.reduce((sum, val) => sum + Math.pow(val - mean, 2), 0) / values.length
      const stdDev = Math.sqrt(variance)
      const cv = mean > 0 ? (stdDev / mean) * 100 : 0
      this.stats.stability = Math.max(0, Math.min(100, Math.round(100 - cv)))
    },

    calculateTrends() {
      if (this.trendData.length < 2) {
        this.stats.populationTrend = 2.3
        this.stats.wealthTrend = 1.8
        this.stats.economicTrend = 0.5
        return
      }
      const current = this.trendData[this.trendData.length - 1]
      const previous = this.trendData[this.trendData.length - 2]

      if (previous.totalPopulation && previous.totalPopulation > 0) {
        this.stats.populationTrend = Math.round(
          ((current.totalPopulation - previous.totalPopulation) / previous.totalPopulation) * 100
        )
      }
      this.stats.wealthTrend = Math.round((Math.random() - 0.5) * 10)
      this.stats.economicTrend = Math.round((Math.random() - 0.5) * 5)
    },

    renderAllCharts() {
      this.renderSocialClassChart()
      this.renderOccupationChart()
      this.renderWealthChart()
      this.renderPopulationTrendChart()
      this.renderWealthTrendChart()
      this.renderRegionChart()
    },

    renderRegionChart() {
      if (!this.$refs.regionChart) return
      if (this.charts.region) this.charts.region.dispose()
      this.charts.region = echarts.init(this.$refs.regionChart)

      const topRegions = (this.regionData.distribution || []).slice(0, 15)
      // 检查数据结构，适配后端返回的格式
      const names = topRegions.map(d => {
        if (d.category) return d.category
        if (d.regionId) return `地区 ${d.regionId}`
        return '未知地区'
      })
      const values = topRegions.map(d => d.value || d.count || 0)

      const option = {
        tooltip: { trigger: 'axis', formatter: p => `${p[0].name}: ${p[0].value.toLocaleString()} 人` },
        grid: { left: '3%', right: '4%', bottom: '20%', top: '5%', containLabel: true },
        xAxis: { type: 'category', data: names, axisLabel: { fontSize: 10, rotate: 30 } },
        yAxis: { type: 'value', axisLabel: { formatter: v => v >= 10000 ? (v/10000).toFixed(1)+'w' : v } },
        series: [{
          type: 'bar',
          data: values.map((v) => ({
            value: v,
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#17a2b8' },
                { offset: 1, color: '#138496' }
              ]),
              borderRadius: [4, 4, 0, 0]
            }
          })),
          barWidth: '60%'
        }]
      }
      this.charts.region.setOption(option)
    },

    renderSocialClassChart() {
      if (!this.$refs.socialClassChart) return
      if (this.charts.socialClass) this.charts.socialClass.dispose()
      this.charts.socialClass = echarts.init(this.$refs.socialClassChart)

      const data = this.socialClassData.distribution.map((item, index) => ({
        name: item.category,
        value: item.value || 0,
        itemStyle: { color: this.chartColors[index % this.chartColors.length] }
      }))

      const option = {
        tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
        series: [{
          type: 'pie',
          radius: ['42%', '68%'],
          center: ['50%', '50%'],
          avoidLabelOverlap: true,
          itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
          label: { show: false },
          emphasis: { label: { show: true, fontSize: 13, fontWeight: 'bold' } },
          data: data
        }]
      }
      this.charts.socialClass.setOption(option)
    },

    renderOccupationChart() {
      if (!this.$refs.occupationChart) return
      if (this.charts.occupation) this.charts.occupation.dispose()
      this.charts.occupation = echarts.init(this.$refs.occupationChart)

      const topN = 20
      const sorted = [...this.occupationData.distribution].sort((a, b) => (b.value || 0) - (a.value || 0))
      const categories = sorted.slice(0, topN).map(d => d.category || '')
      const values = sorted.slice(0, topN).map(d => d.value || 0)

      const option = {
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        grid: { left: '3%', right: '4%', bottom: '25%', top: '8%', containLabel: true },
        xAxis: {
          type: 'category',
          data: categories,
          axisLabel: {
            fontSize: 10,
            rotate: 35,
            interval: 0
          }
        },
        yAxis: {
          type: 'value',
          axisLabel: { formatter: value => value >= 1000 ? (value / 1000).toFixed(1) + 'k' : value }
        },
        series: [{
          type: 'bar',
          data: values.map((value, index) => ({
            value,
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: this.chartColors[index % this.chartColors.length] },
                { offset: 1, color: this.chartColors[(index + 1) % this.chartColors.length] }
              ]),
              borderRadius: [3, 3, 0, 0]
            }
          })),
          barWidth: '45%'
        }]
      }
      this.charts.occupation.setOption(option)
    },

    renderWealthChart() {
      if (!this.$refs.wealthChart) return
      if (this.charts.wealth) this.charts.wealth.dispose()
      this.charts.wealth = echarts.init(this.$refs.wealthChart)

      const categories = this.wealthData.distribution.map(d => d.category || '')
      const values = this.wealthData.distribution.map(d => d.value || 0)

      const option = {
        tooltip: { trigger: 'axis', formatter: params => `${params[0].name}: ${params[0].value} 人` },
        grid: { left: '3%', right: '4%', bottom: '22%', top: '8%', containLabel: true },
        xAxis: {
          type: 'category',
          data: categories,
          axisLabel: { fontSize: 9, interval: 0, rotate: 15 }
        },
        yAxis: { type: 'value' },
        series: [{
          type: 'bar',
          data: values.map((value, index) => ({
            value,
            itemStyle: {
              color: this.chartColors[index % this.chartColors.length],
              borderRadius: [3, 3, 0, 0]
            }
          })),
          barWidth: '65%'
        }]
      }
      this.charts.wealth.setOption(option)
    },

    renderPopulationTrendChart() {
      if (!this.$refs.populationTrendChart) return
      if (this.charts.populationTrend) this.charts.populationTrend.dispose()
      this.charts.populationTrend = echarts.init(this.$refs.populationTrendChart)

      const years = this.trendData.map(d => d.year || '')
      const totalPopulations = this.trendData.map(d => d.totalPopulation || 0)
      
      // 计算每年新增人口数据
      const newPopulations = [0] // 第一年没有前一年数据，设为0
      for (let i = 1; i < totalPopulations.length; i++) {
        newPopulations.push(totalPopulations[i] - totalPopulations[i-1])
      }
      
      // 从derivedStats获取男女比例用于估算历史趋势
      const maleRatio = this.stats.maleRatio > 0 ? this.stats.maleRatio / 100 : 0.52
      const femaleRatio = this.stats.femaleRatio > 0 ? this.stats.femaleRatio / 100 : 0.48
      const malePopulations = totalPopulations.map(v => Math.round(v * maleRatio))
      const femalePopulations = totalPopulations.map(v => Math.round(v * femaleRatio))
      
      // 出生率和死亡率 (‰)，基于当前值模拟趋势
      const baseBirthRate = this.stats.birthRate > 0 ? this.stats.birthRate : 12
      const baseDeathRate = this.stats.deathRate > 0 ? this.stats.deathRate : 7
      const birthRates = years.map((_, idx) => parseFloat((baseBirthRate + (Math.random() - 0.5) * 2 * (idx + 1) / years.length).toFixed(1)))
      const deathRates = years.map((_, idx) => parseFloat((baseDeathRate + (Math.random() - 0.5) * 1.5 * (idx + 1) / years.length).toFixed(1)))

      const option = {
        tooltip: { 
          trigger: 'axis',
          formatter: function(params) {
            let result = params[0].name + '<br/>'
            params.forEach(p => {
              if (p.seriesName.includes('率')) {
                result += p.marker + p.seriesName + ': ' + p.value + '‰<br/>'
              } else {
                result += p.marker + p.seriesName + ': ' + (p.value >= 10000 ? (p.value/10000).toFixed(1)+'w' : p.value.toLocaleString()) + '<br/>'
              }
            })
            return result
          }
        },
        legend: {
          data: ['新增人口', '男性人数', '女性人数', '出生率(‰)', '死亡率(‰)'],
          top: 0,
          textStyle: { fontSize: 11 }
        },
        grid: { left: '3%', right: '8%', bottom: '8%', top: '18%', containLabel: true },
        xAxis: { type: 'category', boundaryGap: false, data: years },
        yAxis: [
          {
            type: 'value',
            name: '人数',
            axisLabel: { formatter: value => value >= 10000 ? (value / 10000).toFixed(1) + 'w' : value }
          },
          {
            type: 'value',
            name: '‰',
            position: 'right',
            axisLabel: { formatter: '{value}‰' }
          }
        ],
        series: [
          {
            name: '新增人口',
            type: 'line',
            smooth: true,
            data: newPopulations,
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(102, 126, 234, 0.35)' },
                { offset: 1, color: 'rgba(102, 126, 234, 0.03)' }
              ])
            },
            lineStyle: { width: 2.5, color: '#667eea' },
            itemStyle: { color: '#667eea' }
          },
          {
            name: '男性人数',
            type: 'line',
            smooth: true,
            data: malePopulations,
            lineStyle: { width: 1.5, color: '#4facfe', type: 'dashed' },
            itemStyle: { color: '#4facfe' }
          },
          {
            name: '女性人数',
            type: 'line',
            smooth: true,
            data: femalePopulations,
            lineStyle: { width: 1.5, color: '#f5576c', type: 'dashed' },
            itemStyle: { color: '#f5576c' }
          },
          {
            name: '出生率(‰)',
            type: 'line',
            smooth: true,
            yAxisIndex: 1,
            data: birthRates,
            lineStyle: { width: 2, color: '#67C23A' },
            itemStyle: { color: '#67C23A' }
          },
          {
            name: '死亡率(‰)',
            type: 'line',
            smooth: true,
            yAxisIndex: 1,
            data: deathRates,
            lineStyle: { width: 2, color: '#F56C6C' },
            itemStyle: { color: '#F56C6C' }
          }
        ]
      }
      this.charts.populationTrend.setOption(option)
    },

    renderWealthTrendChart() {
      if (!this.$refs.wealthTrendChart) return
      if (this.charts.wealthTrend) this.charts.wealthTrend.dispose()
      this.charts.wealthTrend = echarts.init(this.$refs.wealthTrendChart)

      let baseWealth = this.stats.totalWealth * 0.8
      const years = this.trendData.map(d => d.year || '')
      const wealths = years.map(() => {
        baseWealth *= (1 + (Math.random() - 0.48) * 0.1)
        return Math.floor(baseWealth)
      })

      const option = {
        tooltip: {
          trigger: 'axis',
          formatter: params => `${params[0].name}<br/>财富: ¥${(params[0].value / 100000000).toFixed(2)} 亿`
        },
        grid: { left: '3%', right: '4%', bottom: '8%', top: '12%', containLabel: true },
        xAxis: { type: 'category', boundaryGap: false, data: years },
        yAxis: {
          type: 'value',
          axisLabel: { formatter: value => `¥${(value / 100000000).toFixed(1)}亿` }
        },
        series: [{
          type: 'line',
          smooth: true,
          data: wealths,
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(118, 75, 162, 0.35)' },
              { offset: 1, color: 'rgba(118, 75, 162, 0.03)' }
            ])
          },
          lineStyle: { width: 2.5, color: '#764ba2' },
          itemStyle: { color: '#764ba2' }
        }]
      }
      this.charts.wealthTrend.setOption(option)
    },

    formatNumber(num) {
      if (!num) return '0'
      return num.toLocaleString('zh-CN')
    },

    formatCurrency(num) {
      if (!num) return '¥0'
      if (num >= 100000000) return `¥${(num / 100000000).toFixed(2)}亿`
      if (num >= 10000) return `¥${(num / 10000).toFixed(2)}万`
      return `¥${num.toFixed(2)}`
    },

    startAutoRefresh() {
      this.stopAutoRefresh()
      this.refreshTimer = setInterval(() => {
        // 只有在自动运行未启用时才自动刷新
        if (!this.autoRunEnabled) {
          this.loadAllData()
        }
      }, this.autoRefreshInterval)
    },

    stopAutoRefresh() {
      if (this.refreshTimer) {
        clearInterval(this.refreshTimer)
        this.refreshTimer = null
      }
    },
    
    toggleAutoRun() {
      console.log('toggleAutoRun called, current state:', this.autoRunEnabled)
      this.autoRunEnabled = !this.autoRunEnabled
      console.log('New state:', this.autoRunEnabled)
      if (this.autoRunEnabled) {
        console.log('Starting auto run')
        this.startAutoRun()
        // 自动运行时暂停自动刷新，因为自动运行已经会刷新数据
        this.stopAutoRefresh()
      } else {
        console.log('Stopping auto run')
        this.stopAutoRun()
        // 停止自动运行后恢复自动刷新
        this.startAutoRefresh()
      }
    },
    
    async startAutoRun() {
      console.log('startAutoRun called, interval:', this.autoRunInterval)
      if (this.autoRunTimer) {
        console.log('Clearing existing timer')
        clearInterval(this.autoRunTimer)
      }
      this.autoRunTimer = setInterval(async () => {
        console.log('Auto run interval triggered')
        try {
          console.log('Sending step request')
          const response = await axios.post('/api/simulation/step')
          console.log('Step response:', response.data)
          // 等待API响应后再更新时间和数据
          if (response.data && response.data.code === 200) {
            // 直接使用API返回的时间数据
            const newTime = response.data.data
            if (newTime) {
              this.updateSimulationTime(newTime)
            } else {
              // 如果API没有返回时间数据，再单独获取
              await this.fetchSimulationTime()
            }
            // 刷新所有数据
            await this.loadAllData()
          }
        } catch (err) {
          console.error('自动运行失败:', err)
          console.error('Error details:', err.response?.data)
          this.autoRunEnabled = false
          this.stopAutoRun()
        }
      }, this.autoRunInterval)
      console.log('Auto run timer set:', this.autoRunTimer)
    },
    
    stopAutoRun() {
      console.log('stopAutoRun called, current timer:', this.autoRunTimer)
      if (this.autoRunTimer) {
        clearInterval(this.autoRunTimer)
        this.autoRunTimer = null
        console.log('Auto run timer cleared')
      }
    },

    async executeBatchCreate() {
      if (!this.batchCount || this.batchCount < 100) {
        alert('请输入有效数量 (≥100)')
        return
      }
      this.batchCreating = true
      try {
        const res = await axios.post('/api/human/batch-create', { count: this.batchCount })
        const result = res.data?.data || {}
        alert(`成功创建 ${result.created || 0} 人!当前总人口: ${(result.totalPopulation || 0).toLocaleString()} 人`)
        this.showBatchDialog = false
        this.loadAllData()
      } catch (err) {
        alert('批量创建失败: ' + (err.response?.data?.message || err.message))
      } finally {
        this.batchCreating = false
      }
    }
  }
}
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  background: #f0f2f5;
  padding: 12px;
}

/* ========== 页面头部 ========== */
.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  padding: 16px 24px;
  background: #fff;
  border-radius: 12px;
  margin-bottom: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.header-left .dashboard-title {
  font-size: 1.6rem;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 2px;
}

.header-left .dashboard-subtitle {
  font-size: 0.85rem;
  color: #666;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
  width: 100%;
  justify-content: space-between;
}

.quick-actions {
  display: flex;
  gap: 6px;
}

.quick-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 14px;
  border-radius: 8px;
  border: 1px solid #e8e8e8;
  background: #fafafa;
  color: #444;
  text-decoration: none;
  font-size: 0.82rem;
  cursor: pointer;
  transition: all 0.25s ease;
  white-space: nowrap;
}

.quick-btn:hover {
  border-color: #667eea;
  background: #f0eeff;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.15);
}

.quick-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.quick-btn.auto-run-active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: transparent;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.quick-btn.auto-run-active:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.quick-icon { font-size: 0.95rem; }
.quick-label { font-size: 0.8rem; }

.sim-time {
  text-align: right;
  min-width: 140px;
}
.sim-label { font-size: 0.72rem; color: #999; display: block; }
.sim-value { font-size: 1rem; font-weight: 600; color: #333; font-family: 'Courier New', monospace; }

/* ========== 主布局：左主内容 | 右侧栏 ========== */
.main-layout {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 20px;
  align-items: start;
}

.cards-main {
  display: flex;
  flex-direction: column;
  gap: 20px;
  min-width: 0;
}

/* ========== 分类 Section ========== */
.metrics-section {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 20px 0;
}

.section-title {
  font-size: 1rem;
  font-weight: 700;
  color: #333;
  margin: 0;
}

.section-emoji {
  margin-right: 6px;
}

.section-badge {
  color: #fff;
  padding: 2px 12px;
  border-radius: 12px;
  font-size: 0.76rem;
  font-weight: 500;
}

/* ========== 指标卡片网格 ========== */
.metrics-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
  padding: 14px 20px;
}

.metrics-grid-3 {
  grid-template-columns: repeat(3, 1fr);
}

.metric-card {
  background: #fafbfc;
  border-radius: 10px;
  padding: 14px 16px;
  transition: transform 0.25s ease, box-shadow 0.25s ease;
  position: relative;
  border: 1px solid #eef0f2;
}

.metric-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.08);
}

.mini-label {
  font-size: 0.78rem;
  color: #888;
  margin-bottom: 4px;
}

.mini-value {
  font-size: 1.35rem;
  font-weight: 700;
  color: #333;
  line-height: 1.3;
}

.primary-color { color: #667eea; }
.purple-color { color: #764ba2; }
.green-color { color: #28a745; }

.mini-trend {
  font-size: 0.78rem;
  margin-top: 4px;
  font-weight: 600;
}
.mini-trend.up { color: #28a745; }
.mini-trend.down { color: #dc3545; }

.mini-hint {
  font-size: 0.72rem;
  color: #aaa;
  margin-top: 3px;
}

/* 双列小卡片 */
.metric-card-dual {
  display: flex;
  align-items: center;
  gap: 10px;
}

.dual-item {
  flex: 1;
  text-align: center;
}

.mini-label-sm {
  font-size: 0.72rem;
  color: #999;
  margin-bottom: 2px;
}

.mini-val-sm {
  font-size: 1.05rem;
  font-weight: 700;
  color: #333;
}
.mini-val-sm.green { color: #28a745; }
.mini-val-sm.red { color: #dc3545; }

.dual-divider {
  width: 1px;
  height: 32px;
  background: #eee;
}

/* 性别比例条 */
.gender-bar {
  display: flex;
  height: 22px;
  border-radius: 11px;
  overflow: hidden;
  margin-top: 4px;
}

.gender-male {
  background: linear-gradient(90deg, #4a90d9, #667eea);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: width 0.5s ease;
}

.gender-female {
  background: linear-gradient(90deg, #e85d8a, #f5576c);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: width 0.5s ease;
}

.gender-text {
  font-size: 0.68rem;
  color: #fff;
  font-weight: 600;
  white-space: nowrap;
  padding: 0 4px;
}

/* 稳定度大进度条 */
.stability-big {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.stability-num {
  font-size: 1.35rem;
  font-weight: 700;
}

.stability-bar-lg {
  height: 8px;
  background: #eef0f4;
  border-radius: 4px;
  overflow: hidden;
}

.stability-fill-lg {
  height: 100%;
  background: linear-gradient(90deg, #28a745, #5cb85c);
  border-radius: 4px;
  transition: width 0.6s ease;
}

/* 基尼系数 */
.gini-display {
  display: flex;
  flex-direction: column;
  gap: 5px;
  margin-top: 2px;
}

.gini-value {
  font-size: 1.15rem;
  font-weight: 700;
  color: #333;
}

.gini-bar {
  height: 6px;
  background: #eef0f4;
  border-radius: 3px;
  overflow: hidden;
}

.gini-fill {
  height: 100%;
  background: linear-gradient(90deg, #ffc107, #ff9800);
  border-radius: 3px;
  transition: width 0.5s ease;
}

.gini-level {
  font-size: 0.72rem;
  font-weight: 600;
}
.level-good { color: #28a745; }
.level-warn { color: #e6a200; }
.level-danger { color: #dc3545; }

/* ========== Section 内嵌图表区域 ========== */
.section-charts {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin: 0 20px 18px;
}

.economy-charts {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin: 0 20px 18px;
}

.economy-charts .full-width {
  width: 100%;
}

.chart-charts-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.chart-box {
  background: #fafbfc;
  border-radius: 10px;
  padding: 16px;
  border: 1px solid #eef0f2;
}

.chart-box h4 {
  font-size: 0.85rem;
  color: #555;
  font-weight: 600;
  margin: 0 0 10px 0;
}

/* 环形图容器 */
.donut-chart-container {
  position: relative;
  height: 180px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.donut-chart {
  width: 170px;
  height: 170px;
}

.donut-center {
  position: absolute;
  text-align: center;
}

.donut-center-value {
  font-size: 1.3rem;
  font-weight: 700;
  color: #333;
}

.donut-center-label {
  font-size: 0.75rem;
  color: #888;
}

/* 图表图例 */
.chart-legend {
  display: flex;
  flex-direction: column;
  gap: 5px;
  margin-top: 8px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.8rem;
}

.legend-color {
  width: 10px;
  height: 10px;
  border-radius: 3px;
  flex-shrink: 0;
}
.legend-label { flex: 1; color: #555; }
.legend-value { font-weight: 600; color: #333; }
.legend-percentage { color: #aaa; font-size: 0.74rem; }

/* 图表画布 */
.chart-canvas {
  height: 200px;
}

.chart-canvas-lg {
  height: 220px;
}

/* 紧凑型 tab 按钮 */
.trend-tabs-compact {
  display: flex;
  gap: 3px;
  margin-bottom: 8px;
}

.tab-btn-sm {
  padding: 3px 9px;
  border: 1px solid #e0e0e0;
  background: #fff;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.75rem;
  transition: all 0.2s;
}

.tab-btn-sm.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: transparent;
}

.tab-btn-sm:hover:not(.active) {
  background: #f5f5f5;
}

/* 财富摘要 */
.wealth-summary {
  display: flex;
  justify-content: space-around;
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px solid #eee;
}

.wealth-item { text-align: center; }
.wealth-label { display: block; font-size: 0.73rem; color: #888; margin-bottom: 2px; }
.wealth-value { font-weight: 600; color: #333; font-size: 0.85rem; }

/* ========== 右侧实时动态侧边栏 ========== */
.sidebar-events {
  width: 320px;
  flex-shrink: 0;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.06);
  display: flex;
  flex-direction: column;
  max-height: calc(100vh - 120px);
  position: sticky;
  top: 12px;
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  border-bottom: 1px solid #f0f0f0;
}

.sidebar-title {
  font-size: 0.95rem;
  font-weight: 700;
  color: #333;
}

.view-all-link {
  font-size: 0.8rem;
  color: #667eea;
  text-decoration: none;
  transition: color 0.2s;
}

.view-all-link:hover {
  color: #5568d3;
  text-decoration: underline;
}

.events-scroll {
  flex: 1;
  overflow-y: auto;
  padding: 8px 12px;
}

.events-scroll::-webkit-scrollbar { width: 4px; }
.events-scroll::-webkit-scrollbar-thumb { background: #ddd; border-radius: 2px; }

.sidebar-event {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 10px 4px;
  border-bottom: 1px solid #f7f7f7;
  transition: background 0.15s;
}

.sidebar-event:last-child { border-bottom: none; }
.sidebar-event:hover { background: #fafbff; border-radius: 6px; }

.event-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-top: 5px;
  flex-shrink: 0;
}

.dot-birth { background: #28a745; }
.dot-death { background: #dc3545; }
.dot-marriage { background: #ff69b4; }
.dot-economic { background: #ffc107; }
.dot-social { background: #17a2b8; }

.event-info { flex: 1; min-width: 0; }

.event-name {
  font-size: 0.83rem;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.4;
}

.event-rel-time {
  font-size: 0.73rem;
  color: #bbb;
  margin-top: 1px;
}

.empty-events {
  text-align: center;
  padding: 2rem 1rem;
  color: #ccc;
  font-size: 0.85rem;
}

/* ========== 加载状态 ========== */
.loading-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(255, 255, 255, 0.96);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.loading-spinner {
  width: 46px;
  height: 46px;
  border: 4px solid #f0f0f0;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 12px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* ========== 错误消息 ========== */
.message-error {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #ffebee;
  color: #d32f2f;
  border: 1px solid #ffcdd2;
  padding: 10px 16px;
  border-radius: 8px;
  margin-bottom: 12px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.4rem;
  color: #d32f2f;
  cursor: pointer;
  line-height: 1;
}

/* ========== 响应式设计 ========== */

@media (max-width: 1400px) {
  .metrics-grid {
    grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  }
  .section-charts {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 1200px) {
  .main-layout {
    grid-template-columns: 1fr;
  }
  .sidebar-events {
    width: 100%;
    max-height: 340px;
    position: static;
  }
  .events-scroll {
    max-height: 260px;
  }
}

@media (max-width: 992px) {
  .dashboard {
    padding: 10px;
  }
  .metrics-grid {
    grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  }
  .metrics-grid-3 {
    grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  }
  .chart-charts-row {
    grid-template-columns: 1fr;
  }
  .quick-actions {
    flex-wrap: wrap;
  }
}

@media (max-width: 768px) {
  .dashboard-header {
    flex-direction: column;
    align-items: flex-start;
    padding: 14px 18px;
  }
  .header-right {
    width: 100%;
    flex-wrap: wrap;
  }
  .quick-actions {
    order: -1;
    width: 100%;
  }
  .quick-btn {
    flex: 1;
    justify-content: center;
    min-width: 0;
  }
  .current-time {
    width: auto;
  }
  .sidebar-events {
    display: none;
  }
  .section-charts {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 576px) {
  .dashboard { padding: 8px; }
  .dashboard-header {
    padding: 12px 14px;
    border-radius: 8px;
  }
  .header-left .dashboard-title {
    font-size: 1.25rem;
  }
  .metrics-grid {
    grid-template-columns: 1fr 1fr;
    gap: 8px;
    padding: 10px 14px;
  }
  .metrics-grid-3 {
    grid-template-columns: 1fr 1fr;
  }
  .metric-card {
    padding: 10px 12px;
  }
  .mini-value {
    font-size: 1.15rem;
  }
  .section-charts {
    margin: 0 14px 14px;
    gap: 12px;
  }
  .quick-actions {
    gap: 4px;
  }
  .quick-label {
    display: none;
  }
  .quick-btn {
    padding: 8px 10px;
  }
}

/* 批量添加弹窗 */
.modal-overlay {
  position: fixed; top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.5); display: flex;
  justify-content: center; align-items: center; z-index: 2000;
}
.modal-content {
  background: #fff; border-radius: 16px; width: 420px;
  max-width: 90vw; box-shadow: 0 20px 60px rgba(0,0,0,0.2);
}
.modal-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 20px 24px 0;
}
.modal-header h3 { margin: 0; font-size: 1.15rem; color: #333; }
.modal-close {
  background: none; border: none; font-size: 1.6rem; cursor: pointer;
  color: #999; line-height: 1;
}
.modal-body { padding: 16px 24px; }
.modal-desc { font-size: 0.85rem; color: #666; margin-bottom: 16px; }
.form-group { margin-bottom: 14px; }
.form-group label { display: block; font-size: 0.82rem; color: #555; margin-bottom: 6px; font-weight: 600; }
.form-group input {
  width: 100%; padding: 10px 14px; border: 2px solid #e8e8e8;
  border-radius: 8px; font-size: 1rem; outline: none;
  transition: border-color 0.2s; box-sizing: border-box;
}
.form-group input:focus { border-color: #667eea; }
.batch-presets { display: flex; gap: 8px; flex-wrap: wrap; }
.preset-btn {
  padding: 6px 14px; border: 1px solid #e0e0e0; border-radius: 6px;
  background: #fff; cursor: pointer; font-size: 0.82rem; transition: all 0.2s;
}
.preset-btn:hover { border-color: #667eea; background: #f0eeff; }
.preset-btn.active { background: #667eea; color: #fff; border-color: #667eea; }
.modal-footer {
  display: flex; justify-content: flex-end; gap: 10px;
  padding: 0 24px 20px;
}
.btn-cancel, .btn-confirm {
  padding: 8px 20px; border-radius: 8px; font-size: 0.88rem;
  cursor: pointer; border: none; font-weight: 600; transition: all 0.2s;
}
.btn-cancel { background: #f5f5f5; color: #666; }
.btn-cancel:hover { background: #eee; }
.btn-confirm { background: linear-gradient(135deg, #667eea, #764ba2); color: #fff; }
.btn-confirm:hover { opacity: 0.9; transform: translateY(-1px); }
.btn-confirm:disabled { opacity: 0.6; cursor: not-allowed; transform: none; }

/* 模拟时间 */
.sim-time { text-align: right; min-width: 140px; }
.sim-label { font-size: 0.72rem; color: #999; display: block; }
.sim-value { font-size: 1rem; font-weight: 600; color: #17a2b8; font-family: 'Courier New', monospace; }
/* 时间变化动画 */
.time-change { animation: timeFlash 1s ease-in-out; }
@keyframes timeFlash {
  0%, 100% { color: #17a2b8; }
  50% { color: #28a745; transform: scale(1.05); }
}

/* Region section */
.region-section .section-charts { grid-template-columns: 1fr; }

/* Batch btn */
.batch-btn:hover { border-color: #28a745 !important; background: #f0fff0 !important; }
</style>
