<template>
  <div class="simulation">
    <h2>社会模拟</h2>
    <div class="card">
      <div class="form-group">
        <label>模拟年限:</label>
        <input 
          type="number" 
          v-model.number="form.years" 
          placeholder="请输入模拟年限" 
          min="1" 
          max="100"
          :disabled="loading"
        >
      </div>
      <div class="form-group">
        <button 
          class="btn" 
          @click="startSimulation" 
          :disabled="loading || (simulationId && status === 1)"
        >
          开始模拟
        </button>
        <button 
          class="btn" 
          @click="pauseSimulation" 
          :disabled="loading || !simulationId || status !== 1"
          style="margin-left: 0.5rem;"
        >
          暂停
        </button>
        <button 
          class="btn" 
          @click="resumeSimulation" 
          :disabled="loading || !simulationId || status !== 0"
          style="margin-left: 0.5rem;"
        >
          恢复
        </button>
        <button 
          class="btn" 
          @click="stopSimulation" 
          :disabled="loading || !simulationId"
          style="margin-left: 0.5rem;"
        >
          停止
        </button>
      </div>
    </div>
    <div v-if="loading" class="message message-info">
      处理中... <span class="loading"></span>
    </div>
    <div v-if="error" class="message message-error">{{ error }}</div>
    <div class="card" v-if="simulationId">
      <h3>模拟状态</h3>
      <div class="status-grid">
        <div class="status-item">
          <label>模拟ID:</label>
          <span>{{ simulationId }}</span>
        </div>
        <div class="status-item">
          <label>当前年份:</label>
          <span>{{ currentYear }}</span>
        </div>
        <div class="status-item">
          <label>状态:</label>
          <span :class="['status-badge', statusClass]">{{ statusText }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Simulation',
  data() {
    return {
      form: {
        years: 10
      },
      simulationId: null,
      currentYear: 2000,
      status: 0,
      loading: false,
      error: null
    }
  },
  computed: {
    statusText() {
      const statusMap = {
        0: '暂停',
        1: '运行中',
        2: '已完成'
      }
      return statusMap[this.status] || '未知'
    },
    statusClass() {
      const classMap = {
        0: 'status-paused',
        1: 'status-running',
        2: 'status-completed'
      }
      return classMap[this.status] || ''
    }
  },
  methods: {
    async startSimulation() {
      if (this.form.years < 1) {
        this.error = '模拟年限必须大于0'
        return
      }
      this.loading = true
      this.error = null
      try {
        const response = await axios.post(`/api/simulation/start?years=${this.form.years}`)
        this.simulationId = response.data.data.simulationId
        alert('模拟开始')
        await this.loadStatus()
      } catch (err) {
        this.error = '开始模拟失败，请重试'
        console.error('开始模拟失败:', err)
      } finally {
        this.loading = false
      }
    },
    async pauseSimulation() {
      if (!this.simulationId) return
      this.loading = true
      this.error = null
      try {
        await axios.post(`/api/simulation/pause?simulationId=${this.simulationId}`)
        alert('模拟暂停')
        await this.loadStatus()
      } catch (err) {
        this.error = '暂停失败，请重试'
        console.error('暂停模拟失败:', err)
      } finally {
        this.loading = false
      }
    },
    async resumeSimulation() {
      if (!this.simulationId) return
      this.loading = true
      this.error = null
      try {
        await axios.post(`/api/simulation/resume?simulationId=${this.simulationId}`)
        alert('模拟恢复')
        await this.loadStatus()
      } catch (err) {
        this.error = '恢复失败，请重试'
        console.error('恢复模拟失败:', err)
      } finally {
        this.loading = false
      }
    },
    async stopSimulation() {
      if (!this.simulationId) return
      this.loading = true
      this.error = null
      try {
        await axios.post(`/api/simulation/stop?simulationId=${this.simulationId}`)
        alert('模拟停止')
        await this.loadStatus()
      } catch (err) {
        this.error = '停止失败，请重试'
        console.error('停止模拟失败:', err)
      } finally {
        this.loading = false
      }
    },
    async loadStatus() {
      if (!this.simulationId) return
      try {
        const response = await axios.get(`/api/simulation/status?simulationId=${this.simulationId}`)
        this.currentYear = response.data.data.currentYear
        this.status = response.data.data.status
      } catch (err) {
        console.error('加载状态失败:', err)
      }
    }
  }
}
</script>

<style scoped>
.status-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
  margin-top: 1rem;
}

.status-item {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.status-item label {
  font-weight: 600;
  color: #666;
}

.status-badge {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.875rem;
  font-weight: 500;
}

.status-paused {
  background-color: #fff3e0;
  color: #f57c00;
}

.status-running {
  background-color: #e8f5e8;
  color: #2e7d32;
}

.status-completed {
  background-color: #e3f2fd;
  color: #1976d2;
}
</style>
