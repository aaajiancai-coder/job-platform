<template>
  <div class="job-list-container">
    <NavBar />
    <el-card class="job-list-card" ref="cardRef">
      <h2>全部职位</h2>
      <el-form :inline="true" class="filter-form">
        <el-form-item label="关键词">
          <el-input v-model="searchKeyword" placeholder="职位名称/公司" />
        </el-form-item>
        <el-form-item label="地区">
          <el-select v-model="selectedCity" placeholder="全部地区" style="width: 140px;">
            <el-option label="全部" value="" />
            <el-option label="北京" value="北京" />
            <el-option label="上海" value="上海" />
            <el-option label="广州" value="广州" />
            <el-option label="深圳" value="深圳" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="selectedType" placeholder="全部类型" style="width: 140px;">
            <el-option label="全部" value="" />
            <el-option label="技术" value="技术" />
            <el-option label="产品" value="产品" />
            <el-option label="设计" value="设计" />
            <el-option label="运营" value="运营" />
          </el-select>
        </el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </el-form>
      <el-table :data="jobs" style="width: 100%; margin-top: 20px;">
        <el-table-column prop="title" label="职位名称" />
        <el-table-column prop="companyName" label="公司" />
        <el-table-column prop="location" label="地区" />
        <el-table-column prop="salaryRange" label="薪资" />
        <el-table-column prop="jobType" label="类型" />
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" @click="viewJob(scope.row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next"
          :total="total"
          :page-size="pageSize"
          :current-page="currentPage"
          :page-sizes="[8, 12, 16]"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import NavBar from '@/components/common/NavBar.vue'
import Footer from '@/components/common/Footer.vue'
import { fetchJobs } from '@/api/job'
import { ElLoading } from 'element-plus'
import { useRouter } from 'vue-router'

const searchKeyword = ref('')
const selectedCity = ref('')
const selectedType = ref('')
const currentPage = ref(1)
const pageSize = ref(8)

const jobs = ref([])
const total = ref(0)
const cardRef = ref(null)
let loadingInstance = null
const router = useRouter()

function loadJobs() {
  if (cardRef.value) {
    loadingInstance = ElLoading.service({
      target: cardRef.value.$el || cardRef.value,
      lock: true,
      text: '加载中...',
      background: 'rgba(255,255,255,0.7)'
    })
  }
  const params = {
    title: searchKeyword.value || undefined,
    location: selectedCity.value || undefined,
    jobType: selectedType.value || undefined,
    page: currentPage.value,
    pageSize: pageSize.value
  }
  fetchJobs(params).then(res => {
    jobs.value = Array.isArray(res.records) ? res.records : []
    total.value = res.total || 0
  }).finally(() => {
    if (loadingInstance) {
      loadingInstance.close()
      loadingInstance = null
    }
  })
}

function handleSearch() {
  currentPage.value = 1
  loadJobs()
}

function handlePageChange(page) {
  currentPage.value = page
  loadJobs()
}

function handleSizeChange(size) {
  pageSize.value = size
  currentPage.value = 1
  loadJobs()
}

onMounted(() => {
  loadJobs()
})

function viewJob(job) {
  router.push(`/jobs/${job.id}`)
}
</script>

<style scoped>
.job-list-container {
  min-height: 100vh;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
}
.job-list-card {
  width: 1200px;
  margin: 40px auto 0 auto;
  padding: 30px 40px 40px 40px;
}
.filter-form {
  margin-bottom: 20px;
}
.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
