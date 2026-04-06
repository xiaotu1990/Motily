<template>
  <div class="dna-heatmap">
    <div ref="heatmapContainer" class="heatmap-container"></div>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'DnaHeatmap',
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
    console.log('[DnaHeatmap] 组件已挂载，开始初始化...')
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
      console.log('[DnaHeatmap] 检查容器状态...')
      
      if (!this.$refs.heatmapContainer) {
        console.error('[DnaHeatmap] 容器引用不存在')
        return
      }
      
      const container = this.$refs.heatmapContainer
      const width = container.clientWidth
      const height = container.clientHeight
      
      console.log(`[DnaHeatmap] 容器尺寸: ${width}x${height}`)
      
      if (width === 0 || height === 0) {
        console.warn('[DnaHeatmap] 容器尺寸为0，延迟初始化...')
        setTimeout(() => {
          this.initChartWithCheck()
        }, 100)
        return
      }
      
      try {
        this.initChart()
        this.updateChart()
        console.log('[DnaHeatmap] 初始化完成')
      } catch (error) {
        console.error('[DnaHeatmap] 初始化失败:', error)
      }
    },
    
    initChart() {
      this.chart = echarts.init(this.$refs.heatmapContainer)
      
      window.addEventListener('resize', this.onWindowResize)
    },
    
    updateChart() {
      if (!this.chart) return
      
      const featureValues = this.parseDnaString()
      const heatmapData = this.prepareHeatmapData(featureValues)
      
      const option = {
        title: {
          text: 'DNA特征热力图',
          left: 'center'
        },
        tooltip: {
          position: 'top',
          formatter: function(params) {
            return `${params.data[2]}: ${params.data[3]}`
          }
        },
        grid: {
          height: '50%',
          top: '10%'
        },
        xAxis: {
          type: 'category',
          data: ['基础属性', '性格特质', '智力能力', '健康状况', '社会属性'],
          splitArea: {
            show: true
          }
        },
        yAxis: {
          type: 'category',
          data: Array.from({ length: 32 }, (_, i) => i + 1),
          splitArea: {
            show: true
          }
        },
        visualMap: {
          min: 0,
          max: 3,
          calculable: true,
          orient: 'horizontal',
          left: 'center',
          bottom: '5%',
          inRange: {
            color: ['#313695', '#4575b4', '#74add1', '#abd9e9', '#e0f3f8', '#ffffbf', '#fee090', '#fdae61', '#f46d43', '#d73027', '#a50026']
          }
        },
        series: [
          {
            name: '特征强度',
            type: 'heatmap',
            data: heatmapData,
            label: {
              show: true
            },
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      }
      
      this.chart.setOption(option)
    },
    
    prepareHeatmapData(featureValues) {
      const data = []
      const categories = ['基础属性', '性格特质', '智力能力', '健康状况', '社会属性']
      const categorySizes = [32, 24, 24, 24, 24]
      
      let startIndex = 0
      categories.forEach((category, categoryIndex) => {
        const size = categorySizes[categoryIndex]
        for (let i = 0; i < size; i++) {
          const value = featureValues[startIndex + i]
          data.push([categoryIndex, i, value, `${category} ${i + 1}: ${value}`])
        }
        startIndex += size
      })
      
      return data
    },
    
    parseDnaString() {
      if (!this.dnaString) {
        console.log('[DnaHeatmap] DNA字符串为空，使用默认值')
        return Array(128).fill(2)
      }
      
      try {
        console.log('[DnaHeatmap] 解析DNA字符串:', this.dnaString.substring(0, 50) + '...')
        
        const decoded = this.safeAtob(this.dnaString)
        
        if (!decoded) {
          console.warn('[DnaHeatmap] Base64解码失败，使用默认值')
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
        
        console.log('[DnaHeatmap] 解析成功，特征值数量:', featureValues.length)
        return featureValues.slice(0, 128)
      } catch (e) {
        console.error('[DnaHeatmap] 解析DNA字符串失败:', e.message)
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
        console.warn('[DnaHeatmap] atob解码失败:', e.message)
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
.dna-heatmap {
  width: 100%;
  height: 600px;
}

.heatmap-container {
  width: 100%;
  height: 100%;
  border: 1px solid #dee2e6;
  border-radius: 8px;
  overflow: hidden;
}
</style>
