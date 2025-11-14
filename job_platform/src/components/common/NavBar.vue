<template>
  <div class="navbar-container">
    <el-menu
      class="navbar"
      mode="horizontal"
      :ellipsis="false"
      router
      :default-active="route.path"
    >
      <div class="logo" @click="router.push('/home')">求职平台</div>
      <div class="flex-grow" />
      <el-menu-item index="/home">首页</el-menu-item>
      <el-menu-item index="/jobs">职位</el-menu-item>
      <el-menu-item index="/companies">公司</el-menu-item>
      <!-- 学生专属 -->
      <el-menu-item v-if="userStore.user?.role === 'student'" index="/student/dashboard">个人首页</el-menu-item>
      <el-menu-item v-if="userStore.user?.role === 'student'" index="/student/resumes">我的简历</el-menu-item>
      <el-menu-item v-if="userStore.user?.role === 'student'" index="/student/applications">投递记录</el-menu-item>
      <el-menu-item v-if="userStore.user?.role === 'student'" index="/student/notifications">消息通知</el-menu-item>
      <!-- 企业专属 -->
      <el-menu-item v-if="userStore.user?.role === 'company'" index="/company/dashboard">企业中心</el-menu-item>
      <el-menu-item v-if="userStore.user?.role === 'company'" index="/company/jobs">职位管理</el-menu-item>
      <el-menu-item v-if="userStore.user?.role === 'company'" index="/company/resumes">简历筛选</el-menu-item>
      <el-menu-item v-if="userStore.user?.role === 'company'" index="/company/notifications">消息通知</el-menu-item>
      <!-- 管理员专属 -->
      <el-menu-item v-if="userStore.user?.role === 'admin'" index="/admin/dashboard">管理后台</el-menu-item>
      <el-menu-item v-if="userStore.user?.role === 'admin'" index="/admin/users">用户管理</el-menu-item>
      <el-menu-item v-if="userStore.user?.role === 'admin'" index="/admin/companies">企业审核</el-menu-item>
      <el-menu-item v-if="userStore.user?.role === 'admin'" index="/admin/statistics">数据统计</el-menu-item>
    </el-menu>
    <div v-if="!userStore.user" class="auth-buttons">
      <el-button type="primary" plain @click="router.push('/login')">登录</el-button>
      <el-button type="primary" @click="router.push('/register')">注册</el-button>
    </div>
    <div v-else class="user-info">
      <span>{{ userStore.user.username }}（{{ userStore.user.role }}）</span>
      <el-button @click="logout">退出登录</el-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../../store/user'
import { onMounted } from 'vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

onMounted(() => {
  if (userStore.token && !userStore.user) {
    userStore.fetchUser()
  }
})

const logout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.navbar-container {
  display: flex;
  align-items: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 0 20px;
}

.navbar {
  flex: 1;
  border: none;
}

.logo {
  margin: 0;
  font-size: 28px; /* 加大字体大小 */
  color: #409EFF;
  cursor: pointer;
  display: flex;
  align-items: center;
  height: 60px;
  padding: 0 16px;
  user-select: none;
  font-weight: bold; /* 加粗logo字体 */
}

.flex-grow {
  flex-grow: 1;
}

.el-menu-item {
  font-size: 16px;
}

.auth-buttons {
  display: flex;
  gap: 12px;
  margin-left: 20px;
}

.auth-buttons .el-button {
  min-width: 80px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-left: 20px;
}
</style>

