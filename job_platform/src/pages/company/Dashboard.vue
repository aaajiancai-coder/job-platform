<template>
  <div>
    <NavBar />
    <div class="company-dashboard">
      <el-card class="dashboard-card" v-loading="loading">
        <div class="card-header">
          <h2>企业管理平台</h2>
        </div>
        <!-- 企业基本信息：使用 el-upload 实现头像上传/显示 -->
        <div class="company-info">
          <!-- 核心改造：使用 el-upload 组件实现头像功能 -->
          <div class="avatar-container">
            <el-upload
                class="avatar-uploader"
                :action="''"
            :show-file-list="false"
            :before-upload="beforeAvatarUpload"
            :on-success="handleAvatarSuccess"
            :on-error="handleAvatarError"
            accept="image/jpeg,image/png"
            >
            <!-- 头像显示：如果已有头像显示 avatarUrl，否则显示默认占位（可选） -->
            <img
                v-if="avatarUrl"
                class="company-avatar"
                :src="avatarUrl"
                alt="企业头像"
            />
            <!-- 上传图标/默认占位：无头像时显示，有头像时悬浮显示 -->
            <div class="avatar-upload-mask" v-else>
              <el-icon class="upload-icon"><Camera /></el-icon>
            </div>
            </el-upload>
          </div>
          <div class="info-main">
            <div class="company-name">{{ companyInfo.companyName }}</div>
            <div class="company-meta">
              <el-tag v-if="companyInfo.verificationStatus === 1" type="success">已认证</el-tag>
              <el-tag v-else type="info">未认证</el-tag>
              <span class="industry">{{ companyInfo.industry }}</span>
            </div>
          </div>
        </div>
        <!-- 以下内容保持不变 -->
        <el-row :gutter="20" class="stats-row">
          <el-col :span="8">
            <el-card class="stat-card" :body-style="{ padding: '20px' }">
              <div class="stat-title">职位数</div>
              <div class="stat-value">{{ stats.jobCount }}</div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card class="stat-card" :body-style="{ padding: '20px' }">
              <div class="stat-title">收到简历</div>
              <div class="stat-value">{{ stats.resumeCount }}</div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card class="stat-card" :body-style="{ padding: '20px' }">
              <div class="stat-title">未读消息数</div>
              <div class="stat-value">{{ stats.notificationCount }}</div>
            </el-card>
          </el-col>
        </el-row>
        <el-divider>企业详细信息</el-divider>
        <el-form :model="detail" label-width="100px" :disabled="!editMode">
          <el-form-item label="公司名称">
            <el-input v-model="detail.companyName" />
          </el-form-item>
          <el-form-item label="行业">
            <el-input v-model="detail.industry" />
          </el-form-item>
          <el-form-item label="公司规模">
            <el-input v-model="detail.companySize" />
          </el-form-item>
          <el-form-item label="地址">
            <el-input v-model="detail.location" />
          </el-form-item>
          <el-form-item label="简介">
            <el-input type="textarea" v-model="detail.description" />
          </el-form-item>
          <el-form-item label="LOGO">
            <el-input v-model="detail.logoUrl" />
          </el-form-item>
          <el-form-item label="网站">
            <el-input v-model="detail.website" />
          </el-form-item>
        </el-form>
        <div style="margin-top: 10px;">
          <el-button v-if="!editMode" @click="editMode = true">编辑</el-button>
          <el-button v-else type="primary" @click="saveDetail">保存</el-button>
          <el-button v-if="editMode" @click="cancelEdit">取消</el-button>
        </div>
        <div class="quick-actions">
          <el-button type="primary" @click="goTo('jobs')">发布职位</el-button>
          <el-button @click="goTo('jobs')">管理职位</el-button>
          <el-button @click="goTo('resumes')">查看简历</el-button>
        </div>
      </el-card>
    </div>
    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElIcon, ElUpload } from 'element-plus' // 引入 ElUpload 组件
import { Camera } from '@element-plus/icons-vue'
import NavBar from '../../components/common/NavBar.vue'
import Footer from '../../components/common/Footer.vue'
import { fetchCompanyDashboard } from '@/api/company'
import { useUserStore } from '@/store/user'
import { fetchCompanyDetail, updateCompanyDetail } from '@/api/company'
import { uploadAvatar, getAvatar } from '@/api/user.js'

const router = useRouter()
const loading = ref(false)
const avatarUrl = ref('')

const companyInfo = ref({
  companyName: '',
  logoUrl: '',
  industry: '',
  verificationStatus: 0
})

const stats = ref({
  jobCount: 0,
  resumeCount: 0,
  notificationCount: 0
})

const editMode = ref(false)
const detail = ref({})
const userStore = useUserStore()
const companyId = userStore.user?.companyId
const userId = userStore.user?.id
// 移除手动控制的 showAvatarUpload 和 uploadInput 变量

// 加载头像（保持原有逻辑，使用 userId 获取）
const loadAvatar = async () => {
  try {
    if (!userId) return
    const data = await getAvatar(userId)
    const blobUrl = URL.createObjectURL(data)
    avatarUrl.value = blobUrl
  } catch (error) {
    console.error('获取头像失败:', error)
    ElMessage.error('获取头像失败，请稍后重试')
  }
}

