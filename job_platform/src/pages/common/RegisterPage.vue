<template>
  <div class="page-container">
    <NavBar />
    <div class="register-container">
      <el-card class="register-card">
        <h2>用户注册</h2>
        <el-form :model="registerForm" :rules="rules" ref="registerFormRef" label-position="top">
          <el-form-item prop="username" label="用户名">
            <el-input v-model="registerForm.username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item prop="email" label="邮箱">
            <el-input v-model="registerForm.email" placeholder="请输入邮箱" />
          </el-form-item>
          <el-form-item prop="phone" label="手机号">
            <el-input v-model="registerForm.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item prop="password" label="密码">
            <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" show-password />
          </el-form-item>
          <el-form-item prop="confirmPassword" label="确认密码">
            <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请再次输入密码" show-password />
          </el-form-item>
          <el-form-item prop="role" label="注册类型">
            <el-radio-group v-model="registerForm.role">
              <el-radio-button label="student">学生</el-radio-button>
              <el-radio-button label="company">企业</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleRegister" style="width: 100%">立即注册</el-button>
          </el-form-item>
          <div class="login-link">
            已有账号？<el-link type="primary" @click="router.push('/login')">立即登录</el-link>
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
import { ElMessage } from 'element-plus'
import NavBar from '../../components/common/NavBar.vue'
import Footer from '../../components/common/Footer.vue'
import { register } from '../../api/user'

const router = useRouter()
const registerFormRef = ref(null)
const registerForm = reactive({
  username: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: '',
  role: 'student'
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const handleRegister = () => {
  registerFormRef.value?.validate(async (valid) => {
    if (valid) {
      try {
        // 只传递后端需要的字段
        const { username, password, email, phone, role } = registerForm
        await register({ username, password, email, phone, role })
          ElMessage.success('注册成功')
          router.push('/login')
      } catch (error) {
        console.error('注册失败:', error)
      }
    }
  })
}
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.register-container {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
  padding: 40px 20px;
}
.register-card {
  width: 400px;
  padding: 20px;
}
.register-card h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
}
.login-link {
  margin-top: 15px;
  text-align: right;
}
</style>