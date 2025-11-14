<template>
  <div class="notification-list-container">
    <NavBar />
    <el-card class="notification-list-card" ref="cardRef">
      <div class="header-row">
        <h2>消息通知</h2>
        <el-button type="primary" @click="loadNotifications" :loading="isLoading">刷新</el-button>
      </div>
      <el-table 
        :data="notificationList" 
        style="width: 100%; margin-top: 20px;"
        v-loading="isLoading"
      >
        <el-table-column prop="content" label="消息内容" min-width="400"/>
        <el-table-column prop="type" label="类型" width="120">
          <template #default="scope">
            <el-tag :type="getTypeStyle(scope.row.type)">{{ scope.row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="时间" width="160">
          <template #default="scope">
            {{ formatTime(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="isRead" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.isRead ? 'info' : 'success'" effect="plain">
              {{ scope.row.isRead ? '已读' : '未读' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button 
              size="small" 
              type="primary" 
              @click="markRead(scope.row)" 
              v-if="!scope.row.isRead"
            >标记已读</el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="deleteMsg(scope.row)"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :total="total"
        @current-change="loadNotifications"
        @size-change="loadNotifications"
        layout="total, sizes, prev, pager, next, jumper"
        :page-sizes="[5, 10, 20, 50]"
        style="margin-top: 20px; text-align: right;"
      />
    </el-card>
    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import NavBar from '@/components/common/NavBar.vue'
import Footer from '@/components/common/Footer.vue'
import { fetchNotificationList, deleteNotification, markNotificationRead } from '@/api/notification'
import { useUserStore } from '@/store/user'
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()
const userStore = useUserStore()
const userId = userStore.user?.id
const notificationList = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const cardRef = ref(null)
const isLoading = ref(false)

// 检查用户登录状态
function checkAuth() {
  if (!userId) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return false
  }
  return true
}

function loadNotifications() {
  if (!checkAuth()) return
  
  isLoading.value = true
  
  fetchNotificationList(userId, page.value, pageSize.value)
    .then(res => {
      if (res && res.records) {
        notificationList.value = res.records
        total.value = res.total || 0
      } else {
        notificationList.value = []
        total.value = 0
      }
    })
    .catch(error => {
      console.error('加载通知失败:', error)
      ElMessage.error('加载通知失败，请稍后重试')
    })
    .finally(() => {
      isLoading.value = false
    })
}

function deleteMsg(row) {
  if (!checkAuth()) return

  ElMessageBox.confirm('确定要删除该通知吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    deleteNotification(row.id)
      .then(() => {
        ElMessage.success('删除成功')
        loadNotifications()
      })
      .catch(error => {
        console.error('删除通知失败:', error)
        ElMessage.error('删除失败，请稍后重试')
      })
  })
}

function markRead(row) {
  if (!checkAuth()) return

  markNotificationRead(row.id)
    .then(() => {
      ElMessage.success('已标记为已读')
      loadNotifications()
    })
    .catch(error => {
      console.error('标记已读失败:', error)
      ElMessage.error('操作失败，请稍后重试')
    })
}

function formatTime(val) {
  if (!val) return ''
  try {
    const d = new Date(val)
    if (isNaN(d.getTime())) return ''  // 检查日期是否有效
    
    const y = d.getFullYear()
    const m = String(d.getMonth() + 1).padStart(2, '0')
    const day = String(d.getDate()).padStart(2, '0')
    const h = String(d.getHours()).padStart(2, '0')
    const min = String(d.getMinutes()).padStart(2, '0')
    return `${y}-${m}-${day} ${h}:${min}`
  } catch (error) {
    console.error('日期格式化失败:', error)
    return ''
  }
}

onMounted(() => {
  if (checkAuth()) {
    loadNotifications()
  }
})
</script>

<style scoped>
.notification-list-container {
  min-height: 100vh;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
}
.notification-list-card {
  width: 1200px;
  margin: 40px auto 0 auto;
  padding: 30px 40px 40px 40px;
}
.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style>

<script>
// 辅助函数
function getTypeStyle(type) {
  switch (type) {
    case '面试邀请':
      return 'warning'
    case 'offer':
      return 'success'
    case '反馈':
      return 'info'
    case '状态更新':
      return 'primary'
    default:
      return 'info'
  }
}
</script>
