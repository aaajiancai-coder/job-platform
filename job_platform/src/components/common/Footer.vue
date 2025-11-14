<template>
  <footer class="footer">
    <div class="footer-content">
      <div class="footer-section">
        <h3>关于我们</h3>
        <ul>
          <li><a href="#">平台介绍</a></li>
          <li><a href="#">加入我们</a></li>
          <li><a href="#">联系我们</a></li>
        </ul>
      </div>
      <div class="footer-section">
        <h3>求职者</h3>
        <ul>
          <li><a href="#">注册简历</a></li>
          <li><a href="#">职位搜索</a></li>
          <li><a href="#">职业发展</a></li>
        </ul>
      </div>
      <div class="footer-section">
        <h3>企业</h3>
        <ul>
          <li><a href="#">企业注册</a></li>
          <li><a href="#">人才招聘</a></li>
          <li><a href="#">企业服务</a></li>
        </ul>
      </div>
      <div class="footer-section">
        <h3>帮助中心</h3>
        <ul>
          <li><a href="#">常见问题</a></li>
          <li><a href="#">用户指南</a></li>
          <li><a href="#">意见反馈</a></li>
        </ul>
      </div>
    </div>
    <div class="footer-bottom">
      <p>© 2024 求职平台 All Rights Reserved</p>
    </div>
  </footer>
</template>

<script setup>
// 这里可以添加需要的逻辑
</script>

<style scoped>
.footer {
  background-color: #f5f7fa;
  padding: 40px 0 20px;
  margin-top: auto;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-around;
  padding: 0 20px;
}

.footer-section {
  flex: 1;
  max-width: 200px;
}

.footer-section h3 {
  font-size: 18px;
  color: #303133;
  margin-bottom: 20px;
}

.footer-section ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-section ul li {
  margin-bottom: 10px;
}

.footer-section ul li a {
  color: #606266;
  text-decoration: none;
  font-size: 14px;
}

.footer-section ul li a:hover {
  color: #409EFF;
}

.footer-bottom {
  text-align: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}

.footer-bottom p {
  color: #909399;
  font-size: 14px;
}
</style> 