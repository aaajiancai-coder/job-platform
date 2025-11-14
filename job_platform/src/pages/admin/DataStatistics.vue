<template>
  <div class="data-statistics-container">
    <NavBar />
    <el-card class="data-statistics-card">
      <h2>数据统计</h2>
      <el-row :gutter="20" class="chart-row">
        <el-col :span="12">
          <el-card>
            <div class="chart-title">用户增长趋势</div>
            <div id="user-growth-chart" class="chart-placeholder"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card>
            <div class="chart-title">职位投递趋势</div>
            <div id="job-application-chart" class="chart-placeholder"></div>
          </el-card>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="chart-row">
        <el-col :span="12">
          <el-card>
            <div class="chart-title">企业入驻统计</div>
            <div id="company-pie-chart" class="chart-placeholder"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card>
            <div class="chart-title">简历创建趋势</div>
            <div id="resume-trend-chart" class="chart-placeholder"></div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import NavBar from '@/components/common/NavBar.vue'
import Footer from '@/components/common/Footer.vue'
import { fetchUserGrowth, fetchJobApplicationTrend, fetchCompanyPie, fetchResumeTrend } from '@/api/admin'

const userGrowthData = ref([])
const jobApplicationData = ref([])
const companyPieData = ref([])
const resumeTrendData = ref([])

const renderCharts = () => {
  // 用户增长折线图
  const userChart = echarts.init(document.getElementById('user-growth-chart'))
  userChart.setOption({
    title: { text: '' },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: userGrowthData.value.map(i => i.date) },
    yAxis: { type: 'value' },
    series: [{ name: '用户数', type: 'line', data: userGrowthData.value.map(i => i.count) }]
  })
  // 职位投递柱状图
  const jobChart = echarts.init(document.getElementById('job-application-chart'))
  jobChart.setOption({
    title: { text: '' },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: jobApplicationData.value.map(i => i.date) },
    yAxis: { type: 'value' },
    series: [{ name: '投递数', type: 'bar', data: jobApplicationData.value.map(i => i.count) }]
  })
  // 企业入驻饼图
  const companyChart = echarts.init(document.getElementById('company-pie-chart'))
  companyChart.setOption({
    title: { text: '' },
    tooltip: { trigger: 'item' },
    legend: { top: '5%', left: 'center' },
    series: [{
      name: '企业类型',
      type: 'pie',
      radius: '60%',
      center: ['50%', '60%'],
      avoidLabelOverlap: false,
      label: {
        show: true,
        position: 'outside',
        formatter: '{b|{b}}\n{d}%',
        rich: { b: { fontWeight: 'bold', fontSize: 13 } }
      },
      labelLine: {
        show: true,
        length: 18,
        length2: 10
      },
      data: companyPieData.value
    }]
  })
  // 简历投递折线图
  const resumeChart = echarts.init(document.getElementById('resume-trend-chart'))
  resumeChart.setOption({
    title: { text: '' },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: resumeTrendData.value.map(i => i.date) },
    yAxis: { type: 'value' },
    series: [{ name: '投递数', type: 'line', data: resumeTrendData.value.map(i => i.count) }]
  })
}

onMounted(async () => {
  const [userRes, jobRes, companyRes, resumeRes] = await Promise.all([
    fetchUserGrowth(),
    fetchJobApplicationTrend(),
    fetchCompanyPie(),
    fetchResumeTrend()
  ])
  userGrowthData.value = userRes || []
  jobApplicationData.value = jobRes || []
  companyPieData.value = companyRes || []
  resumeTrendData.value = resumeRes || []
  renderCharts()
})
</script>

<style scoped>
.data-statistics-container {
  min-height: 100vh;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
}
.data-statistics-card {
  width: 1200px;
  margin: 40px auto 0 auto;
  padding: 30px 40px 60px 40px;
}
.chart-row {
  margin-bottom: 30px;
}
.chart-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 10px;
}
.chart-placeholder {
  height: 350px;
  background: #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #bbb;
  font-size: 16px;
}
</style>
