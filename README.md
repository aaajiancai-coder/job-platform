# 基于Web的大学生网络求职平台

## 项目简介
本项目是一个面向大学生的全流程网络求职平台，采用前后端分离架构，为大学生提供便捷的求职服务，为企业提供高效的人才招聘渠道。

## 技术架构

### 前端技术栈
- 核心框架：Vue 3
- 构建工具：Vite
- UI框架：Element Plus
- 状态管理：Pinia
- 路由管理：Vue Router
- 数据可视化：ECharts
- HTTP客户端：Axios
- 工具库：lodash、dayjs
- 代码规范：ESLint + Prettier

### 后端技术栈
- 核心框架：Spring Boot 2.7.x
- 持久层：MyBatis-Plus
- 数据库：MySQL 5.7
- 缓存：Redis
- 安全认证：JWT
- 数据库连接池：Druid
- 接口文档：Swagger
- 日志框架：Logback

## 项目结构

### 前端项目结构
```
job_platform/
├── src/
│   ├── api/                # API接口定义
│   │   ├── student/       # 学生相关接口
│   │   ├── company/       # 企业相关接口
│   │   └── admin/         # 管理员相关接口
│   ├── assets/            # 静态资源
│   │   ├── images/        # 图片资源
│   │   ├── styles/        # 样式文件
│   │   └── icons/         # 图标资源
│   ├── components/        # 公共组件
│   │   ├── common/        # 通用组件
│   │   ├── student/       # 学生端组件
│   │   └── company/       # 企业端组件
│   ├── layouts/           # 布局组件
│   ├── pages/             # 页面组件
│   │   ├── student/       # 学生端页面
│   │   ├── company/       # 企业端页面
│   │   └── admin/         # 管理员页面
│   ├── router/            # 路由配置
│   ├── store/             # 状态管理
│   ├── utils/             # 工具函数
│   └── App.vue            # 根组件
├── public/                # 公共资源
├── package.json           # 项目配置
└── vite.config.js         # Vite配置
```

### 后端项目结构
```
spring-boot/
├── src/
│   ├── main/java/com/job/
│   │   ├── config/        # 配置类
│   │   ├── controller/    # 控制器
│   │   ├── service/       # 服务层
│   │   ├── mapper/        # 数据访问层
│   │   ├── entity/        # 实体类
│   │   ├── dto/           # 数据传输对象
│   │   ├── vo/            # 视图对象
│   │   ├── common/        # 公共类
│   │   └── utils/         # 工具类
│   └── main/resources/
│       ├── mapper/        # MyBatis映射文件
│       ├── static/        # 静态资源
│       ├── application.yml # 配置文件
│       └── application-dev.yml
└── pom.xml                # 项目依赖
```

## 数据库设计

### 用户相关表
1. 用户表(user)
```sql
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `role` varchar(20) NOT NULL COMMENT '角色',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

2. 学生信息表(student)
```sql
CREATE TABLE `student` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `gender` varchar(10) DEFAULT NULL COMMENT '性别',
  `birth` date DEFAULT NULL COMMENT '出生日期',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `school` varchar(100) DEFAULT NULL COMMENT '学校',
  `major` varchar(100) DEFAULT NULL COMMENT '专业',
  `education` varchar(20) DEFAULT NULL COMMENT '学历',
  `graduation_time` date DEFAULT NULL COMMENT '毕业时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

3. 企业信息表(company)
```sql
CREATE TABLE `company` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `name` varchar(100) NOT NULL COMMENT '企业名称',
  `industry` varchar(50) DEFAULT NULL COMMENT '行业',
  `scale` varchar(50) DEFAULT NULL COMMENT '规模',
  `address` varchar(200) DEFAULT NULL COMMENT '地址',
  `description` text COMMENT '企业描述',
  `status` tinyint DEFAULT '0' COMMENT '审核状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 业务相关表
1. 职位表(job)
```sql
CREATE TABLE `job` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `company_id` bigint NOT NULL COMMENT '企业ID',
  `title` varchar(100) NOT NULL COMMENT '职位名称',
  `description` text COMMENT '职位描述',
  `requirement` text COMMENT '职位要求',
  `salary` varchar(50) DEFAULT NULL COMMENT '薪资范围',
  `location` varchar(100) DEFAULT NULL COMMENT '工作地点',
  `education` varchar(20) DEFAULT NULL COMMENT '学历要求',
  `experience` varchar(50) DEFAULT NULL COMMENT '经验要求',
  `status` tinyint DEFAULT '1' COMMENT '状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

