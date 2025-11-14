<template>
  <div class="job-detail-container">
    <NavBar />
    <el-card class="job-detail-card" v-loading="loading">
      <h2>职位详情</h2>
      <div v-if="job" class="job-info-block">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="职位名称">{{ job.title }}</el-descriptions-item>
          <el-descriptions-item label="公司">{{ job.companyName }}</el-descriptions-item>
          <el-descriptions-item label="薪资">{{ job.salaryRange }}</el-descriptions-item>
          <el-descriptions-item label="地区">{{ job.location }}</el-descriptions-item>
          <el-descriptions-item label="类型">{{ job.jobType }}</el-descriptions-item>
          <el-descriptions-item label="学历要求">{{ job.educationRequirement }}</el-descriptions-item>
          <el-descriptions-item label="经验要求">{{ job.experienceRequirement }}</el-descriptions-item>
          <el-descriptions-item label="职位描述" :span="2">{{ job.description }}</el-descriptions-item>
          <el-descriptions-item label="职位要求" :span="2">{{ job.requirements }}</el-descriptions-item>
        </el-descriptions>
        <el-button v-if="userStore.user.role === 'student'" type="primary" style="margin-top: 24px;" @click="showApplyDialog = true">投递简历</el-button>
      </div>
      <div v-else class="job-info-block">
        <el-empty description="暂无职位信息" />
      </div>
      <el-dialog title="选择简历投递" v-model="showApplyDialog">
        <el-select v-model="selectedResumeId" placeholder="请选择简历" style="width: 100%;">
          <el-option v-for="resume in resumeList" :key="resume.id" :label="resume.title" :value="resume.id" />
        </el-select>
        <template #footer>
          <el-button @click="showApplyDialog = false">取消</el-button>
          <el-button type="primary" :disabled="!selectedResumeId" @click="handleApply">确认投递</el-button>
        </template>
      </el-dialog>
    </el-card>
    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import NavBar from '@/components/common/NavBar.vue'
import Footer from '@/components/common/Footer.vue'
import { fetchJobDetail } from '@/api/job'
import { fetchResumeList } from '@/api/resume'
import { applyJob } from '@/api/application'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const route = useRoute()
const userStore = useUserStore()
const job = ref(null)
const loading = ref(false)
const showApplyDialog = ref(false)
const resumeList = ref([])
const selectedResumeId = ref(null)

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

function loadResumes() {
  fetchResumeList(userStore.user.id, 1, 100).then(res => {
    resumeList.value = (res.records || [])
  })
}

function handleApply() {
  applyJob({
    jobId: job.value.id,
    resumeId: selectedResumeId.value,
    studentId: userStore.user.id
  }).then(() => {
    ElMessage.success('投递成功')
    showApplyDialog.value = false
  })
}

onMounted(() => {
  const id = route.params.id
  if (!id) return
  loading.value = true
  fetchJobDetail(id).then(res => {
    job.value = res
  }).finally(() => {
    loading.value = false
  })
  if (userStore.user.role === 'student') {
    loadResumes()
  }
})
</script>

<style scoped>
.job-detail-container {
  min-height: 100vh;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
}
.job-detail-card {
  width: 900px;
  margin: 40px auto 0 auto;
  padding: 30px 40px 40px 40px;
}
.job-info-block {
  margin-top: 20px;
}
</style>
