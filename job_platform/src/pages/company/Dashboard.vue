<template>
  <div>
    <NavBar />
    <div class="company-dashboard">
      <el-card class="dashboard-card" v-loading="loading">
        <div class="card-header">
          <h2>企业管理平台</h2>
        </div>
        <!-- 企业基本信息 -->
        <div class="company-info">
          <el-avatar :size="64" :src="companyInfo.logoUrl || defaultLogo" />
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
import { ElMessage } from 'element-plus'
import NavBar from '../../components/common/NavBar.vue'
import Footer from '../../components/common/Footer.vue'
import { fetchCompanyDashboard } from '@/api/company'
import { useUserStore } from '@/store/user'
// 你需要实现 fetchCompanyDetail 和 updateCompanyDetail API
import { fetchCompanyDetail, updateCompanyDetail } from '@/api/company'

const router = useRouter()
const loading = ref(false)
const defaultLogo = 'https://img2.baidu.com/it/u=1814323282,2189629152&fm=253&fmt=auto&app=138&f=JPEG?w=256&h=256'

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
    fetchDetail()
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

const cancelEdit = () => {
  editMode.value = false
  fetchDetail()
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
.company-info .info-main {
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
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
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

