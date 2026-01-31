<template>
  <div class="admin-dashboard-container">
    <NavBar />
    <el-card class="dashboard-card" v-loading="isLoading">
      <h2>平台管理后台</h2>
      <el-row :gutter="20" class="stats-row">
        <el-col :span="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-title">用户总数</div>
            <div class="stat-value">{{ stats.userCount }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-title">企业总数</div>
            <div class="stat-value">{{ stats.companyCount }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-title">职位总数</div>
            <div class="stat-value">{{ stats.jobCount }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-title">简历总数</div>
            <div class="stat-value">{{ stats.resumeCount }}</div>
          </el-card>
        </el-col>
      </el-row>
      <div class="quick-actions">
        <el-button type="primary" @click="goTo('users')">用户管理</el-button>
        <el-button @click="goTo('companies')">企业审核</el-button>
        <el-button @click="goTo('statistics')">数据统计</el-button>
        <el-button @click="goTo('rag-files')">知识库管理</el-button>
      </div>
      <div class="announcement">
        <el-alert title="平台公告：请及时处理企业审核与用户反馈。" type="info" show-icon />
      </div>
    </el-card>
    <Footer />
  </div>
</template>

<script setup>
import NavBar from '@/components/common/NavBar.vue'
import Footer from '@/components/common/Footer.vue'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const stats = ref({
  userCount: 0,
  companyCount: 0,
  jobCount: 0,
  resumeCount: 0
})

const isLoading = ref(false)

async function loadDashboardStats() {
  try {
    isLoading.value = true
    const res = await request({
      url: '/admin/dashboard/stats',
      method: 'get'
    })
    if (res) {
      stats.value = res
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
    ElMessage.error('加载统计数据失败，请稍后重试')
  } finally {
    isLoading.value = false
  }
}

function goTo(type) {
  if (type === 'users') router.push('/admin/users')
  else if (type === 'companies') router.push('/admin/companies')
  else if (type === 'statistics') router.push('/admin/statistics')
  else if (type === 'rag-files') router.push('/admin/rag-files')
}

onMounted(() => {
  loadDashboardStats()
})
</script>

<style scoped>
.admin-dashboard-container {
  min-height: 100vh;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
}
.dashboard-card {
  width: 1200px;
  margin: 40px auto 0 auto;
  padding: 30px 40px 40px 40px;
}
.stats-row {
  margin-bottom: 30px;
}
.stat-card {
  text-align: center;
  padding: 20px 0;
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
  margin-bottom: 30px;
}
.announcement {
  margin-top: 20px;
}
</style>
