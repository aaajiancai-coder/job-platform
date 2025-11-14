import { createApp } from 'vue'
import { createPinia } from 'pinia' // 引入 Pinia
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import './style.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'

const pinia = createPinia()
const app = createApp(App)


// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

app.use(ElementPlus, { locale: zhCn })
app.use(pinia)
app.use(router)
app.mount('#app')
