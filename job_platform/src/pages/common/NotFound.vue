<template>
  <div>
    <NavBar />
    <div style="text-align:center; padding: 100px 0;">
      <h1>404</h1>
      <p>页面未找到</p>
      <el-button type="primary" @click="$router.push('/home')">返回首页</el-button>
    </div>
    <Footer />
  </div>
</template>

<script setup>
import NavBar from '../../components/common/NavBar.vue'
import Footer from '../../components/common/Footer.vue'
// 可选逻辑
</script>
