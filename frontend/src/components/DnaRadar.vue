<template>
  <div class="dna-radar">
    <div ref="radarContainer" class="radar-container"></div>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'DnaRadar',
  props: {
    dnaString: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      chart: null
    }
  },
  mounted() {
    console.log('[DnaRadar] 组件已挂载，开始初始化...')
    this.$nextTick(() => {
      this.initChartWithCheck()
    })
  },
  beforeUnmount() {
    if (this.chart) {
      this.chart.dispose()
    }
    window.removeEventListener('resize', this.onWindowResize)
  },
  watch: {
    dnaString: {
      handler() {
        this.updateChart()
      }
    }
  },
  methods: {
    initChartWithCheck() {
      console.log('[DnaRadar] 检查容器状态...')
      
      if (!this.$refs.radarContainer) {
        console.error('[DnaRadar] 容器引用不存在')
        return
      }
      
      const container = this.$refs.radarContainer
      const width = container.clientWidth
      const height = container.clientHeight
      
      console.log(`[DnaRadar] 容器尺寸: ${width}x${height}`)
      
      if (width === 0 || height === 0) {
        console.warn('[DnaRadar] 容器尺寸为0，延迟初始化...')
        setTimeout(() => {
          this.initChartWithCheck()
        }, 100)
        return
      }
      
      try {
        this.initChart()
        this.updateChart()
        console.log('[DnaRadar] 初始化完成')
      } catch (error) {
        console.error('[DnaRadar] 初始化失败:', error)
      }
    },
    
    initChart() {
      this.chart = echarts.init(this.$refs.radarContainer)
      
      window.addEventListener('resize', this.onWindowResize)
    },
    
    updateChart() {
      if (!this.chart) return
      
      const categoryStats = this.calculateCategoryStats()
      
      const option = {
        title: {
          text: 'DNA特征分类雷达图',
          left: 'center'
        },
        tooltip: {
          trigger: 'item'
        },
        legend: {
          data: ['特征强度'],
          bottom: 10
        },
        radar: {
          indicator: [
            { name: '基础属性', max: 3 },
            { name: '性格特质', max: 3 },
            { name: '智力能力', max: 3 },
            { name: '健康状况', max: 3 },
            { name: '社会属性', max: 3 }
          ],
          radius: '70%'
        },
        series: [
          {
            name: 'DNA特征',
            type: 'radar',
            data: [
              {
                value: [
                  categoryStats.basic,
                  categoryStats.personality,
                  categoryStats.intelligence,
                  categoryStats.health,
                  categoryStats.social
                ],
                name: '特征强度',
                areaStyle: {
                  color: 'rgba(0, 123, 255, 0.2)'
                },
                lineStyle: {
                  color: '#007bff'
                },
                itemStyle: {
                  color: '#007bff'
                }
              }
            ]
          }
        ]
      }
      
      this.chart.setOption(option)
    },
    
    calculateCategoryStats() {
      const featureValues = this.parseDnaString()
      
      const stats = {
        basic: 0,
        personality: 0,
        intelligence: 0,
        health: 0,
        social: 0
      }
      
      let basicSum = 0
      for (let i = 0; i < 32; i++) {
        basicSum += featureValues[i]
      }
      stats.basic = basicSum / 32
      
      let personalitySum = 0
      for (let i = 32; i < 56; i++) {
        personalitySum += featureValues[i]
      }
      stats.personality = personalitySum / 24
      
      let intelligenceSum = 0
      for (let i = 56; i < 80; i++) {
        intelligenceSum += featureValues[i]
      }
      stats.intelligence = intelligenceSum / 24
      
      let healthSum = 0
      for (let i = 80; i < 104; i++) {
        healthSum += featureValues[i]
      }
      stats.health = healthSum / 24
      
      let socialSum = 0
      for (let i = 104; i < 128; i++) {
        socialSum += featureValues[i]
      }
      stats.social = socialSum / 24
      
      return stats
    },
    
    parseDnaString() {
      if (!this.dnaString) {
        console.log('[DnaRadar] DNA字符串为空，使用默认值')
        return Array(128).fill(2)
      }
      
      try {
        console.log('[DnaRadar] 解析DNA字符串:', this.dnaString.substring(0, 50) + '...')
        
        const decoded = this.safeAtob(this.dnaString)
        
        if (!decoded) {
          console.warn('[DnaRadar] Base64解码失败，使用默认值')
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
        
        console.log('[DnaRadar] 解析成功，特征值数量:', featureValues.length)
        return featureValues.slice(0, 128)
      } catch (e) {
        console.error('[DnaRadar] 解析DNA字符串失败:', e.message)
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
        console.warn('[DnaRadar] atob解码失败:', e.message)
        return null
      }
    },
    
    onWindowResize() {
      if (this.chart) {
        this.chart.resize()
      }
    }
  }
}
</script>

<style scoped>
.dna-radar {
  width: 100%;
  height: 400px;
}

.radar-container {
  width: 100%;
  height: 100%;
  border: 1px solid #dee2e6;
  border-radius: 8px;
  overflow: hidden;
}
</style>
