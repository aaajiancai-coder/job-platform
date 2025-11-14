<template>
  <div class="home-container">
    <!-- 导航栏 -->
    <NavBar />

    <!-- 轮播图+平台简介+数据展示 -->
    <div class="home-header">
      <el-carousel  >
        <el-carousel-item v-for="(item, i) in banners" :key="i">
          <img :src="item.img" :alt="item.title" class="banner-img" />
          <div class="banner-caption">{{ item.title }}</div>
        </el-carousel-item>
      </el-carousel>
      <div class="platform-intro">
        <h1>专为大学生打造的高质量求职平台</h1>
        <p>连接名企与青年人才，助力梦想起航</p>
        <div class="platform-stats">
          <div><strong>100,000+</strong><br/>服务大学生</div>
          <div><strong>2,000+</strong><br/>入驻企业</div>
          <div><strong>50,000+</strong><br/>优质职位</div>
        </div>
      </div>
    </div>

    <!-- 热门职位推荐 -->
    <div class="section-container">
      <div class="section-title-row">
        <h2 class="section-title">热门职位推荐</h2>
        <a class="more-link" @click="goToJobs">更多职位</a>
      </div>
      <el-row :gutter="20">
        <el-col :span="8" v-for="job in hotJobs" :key="job.id">
          <InfoCard :data="job" type="job" />
        </el-col>
      </el-row>
    </div>

    <!-- 知名企业 -->
    <div class="section-container">
      <div class="section-title-row">
        <h2 class="section-title">知名企业</h2>
        <a class="more-link" @click="goToCompanies">更多企业</a>
      </div>
      <el-row :gutter="20">
        <el-col :span="8" v-for="company in companies" :key="company.id">
          <InfoCard :data="company" type="company" />
        </el-col>
      </el-row>
    </div>

    <!-- 平台优势 -->
    <div class="section-container advantages">
      <h2 class="section-title">平台优势</h2>
      <el-row :gutter="20">
        <el-col :span="8" v-for="(advantage, index) in advantages" :key="index">
          <div class="advantage-item">
            <el-icon :size="40" class="advantage-icon">
              <component :is="advantage.icon"></component>
            </el-icon>
            <h3>{{ advantage.title }}</h3>
            <p>{{ advantage.description }}</p>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 页脚 -->
    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import NavBar from '@/components/common/NavBar.vue'
import Footer from '@/components/common/Footer.vue'
import InfoCard from '@/components/InfoCard.vue'
import { getHotJobs, getHotCompanies } from '@/api/common'
const router = useRouter()
const goToJobs = () => {
  setTimeout(() => router.push('/jobs'))

}
const goToCompanies = () => {
  setTimeout(() => router.push('/companies'))
}


// 轮播图数据
const banners = [
  { img: '/src/assets/lbt1.jpg', title: '2024春季校园招聘火热进行中' },
  { img: '/src/assets/lbt2.png', title: '招募启示 WE WANT YOU!' },
  { img: '/src/assets/lbt3.png', title: '全城寻人 正在找你' }
]



const hotJobs = ref([])
const companies = ref([])

onMounted(async () => {
  const jobRes = await getHotJobs()
    hotJobs.value = jobRes
  const companyRes = await getHotCompanies()
    companies.value = companyRes
})

const advantages = [
  {
    icon: 'Opportunity',
    title: '海量优质职位',
    description: '覆盖各行各业的优质职位，助你找到理想工作'
  },
  {
    icon: 'Connection',
    title: '直接对接企业',
    description: '去除中间环节，直接与企业HR沟通'
  },
  {
    icon: 'Guide',
    title: '专业求职指导',
    description: '一对一求职指导，助你快速成长'
  }
]
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.section-container {
  padding: 40px 40px;
}

.section-title {
  font-size: 24px;
  margin-bottom: 20px;
  color: #303133;
}

.section-title-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.more-link {
  color: #409EFF;
  cursor: pointer;
  font-size: 15px;
  margin-left: 16px;
  text-decoration: underline;
}

.job-card, .company-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.job-card:hover, .company-card:hover {
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

.advantages {
  background-color: #f5f7fa;
}

.advantage-item {
  text-align: center;
  padding: 20px;
}

.advantage-icon {
  color: #ffffff;
  margin-bottom: 15px;
}

.home-header {
  position: relative;
  margin-bottom: 30px;
  margin-top: 48px;
}

/* 确保轮播图项和图片填满容器 */


.banner-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  /* position: absolute;  */
  /* top: 0; */
  /* left: 0; */
}

.banner-caption {
  position: absolute;
  left: 40px;
  bottom: 30px;
  color: #fff;
  font-size: 28px;
  font-weight: bold;
  text-shadow: 0 2px 8px rgba(0,0,0,0.3);
}

.el-carousel__container{
  position: absolute;
  top: 200;
}

.el-carousel__item {
  background: #ffffff;
  height: 500px;
  /* overflow: hidden; */
  /* padding: 0; */
  /* margin: 0; */
  /* display: flex; */
  /* align-items: center; */
  /* justify-content: center; */
  /* width: 100%;  */
}

.el-carousel {
  height: 550px;
  /* overflow: hidden; */
  position: relative;
}



.platform-intro {
  margin-top: 20px;
  text-align: center;
}

.platform-stats {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-top: 16px;
  font-size: 18px;
  color: #409EFF;
}
</style> 