<template>
  <div>
    <NavBar />
    <div class="resume-review-container">
      <el-card class="resume-review-card">
        <div class="header-row">
          <h2>简历筛选</h2>
        </div>
        <el-form :inline="true" class="filter-form" style="margin-bottom: 20px;">
          <el-form-item label="职位">
            <el-select v-model="filter.jobId" placeholder="全部职位" style="width: 180px;">
              <el-option label="全部" :value="''" />
              <el-option v-for="job in jobOptions" :key="job.id" :label="job.title" :value="job.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="filter.status" placeholder="全部状态" style="width: 140px;">
              <el-option label="全部" value="" />
              <el-option label="已读" value="已读" />
              <el-option label="已拒绝" value="已拒绝" />
              <el-option label="已邀请面试" value="已邀请面试" />
              <el-option label="已通过" value="已通过" />
              <el-option label="已发送offer" value="已发送offer" />
            </el-select>
          </el-form-item>
          <el-button type="primary" @click="loadResumes">筛选</el-button>
        </el-form>
        <el-table :data="resumeList" style="width: 100%;" v-loading="loading">
          <el-table-column prop="studentName" label="姓名" />
          <el-table-column prop="jobTitle" label="应聘职位" />
          <el-table-column prop="status" label="状态">
            <template #default="scope">
              <el-tag :type="statusType(scope.row.status)">{{ scope.row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="applyTime" label="投递时间">
            <template #default="scope">{{ formatTime(scope.row.applyTime) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="450px">
            <template #default="scope">
              <el-button size="small" @click="viewResume(scope.row)">查看</el-button>
              <el-button size="small" @click="setStatus(scope.row, '已邀请面试')">邀请面试</el-button>
              <el-button size="small" @click="setStatus(scope.row, '已拒绝')">拒绝</el-button>
              <el-button size="small" @click="setStatus(scope.row, '已读')">标记已读</el-button>
              <el-button size="small" @click="setStatus(scope.row, '已通过')">通过</el-button>
              <el-button size="small" type="success" :disabled="scope.row.status !== '已通过'" @click="sendOffer(scope.row)">发送offer</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :total="total"
          @current-change="loadResumes"
          @size-change="loadResumes"
          layout="total, sizes, prev, pager, next, jumper"
          :page-sizes="[5, 10, 20, 50]"
          style="margin-top: 20px; text-align: right;"
        />
        <!-- 简历详情弹窗 -->
        <el-dialog title="简历详情" v-model="showDetail" width="700px">
          <div v-if="detailData">
            <div class="avatar-container">
                <!-- 头像显示：如果已有头像显示 avatarUrl，否则显示默认占位（可选） -->
                <img
                    v-if="avatarUrl"
                    class="company-avatar"
                    :src="avatarUrl"
                    alt="企业头像"
                />
            </div>
            <h4>投递信息</h4>
            <el-table :data="[detailData.application]" border style="margin-bottom: 20px">
              <el-table-column prop="status" label="状态" />
              <el-table-column prop="applyTime" label="投递时间">
                <template #default="scope">{{ formatTime(scope.row.applyTime) }}</template>
              </el-table-column>
              <el-table-column prop="updatedAt" label="更新时间">
                <template #default="scope">{{ formatTime(scope.row.updatedAt) }}</template>
              </el-table-column>
            </el-table>
            <h4>学生信息</h4>
            <el-table :data="[detailData.student]" border style="margin-bottom: 20px">
              <el-table-column prop="realName" label="姓名" />
              <el-table-column prop="gender" label="性别">
                <template #default="scope">{{ genderText(scope.row.gender) }}</template>
              </el-table-column>
              <el-table-column prop="university" label="学校" />
              <el-table-column prop="major" label="专业" />
              <el-table-column prop="education" label="学历" />
              <el-table-column prop="graduationYear" label="毕业年份" />
              <el-table-column prop="skills" label="技能" />
              <el-table-column prop="introduction" label="自我介绍" />
            </el-table>
            <h4>简历信息</h4>
            <el-table :data="[detailData.resume]" border>
              <el-table-column prop="title" label="标题" />
              <el-table-column prop="status" label="简历状态" />
              <el-table-column prop="createdAt" label="创建时间">
                <template #default="scope">{{ formatTime(scope.row.createdAt) }}</template>
              </el-table-column>
              <el-table-column prop="updatedAt" label="更新时间">
                <template #default="scope">{{ formatTime(scope.row.updatedAt) }}</template>
              </el-table-column>
            </el-table>
            <div v-if="detailData.resume && detailData.resume.content" style="margin-top: 16px;">
              <h4>求职意向</h4>
              <el-card shadow="never">{{ detailData.resume.content }}</el-card>
            </div>
          </div>
        </el-dialog>
      </el-card>
    </div>
    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import NavBar from '../../components/common/NavBar.vue'
import Footer from '../../components/common/Footer.vue'
import { fetchCompanyResumes, updateResumeStatus, fetchApplicationDetail, sendOfferToStudent } from '@/api/resume'
import { fetchCompanyJobs } from '@/api/job'
import { useUserStore } from '@/store/user'
import {getAvatar} from '@/api/user'
import dayjs from 'dayjs'
import {ElMessage} from "element-plus";


const userStore = useUserStore()
const companyId = userStore.user?.companyId

const jobOptions = ref([])
const filter = ref({ jobId: '', status: '' })
const resumeList = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)
const showDetail = ref(false)
const currentResume = ref(null)
const detailData = ref(null)
const avatarUrl = ref(null)


function statusType(status) {
  if (status === '已读') return 'info'
  if (status === '已拒绝') return 'danger'
  if (status === '已邀请面试') return 'warning'
  if (status === '已通过') return 'success'
  if (status === '已发送offer') return 'success'
  return 'info'
}

async function viewResume(row) {
  // 异步获取详情
  try {
    const res = await fetchApplicationDetail(row.id)
    detailData.value = res
    showDetail.value = true
    const url = URL.createObjectURL(await getAvatar(res.student.userId))
    avatarUrl.value = url
  } catch {
    ElMessage.error('获取详情失败')
  }
}

async function setStatus(row, status) {
  try {
    await updateResumeStatus(row.id, status)
    ElMessage.success('状态更新成功')
    await loadResumes()
  } catch {
    ElMessage.error('状态更新失败')
  }
}

async function loadResumes() {
  loading.value = true
  try {
    const params = {
      companyId,
      jobId: filter.value.jobId,
      status: filter.value.status,
      page: page.value,
      pageSize: pageSize.value
    }
    const res = await fetchCompanyResumes(params)
    resumeList.value = res.records || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

async function loadJobs() {
  // 获取公司所有职位，用于筛选
  const res = await fetchCompanyJobs(companyId)
  jobOptions.value = res.records || []
}

function formatTime(val) {
  return val ? dayjs(val).format('YYYY-MM-DD HH:mm') : ''
}

function genderText(gender) {
  if (gender === 'male') return '男'
  if (gender === 'female') return '女'
  return '其他'
}

async function sendOffer(row) {
  try {
    await sendOfferToStudent(row.id)
    ElMessage.success('Offer已发送')
    loadResumes()
  } catch {
    ElMessage.error('发送Offer失败')
  }
}

onMounted(() => {
  loadJobs()
  loadResumes()
})
</script>

<style scoped>
.company-avatar {
  width: 64px;
  height: 64px;
  border-radius: 4px;
  object-fit: cover;
  border: none;
  background: none;
}
.resume-review-container {
  padding: 30px 0;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
  display: flex;
  justify-content: center;
}
.resume-review-card {
  width: 1200px;
  margin: 0 auto;
  padding: 30px 40px 40px 40px;
}
.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.filter-form {
  margin-bottom: 20px;
}
</style>
