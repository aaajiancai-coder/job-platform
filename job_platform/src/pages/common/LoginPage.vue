<template>
  <div class="page-container">
    <NavBar />
    <div class="login-container">
      <el-card class="login-card">
        <h2>用户登录</h2>
        <el-form :model="loginForm" :rules="rules" ref="loginFormRef">
          <el-form-item prop="username">
            <el-input v-model="loginForm.username" placeholder="用户名">
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="loginForm.password" type="password" placeholder="密码">
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSubmit" style="width: 100%">登录</el-button>
          </el-form-item>
          <div class="login-links">
            <el-link type="primary" @click="router.push('/register')">注册账号</el-link>
            <el-link type="primary">忘记密码？</el-link>
          </div>
        </el-form>
      </el-card>
    </div>
    <Footer />
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import NavBar from '../../components/common/NavBar.vue'
import Footer from '../../components/common/Footer.vue'
import { useUserStore } from '../../store/user'

const router = useRouter()
const loginFormRef = ref(null)
const userStore = useUserStore()

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate()
  await userStore.login(loginForm.username, loginForm.password)
  ElMessage.success('登录成功')
  router.push('/home')
}
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.login-container {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
  padding: 40px 20px;
}
.login-card {
  width: 400px;
  padding: 20px;
}
.login-card h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
}
.login-links {
  display: flex;
  justify-content: space-between;
  margin-top: 15px;
}
</style>