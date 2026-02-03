<template>
  <div>
    <NavBar />
    <div class="company-dashboard">
      <el-card class="dashboard-card" v-loading="loading">
        <div class="card-header">
          <h2>企业管理平台</h2>
        </div>
        <!-- 企业基本信息：头像悬浮显示上传图标 -->
        <div class="company-info">
          <!-- 头像容器：悬浮显示上传按钮 -->
          <div class="avatar-container" @mouseenter="showAvatarUpload = true" @mouseleave="showAvatarUpload = false">
            <!-- 核心修改：移除el-avatar默认灰色背景，直接显示头像内容 -->
            <img
                class="company-avatar"
                :src="avatarUrl"
                alt="企业头像"
            />
            <!-- 上传图标：仅悬浮时显示，覆盖在头像上 -->
            <div class="avatar-upload-trigger" v-if="showAvatarUpload" @click="openUploadDialog">
              <el-icon class="upload-icon"><Camera /></el-icon>
            </div>
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
        <!-- 统计数据 -->
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
        <!-- 企业详细信息 -->
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
        <!-- 快捷入口 -->
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
import { ElMessage, ElIcon } from 'element-plus'
import { Camera } from '@element-plus/icons-vue' // 相机图标（上传标识）
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
// 控制上传图标显示/隐藏
const showAvatarUpload = ref(false)
// 上传文件的input（用于手动触发）
const uploadInput = ref(null)

const loadAvatar = async () => {
  const data = await getAvatar(userId)
  // 创建临时URL[1,3](@ref)
  const blobUrl = URL.createObjectURL(data)
  avatarUrl.value = blobUrl
  console.log(data)
}

// 打开上传对话框（手动触发input选择文件）
const openUploadDialog = () => {
  if (uploadInput.value) {
    // 手动触发隐藏input的点击，仅触发一次文件选择
    uploadInput.value.click()
  }
}

// 上传前校验
const beforeAvatarUpload = (file) => {
  const isJPGOrPNG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPGOrPNG) {
    ElMessage.error('上传LOGO只能是 JPG 或 PNG 格式！')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('上传LOGO大小不能超过 2MB！')
    return false
  }

  // 校验通过后调用上传接口
  const formData = new FormData()
  formData.append('avatar', file)
  uploadAvatar(userId, formData)
      .then(res => {
        const avatarFileName = res.data
        // 更新头像数据（实时显示上传后的头像）
        detail.value.logoUrl = avatarFileName
        companyInfo.value.logoUrl = avatarFileName
        ElMessage.success('头像上传成功')
      })
      .catch(err => {
        ElMessage.error('头像上传失败')
        console.error('上传失败详情：', err)
      })

  return false // 无需阻止默认行为（已脱离el-upload，仅处理逻辑）
}

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
  // 初始化隐藏的上传input
  uploadInput.value = document.createElement('input')
  uploadInput.value.type = 'file'
  uploadInput.value.accept = 'image/jpeg,image/png'
  uploadInput.value.style.display = 'none'
  uploadInput.value.onchange = (e) => {
    if (e.target.files[0]) {
      // 调用校验和上传逻辑，仅执行一次
      beforeAvatarUpload(e.target.files[0])
      // 重置input值，允许重复选择同一文件，且不会触发二次选择
      e.target.value = ''
    }
  }
  document.body.appendChild(uploadInput.value)
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

/* 头像容器：悬浮显示上传图标 */
.avatar-container {
  position: relative;
  cursor: pointer;
  /* 保证头像和上传图标对齐 */
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 核心修改：企业头像样式，取消灰色圆形，直接显示头像内容 */
.company-avatar {
  width: 64px;
  height: 64px;
  /* 可选：保留圆角（如需方形头像，删除border-radius即可） */
  border-radius: 4px;
  /* 保证头像不变形，填充容器 */
  object-fit: cover;
  /* 取消默认边框和背景，彻底移除灰色样式 */
  border: none;
  background: none;
}

/* 上传图标样式：悬浮时显示，覆盖在头像上 */
.avatar-upload-trigger {
  position: absolute;
  top: 0;
  left: 0;
  width: 64px;
  height: 64px;
  /* 可选：和头像圆角保持一致 */
  border-radius: 4px;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
  /* 阻止事件冒泡，避免额外触发 */
  pointer-events: auto;
}

.avatar-container:hover .avatar-upload-trigger {
  opacity: 1;
}

.upload-icon {
  color: #fff;
  font-size: 20px;
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