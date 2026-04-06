<template>
  <div id="app" :class="{ 'sidebar-expanded': sidebarExpanded }">
    <aside class="sidebar" :class="{ expanded: sidebarExpanded }" @mouseenter="sidebarExpanded = true" @mouseleave="sidebarExpanded = false">
      <div class="sidebar-logo" @click="$router.push('/')">
        <span class="logo-icon">M</span>
        <span class="logo-text">Motily</span>
      </div>

      <nav class="sidebar-nav">
        <router-link v-for="item in navItems"
          :key="item.path"
          :to="item.path"
          class="nav-item"
          :class="{ active: $route.path === item.path || (item.path !== '/' && $route.path.startsWith(item.path)) }"
          :title="item.label"
        >
          <span class="nav-icon">{{ item.icon }}</span>
          <span class="nav-label">{{ item.label }}</span>
        </router-link>
      </nav>

      <div class="sidebar-footer">
        <div class="sidebar-toggle" @click="toggleSidebar">
          {{ sidebarExpanded ? '◀' : '▶' }}
        </div>
      </div>
    </aside>

    <main class="main-content">
      <router-view />
    </main>

    <nav class="mobile-nav">
      <router-link v-for="item in navItems"
        :key="'m-'+item.path"
        :to="item.path"
        class="mobile-nav-item"
        :class="{ active: $route.path === item.path }"
      >
        <span class="mobile-nav-icon">{{ item.icon }}</span>
        <span class="mobile-nav-label">{{ item.label.replace('管理', '').replace('社会', '') }}</span>
      </router-link>
    </nav>
  </div>
</template>

<script>
export default {
  name: 'App',
  data() {
    return {
      sidebarExpanded: false,
      navItems: [
        { path: '/', icon: '📊', label: '实时监控' },
        { path: '/human', icon: '👤', label: '数字人管理' },
        { path: '/simulation', icon: '🔄', label: '社会模拟' },
        { path: '/indicator', icon: '📈', label: '社会指标' },
        { path: '/family', icon: '👨‍👩‍👧‍👦', label: '家族管理' },
        { path: '/event', icon: '📅', label: '社会事件' }
      ]
    }
  },
  methods: {
    toggleSidebar() {
      this.sidebarExpanded = !this.sidebarExpanded
    }
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: #f5f5f5;
  color: #333;
}

#app {
  min-height: 100vh;
}

.sidebar {
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  width: 64px;
  background: #1a1a2e;
  z-index: 1000;
  box-shadow: 2px 0 12px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}

.sidebar.expanded {
  width: 200px;
}

.sidebar-logo {
  padding: 16px 12px;
  cursor: pointer;
  display: flex;
  align-items: center;
}

.logo-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  font-weight: 700;
  font-size: 1rem;
  flex-shrink: 0;
}

.logo-text {
  color: white;
  font-weight: 700;
  font-size: 1.1rem;
  margin-left: 10px;
  opacity: 0;
  transition: opacity 0.3s;
  white-space: nowrap;
}

.sidebar.expanded .logo-text {
  opacity: 1;
}

.sidebar-nav {
  flex: 1;
  padding: 8px 0;
  overflow-y: auto;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
  border-radius: 8px;
  margin: 4px 8px;
  transition: all 0.2s;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.nav-item.active {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.4);
}

.nav-icon {
  font-size: 1.3rem;
  flex-shrink: 0;
}

.nav-label {
  font-size: 0.85rem;
  margin-left: 12px;
  white-space: nowrap;
  overflow: hidden;
  opacity: 0;
  width: 0;
  transition: opacity 0.3s, width 0.3s;
}

.sidebar.expanded .nav-label {
  opacity: 1;
  width: auto;
}

.sidebar-footer {
  margin-top: auto;
  padding: 12px;
}

.sidebar-toggle {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  margin: 0 auto;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.7);
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
  user-select: none;
}

.sidebar-toggle:hover {
  background: rgba(255, 255, 255, 0.2);
  color: white;
}

.main-content {
  margin-left: 64px;
  transition: margin-left 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  min-height: 100vh;
  padding: 2rem;
}

.sidebar-expanded .main-content,
.sidebar.expanded ~ .main-content {
  margin-left: 200px;
}

.mobile-nav {
  display: none;
}

@media (max-width: 768px) {
  .sidebar {
    display: none;
  }

  .main-content {
    margin-left: 0;
    padding-bottom: 56px;
  }

  .mobile-nav {
    display: flex;
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    height: 56px;
    background: white;
    box-shadow: 0 -2px 12px rgba(0, 0, 0, 0.1);
    z-index: 999;
  }

  .mobile-nav-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    text-decoration: none;
    color: #666;
    font-size: 0.75rem;
    transition: color 0.2s;
  }

  .mobile-nav-item.active {
    color: #667eea;
  }

  .mobile-nav-icon {
    font-size: 1.2rem;
    margin-bottom: 2px;
  }

  .mobile-nav-label {
    white-space: nowrap;
  }
}

.card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 1.5rem;
  margin-bottom: 1rem;
}

.btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  transition: transform 0.2s, box-shadow 0.2s;
}

.btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.form-group input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.table {
  width: 100%;
  border-collapse: collapse;
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.table th,
.table td {
  padding: 1rem;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.table th {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-weight: 600;
}

.table tr:hover {
  background-color: #f8f9fa;
}

.loading {
  display: inline-block;
  width: 20px;
  height: 20px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.message {
  padding: 1rem;
  border-radius: 4px;
  margin-bottom: 1rem;
}

.message-info {
  background-color: #e3f2fd;
  color: #1976d2;
  border: 1px solid #bbdefb;
}

.message-error {
  background-color: #ffebee;
  color: #d32f2f;
  border: 1px solid #ffcdd2;
}

.empty-state {
  text-align: center;
  padding: 3rem;
  color: #666;
}
</style>
