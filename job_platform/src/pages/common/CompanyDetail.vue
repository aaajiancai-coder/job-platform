<template>
  <div class="company-detail-container">
    <NavBar />
    <el-card class="company-detail-card" v-loading="loading">
      <h2>企业详情</h2>
      <div v-if="company" class="company-info-block">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="企业名称">{{ company.companyName }}</el-descriptions-item>
          <el-descriptions-item label="行业">{{ company.industry }}</el-descriptions-item>
          <el-descriptions-item label="公司规模">{{ company.companySize }}</el-descriptions-item>
          <el-descriptions-item label="地区">{{ company.location }}</el-descriptions-item>
          <el-descriptions-item label="官网" :span="2">
            <a :href="company.website" target="_blank">{{ company.website }}</a>
          </el-descriptions-item>
          <el-descriptions-item label="认证状态">{{ company.verificationStatus === 1 ? '已认证' : '未认证' }}</el-descriptions-item>
          <el-descriptions-item label="简介" :span="2">{{ company.description }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <div v-else class="company-info-block">
        <el-empty description="暂无企业信息" />
      </div>
    </el-card>
    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import NavBar from '@/components/common/NavBar.vue'
import Footer from '@/components/common/Footer.vue'
import { fetchCompanyDetail } from '@/api/company'

const route = useRoute()
const company = ref(null)
const loading = ref(false)

onMounted(() => {
  const id = route.params.id
  if (!id) return
  loading.value = true
  fetchCompanyDetail(id).then(res => {
    company.value = res
  }).finally(() => {
    loading.value = false
  })
})
</script>

<style scoped>
.company-detail-container {
  min-height: 100vh;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
}
.company-detail-card {
  width: 900px;
  margin: 40px auto 0 auto;
  padding: 30px 40px 40px 40px;
}
.company-info-block {
  margin-top: 20px;
}
</style>
