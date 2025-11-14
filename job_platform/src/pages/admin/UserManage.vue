<template>
  <div class="user-manage-container">
    <NavBar />
    <el-card class="user-manage-card">
      <h2>用户管理</h2>
      <el-form :inline="true" class="filter-form">
        <el-form-item label="用户名">
          <el-input v-model="searchKeyword" placeholder="输入用户名" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="searchPhone" placeholder="输入手机号" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="selectedStatus" placeholder="选择状态" clearable>
            <el-option label="正常" value="1" />
            <el-option label="禁用" value="0" />
          </el-select>
        </el-form-item>
        <el-button type="primary" @click="handleSearch" :loading="loading">搜索</el-button>
      </el-form>
      
      <el-table :data="filteredUsers" style="width: 100%; margin-top: 20px;" v-loading="loading" @sort-change="handleSortChange">
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column label="注册时间" sortable="custom" prop="createdAt">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="scope.row.status == 1 ? 'success' : 'info'">
              {{ scope.row.status == 1 ? '已启用' : '未启用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="toggleStatus(scope.row)">
              {{ scope.row.status == 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button size="small" @click="handleResetPwd(scope.row)">重置密码</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination" style="margin-top: 20px; text-align: right;">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadUsers"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
    <Footer />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import NavBar from '@/components/common/NavBar.vue'
import Footer from '@/components/common/Footer.vue'
import { fetchUserList, updateUserStatus, resetUserPassword } from '@/api/admin'

const searchKeyword = ref('')
const searchPhone = ref('')
const selectedStatus = ref('')
const userList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const filteredUsers = computed(() => userList.value)

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

async function loadUsers() {
  try {
    loading.value = true
    const params = {
      username: searchKeyword.value || undefined,
      phone: searchPhone.value || undefined,
      status: selectedStatus.value ,
      page: currentPage.value,
      pageSize: pageSize.value
    }
    
    const data = await fetchUserList(params)
    if (data) {
      userList.value = data.records || []
      total.value = data.total || 0
    } else {
      userList.value = []
      total.value = 0
    }
  } catch (error) {
    userList.value = []
    total.value = 0
    ElMessage.error(error.message || '获取用户列表失败')
    console.error('Load users error:', error)
  } finally {
    loading.value = false
  }
}

async function handleSearch() {
  currentPage.value = 1
  await loadUsers()
}

const toggleStatus = async (user) => {
  try {
    const newStatus = user.status === 1 ? 0 : 1;
    await updateUserStatus(user.id, newStatus);
    ElMessage.success('状态更新成功');
    user.status = newStatus;
  } catch (error) {
    console.error('更新用户状态失败:', error);
    ElMessage.error('更新用户状态失败');
  }
};

async function handleResetPwd(row) {
  try {
    const newPassword = await resetUserPassword(row.id)
    ElMessage.success(`密码重置成功，新密码：${newPassword}`)
  } catch (error) {
    ElMessage.error(error.message || '重置密码失败')
    console.error(error)
  }
}

async function handlePageChange(page) {
  currentPage.value = page
  await loadUsers()
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.user-manage-container {
  min-height: 100vh;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
}
.user-manage-card {
  width: 1200px;
  margin: 40px auto 0 auto;
  padding: 30px 40px 40px 40px;
}
.filter-form {
  margin-bottom: 20px;
}
</style>
