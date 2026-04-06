<template>
  <div class="home">
    <div class="hero">
      <h1>数字人类社会演化模拟</h1>
      <p>探索数字人类社会的演化过程，观察社会结构的变化</p>
    </div>
    <div class="features">
      <div class="feature-card">
        <h3>社会总览</h3>
        <p>直观查看社会各项指标，掌握整体发展态势</p>
        <router-link to="/overview" class="btn">进入</router-link>
      </div>
      <div class="feature-card">
        <h3>数字人管理</h3>
        <p>创建和管理数字人，查看他们的属性和状态</p>
        <router-link to="/human" class="btn">进入</router-link>
      </div>
      <div class="feature-card">
        <h3>社会模拟</h3>
        <p>启动社会演化模拟，观察社会结构的变化</p>
        <router-link to="/simulation" class="btn">进入</router-link>
      </div>
      <div class="feature-card">
        <h3>社会指标</h3>
        <p>查看社会指标数据，分析社会发展趋势</p>
        <router-link to="/indicator" class="btn">进入</router-link>
      </div>
      <div class="feature-card">
        <h3>家族管理</h3>
        <p>管理家族信息，查看家族财富和影响力</p>
        <router-link to="/family" class="btn">进入</router-link>
      </div>
      <div class="feature-card">
        <h3>社会事件</h3>
        <p>查看社会事件记录，了解历史演变</p>
        <router-link to="/event" class="btn">进入</router-link>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Home'
}
</script>

<style scoped>
.hero {
  text-align: center;
  padding: 3rem 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 8px;
  margin-bottom: 2rem;
}

.hero h1 {
  font-size: 2.5rem;
  margin-bottom: 1rem;
}

.hero p {
  font-size: 1.2rem;
  opacity: 0.9;
}

.features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 1.5rem;
}

.feature-card {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  text-align: center;
  transition: transform 0.2s;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-card h3 {
  color: #667eea;
  margin-bottom: 1rem;
}

.feature-card p {
  color: #666;
  margin-bottom: 1.5rem;
  line-height: 1.6;
}
</style>
