<template>
  <div class="human">
    <h2>数字人管理</h2>
    <div class="card">
      <button class="btn" @click="generateHumans" :disabled="loading">
        {{ loading ? '生成中...' : '生成数字人' }}
      </button>
    </div>
    <div v-if="loading" class="message message-info">
      加载中... <span class="loading"></span>
    </div>
    <div v-if="error" class="message message-error">{{ error }}</div>
    <table class="table" v-if="humans.length > 0">
      <thead>
        <tr>
          <th>ID</th>
          <th>姓名</th>
          <th>性别</th>
          <th>出生年份</th>
          <th>财富</th>
          <th>社会阶层</th>
          <th>职业</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="human in humans" :key="human.id">
          <td>{{ human.id }}</td>
          <td>{{ human.name }}</td>
          <td>{{ human.gender === 0 ? '女' : '男' }}</td>
          <td>{{ human.birthYear }}</td>
          <td>{{ human.wealth.toFixed(2) }}</td>
          <td>{{ getSocialClass(human.socialClass) }}</td>
          <td>{{ human.occupation || '无' }}</td>
        </tr>
      </tbody>
    </table>
    <div v-else class="empty-state">
      <p>暂无数字人数据</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Human',
  data() {
    return {
      humans: [],
      loading: false,
      error: null
    }
  },
  mounted() {
    this.loadHumans()
  },
  methods: {
    async loadHumans() {
      this.loading = true
      this.error = null
      try {
        const response = await axios.get('/api/human/list?page=0&size=100')
        this.humans = response.data.data.list || []
      } catch (err) {
        this.error = '加载失败，请重试'
        console.error('加载数字人失败:', err)
      } finally {
        this.loading = false
      }
    },
    async generateHumans() {
      this.loading = true
      this.error = null
      try {
        await axios.post('/api/human/generate?count=10')
        alert('生成成功')
        await this.loadHumans()
      } catch (err) {
        this.error = '生成失败，请重试'
        console.error('生成数字人失败:', err)
      } finally {
        this.loading = false
      }
    },
    getSocialClass(classId) {
      const classes = {
        1: '底层',
        2: '中层',
        3: '上层'
      }
      return classes[classId] || '未知'
    }
  }
}
</script>
