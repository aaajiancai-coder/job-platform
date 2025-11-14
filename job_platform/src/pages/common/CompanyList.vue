<template>
  <div class="company-list-container">
    <NavBar />
    <el-card class="company-list-card" ref="cardRef">
      <h2>全部企业</h2>
      <el-form :inline="true" class="filter-form">
        <el-form-item label="企业名称">
          <el-input v-model="searchKeyword" placeholder="输入企业名称" />
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
        <el-form-item label="行业">
          <el-select v-model="selectedIndustry" placeholder="全部行业" style="width: 140px;">
            <el-option label="全部" value="" />
            <el-option label="互联网/IT" value="互联网/IT" />
            <el-option label="金融" value="金融" />
            <el-option label="教育" value="教育" />
            <el-option label="制造业" value="制造业" />
          </el-select>
        </el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </el-form>
      <el-table :data="companies" style="width: 100%; margin-top: 20px;">
        <el-table-column prop="companyName" label="企业名称" />
        <el-table-column prop="location" label="地区" />
        <el-table-column prop="industry" label="行业" />
        <el-table-column prop="companySize" label="公司规模" />
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" @click="viewCompany(scope.row)">查看详情</el-button>
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
import { fetchCompanies } from '@/api/company'
import { ElLoading } from 'element-plus'
import { useRouter } from 'vue-router'

const searchKeyword = ref('')
const selectedCity = ref('')
const selectedIndustry = ref('')
const currentPage = ref(1)
const pageSize = ref(8)

const companies = ref([])
const total = ref(0)
const cardRef = ref(null)
let loadingInstance = null
const router = useRouter()

function loadCompanies() {
  if (cardRef.value) {
    loadingInstance = ElLoading.service({
      target: cardRef.value.$el || cardRef.value,
      lock: true,
      text: '加载中...',
      background: 'rgba(255,255,255,0.7)'
    })
  }
  const params = {
    companyName: searchKeyword.value || undefined,
    location: selectedCity.value || undefined,
    industry: selectedIndustry.value || undefined,
    page: currentPage.value,
    pageSize: pageSize.value
  }
  fetchCompanies(params).then(res => {
    companies.value = Array.isArray(res.records) ? res.records : []
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
  loadCompanies()
}

function handlePageChange(page) {
  currentPage.value = page
  loadCompanies()
}

function handleSizeChange(size) {
  pageSize.value = size
  currentPage.value = 1
  loadCompanies()
}

onMounted(() => {
  loadCompanies()
})

function viewCompany(company) {
  router.push(`/companies/${company.id}`)
}
</script>

<style scoped>
.company-list-container {
  min-height: 100vh;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
}
.company-list-card {
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
