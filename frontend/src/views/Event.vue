<template>
  <div class="event">
    <h2>社会事件</h2>
    <div v-if="loading" class="message message-info">
      加载中... <span class="loading"></span>
    </div>
    <div v-if="error" class="message message-error">{{ error }}</div>
    <table class="table" v-if="events.length > 0">
      <thead>
        <tr>
          <th>年份</th>
          <th>事件类型</th>
          <th>事件描述</th>
          <th>影响程度</th>
          <th>发生概率</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="event in events" :key="event.id">
          <td>{{ event.eventYear }}</td>
          <td>
            <span :class="['event-type-badge', 'type-' + getEventTypeClass(event.eventType)]">
              {{ event.eventType }}
            </span>
          </td>
          <td class="event-desc">{{ event.description }}</td>
          <td>
            <span :class="['impact-badge', getImpactClass(event.influenceScore)]">
              {{ getImpactLabel(event.influenceScore) }}
            </span>
          </td>
          <td>{{ event.probability }}%</td>
        </tr>
      </tbody>
    </table>
    <div v-else class="empty-state">
      <p>暂无社会事件数据</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Event',
  data() {
    return {
      events: [],
      loading: false,
      error: null
    }
  },
  mounted() {
    this.loadEvents()
  },
  methods: {
    async loadEvents() {
      this.loading = true
      this.error = null
      try {
        const response = await axios.get('/api/simulation/events')
        this.events = response.data.data || []
      } catch (err) {
        console.error('加载社会事件数据失败:', err)
        this.events = []
        this.error = '加载社会事件数据失败，请确认模拟已启动'
      } finally {
        this.loading = false
      }
    },
    getEventTypeClass(eventType) {
      if (eventType.includes('出生')) return 'birth'
      if (eventType.includes('死亡')) return 'death'
      if (eventType.includes('结婚')) return 'marriage'
      if (eventType.includes('经济')) return 'economic'
      return 'social'
    },
    getImpactClass(score) {
      if (score >= 50) return 'impact-high'
      if (score >= 15) return 'impact-medium'
      return 'impact-low'
    },
    getImpactLabel(score) {
      if (score >= 50) return '高'
      if (score >= 15) return '中'
      return '低'
    }
  }
}
</script>

<style scoped>
.event-type-badge {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.875rem;
  font-weight: 500;
}

.type-birth { background-color: #e8f5e8; color: #2e7d32; }
.type-death { background-color: #ffebee; color: #d32f2f; }
.type-marriage { background-color: #fce4ec; color: #c2185b; }
.type-economic { background-color: #fff3e0; color: #f57c00; }
.type-social { background-color: #e3f2fd; color: #1976d2; }

.impact-badge {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.875rem;
  font-weight: 500;
}

.impact-high { background-color: #ffebee; color: #d32f2f; }
.impact-medium { background-color: #fff3e0; color: #f57c00; }
.impact-low { background-color: #e8f5e8; color: #2e7d32; }

.event-desc {
  max-width: 400px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.table {
  width: 100%;
  border-collapse: collapse;
}

.table th, .table td {
  padding: 0.75rem;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.table th {
  background-color: #f8f9fa;
  font-weight: 600;
  color: #555;
}

.empty-state {
  text-align: center;
  padding: 3rem;
  color: #999;
}

.message-error {
  background-color: #ffebee;
  color: #d32f2f;
  padding: 0.75rem 1rem;
  border-radius: 8px;
  margin-bottom: 1rem;
}

.message-info {
  background-color: #e3f2fd;
  color: #1976d2;
  padding: 0.75rem 1rem;
  border-radius: 8px;
  margin-bottom: 1rem;
}
</style>
