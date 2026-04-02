<template>
  <div class="indicator">
    <h2>社会指标</h2>
    <div class="card">
      <div class="form-group">
        <label>年份:</label>
        <input 
          type="number" 
          v-model.number="form.year" 
          placeholder="请输入年份" 
          min="2000" 
          max="2100"
          :disabled="loading"
        >
      </div>
      <div class="form-group">
        <button class="btn" @click="loadIndicator" :disabled="loading">
          {{ loading ? '查询中...' : '查询' }}
        </button>
      </div>
    </div>
    <div v-if="loading" class="message message-info">
      加载中... <span class="loading"></span>
    </div>
    <div v-if="error" class="message message-error">{{ error }}</div>
    <div class="card" v-if="indicator">
      <h3>{{ form.year }}年社会指标</h3>
      <div class="indicator-grid">
        <div class="indicator-item">
          <label>阶层分布:</label>
          <div class="indicator-value">{{ indicator.classDistribution }}</div>
        </div>
        <div class="indicator-item">
          <label>职业结构:</label>
          <div class="indicator-value">{{ indicator.occupationStructure }}</div>
        </div>
        <div class="indicator-item">
          <label>财富分布:</label>
          <div class="indicator-value">{{ indicator.wealthDistribution }}</div>
        </div>
      </div>
    </div>
    <div v-if="!loading && !error && !indicator" class="empty-state">
      <p>请输入年份并点击查询</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Indicator',
  data() {
    return {
      form: {
        year: 2000
      },
      indicator: null,
      loading: false,
      error: null
    }
  },
  methods: {
    async loadIndicator() {
      if (this.form.year < 2000 || this.form.year > 2100) {
        this.error = '年份必须在2000-2100之间'
        return
      }
      this.loading = true
      this.error = null
      try {
        const response = await axios.get(`/api/indicator/year?year=${this.form.year}`)
        this.indicator = response.data.data
      } catch (err) {
        this.error = '查询失败，请重试'
        console.error('查询社会指标失败:', err)
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.indicator-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 1.5rem;
  margin-top: 1rem;
}

.indicator-item {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 6px;
  border-left: 4px solid #667eea;
}

.indicator-item label {
  display: block;
  font-weight: 600;
  color: #666;
  margin-bottom: 0.5rem;
}

.indicator-value {
  color: #333;
  line-height: 1.6;
}
</style>
