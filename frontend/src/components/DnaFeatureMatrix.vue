<template>
  <div class="dna-feature-matrix">
    <h4>DNA特征矩阵</h4>
    <div class="matrix-container">
      <div v-for="category in categories" :key="category.name" class="category-section">
        <h5>{{ category.name }}</h5>
        <div class="feature-grid">
          <div v-for="feature in category.features" :key="feature.id" class="feature-item">
            <div class="feature-header">
              <span class="feature-name">{{ feature.name }}</span>
              <span class="feature-value">{{ feature.value }}</span>
            </div>
            <div class="feature-description">{{ feature.description }}</div>
            <div class="feature-control">
              <select v-model="feature.value" @change="updateFeatureValue(feature.id, feature.value)" class="form-control">
                <option value="0">极低</option>
                <option value="1">低</option>
                <option value="2">高</option>
                <option value="3">极高</option>
              </select>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'DnaFeatureMatrix',
  props: {
    dnaString: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      categories: [
        {
          name: '基础属性',
          features: []
        },
        {
          name: '性格特质',
          features: []
        },
        {
          name: '智力能力',
          features: []
        },
        {
          name: '健康状况',
          features: []
        },
        {
          name: '社会属性',
          features: []
        }
      ]
    }
  },
  mounted() {
    this.loadFeatures()
  },
  watch: {
    dnaString: {
      handler() {
        this.loadFeatures()
      }
    }
  },
  methods: {
    loadFeatures() {
      const featureValues = this.parseDnaString()
      
      this.categories.forEach(category => {
        category.features = []
      })
      
      for (let i = 0; i < 32; i++) {
        this.categories[0].features.push({
          id: i,
          name: this.getFeatureName(i),
          description: this.getFeatureDescription(i),
          value: featureValues[i]
        })
      }
      
      for (let i = 32; i < 56; i++) {
        this.categories[1].features.push({
          id: i,
          name: this.getFeatureName(i),
          description: this.getFeatureDescription(i),
          value: featureValues[i]
        })
      }
      
      for (let i = 56; i < 80; i++) {
        this.categories[2].features.push({
          id: i,
          name: this.getFeatureName(i),
          description: this.getFeatureDescription(i),
          value: featureValues[i]
        })
      }
      
      for (let i = 80; i < 104; i++) {
        this.categories[3].features.push({
          id: i,
          name: this.getFeatureName(i),
          description: this.getFeatureDescription(i),
          value: featureValues[i]
        })
      }
      
      for (let i = 104; i < 128; i++) {
        this.categories[4].features.push({
          id: i,
          name: this.getFeatureName(i),
          description: this.getFeatureDescription(i),
          value: featureValues[i]
        })
      }
    },
    
    getFeatureName(id) {
      const featureNames = [
        // 基础属性
        '年龄', '性别', '身高', '体重', '外貌吸引力', '体力', '耐力', '速度',
        '敏捷性', '协调性', '平衡感', '反应速度', '力量', '柔韧性', '免疫力', '恢复能力',
        '寿命潜力', '生育能力', '遗传健康', '代谢率', '睡眠质量', '饮食习惯', '运动习惯', '压力水平',
        '激素水平', '感官灵敏度', '身体协调性', '平衡能力', '灵活性', '耐力水平', '爆发力', '整体健康',
        // 性格特质
        '外向性', '神经质', '开放性', '宜人性', '尽责性', '冒险精神', '好奇心', '创造力',
        '自信心', '同理心', '情绪稳定性', '耐心', '毅力', '决断力', '适应性', '社交能力',
        '领导能力', '团队合作', '沟通能力', '倾听能力', '表达能力', '说服力', '幽默感', '情商',
        // 智力能力
        '智商', '记忆力', '注意力', '逻辑思维', '抽象思维', '创造性思维', '问题解决能力', '学习能力',
        '语言能力', '数学能力', '空间能力', '音乐能力', '艺术能力', '运动能力', '实践能力', '分析能力',
        '综合能力', '推理能力', '判断能力', '决策能力', '规划能力', '执行能力', '创新能力', '整体智力',
        // 健康状况
        '心脏健康', '肺部健康', '肝脏健康', '肾脏健康', '消化系统健康', '免疫系统健康', '神经系统健康', '内分泌系统健康',
        '骨骼健康', '肌肉健康', '皮肤健康', '视力健康', '听力健康', '口腔健康', '心理健康', '精神健康',
        '睡眠健康', '营养健康', '运动健康', '压力管理', '疾病抵抗力', '康复能力', '整体健康', '健康意识',
        // 社会属性
        '社会地位', '经济状况', '教育水平', '职业地位', '社交网络', '社会影响力', '领导力', '团队合作',
        '沟通能力', '人际关系', '家庭关系', '社会适应能力', '文化认同', '价值观', '信仰', '道德观念',
        '法律意识', '公民意识', '环保意识', '社会责任', '社会参与', '社会贡献', '社会评价', '整体社会属性'
      ]
      return featureNames[id] || `特征${id}`
    },
    
    getFeatureDescription(id) {
      return `特征${id}的描述`
    },
    
    parseDnaString() {
      if (!this.dnaString) {
        console.log('[DnaFeatureMatrix] DNA字符串为空，使用默认值')
        return Array(128).fill(2)
      }
      
      try {
        console.log('[DnaFeatureMatrix] 解析DNA字符串:', this.dnaString.substring(0, 50) + '...')
        
        const decoded = this.safeAtob(this.dnaString)
        
        if (!decoded) {
          console.warn('[DnaFeatureMatrix] Base64解码失败，使用默认值')
          return Array(128).fill(2)
        }
        
        let bitString = ''
        
        for (let i = 0; i < decoded.length; i++) {
          const charCode = decoded.charCodeAt(i)
          const bits = charCode.toString(2).padStart(8, '0')
          bitString += bits
        }
        
        const featureValues = []
        for (let i = 0; i < 256; i += 2) {
          if (i + 1 < bitString.length) {
            const value = parseInt(bitString.substr(i, 2), 2)
            featureValues.push(value)
          } else {
            featureValues.push(2)
          }
        }
        
        while (featureValues.length < 128) {
          featureValues.push(2)
        }
        
        console.log('[DnaFeatureMatrix] 解析成功，特征值数量:', featureValues.length)
        return featureValues.slice(0, 128)
      } catch (e) {
        console.error('[DnaFeatureMatrix] 解析DNA字符串失败:', e.message)
        return Array(128).fill(2)
      }
    },
    
    safeAtob(base64String) {
      try {
        if (!base64String || typeof base64String !== 'string') {
          return null
        }
        
        let cleaned = base64String.replace(/[^A-Za-z0-9+/=]/g, '')
        
        if (cleaned.length === 0) {
          return null
        }
        
        while (cleaned.length % 4 !== 0) {
          cleaned += '='
        }
        
        const decoded = atob(cleaned)
        return decoded
      } catch (e) {
        console.warn('[DnaFeatureMatrix] atob解码失败:', e.message)
        return null
      }
    },
    
    updateFeatureValue(featureId, value) {
      this.$emit('feature-updated', {
        featureId: parseInt(featureId),
        value: parseInt(value)
      })
    }
  }
}
</script>

<style scoped>
.dna-feature-matrix {
  width: 100%;
}

.matrix-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.category-section {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.category-section h5 {
  margin-top: 0;
  margin-bottom: 15px;
  color: #333;
  border-bottom: 1px solid #dee2e6;
  padding-bottom: 5px;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 15px;
}

.feature-item {
  background: white;
  padding: 15px;
  border-radius: 6px;
  border: 1px solid #dee2e6;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.feature-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.feature-name {
  font-weight: 600;
  color: #333;
}

.feature-value {
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.feature-value[value="0"] {
  background: #dc3545;
  color: white;
}

.feature-value[value="1"] {
  background: #ffc107;
  color: #212529;
}

.feature-value[value="2"] {
  background: #28a745;
  color: white;
}

.feature-value[value="3"] {
  background: #007bff;
  color: white;
}

.feature-description {
  font-size: 12px;
  color: #666;
  margin-bottom: 10px;
  line-height: 1.4;
}

.feature-control {
  margin-top: 10px;
}

.form-control {
  width: 100%;
  padding: 6px;
  border: 1px solid #ced4da;
  border-radius: 4px;
  font-size: 14px;
}
</style>
