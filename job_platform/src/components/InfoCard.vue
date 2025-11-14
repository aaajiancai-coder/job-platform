<template>
  <el-card class="info-card" @click="handleClick" shadow="hover">
    <template v-if="type === 'job'">
      <h3>{{ data.title }}</h3>
      <p class="job-info">{{ data.location }}<span v-if="data.salaryRange"> | {{ data.salaryRange }}</span></p>
    </template>
    <template v-else-if="type === 'company'">
      <img v-if="data.logo" :src="data.logo" :alt="data.companyName" class="company-logo" />
      <p class="company-name">{{ data.companyName }}</p>
      <p class="job-info">{{ data.location }}<span v-if="data.companySize"> | {{ data.companySize }}</span></p>
    </template>
  </el-card>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
const props = defineProps({
  data: Object,
  type: String // 'job' or 'company'
})
const router = useRouter()
const userStore = useUserStore()
function handleClick() {
  if (!userStore.user) {
    ElMessage.warning('请先登录')
    window.scrollTo({ top: 0, behavior: 'smooth' })
    router.push('/login')
  } else {
    if (props.type === 'job') {
      router.push(`/jobs/${props.data.id}`)
    } else if (props.type === 'company') {
      router.push(`/companies/${props.data.id}`)
    }
  }
}
</script>

<style scoped>
.info-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s;
}
.info-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
.company-logo {
  width: 100px;
  height: 100px;
  object-fit: contain;
  margin-bottom: 10px;
}
.company-name {
  font-size: 16px;
  color: #606266;
  margin: 10px 0;
}
.job-info {
  color: #909399;
  font-size: 14px;
}
</style>
