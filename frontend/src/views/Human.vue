<template>
  <div class="human">
    <h2>数字人管理</h2>
    <div class="card">
      <div class="btn-group">
        <button class="btn" @click="showGenerateModalDialog" :disabled="loading">
          {{ loading ? '生成中...' : '生成数字人' }}
        </button>
        <button class="btn btn-success" @click="showAddModal">
          添加数字人
        </button>
      </div>
    </div>

    <!-- 批量生成数字人配置模态框 -->
    <div v-if="showGenerateModal" class="modal-overlay" @click="closeGenerateModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>批量生成数字人</h3>
          <button class="btn-close" @click="closeGenerateModal">&times;</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label for="generateCount">生成数量</label>
            <input type="number" id="generateCount" v-model.number="generateForm.count" class="form-control" placeholder="输入生成数量" min="1" max="100">
          </div>
          <div class="form-group">
            <label for="genderRatio">性别比例</label>
            <div class="radio-group">
              <label><input type="radio" v-model="generateForm.genderRatio" value="balanced"> 平衡</label>
              <label><input type="radio" v-model="generateForm.genderRatio" value="moreMale"> 更多男性</label>
              <label><input type="radio" v-model="generateForm.genderRatio" value="moreFemale"> 更多女性</label>
            </div>
          </div>
          <div class="form-group">
            <label for="wealthMin">最低财富</label>
            <input type="number" id="wealthMin" v-model.number="generateForm.wealthMin" class="form-control" placeholder="输入最低财富" min="0">
          </div>
          <div class="form-group">
            <label for="wealthMax">最高财富</label>
            <input type="number" id="wealthMax" v-model.number="generateForm.wealthMax" class="form-control" placeholder="输入最高财富" min="0">
          </div>
          <div class="form-group">
            <label for="birthYear">出生年份</label>
            <input type="number" id="birthYear" v-model.number="generateForm.birthYear" class="form-control" placeholder="输入出生年份" min="1900" max="2026">
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="closeGenerateModal">取消</button>
          <button class="btn" @click="generateHumans" :disabled="loading">
            {{ loading ? '生成中...' : '生成' }}
          </button>
        </div>
      </div>
    </div>
    <!-- 搜索和过滤区域 -->
    <div class="card search-card">
      <div class="search-row">
        <div class="search-item">
          <label for="searchName">姓名:</label>
          <input 
            type="text" 
            id="searchName" 
            v-model="searchFilters.name" 
            class="form-control search-input" 
            placeholder="输入姓名搜索"
          >
        </div>
        <div class="search-item">
          <label for="searchGender">性别:</label>
          <select id="searchGender" v-model="searchFilters.gender" class="form-control search-select">
            <option value="">全部</option>
            <option value="0">女</option>
            <option value="1">男</option>
          </select>
        </div>
        <div class="search-item">
          <label for="searchClass">社会阶层:</label>
          <select id="searchClass" v-model="searchFilters.socialClass" class="form-control search-select">
            <option value="">全部</option>
            <option value="1">底层</option>
            <option value="2">中层</option>
            <option value="3">上层</option>
          </select>
        </div>
        <div class="search-item">
          <label for="searchWealthMin">最低财富:</label>
          <input 
            type="number" 
            id="searchWealthMin" 
            v-model.number="searchFilters.wealthMin" 
            class="form-control search-input" 
            placeholder="最低财富"
          >
        </div>
        <div class="search-item">
          <label for="searchWealthMax">最高财富:</label>
          <input 
            type="number" 
            id="searchWealthMax" 
            v-model.number="searchFilters.wealthMax" 
            class="form-control search-input" 
            placeholder="最高财富"
          >
        </div>
        <div class="search-item search-buttons">
          <button class="btn btn-sm" @click="resetFilters">重置</button>
        </div>
      </div>
    </div>

    <!-- 分页控制 -->
    <div class="pagination-control" v-if="pagination.total > 0">
      <div class="pagination-info">
        <span v-if="filteredHumans.length === 0">暂无匹配数据</span>
        <span v-else>显示 {{ pagination.startIndex + 1 }} - {{ Math.min(pagination.endIndex + 1, pagination.total) }} 条，共 {{ pagination.total }} 条</span>
      </div>
      <div class="pagination-actions">
        <button 
          class="btn btn-sm" 
          @click="changePage(pagination.currentPage - 1)" 
          :disabled="pagination.currentPage === 1"
        >
          上一页
        </button>
        <div class="page-numbers">
          <button 
            v-for="page in visiblePages" 
            :key="page"
            :class="['btn btn-sm page-btn', { active: page === pagination.currentPage }]"
            @click="changePage(page)"
          >
            {{ page }}
          </button>
        </div>
        <button 
          class="btn btn-sm" 
          @click="changePage(pagination.currentPage + 1)" 
          :disabled="pagination.currentPage === pagination.totalPages"
        >
          下一页
        </button>
        <select v-model.number="pagination.pageSize" @change="onPageSizeChange" class="form-control page-size-select">
          <option :value="10">10 条/页</option>
          <option :value="20">20 条/页</option>
          <option :value="50">50 条/页</option>
          <option :value="100">100 条/页</option>
        </select>
      </div>
    </div>

    <div v-if="loading" class="message message-info">
      加载中... <span class="loading"></span>
    </div>
    <div v-if="error" class="message message-error">{{ error }}</div>
    <table class="table" v-if="filteredHumans.length > 0">
      <thead>
        <tr>
          <th class="sortable">ID</th>
          <th class="sortable">姓名</th>
          <th class="sortable">性别</th>
          <th class="sortable">出生年份</th>
          <th class="sortable">财富</th>
          <th class="sortable">社会阶层</th>
          <th class="sortable">职业</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="human in filteredHumans" :key="human.id">
          <td>{{ human.id }}</td>
          <td>{{ human.name }}</td>
          <td>{{ human.gender === 0 ? '女' : '男' }}</td>
          <td>{{ human.birthYear }}</td>
          <td>{{ human.wealth.toFixed(2) }}</td>
          <td>{{ getSocialClass(human.socialClass) }}</td>
          <td>{{ human.occupation || '无' }}</td>
          <td>
            <div class="action-buttons">
              <button class="btn btn-sm" @click="viewHumanDetail(human.id)">查看详情</button>
              <button class="btn btn-sm btn-primary" @click="showEditModal(human)">编辑</button>
              <button class="btn btn-sm btn-danger" @click="confirmDelete(human.id)">删除</button>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
    <div v-else class="empty-state">
      <p v-if="humans.length > 0">当前筛选条件下暂无匹配数据</p>
      <p v-else>暂无数字人数据</p>
    </div>
    
    <!-- 数字人详情模态框 -->
    <div v-if="showDetail" class="modal-overlay" @click="closeDetail">
      <div class="modal-content detail-modal" @click.stop>
        <div class="modal-header">
          <h3>数字人详情</h3>
          <button class="btn-close" @click="closeDetail">&times;</button>
        </div>
        <div class="modal-body">
          <div v-if="loadingDetail" class="loading">加载中...</div>
          <div v-else-if="detailError" class="error" style="color: #dc3545; padding: 20px; text-align: center;">
            {{ detailError }}
          </div>
          <div v-else-if="currentHuman" class="human-detail">
            <!-- 详情标签页 -->
            <div class="detail-tabs">
              <button 
                v-for="tab in detailTabs" 
                :key="tab.value"
                :class="['tab-btn', { active: activeDetailTab === tab.value }]"
                @click="activeDetailTab = tab.value"
              >
                {{ tab.label }}
              </button>
            </div>
            
            <!-- 基本信息 -->
            <div v-if="activeDetailTab === 'basic'" class="detail-content">
              <div class="detail-row">
                <span class="label">ID:</span>
                <span class="value">{{ currentHuman.id }}</span>
              </div>
              <div class="detail-row">
                <span class="label">姓名:</span>
                <span class="value">{{ currentHuman.name }}</span>
              </div>
              <div class="detail-row">
                <span class="label">性别:</span>
                <span class="value">{{ currentHuman.gender === 0 ? '女' : '男' }}</span>
              </div>
              <div class="detail-row">
                <span class="label">出生年份:</span>
                <span class="value">{{ currentHuman.birthYear }}</span>
              </div>
              <div class="detail-row">
                <span class="label">死亡年份:</span>
                <span class="value">{{ currentHuman.deathYear || '在世' }}</span>
              </div>
              <div class="detail-row">
                <span class="label">财富:</span>
                <span class="value">{{ currentHuman.wealth.toFixed(2) }}</span>
              </div>
              <div class="detail-row">
                <span class="label">社会阶层:</span>
                <span class="value">{{ getSocialClass(currentHuman.socialClass) }}</span>
              </div>
              <div class="detail-row">
                <span class="label">职业:</span>
                <span class="value">{{ currentHuman.occupation || '无' }}</span>
              </div>
              <div class="detail-row">
                <span class="label">创建时间:</span>
                <span class="value">{{ formatDate(currentHuman.createdAt) }}</span>
              </div>
              <div class="detail-row">
                <span class="label">更新时间:</span>
                <span class="value">{{ formatDate(currentHuman.updatedAt) }}</span>
              </div>
            </div>
            
            <!-- DNA信息 - 简化版本 -->
            <div v-if="activeDetailTab === 'dna'" class="detail-content">
              <div class="dna-section">
                <h4>DNA信息</h4>
                
                <!-- DNA编码显示 -->
                <div class="dna-code-display">
                  <span>{{ currentHuman.dnsCode || '无' }}</span>
                  <button v-if="!editingDna" class="btn btn-sm" @click="startEditDna">编辑</button>
                </div>
                
                <!-- DNA可视化 - 始终显示 -->
                <div class="dna-visual-editor">
                  <h4>DNA可视化</h4>
                  
                  <!-- 可视化方式切换 -->
                  <div class="visualization-tabs">
                    <button 
                      v-for="tab in visualizationTabs" 
                      :key="tab.value"
                      :class="['tab-btn', { active: activeVisualization === tab.value }]"
                      @click="activeVisualization = tab.value"
                    >
                      {{ tab.label }}
                    </button>
                  </div>
                  
                  <!-- 不同的可视化组件 - 直接渲染，不用额外的div包裹 -->
                  <div class="visualization-content" style="min-height: 400px;">
                    <DnaHelix v-if="activeVisualization === 'helix'" :dnaString="currentHuman.dnsCode || ''" key="helix-view" />
                    <DnaRadar v-else-if="activeVisualization === 'radar'" :dnaString="currentHuman.dnsCode || ''" key="radar-view" />
                    <DnaHeatmap v-else-if="activeVisualization === 'heatmap'" :dnaString="currentHuman.dnsCode || ''" key="heatmap-view" />
                    <DnaFeatureMatrix v-else-if="activeVisualization === 'matrix'" :dnaString="currentHuman.dnsCode || ''" key="matrix-view" />
                  </div>
                  
                  <!-- 编辑模式下的保存/取消按钮 -->
                  <div v-if="editingDna" class="btn-group" style="margin-top: 15px;">
                    <button class="btn btn-sm" @click="saveDna">保存</button>
                    <button class="btn btn-sm btn-secondary" @click="cancelEditDna">取消</button>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 个性信息 -->
            <div v-if="activeDetailTab === 'personality'" class="detail-content">
              <div class="detail-row">
                <span class="label">性格:</span>
                <span class="value">{{ parseJson(currentHuman.personality) }}</span>
              </div>
              <div class="detail-row">
                <span class="label">天赋:</span>
                <span class="value">{{ parseJson(currentHuman.talent) }}</span>
              </div>
              <div class="detail-row">
                <span class="label">信仰:</span>
                <span class="value">{{ parseJson(currentHuman.belief) }}</span>
              </div>
            </div>
          </div>
          <div v-else class="error">加载失败</div>
        </div>
        <div class="modal-footer">
          <button class="btn" @click="closeDetail">关闭</button>
        </div>
      </div>
    </div>

    <!-- 添加/编辑数字人模态框 -->
    <div v-if="showAddEditModal" class="modal-overlay" @click="closeAddEditModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ isEditing ? '编辑数字人' : '添加数字人' }}</h3>
          <button class="btn-close" @click="closeAddEditModal">&times;</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label for="name">姓名</label>
            <input type="text" id="name" v-model="formData.name" class="form-control" placeholder="输入姓名">
          </div>
          <div class="form-group">
            <label for="gender">性别</label>
            <select id="gender" v-model="formData.gender" class="form-control">
              <option value="0">女</option>
              <option value="1">男</option>
            </select>
          </div>
          <div class="form-group">
            <label for="birthYear">出生年份</label>
            <input type="number" id="birthYear" v-model="formData.birthYear" class="form-control" placeholder="输入出生年份">
          </div>
          <div class="form-group">
            <label for="wealth">财富</label>
            <input type="number" id="wealth" v-model="formData.wealth" class="form-control" placeholder="输入初始财富">
          </div>
          <div class="form-group">
            <label for="socialClass">社会阶层</label>
            <select id="socialClass" v-model="formData.socialClass" class="form-control">
              <option value="1">底层</option>
              <option value="2">中层</option>
              <option value="3">上层</option>
            </select>
          </div>
          <div class="form-group">
            <label for="occupation">职业</label>
            <input type="text" id="occupation" v-model="formData.occupation" class="form-control" placeholder="输入职业">
          </div>
          <div class="form-group">
            <label for="dnsCode">DNA编码</label>
            <input type="text" id="dnsCode" v-model="formData.dnsCode" class="form-control" placeholder="输入DNA编码">
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="closeAddEditModal">取消</button>
          <button class="btn" @click="saveHuman" :disabled="loading">
            {{ loading ? '保存中...' : '保存' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 删除确认对话框 -->
    <div v-if="showDeleteConfirm" class="modal-overlay" @click="closeDeleteConfirm">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>确认删除</h3>
          <button class="btn-close" @click="closeDeleteConfirm">&times;</button>
        </div>
        <div class="modal-body">
          <p>确定要删除这个数字人吗？此操作不可撤销。</p>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="closeDeleteConfirm">取消</button>
          <button class="btn btn-danger" @click="deleteHuman" :disabled="loading">
            {{ loading ? '删除中...' : '删除' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import DnaHelix from '../components/DnaHelix.vue'
import DnaRadar from '../components/DnaRadar.vue'
import DnaHeatmap from '../components/DnaHeatmap.vue'
import DnaFeatureMatrix from '../components/DnaFeatureMatrix.vue'

export default {
  name: 'Human',
  components: {
    DnaHelix,
    DnaRadar,
    DnaHeatmap,
    DnaFeatureMatrix
  },
  data() {
    return {
      humans: [],
      loading: false,
      error: null,
      showDetail: false,
      currentHuman: null,
      loadingDetail: false,
      detailError: null,
      editingDna: false,
      editDnaValue: '',
      showAddEditModal: false,
      isEditing: false,
      formData: {
        id: null,
        name: '',
        gender: 0,
        birthYear: 2000,
        wealth: 10000,
        socialClass: 2,
        occupation: '',
        dnsCode: ''
      },
      showDeleteConfirm: false,
      deleteId: null,
      showGenerateModal: false,
      generateForm: {
        count: 10,
        genderRatio: 'balanced',
        wealthMin: 1000,
        wealthMax: 100000,
        birthYear: 2000
      },
      dnaComponents: {
        personality: 'neutral',
        intelligence: 'medium',
        health: 'average',
        wealth: 'medium',
        social: 'medium'
      },
      // 搜索过滤
      searchFilters: {
        name: '',
        gender: '',
        socialClass: '',
        wealthMin: null,
        wealthMax: null
      },
      // 分页
      pagination: {
        currentPage: 1,
        pageSize: 10,
        totalPages: 0,
        startIndex: 0,
        endIndex: 0,
        total: 0
      },
      // 可视化相关
      visualizationTabs: [
        { label: '双螺旋结构', value: 'helix' },
        { label: '雷达图', value: 'radar' },
        { label: '热力图', value: 'heatmap' },
        { label: '特征矩阵', value: 'matrix' }
      ],
      activeVisualization: 'helix',
      // 详情页标签
      detailTabs: [
        { label: '基本信息', value: 'basic' },
        { label: 'DNA 信息', value: 'dna' },
        { label: '个性信息', value: 'personality' }
      ],
      activeDetailTab: 'basic'
    }
  },
  watch: {
    dnaComponents: {
      handler() {
        this.generateDnaCode()
      },
      deep: true
    },
    searchFilters: {
      handler() {
        this.pagination.currentPage = 1
        this.updatePagination()
      },
      deep: true
    }
  },
  computed: {
    filteredHumans() {
      return this.humans.filter(human => {
        if (this.searchFilters.name && !human.name.toLowerCase().includes(this.searchFilters.name.toLowerCase())) {
          return false
        }
        if (this.searchFilters.gender !== '' && human.gender.toString() !== this.searchFilters.gender) {
          return false
        }
        if (this.searchFilters.socialClass !== '' && human.socialClass.toString() !== this.searchFilters.socialClass) {
          return false
        }
        if (this.searchFilters.wealthMin !== null && human.wealth < this.searchFilters.wealthMin) {
          return false
        }
        if (this.searchFilters.wealthMax !== null && human.wealth > this.searchFilters.wealthMax) {
          return false
        }
        return true
      })
    },
    paginatedHumans() {
      const start = (this.pagination.currentPage - 1) * this.pagination.pageSize
      const end = start + this.pagination.pageSize
      return this.filteredHumans.slice(start, end)
    },
    visiblePages() {
      const pages = []
      const maxVisible = 5
      let start = Math.max(1, this.pagination.currentPage - Math.floor(maxVisible / 2))
      let end = Math.min(this.pagination.totalPages, start + maxVisible - 1)
      
      if (end - start < maxVisible - 1) {
        start = Math.max(1, end - maxVisible + 1)
      }
      
      for (let i = start; i <= end; i++) {
        pages.push(i)
      }
      return pages
    }
  },
  mounted() {
    this.loadHumans()
  },
  methods: {
    changePage(page) {
      if (page < 1 || page > this.pagination.totalPages) return
      this.pagination.currentPage = page
      this.loadHumans()
    },
    onPageSizeChange() {
      this.pagination.currentPage = 1
      this.loadHumans()
    },
    updatePagination() {
      const total = this.pagination.total || 0
      this.pagination.totalPages = Math.ceil(total / this.pagination.pageSize)
      this.pagination.startIndex = (this.pagination.currentPage - 1) * this.pagination.pageSize
      this.pagination.endIndex = Math.min(this.pagination.startIndex + this.pagination.pageSize - 1, total - 1)

      if (this.pagination.currentPage > this.pagination.totalPages && this.pagination.totalPages > 0) {
        this.pagination.currentPage = this.pagination.totalPages
        this.loadHumans()
      }
    },
    resetFilters() {
      this.searchFilters = {
        name: '',
        gender: '',
        socialClass: '',
        wealthMin: null,
        wealthMax: null
      }
      this.pagination.currentPage = 1
      this.updatePagination()
    },
    async loadHumans() {
      this.loading = true
      this.error = null
      try {
        const page = (this.pagination.currentPage || 1) - 1
        const response = await axios.get('/api/human/list', {
          params: {
            page: page,
            size: this.pagination.pageSize || 50
          }
        })

        if (!response.data || !response.data.data || !Array.isArray(response.data.data.list)) {
          throw new Error('API 返回数据格式异常')
        }

        this.humans = response.data.data.list
        this.pagination.total = response.data.data.total || 0
        this.error = null

        this.updatePagination()
      } catch (err) {
        this.error = '加载失败：' + (err.message || '未知错误')
        this.humans = []
        console.error('加载数字人失败:', err)
      } finally {
        this.loading = false
      }
    },
    showGenerateModalDialog() {
      this.showGenerateModal = true
    },
    closeGenerateModal() {
      this.showGenerateModal = false
    },
    async generateHumans() {
      this.loading = true
      this.error = null
      try {
        await axios.post('/api/human/generate', this.generateForm)
        alert('生成成功')
        await this.loadHumans()
        this.closeGenerateModal()
      } catch (err) {
        this.error = '生成失败，请重试'
        console.error('生成数字人失败:', err)
      } finally {
        this.loading = false
      }
    },
    async viewHumanDetail(id) {
      this.loadingDetail = true
      this.currentHuman = null
      this.showDetail = true
      this.detailError = null
      try {
        const response = await axios.get(`/api/human/detail?id=${id}`)
        this.currentHuman = response.data.data
      } catch (err) {
        if (err.response && err.response.status === 404) {
          this.detailError = '该数字人不存在或已被删除'
          setTimeout(() => {
            this.closeDetail()
          }, 2000)
        } else {
          this.detailError = '加载失败：' + (err.message || '未知错误')
        }
        console.error('加载数字人详情失败:', err)
      } finally {
        this.loadingDetail = false
      }
    },
    closeDetail() {
      this.showDetail = false
      this.currentHuman = null
    },
    getSocialClass(classId) {
      const classes = {
        1: '底层',
        2: '中层',
        3: '上层'
      }
      return classes[classId] || '未知'
    },
    parseJson(jsonString) {
      try {
        const obj = JSON.parse(jsonString)
        return JSON.stringify(obj, null, 2)
      } catch (e) {
        return jsonString
      }
    },
    formatDate(dateString) {
      try {
        const date = new Date(dateString)
        return date.toLocaleString()
      } catch (e) {
        return dateString
      }
    },
    startEditDna() {
      this.editDnaValue = this.currentHuman.dnsCode || ''
      // 初始化 DNA 组件
      this.initDnaComponents()
      this.editingDna = true
    },
    initDnaComponents() {
      // 如果有现有的 DNA 编码，解析它
      if (this.currentHuman.dnsCode) {
        try {
          // 简单的解析逻辑，实际项目中可能需要更复杂的解析
          // 这里只是示例，实际解析逻辑需要根据 DNA 编码的格式来确定
          this.dnaComponents = {
            personality: 'neutral',
            intelligence: 'medium',
            health: 'average',
            wealth: 'medium',
            social: 'medium'
          }
        } catch (e) {
          // 解析失败，使用默认值
          this.dnaComponents = {
            personality: 'neutral',
            intelligence: 'medium',
            health: 'average',
            wealth: 'medium',
            social: 'medium'
          }
        }
      } else {
        // 使用默认值
        this.dnaComponents = {
          personality: 'neutral',
          intelligence: 'medium',
          health: 'average',
          wealth: 'medium',
          social: 'medium'
        }
      }
      // 生成 DNA 编码
      this.generateDnaCode()
    },
    generateDnaCode() {
      // 根据 DNA 组件生成 DNA 编码
      const components = this.dnaComponents
      // 简单的编码逻辑，实际项目中可能需要更复杂的编码
      const dnaString = `${components.personality}_${components.intelligence}_${components.health}_${components.wealth}_${components.social}`
      // 生成 Base64 编码
      this.editDnaValue = btoa(dnaString)
    },
    async saveDna() {
      try {
        const updatedHuman = { ...this.currentHuman, dnsCode: this.editDnaValue }
        await axios.put('/api/human/update', updatedHuman)
        this.currentHuman.dnsCode = this.editDnaValue
        this.editingDna = false
        alert('DNA编码更新成功')
      } catch (err) {
        console.error('更新DNA编码失败:', err)
        alert('更新失败，请重试')
      }
    },
    cancelEditDna() {
      this.editingDna = false
      this.editDnaValue = ''
    },
    showAddModal() {
      this.isEditing = false
      this.formData = {
        id: null,
        name: '',
        gender: 0,
        birthYear: 2000,
        wealth: 10000,
        socialClass: 2,
        occupation: '',
        dnsCode: ''
      }
      this.showAddEditModal = true
    },
    showEditModal(human) {
      this.isEditing = true
      this.formData = {
        id: human.id,
        name: human.name,
        gender: human.gender,
        birthYear: human.birthYear,
        wealth: human.wealth,
        socialClass: human.socialClass,
        occupation: human.occupation,
        dnsCode: human.dnsCode
      }
      this.showAddEditModal = true
    },
    closeAddEditModal() {
      this.showAddEditModal = false
      this.formData = {
        id: null,
        name: '',
        gender: 0,
        birthYear: 2000,
        wealth: 10000,
        socialClass: 2,
        occupation: '',
        dnsCode: ''
      }
    },
    async saveHuman() {
      this.loading = true
      this.error = null
      try {
        if (this.isEditing) {
          // 编辑数字人
          await axios.put('/api/human/update', this.formData)
          alert('编辑成功')
        } else {
          // 添加数字人
          await axios.post('/api/human/generate?count=1', this.formData)
          alert('添加成功')
        }
        await this.loadHumans()
        this.closeAddEditModal()
      } catch (err) {
        this.error = '操作失败，请重试'
        console.error('保存数字人失败:', err)
      } finally {
        this.loading = false
      }
    },
    confirmDelete(id) {
      this.deleteId = id
      this.showDeleteConfirm = true
    },
    closeDeleteConfirm() {
      this.showDeleteConfirm = false
      this.deleteId = null
    },
    async deleteHuman() {
      this.loading = true
      this.error = null
      try {
        // 这里需要实现删除数字人的API
        // 暂时使用模拟删除，因为后端还没有实现删除接口
        this.humans = this.humans.filter(human => human.id !== this.deleteId)
        alert('删除成功')
        this.closeDeleteConfirm()
      } catch (err) {
        this.error = '删除失败，请重试'
        console.error('删除数字人失败:', err)
      } finally {
        this.loading = false
      }
    },
    
    // 处理特征更新事件
    handleFeatureUpdated(event) {
      // 这里需要实现特征更新逻辑
      // 暂时只是打印事件信息
      console.log('Feature updated:', event)
      // 实际项目中，这里应该调用API更新DNA编码
    }
  }
}
</script>

<style scoped>
.human {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.search-row {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  align-items: flex-end;
}

.search-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
  min-width: 150px;
}

.search-input,
.search-select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  min-width: 120px;
}

.search-buttons {
  justify-content: flex-end;
}

.pagination-control {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  margin-bottom: 10px;
  border-bottom: 1px solid #e0e0e0;
}

.pagination-info {
  color: #666;
  font-size: 14px;
}

.pagination-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.page-numbers {
  display: flex;
  gap: 5px;
}

.page-btn {
  min-width: 36px;
  padding: 6px 10px;
}

.page-btn.active {
  background: #4f46e5;
  color: white;
  border-color: #4f46e5;
}

.page-btn:disabled {
  background: #e0e0e0;
  cursor: not-allowed;
}

.page-size-select {
  padding: 6px 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  margin-left: 10px;
}

.card {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.btn {
  background: #007bff;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn:hover {
  background: #0069d9;
}

.btn:disabled {
  background: #6c757d;
  cursor: not-allowed;
}

.btn-sm {
  padding: 5px 10px;
  font-size: 12px;
}

.message {
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.message-info {
  background: #d1ecf1;
  color: #0c5460;
  border: 1px solid #bee5eb;
}

.message-error {
  background: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

.table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  margin-top: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.12);
  border-radius: 12px;
  overflow: hidden;
  background: white;
}

.table th, .table td {
  border: 1px solid #e0e6ed;
  padding: 14px 18px;
  text-align: left;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-size: 14px;
}

.table th {
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
  color: white;
  font-weight: 700;
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 0.8px;
  position: sticky;
  top: 0;
  z-index: 10;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-bottom: 3px solid #3730a3;
}

.table th:hover {
  background: linear-gradient(135deg, #4338ca 0%, #6d28d9 100%);
  box-shadow: inset 0 2px 4px rgba(255,255,255,0.2);
  transform: translateY(-1px);
}

.table th.sortable {
  position: relative;
  padding-right: 30px;
}

.table th.sortable::after {
  content: '⇅';
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 12px;
  opacity: 0.8;
  transition: opacity 0.3s ease;
}

.table th.sortable:hover::after {
  opacity: 1;
}

.table tbody tr {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-bottom: 1px solid #f1f5f9;
}

.table tbody tr:hover {
  background: rgba(79, 70, 229, 0.04);
  transform: translateX(4px);
  box-shadow: 0 2px 4px rgba(79, 70, 229, 0.1);
}

.table tbody tr:nth-child(even) {
  background: #f8fafc;
}

.table tbody tr:nth-child(odd) {
  background: white;
}

.table .action-buttons {
  display: flex;
  gap: 8px;
  align-items: center;
  justify-content: flex-start;
}

.table .action-buttons .btn {
  font-size: 12px;
  padding: 6px 12px;
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.table .action-buttons .btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #64748b;
  background: #f8fafc;
  border-radius: 12px;
  margin-top: 20px;
  border: 2px dashed #cbd5e1;
  font-size: 16px;
}

.empty-state p {
  margin: 0;
  font-weight: 500;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .table {
    font-size: 12px;
  }
  
  .table th, .table td {
    padding: 10px 12px;
  }
  
  .table .action-buttons {
    flex-direction: column;
    align-items: stretch;
    gap: 4px;
  }
  
  .table .action-buttons .btn {
    width: 100%;
    text-align: center;
  }
}

.loading {
  display: inline-block;
  width: 20px;
  height: 20px;
  border: 2px solid #f3f3f3;
  border-top: 2px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 600px;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.modal-header {
  padding: 20px;
  border-bottom: 1px solid #dee2e6;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
}

.btn-close {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #6c757d;
}

.btn-close:hover {
  color: #000;
}

.modal-body {
  padding: 20px;
}

.modal-footer {
  padding: 20px;
  border-top: 1px solid #dee2e6;
  text-align: right;
}

.human-detail {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.detail-row {
  display: flex;
  gap: 20px;
}

.detail-row .label {
  font-weight: 600;
  min-width: 100px;
}

.detail-row .value {
  flex: 1;
  word-break: break-all;
}

.error {
  color: #dc3545;
  text-align: center;
  padding: 20px;
}

.form-control {
  width: 100%;
  padding: 8px;
  border: 1px solid #ced4da;
  border-radius: 4px;
  font-size: 14px;
  margin-bottom: 10px;
}

.btn-group {
  display: flex;
  gap: 10px;
}

.btn-secondary {
  background: #6c757d;
}

.btn-secondary:hover {
  background: #5a6268;
}

.btn-success {
  background: #28a745;
}

.btn-success:hover {
  background: #218838;
}

.btn-primary {
  background: #007bff;
}

.btn-primary:hover {
  background: #0069d9;
}

.btn-danger {
  background: #dc3545;
}

.btn-danger:hover {
  background: #c82333;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: 600;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 8px;
  border: 1px solid #ced4da;
  border-radius: 4px;
  font-size: 14px;
}

/* 响应式搜索和分页 */
@media (max-width: 768px) {
  .search-row {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-item {
    width: 100%;
  }
  
  .search-input,
  .search-select {
    width: 100%;
  }
  
  .pagination-control {
    flex-direction: column;
    gap: 10px;
    align-items: stretch;
  }
  
  .pagination-actions {
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .page-size-select {
    margin-left: 0;
    width: 100%;
  }
}

/* 响应式按钮组 */
@media (max-width: 768px) {
  .btn-group {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .btn-group .btn {
    margin-bottom: 5px;
  }
}

.radio-group {
  display: flex;
  gap: 20px;
  margin-top: 5px;
}

.radio-group label {
  display: flex;
  align-items: center;
  gap: 5px;
  font-weight: normal;
}

.dna-visual-editor {
  margin-bottom: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.dna-visual-editor h4 {
  margin-top: 0;
  margin-bottom: 15px;
  color: #333;
}

.dna-sections {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
  margin-bottom: 20px;
}

.dna-section {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.dna-section label {
  font-weight: 600;
  font-size: 14px;
  color: #666;
}

/* 可视化标签样式 */
.visualization-tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  border-bottom: 1px solid #dee2e6;
  padding-bottom: 10px;
}

.tab-btn {
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  padding: 8px 16px;
  border-radius: 4px 4px 0 0;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.tab-btn:hover {
  background: #e9ecef;
}

.tab-btn.active {
  background: #007bff;
  color: white;
  border-color: #007bff;
}

.visualization-content {
  margin-bottom: 20px;
}

/* 详情模态框样式 */
.detail-modal {
  width: 80%;
  max-width: 1000px;
  max-height: 80vh;
  overflow-y: auto;
}

/* 详情标签页样式 */
.detail-tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  border-bottom: 1px solid #dee2e6;
  padding-bottom: 10px;
}

.detail-content {
  padding: 10px 0;
}

.dna-code-display {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.dna-code-display span {
  flex: 1;
  word-break: break-all;
}
</style>
