<template>
  <div>
    <NavBar />
    <div class="student-dashboard-container">
      <el-card class="dashboard-card" v-loading="loading">
        <div class="header-row">
          <div class="user-info">
            <el-avatar :size="64" :src="user.avatar || defaultAvatar" />
            <div class="info-main">
              <div class="user-name">{{ user.name }}</div>
              <div class="user-meta">{{ user.school }} · {{ user.major }}</div>
            </div>
          </div>
          <div class="stats-row">
            <div class="stat-item">
              <div class="stat-title">已投递</div>
              <div class="stat-value">{{ stats.applied }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-title">未读消息</div>
              <div class="stat-value">{{ stats.unreadMessages }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-title">录用数</div>
              <div class="stat-value">{{ stats.offer }}</div>
            </div>
          </div>
        </div>
        <div class="quick-actions">
          <el-button type="primary" @click="goTo('resume')">我的简历</el-button>
          <el-button @click="goTo('jobs')">职位推荐</el-button>
          <el-button @click="goTo('applications')">投递记录</el-button>
        </div>
        <el-divider>个人详细信息</el-divider>
        <el-form :model="detail" label-width="100px" :disabled="!editMode">
          <el-form-item label="真实姓名">
            <el-input v-model="detail.realName" />
          </el-form-item>
          <el-form-item label="性别">
  <el-select v-model="detail.gender" placeholder="请选择">
    <el-option label="男" value="male" />
    <el-option label="女" value="female" />
    <el-option label="其他" value="other" />
  </el-select>
</el-form-item>
          <el-form-item label="出生日期">
            <el-date-picker v-model="detail.birthDate" type="date" placeholder="选择日期" style="width: 100%;" />
          </el-form-item>
          <el-form-item label="学校">
            <el-input v-model="detail.university" />
          </el-form-item>
          <el-form-item label="专业">
            <el-input v-model="detail.major" />
          </el-form-item>
          <el-form-item label="学历">
            <el-input v-model="detail.education" />
          </el-form-item>
          <el-form-item label="毕业年份">
            <el-input v-model="detail.graduationYear" />
          </el-form-item>
          <el-form-item label="技能">
            <el-input v-model="detail.skills" />
          </el-form-item>
          <el-form-item label="自我介绍">
            <el-input type="textarea" v-model="detail.introduction" />
          </el-form-item>
        </el-form>
        <div style="margin-top: 10px;">
          <el-button v-if="!editMode" @click="editMode = true">编辑</el-button>
          <el-button v-else type="primary" @click="saveDetail">保存</el-button>
          <el-button v-if="editMode" @click="cancelEdit">取消</el-button>
        </div>
      </el-card>
    </div>
    <Footer />
  </div>
</template>

<script setup>
import NavBar from '@/components/common/NavBar.vue'
import Footer from '@/components/common/Footer.vue'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { fetchStudentDashboard, fetchStudentDetail, updateStudentDetail } from '@/api/student'
import { useUserStore } from '@/store/user'
import { getUnreadNotificationCount } from '@/api/notification'

const router = useRouter()
const userStore = useUserStore()
const studentId = userStore.user?.studentId
const defaultAvatar = 'https://img2.baidu.com/it/u=1814323282,2189629152&fm=253&fmt=auto&app=138&f=JPEG?w=256&h=256'

const user = ref({
  name: '',
  school: '',
  major: '',
  avatar: ''
})
const stats = ref({
  applied: 0,
  unreadMessages: 0,
  offer: 0
})
const loading = ref(false)
const editMode = ref(false)
const detail = ref({})

function goTo(type) {
  if (type === 'resume') router.push('/student/resumes')
  else if (type === 'jobs') router.push('/jobs')
  else if (type === 'applications') router.push('/student/applications')
}

const fetchDetail = async () => {
  if (!studentId) return
  loading.value = true
  try {
    const res = await fetchStudentDetail(studentId)
    detail.value = res
  } catch (e) {
    ElMessage.error('获取个人详细信息失败')
  } finally {
    loading.value = false
  }
}

const saveDetail = async () => {
  try {
    await updateStudentDetail(studentId, detail.value)
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

onMounted(async () => {
  loading.value = true
  try {
    const [dashboard, unreadCount] = await Promise.all([
      fetchStudentDashboard(studentId),
      getUnreadNotificationCount(userStore.user.id)
    ])
    user.value = {
      name: dashboard.name,
      school: dashboard.school,
      major: dashboard.major,
      avatar: dashboard.avatar || defaultAvatar
    }
    stats.value = {
      applied: dashboard.applied,
      unreadMessages: unreadCount,
      offer: dashboard.offer
    }
    await fetchDetail()
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.student-dashboard-container {
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
.header-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 30px;
}
.user-info {
  display: flex;
  align-items: center;
}
.info-main {
  margin-left: 20px;
}
.user-name {
  font-size: 22px;
  font-weight: bold;
  color: #303133;
}
.user-meta {
  margin-top: 8px;
  color: #909399;
}
.stats-row {
  display: flex;
  gap: 40px;
}
.stat-item {
  text-align: center;
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
</style>
