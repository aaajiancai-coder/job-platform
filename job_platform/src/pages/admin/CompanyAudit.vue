<template>
  <div class="company-audit-container">
    <NavBar />
    <el-card class="company-audit-card">
      <h2>企业审核</h2>
      <el-form :inline="true" :model="searchForm" class="filter-form">
        <el-form-item label="企业名称">
          <el-input v-model="searchForm.name" placeholder="输入企业名称" clearable />
        </el-form-item>
        <el-form-item label="认证状态">
          <el-select v-model="searchForm.status" placeholder="全部">
            <el-option label="全部" value="" />
            <el-option label="未认证" value="0" />
            <el-option label="已认证" value="1" />
          </el-select>
        </el-form-item>
        <el-button type="primary" @click="fetchList">查询</el-button>
      </el-form>
      <el-table :data="companyList" style="width: 100%" v-loading="loading">
        <el-table-column prop="companyName" label="企业名称" />
        <el-table-column prop="industry" label="行业" />
        <el-table-column prop="companySize" label="规模" />
        <el-table-column prop="location" label="地址" />
        <el-table-column prop="verificationStatus" label="认证状态">
          <template #default="scope">
            <el-tag :type="scope.row.verificationStatus === 1 ? 'success' : 'info'">
              {{ scope.row.verificationStatus === 1 ? '已认证' : '未认证' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="scope">
            <el-button size="small" type="success" v-if="scope.row.verificationStatus !== 1" @click="changeStatus(scope.row, 1)">通过认证</el-button>
            <el-button size="small" type="warning" v-if="scope.row.verificationStatus === 1" @click="changeStatus(scope.row, 0)">取消认证</el-button>
            <el-button size="small" @click="showDetail(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchList"
        @current-change="fetchList"
        style="margin-top: 20px; text-align: right;"
      />
    </el-card>
    <el-dialog v-model="detailVisible" title="企业详情" width="700px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="企业ID">{{ detail.id }}</el-descriptions-item>
        <el-descriptions-item label="企业名称">{{ detail.companyName }}</el-descriptions-item>
        <el-descriptions-item label="行业">{{ detail.industry }}</el-descriptions-item>
        <el-descriptions-item label="规模">{{ detail.companySize }}</el-descriptions-item>
        <el-descriptions-item label="地址">{{ detail.location }}</el-descriptions-item>
        <el-descriptions-item label="简介">{{ detail.description }}</el-descriptions-item>
        <el-descriptions-item label="LOGO">{{ detail.logoUrl }}</el-descriptions-item>
        <el-descriptions-item label="网站">{{ detail.website }}</el-descriptions-item>
        <el-descriptions-item label="认证状态">{{ detail.verificationStatus === 1 ? '已认证' : '未认证' }}</el-descriptions-item>
        <el-descriptions-item label="关联用户ID">{{ detail.user?.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ detail.user?.username }}</el-descriptions-item>
        <el-descriptions-item label="用户邮箱">{{ detail.user?.email }}</el-descriptions-item>
        <el-descriptions-item label="用户手机号">{{ detail.user?.phone }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { fetchCompanyAuditList, changeCompanyVerifyStatus, fetchCompanyDetailWithUser } from '@/api/admin'
import NavBar from '@/components/common/NavBar.vue'
import Footer from '@/components/common/Footer.vue'

const searchForm = ref({ name: '', status: '' })
const companyList = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)
const detailVisible = ref(false)
const detail = ref({})

const fetchList = async () => {
  loading.value = true
  try {
    const params = {
      page: page.value,
      pageSize: pageSize.value,
      name: searchForm.value.name,
      status: searchForm.value.status
    }
    const res = await fetchCompanyAuditList(params)
    companyList.value = res.records
    total.value = res.total
  } catch (e) {
    ElMessage.error('获取企业列表失败')
  } finally {
    loading.value = false
  }
}

const changeStatus = async (row, status) => {
  try {
    await changeCompanyVerifyStatus(row.id, status)
    ElMessage.success('操作成功')
    fetchList()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const showDetail = async (row) => {
  try {
    const data = await fetchCompanyDetailWithUser(row.id)
    detail.value = data.company
    detail.value.user = data.user
    detailVisible.value = true
  } catch (e) {
    ElMessage.error('获取详情失败')
  }
}

onMounted(fetchList)
</script>

<style scoped>
.company-audit-container {
  min-height: 100vh;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
}
.company-audit-card {
  width: 1200px;
  margin: 40px auto 0 auto;
  padding: 30px 40px 40px 40px;
}
.filter-form {
  margin-bottom: 20px;
}
</style>
