<template>
  <div>
    <NavBar />
    <div class="job-manage-container">
      <el-card class="job-manage-card">
        <div class="header-row">
          <h2>职位管理</h2>
          <el-button type="primary" @click="onAddJob">发布新职位</el-button>
        </div>
        <el-table :data="jobList" style="width: 100%; margin-top: 20px;" v-loading="loading">
          <el-table-column prop="title" label="职位名称" />
          <el-table-column prop="status" label="状态">
            <template #default="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">{{ scope.row.status === 1 ? '在招' : '已下架' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="location" label="工作地点" />
          <el-table-column prop="salaryRange" label="薪资" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="editJob(scope.row)">编辑</el-button>
              <el-button size="small" @click="toggleStatus(scope.row)">{{ scope.row.status === 1 ? '下架' : '上架' }}</el-button>
              <el-button size="small" type="danger" @click="deleteJobRow(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :total="total"
          @current-change="loadJobs"
          @size-change="loadJobs"
          layout="total, sizes, prev, pager, next, jumper"
          :page-sizes="[5, 10, 20, 50]"
          style="margin-top: 20px; text-align: right;"
        />
        <!-- 新增/编辑职位弹窗 -->
        <el-dialog :title="editForm.id ? '编辑职位' : '发布新职位'" v-model="showAddDialog">
          <el-form :model="editForm" label-width="80px">
            <el-form-item label="职位名称">
              <el-input v-model="editForm.title" />
            </el-form-item>
            <el-form-item label="工作地点">
              <el-input v-model="editForm.location" />
            </el-form-item>
            <el-form-item label="薪资">
              <el-input v-model="editForm.salaryRange" />
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="editForm.status">
                <el-option label="在招" :value="1" />
                <el-option label="已下架" :value="0" />
              </el-select>
            </el-form-item>
            <el-form-item label="职位描述">
              <el-input type="textarea" v-model="editForm.description" />
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="showAddDialog = false">取消</el-button>
            <el-button type="primary" @click="saveJob">保存</el-button>
          </template>
        </el-dialog>
      </el-card>
    </div>
    <Footer />
  </div>
</template>

<script>
import NavBar from '../../components/common/NavBar.vue'
import Footer from '../../components/common/Footer.vue'
import { fetchCompanyJobs, createJob, updateJob, deleteJob, changeJobStatus } from '@/api/job'
import { useUserStore } from '@/store/user'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'JobManage',
  components: { NavBar, Footer },
  data() {
    return {
      jobList: [],
      showAddDialog: false,
      editForm: {
        id: null,
        title: '',
        location: '',
        salaryRange: '',
        status: 1,
        description: ''
      },
      page: 1,
      pageSize: 10,
      total: 0,
      loading: false
    }
  },
  created() {
    this.loadJobs()
  },
  methods: {
    async loadJobs() {
      const userStore = useUserStore()
      const companyId = userStore.user?.companyId
      if (!companyId) return
      this.loading = true
      try {
        const res = await fetchCompanyJobs(companyId, this.page, this.pageSize)
        const pageData = res || {}
        this.jobList = pageData.records || []
        this.total = pageData.total || 0
      } finally {
        this.loading = false
      }
    },
    onAddJob() {
      this.editForm = { id: null, title: '', location: '', salaryRange: '', status: 1, description: '' }
      this.showAddDialog = true
    },
    editJob(row) {
      this.editForm = { ...row }
      this.showAddDialog = true
    },
    async saveJob() {
      const userStore = useUserStore()
      const companyId = userStore.user?.companyId
      if (!companyId) return
      const data = { ...this.editForm, companyId }
      try {
        if (this.editForm.id) {
          await updateJob(this.editForm.id, data)
          ElMessage.success('修改成功')
        } else {
          await createJob(data)
          ElMessage.success('新增成功')
        }
        this.showAddDialog = false
        this.loadJobs()
      } catch (e) {
        ElMessage.error('操作失败')
      }
    },
    async deleteJobRow(row) {
      ElMessageBox.confirm('确定要删除该职位吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(async () => {
        await deleteJob(row.id)
        ElMessage.success('删除成功')
        this.loadJobs()
      })
    },
    async toggleStatus(row) {
      const newStatus = row.status === 1 ? 0 : 1
      await changeJobStatus(row.id, newStatus)
      ElMessage.success('操作成功')
      this.loadJobs()
    }
  }
}
</script>

<style scoped>
.job-manage-container {
  padding: 30px 0;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
  display: flex;
  justify-content: center;
}
.job-manage-card {
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
</style>
