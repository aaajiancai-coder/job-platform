<template>
  <div class="notification-list-container">
    <NavBar />
    <el-card class="notification-list-card" ref="cardRef">
      <div class="header-row">
        <h2>消息通知</h2>
      </div>
      <el-table :data="notificationList" style="width: 100%; margin-top: 20px;">
        <el-table-column prop="content" label="消息内容" />
        <el-table-column prop="type" label="类型" />
        <el-table-column prop="createdAt" label="时间">
          <template #default="scope">
            {{ formatTime(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="isRead" label="状态">
          <template #default="scope">
            <el-tag :type="scope.row.isRead ? 'info' : 'success'">{{ scope.row.isRead ? '已读' : '未读' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" type="primary" @click="markRead(scope.row)" v-if="!scope.row.isRead">标记已读</el-button>
            <el-button size="small" type="danger" @click="deleteMsg(scope.row)">删除</el-button>
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

const userStore = useUserStore()
const userId = userStore.user?.id
const notificationList = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const cardRef = ref(null)
let loadingInstance = null

function loadNotifications() {
  if (cardRef.value) {
    loadingInstance = ElLoading.service({
      target: cardRef.value.$el || cardRef.value,
      lock: true,
      text: '加载中...',
      background: 'rgba(255,255,255,0.7)'
    })
  }
  fetchNotificationList(userId, page.value, pageSize.value).then(res => {
    const pageData = res || {}
    notificationList.value = pageData.records || []
    total.value = pageData.total || 0
  }).finally(() => {
    if (loadingInstance) {
      loadingInstance.close()
      loadingInstance = null
    }
  })
}
function deleteMsg(row) {
  ElMessageBox.confirm('确定要删除该通知吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    deleteNotification(row.id).then(() => {
      ElMessage.success('删除成功')
      loadNotifications()
    })
  })
}
function markRead(row) {
  markNotificationRead(row.id).then(() => {
    ElMessage.success('已标记为已读')
    loadNotifications()
  })
}
function formatTime(val) {
  if (!val) return ''
  const d = new Date(val)
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const h = String(d.getHours()).padStart(2, '0')
  const min = String(d.getMinutes()).padStart(2, '0')
  return `${y}-${m}-${day} ${h}:${min}`
}
onMounted(() => {
  loadNotifications()
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