2. 简历表(resume)
```sql
CREATE TABLE `resume` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `title` varchar(100) NOT NULL COMMENT '简历标题',
  `content` text COMMENT '简历内容',
  `status` tinyint DEFAULT '1' COMMENT '状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

3. 投递记录表(application)
```sql
CREATE TABLE `application` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `job_id` bigint NOT NULL COMMENT '职位ID',
  `resume_id` bigint NOT NULL COMMENT '简历ID',
  `status` tinyint DEFAULT '0' COMMENT '状态',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

## 功能模块

### 1. 学生端功能
1. 用户管理
   - 注册登录
   - 个人信息管理
   - 密码修改
   - 账号安全

2. 简历管理
   - 简历创建
   - 简历编辑
   - 简历投递
   - 投递记录

3. 职位管理
   - 职位搜索
   - 职位收藏
   - 职位投递
   - 投递进度

4. 消息中心
   - 系统通知
   - 投递反馈
   - 面试通知
   - 消息设置

### 2. 企业端功能
1. 企业管理
   - 企业信息管理
   - 企业认证
   - 企业展示

2. 职位管理
   - 职位发布
   - 职位管理
   - 职位统计

3. 简历管理
   - 简历筛选
   - 简历处理
   - 人才库管理

4. 面试管理
   - 面试通知
   - 面试安排
   - 面试反馈

### 3. 管理员功能
1. 用户管理
   - 用户列表
   - 用户审核
   - 用户封禁

2. 企业管理
   - 企业审核
   - 企业管理
   - 企业统计

3. 系统管理
   - 系统配置
   - 日志管理
   - 数据统计

## 接口文档

### 1. 用户接口
```
POST /api/user/register     # 用户注册
POST /api/user/login        # 用户登录
GET /api/user/info          # 获取用户信息
PUT /api/user/info          # 更新用户信息
PUT /api/user/password      # 修改密码
```

### 2. 学生接口
```
GET /api/student/resume     # 获取简历列表
POST /api/student/resume    # 创建简历
PUT /api/student/resume     # 更新简历
GET /api/student/application # 获取投递记录
POST /api/student/application # 投递简历
```

### 3. 企业接口
```
GET /api/company/info       # 获取企业信息
PUT /api/company/info       # 更新企业信息
POST /api/company/job       # 发布职位
GET /api/company/job        # 获取职位列表
PUT /api/company/job        # 更新职位
```

## 部署说明

### 前端部署
```bash
# 安装依赖
npm install

# 开发环境运行
npm run dev

# 生产环境构建
npm run build
```

### 后端部署
```bash
# 打包
mvn clean package

# 运行
java -jar target/job-platform-1.0-SNAPSHOT.jar
```

## 开发环境要求
- Node.js >= 16.0.0
- JDK >= 1.8
- MySQL >= 5.7
- Redis >= 6.0
- Maven >= 3.6

## 注意事项
1. 数据库配置
   - 修改 application.yml 中的数据库连接信息
   - 执行数据库初始化脚本

2. 开发环境
   - 前端开发端口：5173
   - 后端开发端口：8080
   - 跨域配置已预设

3. 生产环境
   - 修改生产环境配置
   - 配置 SSL 证书
   - 配置域名解析

## 贡献指南
1. Fork 本仓库
2. 创建特性分支
3. 提交代码
4. 创建 Pull Request

## 许可证
MIT License

## 联系方式
- 项目维护者：[维护者姓名]
- 邮箱：[邮箱地址]
- 项目地址：[项目地址]
