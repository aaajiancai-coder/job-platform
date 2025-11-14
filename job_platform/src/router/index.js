// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../store/user'
import { ElMessage } from 'element-plus'

const routes = [
    // 公共
    { path: '/', redirect: '/home' },
    { path: '/home', component: () => import('@/pages/common/HomePage.vue') },
    { path: '/login', component: () => import('@/pages/common/LoginPage.vue') },
    { path: '/register', component: () => import('@/pages/common/RegisterPage.vue') },
    { path: '/jobs', component: () => import('@/pages/common/JobList.vue') },
    { path: '/companies', component: () => import('@/pages/common/CompanyList.vue') },
    { path: '/jobs/:id', component: () => import('@/pages/common/JobDetail.vue') },
    { path: '/companies/:id', component: () => import('@/pages/common/CompanyDetail.vue') },
    // 学生端
    {
        path: '/student',
        redirect: '/home',
        children: [
            { path: 'dashboard', component: () => import('@/pages/student/Dashboard.vue') },
            { path: 'resumes', component: () => import('@/pages/student/ResumeList.vue') },
            { path: 'applications', component: () => import('@/pages/student/ApplicationList.vue') },
            { path: 'notifications', component: () => import('@/pages/student/NotificationList.vue') }
        ]
    },

    // 企业端
    {
        path: '/company',
        redirect: '/home',
        children: [
            { path: 'dashboard', component: () => import('@/pages/company/Dashboard.vue') },
            { path: 'jobs', component: () => import('@/pages/company/JobManage.vue') },
            { path: 'resumes', component: () => import('@/pages/company/ResumeReview.vue') },
            { path: 'notifications', component: () => import('@/pages/company/NotificationList.vue') }
        ]
    },

    // 管理员端
    {
        path: '/admin',
        redirect: '/admin/dashboard',
        children: [
            { path: 'dashboard', component: () => import('@/pages/admin/Dashboard.vue') },
            { path: 'users', component: () => import('@/pages/admin/UserManage.vue') },
            { path: 'companies', component: () => import('@/pages/admin/CompanyAudit.vue') },
            { path: 'statistics', component: () => import('@/pages/admin/DataStatistics.vue') }
        ]
    },

    // 404
    { path: '/:pathMatch(.*)*', component: () => import('@/pages/common/NotFound.vue') }
]

const router = createRouter({
    history: createWebHistory(),
    routes,
    scrollBehavior(to, from, savedPosition) {
        return { top: 0, behavior: 'smooth' }
    }
})
router.beforeEach(async (to, from, next) => {
    const userStore = useUserStore()
    if (!userStore.user && userStore.token) {
        await userStore.fetchUser()
    }
    const user = userStore.user
    const role = user?.role

    // 允许未登录访问 /home、/jobs、/companies
    if (["/home", "/jobs", "/companies"].includes(to.path)) {
        next()
        return
    }

    // 登录/注册页面
    if (to.path === '/login' || to.path === '/register') {
        if (user) {
            next('/')
        } else {
            next()
        }
    } else {
        if (!user) {
            ElMessage.warning('请先登录')
            next('/login')
        } else {
            // 企业未认证拦截
            if (role === 'company' && user.verified !== true && to.path.startsWith('/company') && to.path !== '/company/dashboard') {
                ElMessage.warning('企业未认证，仅可访问企业首页')
                next('/company/dashboard')
                return
            }
            // 角色分流
            if (to.path === '/' || to.path === '/home') {
                if (role === 'student') next('/student/dashboard')
                else if (role === 'company') next('/company/dashboard')
                else if (role === 'admin') next('/admin/dashboard')
                else next('/login')
            } else {
                // 所有已登录用户都可以访问职位和企业相关页面
                if (to.path.startsWith('/jobs/') || to.path.startsWith('/companies/')) {
                    next()
                } else if (role === 'student' && to.path.startsWith('/student')) {
                    next()
                } else if (role === 'company' && to.path.startsWith('/company')) {
                    next()
                } else if (role === 'admin' && to.path.startsWith('/admin')) {
                    next()
                } else {
                    next('/login')
                }
            }
        }
    }
})

export default router