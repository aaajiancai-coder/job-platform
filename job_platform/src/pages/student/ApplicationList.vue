<template>
  <div class="application-list-container">
    <NavBar />
    <el-card class="application-list-card" ref="cardRef">
      <h2>投递记录</h2>
      <el-table :data="applicationList" style="width: 100%; margin-top: 20px;">
        <el-table-column prop="jobTitle" label="职位名称" />
        <el-table-column prop="companyName" label="公司" />
        <el-table-column prop="applyTime" label="投递时间">
          <template #default="scope">
            {{ formatTime(scope.row.applyTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="进度">
          <template #default="scope">
            <el-tag 
              :type="statusType(scope.row.status)"
              :effect="scope.row.status === '已发送offer' ? 'dark' : 'light'"
              :class="{ 'status-tag': true, 'offer-tag': scope.row.status === '已发送offer' }"
            >
              {{ getStatusDisplay(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" @click="viewJob(scope.row)">查看职位</el-button>
            <el-button size="small" type="danger" v-if="scope.row.status === '待处理'" @click="onWithdraw(scope.row)">撤回</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :total="total"
        @current-change="loadApplications"
        @size-change="loadApplications"
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
import { fetchApplicationList, withdrawApplication } from '@/api/application'
import { useUserStore } from '@/store/user'
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const applicationList = ref([])
const router = useRouter()
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const cardRef = ref(null)
let loadingInstance = null

function statusType(status) {
  switch (status) {
    case '待处理':
      return 'info'
    case '已读':
      return ''  // 默认灰色
    case '已邀请面试':
      return 'warning'
    case '已通过':
      return 'success'
    case '已发送offer':
      return 'success'
    case '已拒绝':
      return 'danger'
    default:
      return 'info'
  }
}

function getStatusDisplay(status) {
  switch (status) {
    case '待处理':
      return '待处理'
    case '已读':
      return '简历已读'
    case '已邀请面试':
      return '面试邀请'
    case '已通过':
      return '简历通过'
    case '已发送offer':
      return 'Offer'
    case '已拒绝':
      return '不合适'
    default:
      return status
  }
}

function viewJob(row) {
  router.push(`/jobs/${row.jobId}`)
}
function onWithdraw(row) {
  ElMessageBox.confirm('确定要撤回该投递吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    withdrawApplication(row.id).then(() => {
      loadApplications()
      ElMessage.success('撤回成功')
    })
  })
}
function loadApplications() {
  if (cardRef.value) {
    loadingInstance = ElLoading.service({
      target: cardRef.value.$el || cardRef.value,
      lock: true,
      text: '加载中...',
      background: 'rgba(255,255,255,0.7)'
    })
  }
  fetchApplicationList(userStore.user.id, page.value, pageSize.value).then(res => {
    const pageData = res || {}
    applicationList.value = pageData.records || []
    total.value = pageData.total || 0
  }).finally(() => {
    if (loadingInstance) {
      loadingInstance.close()
      loadingInstance = null
    }
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
  loadApplications()
})
</script>

<style scoped>
.application-list-container {
  min-height: 100vh;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
}
.application-list-card {
  width: 1200px;
  margin: 40px auto 0 auto;
  padding: 30px 40px 40px 40px;
}
.status-tag {
  min-width: 72px;
  text-align: center;
}
.offer-tag {
  background-color: #67c23a !important;
  border-color: #67c23a !important;
  color: white !important;
}
</style>
