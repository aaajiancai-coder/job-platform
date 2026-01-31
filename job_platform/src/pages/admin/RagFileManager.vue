<template>
  <div class="rag-file-manage-container">
    <NavBar />
    <el-card class="rag-file-manage-card">
      <h2>知识库文件管理</h2>
      
      <!-- 上传区域 -->
      <el-upload
        class="upload-demo"
        drag
        :action="uploadUrl"
        :headers="authHeaders"
        :data="{ userId: currentUser?.id }"
        :show-file-list="false"
        :before-upload="beforeUpload"
        :on-success="handleUploadSuccess"
        :on-error="handleUploadError"
        accept=".txt,.pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx,.html,.htm,.xml,.json"
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          拖拽文件到这里或者 <em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            支持的文件格式：TXT, PDF, DOC, DOCX, XLS, XLSX, PPT, PPTX, HTML, XML, JSON
          </div>
        </template>
      </el-upload>
      
      <!-- 文件列表 -->
      <div class="file-list-section" v-if="fileList.length > 0">
        <h3>已上传文件 ({{ fileList.length }})</h3>
        <el-table :data="fileList" style="width: 100%; margin-top: 20px;" v-loading="loading">
          <el-table-column prop="filename" label="文件名" width="300">
            <template #default="scope">
              <div class="file-info">
                <el-icon><document /></el-icon>
                <span>{{ scope.row.filename }}</span>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="fileType" label="文件类型" width="120">
            <template #default="scope">
              <el-tag type="info">{{ scope.row.fileType }}</el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="fileSize" label="文件大小" width="120">
            <template #default="scope">
              {{ formatFileSize(scope.row.fileSize) }}
            </template>
          </el-table-column>
          
          <el-table-column prop="chunkCount" label="切片数量" width="120">
            <template #default="scope">
              <el-tag type="success">{{ scope.row.chunkCount }}</el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="createdAt" label="上传时间" width="180">
            <template #default="scope">
              {{ formatDate(scope.row.createdAt) }}
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="200">
            <template #default="scope">
              <el-button 
                size="small" 
                type="danger" 
                @click="handleDelete(scope.row)"
                :disabled="deletingIds.includes(scope.row.id)"
              >
                <el-icon v-if="deletingIds.includes(scope.row.id)"><Loading /></el-icon>
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      
      <div v-else-if="!loading" class="empty-state">
        <el-empty description="暂无上传的文件" />
      </div>
    </el-card>
    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'
import { UploadFilled, Document, Loading } from '@element-plus/icons-vue'
import NavBar from '@/components/common/NavBar.vue'
import Footer from '@/components/common/Footer.vue'
import { getRagFiles, deleteRagFile, checkFileType } from '@/api/ragFile'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const fileList = ref([])
const loading = ref(false)
const deletingIds = ref([])

const currentUser = computed(() => userStore.user)

const uploadUrl = computed(() => {
  return '/api/vector/embedding'
})

const authHeaders = computed(() => {
  return {
    'Authorization': `Bearer ${sessionStorage.getItem('token')}`
  }
})

function formatFileSize(size) {
  if (size < 1024) {
    return size + ' B'
  } else if (size < 1024 * 1024) {
    return (size / 1024).toFixed(2) + ' KB'
  } else {
    return (size / (1024 * 1024)).toFixed(2) + ' MB'
  }
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

async function loadFiles() {
  try {
    loading.value = true
    if (!currentUser.value?.id) {
      ElMessage.error('用户未登录')
      return
    }
    
    const response = await getRagFiles(currentUser.value.id)
    if (response.code === 200) {
      fileList.value = response.data || []
    } else {
      ElMessage.error(response.message || '获取文件列表失败')
    }
  } catch (error) {
    console.error('加载文件列表失败:', error)
    ElMessage.error('获取文件列表失败')
  } finally {
    loading.value = false
  }
}

async function beforeUpload(file) {
  // 检查文件类型
  const isSupported = await checkFileType(file.name)
  if (!isSupported.data) {
    ElMessage.error(`不支持的文件类型: ${file.name}`)
    return false
  }
  
  // 检查文件大小 (限制为50MB)
  const isLt50M = file.size / 1024 / 1024 < 50
  if (!isLt50M) {
    ElMessage.error('文件大小不能超过50MB!')
    return false
  }
  
  return true
}

function handleUploadSuccess(response, file) {
  if (response.code === 200) {
    ElMessage.success('文件上传并处理成功')
    loadFiles() // 重新加载文件列表
  } else {
    ElMessage.error(response.message || '文件上传失败')
  }
}

function handleUploadError(error, file) {
  console.error('文件上传失败:', error)
  ElMessage.error('文件上传失败: ' + (error.message || '未知错误'))
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(
      `确认要删除文件 "${row.filename}" 吗？此操作将同时删除向量数据库中的相关内容。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    deletingIds.value.push(row.id)
    
    if (!currentUser.value?.id) {
      ElMessage.error('用户未登录')
      return
    }
    const response = await deleteRagFile(row.id, currentUser.value.id)
    if (response.code === 200) {
      ElMessage.success('文件删除成功')
      // 从列表中移除该文件
      const index = fileList.value.findIndex(item => item.id === row.id)
      if (index > -1) {
        fileList.value.splice(index, 1)
      }
    } else {
      ElMessage.error(response.message || '文件删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除文件失败:', error)
      ElMessage.error('文件删除失败')
    }
  } finally {
    // 移除正在删除的状态
    const index = deletingIds.value.indexOf(row.id)
    if (index > -1) {
      deletingIds.value.splice(index, 1)
    }
  }
}

onMounted(() => {
  loadFiles()
})
</script>

<style scoped>
.rag-file-manage-container {
  min-height: 100vh;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
}

.rag-file-manage-card {
  width: 1200px;
  margin: 40px auto 0 auto;
  padding: 30px 40px 40px 40px;
}

.upload-demo {
  margin-bottom: 30px;
}

.file-list-section h3 {
  margin-bottom: 20px;
  color: #303133;
}

.file-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.file-info .el-icon {
  color: #409eff;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
}

.el-upload__tip {
  color: #909399;
  font-size: 12px;
  margin-top: 10px;
}
</style>