// 上传前校验（保持原有逻辑，返回 Promise 适配 el-upload）
const beforeAvatarUpload =  (file) => {
  return new Promise(async (resolve, reject) => {
    const isJPGOrPNG = file.type === 'image/jpeg' || file.type === 'image/png'
    const isLt2M = file.size / 1024 / 1024 < 2

    if (!isJPGOrPNG) {
      ElMessage.error('上传LOGO只能是 JPG 或 PNG 格式！')
      reject(new Error('格式错误'))
      return
    }
    if (!isLt2M) {
      ElMessage.error('上传LOGO大小不能超过 2MB！')
      reject(new Error('文件过大'))
      return
    }

    // 校验通过后手动调用上传接口
    const formData = new FormData()
    formData.append('avatar', file)
    uploadAvatar(userId, formData).then((res) => {
      ElMessage.success(res)
      loadAvatar()
    })

  })
}

// 头像上传成功回调
const handleAvatarSuccess = (res) => {
  const avatarFileName = res.data
  // 更新相关数据
  detail.value.logoUrl = avatarFileName
  companyInfo.value.logoUrl = avatarFileName
  ElMessage.success('头像上传成功')
  // 刷新头像显示
  loadAvatar()
}

// 头像上传失败回调
const handleAvatarError = (err) => {
  console.error('上传失败详情：', err)
  ElMessage.error('头像上传失败')
}

// 以下逻辑保持不变
const fetchDashboardData = async () => {
  if (!companyId) return
  loading.value = true
  try {
    const data = await fetchCompanyDashboard(companyId)
    companyInfo.value = {
      companyName: data.companyName || '未设置企业名称',
      logoUrl: data.logoUrl || '',
      industry: data.industry || '未设置行业',
      verificationStatus: data.verificationStatus || 0
    }
    stats.value = {
      jobCount: data.jobCount || 0,
      resumeCount: data.resumeCount || 0,
      notificationCount: data.notificationCount || 0
    }
  } catch (error) {
    console.error('获取企业数据失败:', error)
    ElMessage.error(error.message || '获取企业数据失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const fetchDetail = async () => {
  if (!companyId) return
  loading.value = true
  try {
    const res = await fetchCompanyDetail(companyId)
    detail.value = res || {}
  } catch (e) {
    ElMessage.error('获取企业详细信息失败')
  } finally {
    loading.value = false
  }
}

const saveDetail = async () => {
  try {
    await updateCompanyDetail(companyId, detail.value)
    ElMessage.success('保存成功')
    editMode.value = false
    fetchDashboardData()
    fetchDetail()
  } catch (e) {
    ElMessage.error('保存失败')
    console.error('保存详情：', e)
  }
}

const cancelEdit = () => {
  editMode.value = false
  fetchDetail()
  companyInfo.value.logoUrl = detail.value.logoUrl || ''
}

const goTo = (type) => {
  if (type === 'jobs') {
    router.push('/company/jobs')
  } else if (type === 'resumes') {
    router.push('/company/resumes')
  }
}

onMounted(() => {
  fetchDashboardData()
  fetchDetail()
  loadAvatar()
  // 移除手动创建 input 的逻辑，由 el-upload 接管
})
</script>

<style scoped>
.company-dashboard {
  padding: 30px 0;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
  display: flex;
  justify-content: center;
}

.dashboard-card {
  width: 1200px;
  margin: 0 auto;
  padding: 30px 40px 40px 40px;
}

.card-header {
  margin-bottom: 30px;
  text-align: left;
}

.company-info {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
}

/* 核心改造：el-upload 头像容器样式 */
.avatar-container {
  position: relative;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-uploader {
  display: inline-block;
}

/* 企业头像样式（保持原有样式不变） */
.company-avatar {
  width: 64px;
  height: 64px;
  border-radius: 4px;
  object-fit: cover;
  border: none;
  background: none;
}

/* 上传遮罩：悬浮时显示（适配 el-upload） */
.avatar-uploader:hover .company-avatar {
  opacity: 0.7;
}

.avatar-uploader:hover .avatar-upload-mask {
  display: flex;
}

/* 上传图标/默认占位样式 */
.avatar-upload-mask {
  width: 64px;
  height: 64px;
  border-radius: 4px;
  background: #f5f7fa;
  display: none;
  align-items: center;
  justify-content: center;
  border: 1px dashed #dcdfe6;
}

.upload-icon {
  color: #c0c4cc;
  font-size: 20px;
}

/* 无头像时默认显示上传占位 */
.avatar-uploader:has(:not(.company-avatar)) .avatar-upload-mask {
  display: flex;
}

.info-main {
  margin-left: 20px;
}

.company-name {
  font-size: 22px;
  font-weight: bold;
  color: #303133;
}

.company-meta {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 16px;
  color: #909399;
}

.industry {
  margin-left: 8px;
}

.stats-row {
  margin-bottom: 30px;
}

.stat-card {
  text-align: center;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, .1);
}

.stat-title {
  color: #909399;
  font-size: 16px;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #409EFF;
}

.quick-actions {
  display: flex;
  gap: 20px;
  justify-content: center;
  margin-top: 20px;
}
</style>