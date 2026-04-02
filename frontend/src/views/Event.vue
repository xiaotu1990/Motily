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
          <th>ID</th>
          <th>事件类型</th>
          <th>影响程度</th>
          <th>影响范围</th>
          <th>年份</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="event in events" :key="event.id">
          <td>{{ event.id }}</td>
          <td>{{ event.type }}</td>
          <td>
            <span :class="['impact-badge', 'impact-' + event.impactLevel]">
              {{ event.impactLevel }}
            </span>
          </td>
          <td>{{ event.impactScope }}</td>
          <td>{{ event.year }}</td>
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
        const response = await axios.get('/api/event/list')
        this.events = response.data.data || []
      } catch (err) {
        console.error('加载社会事件数据失败:', err)
        // 加载失败时使用模拟数据
        this.events = [
          { id: 1, type: '经济危机', impactLevel: '高', impactScope: '全球', year: 2008 },
          { id: 2, type: '技术革命', impactLevel: '中', impactScope: '区域', year: 2015 },
          { id: 3, type: '自然灾害', impactLevel: '高', impactScope: '局部', year: 2020 }
        ]
        this.error = '加载真实数据失败，显示模拟数据'
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.impact-badge {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.875rem;
  font-weight: 500;
}

.impact-高 {
  background-color: #ffebee;
  color: #d32f2f;
}

.impact-中 {
  background-color: #fff3e0;
  color: #f57c00;
}

.impact-低 {
  background-color: #e8f5e8;
  color: #2e7d32;
}
</style>
