<template>
  <div>
    <NavBar />
    <div class="application-manage-container">
      <el-card class="application-manage-card">
        <div class="header-row">
          <h2>招聘流程管理</h2>
        </div>
        <el-table row-key="id" :data="applicationList" style="width: 100%; margin-top: 20px;">
          <el-table-column type="selection" />
          <el-table-column prop="applicant" label="姓名" />
          <el-table-column prop="jobTitle" label="应聘职位" />
          <el-table-column prop="status" label="进度">
            <template #default="scope">
              <el-tag :type="statusType(scope.row.status)">{{ scope.row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="applyTime" label="投递时间" />
          <el-table-column label="操作" width="220px">
            <template #default="scope">
              <el-button size="small" @click="setStatus(scope.row, '待面试')">邀请面试</el-button>
              <el-button size="small" @click="setStatus(scope.row, '已通过')">通过</el-button>
              <el-button size="small" @click="setStatus(scope.row, '已拒绝')">拒绝</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="batch-actions">
          <el-button size="small" @click="batchSetStatus('待面试')">批量邀请面试</el-button>
          <el-button size="small" @click="batchSetStatus('已通过')">批量通过</el-button>
          <el-button size="small" @click="batchSetStatus('已拒绝')">批量拒绝</el-button>
        </div>
      </el-card>
    </div>
    <Footer />
  </div>
</template>

<script>
import NavBar from '../../components/common/NavBar.vue'
import Footer from '../../components/common/Footer.vue'

export default {
  name: 'ApplicationManage',
  components: { NavBar, Footer },
  data() {
    return {
      applicationList: [
        { id: 1, applicant: '张三', jobTitle: '前端开发工程师', status: '已投递', applyTime: '2024-04-01' },
        { id: 2, applicant: '李四', jobTitle: 'Java开发', status: '待面试', applyTime: '2024-04-02' }
      ],
      multipleSelection: []
    }
  },
  methods: {
    statusType(status) {
      if (status === '已投递') return 'info'
      if (status === '已查看') return 'warning'
      if (status === '待面试') return 'success'
      if (status === '已通过') return 'success'
      if (status === '已拒绝') return 'danger'
      return 'info'
    },
    setStatus(row, status) {
      row.status = status
    },
    batchSetStatus(status) {
      this.multipleSelection.forEach(row => {
        row.status = status
      })
    },
    handleSelectionChange(val) {
      this.multipleSelection = val
    }
  }
}
</script>

<style scoped>
.application-manage-container {
  padding: 30px 0;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
  display: flex;
  justify-content: center;
}
.application-manage-card {
  width: 1200px;
  margin: 0 auto;
  padding: 30px 40px 40px 40px;
}
.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.batch-actions {
  margin-top: 20px;
  display: flex;
  gap: 16px;
}
</style>
