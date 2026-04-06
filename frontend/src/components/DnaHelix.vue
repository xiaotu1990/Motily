<template>
  <div class="dna-helix">
    <div ref="helixContainer" class="helix-container"></div>
    <div class="controls">
      <button class="btn" @click="rotateLeft">向左旋转</button>
      <button class="btn" @click="rotateRight">向右旋转</button>
      <button class="btn" @click="zoomIn">放大</button>
      <button class="btn" @click="zoomOut">缩小</button>
      <button class="btn" @click="resetView">重置视角</button>
    </div>
  </div>
</template>

<script>
import * as THREE from 'three'
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js'

let sceneInstance = null
let cameraInstance = null
let rendererInstance = null
let controlsInstance = null
let dnaInstance = null
let animationIdInstance = null

export default {
  name: 'DnaHelix',
  props: {
    dnaString: {
      type: String,
      default: ''
    }
  },
  data() {
    return {}
  },
  mounted() {
    console.log('[DnaHelix] 组件已挂载，开始初始化...')
    this.$nextTick(() => {
      this.initSceneWithCheck()
    })
  },
  beforeUnmount() {
    if (animationIdInstance) {
      cancelAnimationFrame(animationIdInstance)
      animationIdInstance = null
    }
    if (rendererInstance) {
      rendererInstance.dispose()
      rendererInstance = null
    }
    if (sceneInstance) {
      sceneInstance.traverse((object) => {
        if (object.geometry) {
          object.geometry.dispose()
        }
        if (object.material) {
          if (Array.isArray(object.material)) {
            object.material.forEach(material => material.dispose())
          } else {
            object.material.dispose()
          }
        }
      })
      sceneInstance = null
    }
    window.removeEventListener('resize', this.onWindowResize)
  },
  watch: {
    dnaString: {
      handler() {
        this.updateDnaHelix()
      }
    }
  },
  methods: {
    initSceneWithCheck() {
      console.log('[DnaHelix] 检查容器状态...')
      
      if (!this.$refs.helixContainer) {
        console.error('[DnaHelix] 容器引用不存在')
        return
      }
      
      const container = this.$refs.helixContainer
      const width = container.clientWidth
      const height = container.clientHeight
      
      console.log(`[DnaHelix] 容器尺寸: ${width}x${height}`)
      
      if (width === 0 || height === 0) {
        console.warn('[DnaHelix] 容器尺寸为0，延迟初始化...')
        setTimeout(() => {
          this.initSceneWithCheck()
        }, 100)
        return
      }
      
      try {
        this.initScene()
        this.createDnaHelix()
        this.animate()
        console.log('[DnaHelix] 初始化完成')
      } catch (error) {
        console.error('[DnaHelix] 初始化失败:', error)
      }
    },
    
    initScene() {
      sceneInstance = new THREE.Scene()
      sceneInstance.background = new THREE.Color(0xf0f0f0)
      
      cameraInstance = new THREE.PerspectiveCamera(
        75,
        this.$refs.helixContainer.clientWidth / this.$refs.helixContainer.clientHeight,
        0.1,
        1000
      )
      cameraInstance.position.z = 10
      
      rendererInstance = new THREE.WebGLRenderer({ antialias: true })
      rendererInstance.setSize(
        this.$refs.helixContainer.clientWidth,
        this.$refs.helixContainer.clientHeight
      )
      this.$refs.helixContainer.appendChild(rendererInstance.domElement)
      
      controlsInstance = new OrbitControls(cameraInstance, rendererInstance.domElement)
      controlsInstance.enableDamping = true
      controlsInstance.dampingFactor = 0.05
      
      const ambientLight = new THREE.AmbientLight(0xffffff, 0.5)
      sceneInstance.add(ambientLight)
      
      const directionalLight = new THREE.DirectionalLight(0xffffff, 0.8)
      directionalLight.position.set(1, 1, 1)
      sceneInstance.add(directionalLight)
      
      window.addEventListener('resize', this.onWindowResize)
    },
    
    createDnaHelix() {
      dnaInstance = new THREE.Group()
      
      const featureValues = this.parseDnaString()
      
      const strand1 = this.createStrand(0.3, 0x007bff, featureValues, 0)
      const strand2 = this.createStrand(0.3, 0xdc3545, featureValues, Math.PI)
      
      dnaInstance.add(strand1)
      dnaInstance.add(strand2)
      
      this.createCrossBars(featureValues)
      
      sceneInstance.add(dnaInstance)
    },
    
    parseDnaString() {
      if (!this.dnaString) {
        console.log('[DnaHelix] DNA字符串为空，使用默认值')
        return Array(128).fill(2)
      }
      
      try {
        console.log('[DnaHelix] 解析DNA字符串:', this.dnaString.substring(0, 50) + '...')
        
        const decoded = this.safeAtob(this.dnaString)
        
        if (!decoded) {
          console.warn('[DnaHelix] Base64解码失败，使用默认值')
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
        
        console.log('[DnaHelix] 解析成功，特征值数量:', featureValues.length)
        return featureValues.slice(0, 128)
      } catch (e) {
        console.error('[DnaHelix] 解析DNA字符串失败:', e.message)
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
        console.warn('[DnaHelix] atob解码失败:', e.message)
        return null
      }
    },
    
    createStrand(radius, color, featureValues, angleOffset) {
      const strand = new THREE.Group()
      const height = 20
      const turns = 5
      const points = []
      
      for (let i = 0; i <= 100; i++) {
        const y = (i / 100) * height - height / 2
        const angle = (i / 100) * Math.PI * 2 * turns + angleOffset
        const x = Math.cos(angle) * radius
        const z = Math.sin(angle) * radius
        points.push(new THREE.Vector3(x, y, z))
      }
      
      const geometry = new THREE.BufferGeometry().setFromPoints(points)
      const material = new THREE.LineBasicMaterial({ color })
      const line = new THREE.Line(geometry, material)
      strand.add(line)
      
      for (let i = 0; i < featureValues.length; i++) {
        const y = (i / featureValues.length) * height - height / 2
        const angle = (i / featureValues.length) * Math.PI * 2 * turns + angleOffset
        const x = Math.cos(angle) * radius
        const z = Math.sin(angle) * radius
        
        const nucleotide = this.createNucleotide(featureValues[i])
        nucleotide.position.set(x, y, z)
        strand.add(nucleotide)
      }
      
      return strand
    },
    
    createNucleotide(value) {
      const nucleotide = new THREE.Group()
      
      const colors = [
        0x000000,
        0x6c757d,
        0x28a745,
        0xffc107
      ]
      
      const color = colors[Math.min(value, 3)]
      
      const geometry = new THREE.SphereGeometry(0.1, 16, 16)
      const material = new THREE.MeshPhongMaterial({ color })
      const sphere = new THREE.Mesh(geometry, material)
      nucleotide.add(sphere)
      
      return nucleotide
    },
    
    createCrossBars(featureValues) {
      const height = 20
      const turns = 5
      const radius = 0.3
      
      for (let i = 0; i < featureValues.length; i++) {
        const y = (i / featureValues.length) * height - height / 2
        const angle = (i / featureValues.length) * Math.PI * 2 * turns
        
        const x1 = Math.cos(angle) * radius
        const z1 = Math.sin(angle) * radius
        const x2 = Math.cos(angle + Math.PI) * radius
        const z2 = Math.sin(angle + Math.PI) * radius
        
        const geometry = new THREE.BufferGeometry().setFromPoints([
          new THREE.Vector3(x1, y, z1),
          new THREE.Vector3(x2, y, z2)
        ])
        
        const colors = [
          0x000000,
          0x6c757d,
          0x28a745,
          0xffc107
        ]
        
        const color = colors[Math.min(featureValues[i], 3)]
        const material = new THREE.LineBasicMaterial({ color })
        const line = new THREE.Line(geometry, material)
        
        dnaInstance.add(line)
      }
    },
    
    updateDnaHelix() {
      if (dnaInstance && sceneInstance) {
        sceneInstance.remove(dnaInstance)
        dnaInstance = null
      }
      this.createDnaHelix()
    },
    
    animate() {
      animationIdInstance = requestAnimationFrame(this.animate.bind(this))
      
      if (controlsInstance) {
        controlsInstance.update()
      }
      
      if (dnaInstance) {
        dnaInstance.rotation.y += 0.005
      }
      
      if (rendererInstance && sceneInstance && cameraInstance) {
        rendererInstance.render(sceneInstance, cameraInstance)
      }
    },
    
    onWindowResize() {
      if (cameraInstance && rendererInstance && this.$refs.helixContainer) {
        cameraInstance.aspect = this.$refs.helixContainer.clientWidth / this.$refs.helixContainer.clientHeight
        cameraInstance.updateProjectionMatrix()
        rendererInstance.setSize(
          this.$refs.helixContainer.clientWidth,
          this.$refs.helixContainer.clientHeight
        )
      }
    },
    
    rotateLeft() {
      if (dnaInstance) {
        dnaInstance.rotation.y -= Math.PI / 4
      }
    },
    
    rotateRight() {
      if (dnaInstance) {
        dnaInstance.rotation.y += Math.PI / 4
      }
    },
    
    zoomIn() {
      if (cameraInstance) {
        cameraInstance.position.z -= 1
      }
    },
    
    zoomOut() {
      if (cameraInstance) {
        cameraInstance.position.z += 1
      }
    },
    
    resetView() {
      if (cameraInstance) {
        cameraInstance.position.set(0, 0, 10)
        cameraInstance.lookAt(0, 0, 0)
      }
      if (dnaInstance) {
        dnaInstance.rotation.set(0, 0, 0)
      }
    }
  }
}
</script>

<style scoped>
.dna-helix {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.helix-container {
  width: 100%;
  height: 500px;
  border: 1px solid #dee2e6;
  border-radius: 8px;
  overflow: hidden;
}

.controls {
  display: flex;
  gap: 10px;
  justify-content: center;
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
</style>
