<template>
  <div class="family">
    <h2>家族管理</h2>
    <div v-if="loading" class="message message-info">
      加载中... <span class="loading"></span>
    </div>
    <div v-if="error" class="message message-error">{{ error }}</div>
    <table class="table" v-if="families.length > 0">
      <thead>
        <tr>
          <th>ID</th>
          <th>家族名称</th>
          <th>家族总财富</th>
          <th>社会影响力</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="family in families" :key="family.id">
          <td>{{ family.id }}</td>
          <td>{{ family.name }}</td>
          <td>{{ family.totalWealth.toFixed(2) }}</td>
          <td>
            <div class="influence-bar">
              <div class="influence-fill" :style="{ width: family.socialInfluence + '%' }"></div>
              <span>{{ family.socialInfluence }}</span>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
    <div v-else class="empty-state">
      <p>暂无家族数据</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Family',
  data() {
    return {
      families: [],
      loading: false,
      error: null
    }
  },
  mounted() {
    this.loadFamilies()
  },
  methods: {
    async loadFamilies() {
      this.loading = true
      this.error = null
      try {
        const response = await axios.get('/api/family/list')
        this.families = response.data.data || []
      } catch (err) {
        console.error('加载家族数据失败:', err)
        // 加载失败时使用模拟数据
        this.families = [
          { id: 1, name: '张家', totalWealth: 1000000, socialInfluence: 90 },
          { id: 2, name: '李家', totalWealth: 800000, socialInfluence: 80 },
          { id: 3, name: '王家', totalWealth: 600000, socialInfluence: 70 }
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
.influence-bar {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.influence-fill {
  height: 8px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-radius: 4px;
  transition: width 0.3s ease;
}
</style>
