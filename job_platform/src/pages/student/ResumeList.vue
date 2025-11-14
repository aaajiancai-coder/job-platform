<template>
  <div class="resume-list-container">
    <NavBar />
    <el-card class="resume-list-card" ref="cardRef">
      <div class="header-row">
        <h2>我的简历</h2>
        <el-button type="primary" @click="onAddResume">新建简历</el-button>
      </div>
      <el-table :data="resumeList" style="width: 100%; margin-top: 20px;">
        <el-table-column prop="title" label="简历名称" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="scope.row.status === '已完善' ? 'success' : 'warning'">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" />
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" @click="editResume(scope.row)">编辑</el-button>
            <el-button size="small" @click="previewResume(scope.row)">预览</el-button>
            <el-button size="small" type="danger" @click="deleteResume(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :total="total"
        @current-change="loadResumes"
        @size-change="loadResumes"
        layout="total, sizes, prev, pager, next, jumper"
        :page-sizes="[5, 10, 20, 50]"
        style="margin-top: 20px; text-align: right;"
      />
      <el-dialog :title="editForm.id ? '编辑简历' : '新增简历'" v-model="showEditDialog">
        <el-form :model="editForm" label-width="80px">
          <el-form-item label="简历名称">
            <el-input v-model="editForm.title" />
          </el-form-item>
          <el-form-item label="内容">
            <el-input type="textarea" v-model="editForm.content" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showEditDialog = false">取消</el-button>
          <el-button type="primary" @click="saveResume">保存</el-button>
        </template>
      </el-dialog>
      <el-dialog title="简历预览" v-model="showPreview">
        <div v-if="currentResume">
          <h3>{{ currentResume.title }}</h3>
          <p>{{ currentResume.content }}</p>
        </div>
      </el-dialog>
    </el-card>
    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import NavBar from '@/components/common/NavBar.vue'
import Footer from '@/components/common/Footer.vue'
import { fetchResumeList, createResume, updateResume, deleteResume as apiDeleteResume } from '@/api/resume'
import { useUserStore } from '@/store/user'
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'

const userStore = useUserStore()
const resumeList = ref([])
const showEditDialog = ref(false)
const showPreview = ref(false)
const editForm = ref({ id: null, title: '', content: '', status: '待完善', updateTime: '' })
const currentResume = ref(null)
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const cardRef = ref(null)
let loadingInstance = null

function loadResumes() {
  if (cardRef.value) {
    loadingInstance = ElLoading.service({
      target: cardRef.value.$el || cardRef.value,
      lock: true,
      text: '加载中...',
      background: 'rgba(255,255,255,0.7)'
    })
  }
  fetchResumeList(userStore.user.id, page.value, pageSize.value).then(res => {
    const pageData = res || {}
    resumeList.value = (pageData.records || []).map(r => ({
      ...r,
      updateTime: r.updatedAt ? r.updatedAt.slice(0, 10) : '',
      status: r.status === 1 ? '已完善' : '待完善',
    }))
    total.value = pageData.total || 0
  }).finally(() => {
    if (loadingInstance) {
      loadingInstance.close()
      loadingInstance = null
    }
  })
}

function editResume(row) {
  editForm.value = { ...row }
  showEditDialog.value = true
}
function previewResume(row) {
  currentResume.value = row
  showPreview.value = true
}
function deleteResume(row) {
  ElMessageBox.confirm('确定要删除该简历吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    apiDeleteResume(row.id).then(() => {
      loadResumes()
      ElMessage.success('删除成功')
    })
  })
}
function saveResume() {
  const data = { ...editForm.value, studentId: userStore.user.id, status: 1 }
  if (editForm.value.id) {
    updateResume(data).then(() => {
      loadResumes()
      showEditDialog.value = false
      ElMessage.success('修改成功')
    })
  } else {
    createResume(data).then(() => {
      loadResumes()
      showEditDialog.value = false
      ElMessage.success('新增成功')
    })
  }
  editForm.value = { id: null, title: '', content: '', status: '待完善', updateTime: '' }
}
function onAddResume() {
  editForm.value = { id: null, title: '', content: '', status: '待完善', updateTime: '' }
  showEditDialog.value = true
}
onMounted(() => {
  loadResumes()
})
</script>

<style scoped>
.resume-list-container {
  min-height: 100vh;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
}
.resume-list-card {
  width: 1200px;
  margin: 40px auto 0 auto;
  padding: 30px 40px 40px 40px;
}
.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style>
