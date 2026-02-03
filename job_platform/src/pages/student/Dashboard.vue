<template>
  <div>
    <NavBar />
    <div class="student-dashboard-container">
      <el-card class="dashboard-card" v-loading="loading">
        <div class="header-row">
          <div class="user-info">
            <!-- 核心改造：替换 el-avatar 为 el-upload 实现可上传头像 -->
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
              <!-- 头像显示：有头像显示当前头像，无头像显示默认头像 -->
              <img
                  class="user-avatar"
                  :src="avatarUrl || defaultAvatar"
                  alt="个人头像"
              />
              <!-- 上传遮罩：悬浮时显示上传图标 -->
              <div class="avatar-upload-mask">
                <el-icon class="upload-icon"><Camera /></el-icon>
              </div>
              </el-upload>
            </div>
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
import { ElMessage, ElIcon, ElUpload } from 'element-plus' // 引入 ElUpload 组件
import { Camera } from '@element-plus/icons-vue' // 引入相机上传图标
import { fetchStudentDashboard, fetchStudentDetail, updateStudentDetail } from '@/api/student'
import { useUserStore } from '@/store/user'
import { getUnreadNotificationCount } from '@/api/notification'
// 引入头像上传/获取接口（和企业端一致，若接口路径不同请调整）
import { uploadAvatar, getAvatar } from '@/api/user.js'

const router = useRouter()
const userStore = useUserStore()
const studentId = userStore.user?.studentId
const userId = userStore.user?.id // 获取当前用户ID，用于头像上传/获取
const defaultAvatar = 'https://img2.baidu.com/it/u=1814323282,2189629152&fm=253&fmt=auto&app=138&f=JPEG?w=256&h=256'

// 响应式变量
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
const avatarUrl = ref('') // 用于存储当前用户的头像 Blob 地址

// 页面跳转逻辑（保持不变）
function goTo(type) {
  if (type === 'resume') router.push('/student/resumes')
  else if (type === 'jobs') router.push('/jobs')
  else if (type === 'applications') router.push('/student/applications')
}

// 获取个人详细信息（保持不变）
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

// 保存个人详细信息（保持不变）
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

// 取消编辑（保持不变）
const cancelEdit = () => {
  editMode.value = false
  fetchDetail()
}

// 加载用户头像（和企业端逻辑一致，使用 userId 获取）
const loadAvatar = async () => {
  try {
    if (!userId) return
    const data = await getAvatar(userId)
    const blobUrl = URL.createObjectURL(data)
    avatarUrl.value = blobUrl
  } catch (error) {
    console.error('获取个人头像失败:', error)
    // 失败时使用默认头像，不弹出错误提示（避免干扰用户）
    avatarUrl.value = ''
  }
}

// 头像上传前校验（格式+大小，返回 Promise 适配 el-upload）
const beforeAvatarUpload = (file) => {
  return new Promise((resolve, reject) => {
    const isJPGOrPNG = file.type === 'image/jpeg' || file.type === 'image/png'
    const isLt2M = file.size / 1024 / 1024 < 2

    if (!isJPGOrPNG) {
      ElMessage.error('上传头像只能是 JPG 或 PNG 格式！')
      reject(new Error('格式错误'))
      return
    }
    if (!isLt2M) {
      ElMessage.error('上传头像大小不能超过 2MB！')
      reject(new Error('文件过大'))
      return
    }

    // 校验通过后手动调用上传接口
    const formData = new FormData()
    formData.append('avatar', file)
    uploadAvatar(userId, formData)
        .then(res => {
          ElMessage.success(res || '头像上传成功')
          loadAvatar()
        })
  })
}

// 头像上传成功回调
const handleAvatarSuccess = (res) => {
  const avatarFileName = res.data
  // 更新用户头像相关数据（若需要同步到详情表单可补充）
  user.value.avatar = avatarFileName
  detail.value.avatar = avatarFileName
  ElMessage.success('头像上传成功')
  // 刷新头像显示
  loadAvatar()
}

// 头像上传失败回调
const handleAvatarError = (err) => {
  console.error('头像上传失败详情：', err)
  ElMessage.error('头像上传失败，请稍后重试')
}

// 页面挂载逻辑（补充加载头像）
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
    // 加载用户头像（优先级高于接口返回的静态头像）
    await loadAvatar()
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

/* 核心改造：头像上传相关样式 */
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
.user-avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%; /* 保持圆形头像样式，和原有 el-avatar 一致 */
  object-fit: cover;
  border: none;
  background: none;
  transition: opacity 0.3s ease;
}
/* 上传遮罩：悬浮时显示 */
.avatar-upload-mask {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.3);
  display: none;
  align-items: center;
  justify-content: center;
  position: absolute;
  top: 0;
  left: 0;
}
.avatar-uploader:hover .user-avatar {
  opacity: 0.7;
}
.avatar-uploader:hover .avatar-upload-mask {
  display: flex;
}
.upload-icon {
  color: #fff;
  font-size: 20px;
}

/* 原有样式保持不变 */
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