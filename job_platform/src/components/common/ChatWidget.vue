<script setup>
import { ref, reactive, onMounted, nextTick, computed } from 'vue'
import { useUserStore } from '@/store/user'
import { chatStream } from '@/api/ai'
import { marked } from 'marked'

// 配置 marked 选项
marked.setOptions({
  breaks: true,
  gfm: true
})

const userStore = useUserStore()
const isChatOpen = ref(false)
const currentModel = ref('smart') // 'smart' for functionCall, 'database' for vectorSearch
const inputMsg = ref('')
const messages = reactive([
  {
    role: 'bot',
    content: '您好！我是校园招聘智能助手，可以回答您关于职位、公司、申请流程等问题。请问有什么可以帮助您的？',
    mode: 'smart'
  }
])
const isTyping = ref(false)
const messagesRef = ref(null)

// 悬浮球位置和拖拽
const ballRef = ref(null)
const ballPos = reactive({
  x: window.innerWidth - 80,
  y: window.innerHeight - 80,
  isDragging: false
})

const ballStyle = computed(() => ({
  left: ballPos.x + 'px',
  top: ballPos.y + 'px',
  right: 'auto',
  bottom: 'auto',
  opacity: ballPos.isDragging ? 0.8 : 1
}))

const openChat = () => {
  if (ballPos.isDragging) return
  isChatOpen.value = true
}

const closeChat = () => {
  isChatOpen.value = false
}

const switchModel = (model) => {
  currentModel.value = model
  messages.push({
    role: 'system',
    content: `已切换到${model === 'smart' ? '功能对话' : '知识检索'}模式`
  })
  scrollToBottom()
}

const scrollToBottom = async () => {
  await nextTick()
  if (messagesRef.value) {
    messagesRef.value.scrollTop = messagesRef.value.scrollHeight
  }
}

const sendMessage = async () => {
  if (!inputMsg.value.trim() || isTyping.value) return
  
  const userPrompt = inputMsg.value.trim()
  const mode = currentModel.value
  const userId = userStore.user?.id || 'guest'
  
  messages.push({
    role: 'user',
    content: userPrompt
  })
  inputMsg.value = ''
  isTyping.value = true
  scrollToBottom()

  try {
    const response = await chatStream(userPrompt, userId, mode)
    if (!response.ok) throw new Error('Network response was not ok')

    const reader = response.body.getReader()
    const decoder = new TextDecoder()
    let botMsg = reactive({
      role: 'bot',
      content: '',
      mode: mode
    })
    messages.push(botMsg)

    let typewriterQueue = []
    let isTypewriting = false

    const startTypewriter = () => {
      if (isTypewriting) return
      isTypewriting = true
      
      const type = () => {
        if (typewriterQueue.length > 0) {
          const char = typewriterQueue.shift()
          botMsg.content += char
          scrollToBottom()
          setTimeout(type, 15) // 控制打字速度
        } else {
          isTypewriting = false
        }
      }
      type()
    }

    try {
      while (true) {
        const { done, value } = await reader.read()
        if (done) break
        
        let chunk = decoder.decode(value, { stream: true })
        
        // 处理 SSE 格式 (data: chunk\n\n)
        const lines = chunk.split('\n')
        for (const line of lines) {
          if (line.startsWith('data:')) {
            // 关键修复：不要直接使用 trim()，否则会删掉换行符或表格必要的空格
            let content = line.substring(5)
            
            // 如果 content 以空格开头（SSE 标准通常 data: 后面跟一个空格），则去掉第一个空格
            if (content.startsWith(' ')) {
              content = content.substring(1)
            }

            if (content) {
              // 尝试解析 JSON（如果是 Spring Boot 包装的简单 String，可能直接就是文字）
              try {
                // 如果是 JSON 字符串，解析出 data 字段
                if (content.trim().startsWith('{') && content.trim().endsWith('}')) {
                  const json = JSON.parse(content)
                  if (json.data) content = json.data
                }
              } catch (e) {
                // 解析失败说明就是普通文字，保持原样
              }
              
              // 将内容加入打字机队列
              typewriterQueue.push(...content.split(''))
              startTypewriter()
            } else {
              // 处理 data: 后面是空的情况（可能是换行符 chunk）
              // Spring AI 的流式 chunk 如果是换行，data: 后面可能就是空的或只有换行
              typewriterQueue.push('\n')
              startTypewriter()
            }
          }
        }
      }
    } finally {
      reader.releaseLock()
    }
    
    isTyping.value = false
    
    // 如果没有任何内容返回，显示错误
    if (!botMsg.content) {
      botMsg.content = '智能助手暂时无法回复，请检查网络或稍后再试。'
    }
  } catch (error) {
    console.error('Chat error:', error)
    messages.push({
      role: 'bot',
      content: '抱歉，服务出现了一些问题，请稍后再试。',
      mode: mode
    })
    isTyping.value = false
  }
}

const renderMarkdown = (content) => {
  if (!content) return ''
  // 仅保留最基础的 marked 渲染以处理换行，不再做任何符号修复
  return marked(content)
}

// 拖拽逻辑
let dragStartX, dragStartY, initialX, initialY

const startDrag = (e) => {
  if (isChatOpen.value) return
  
  ballPos.isDragging = true
  dragStartX = e.clientX
  dragStartY = e.clientY
  initialX = ballPos.x
  initialY = ballPos.y
  
  document.addEventListener('mousemove', onDrag)
  document.addEventListener('mouseup', stopDrag)
  e.preventDefault()
}

const onDrag = (e) => {
  if (!ballPos.isDragging) return
  
  const deltaX = e.clientX - dragStartX
  const deltaY = e.clientY - dragStartY
  
  let newX = initialX + deltaX
  let newY = initialY + deltaY
  
  const ballSize = 60
  const margin = 10
  
  newX = Math.max(margin, Math.min(newX, window.innerWidth - ballSize - margin))
  newY = Math.max(margin, Math.min(newY, window.innerHeight - ballSize - margin))
  
  ballPos.x = newX
  ballPos.y = newY
}

const stopDrag = () => {
  if (!ballPos.isDragging) return
  ballPos.isDragging = false
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', stopDrag)
  
  // 吸附逻辑
  snapToBoundary()
}

const snapToBoundary = () => {
  const ballSize = 60
  const margin = 20
  const centerX = ballPos.x + ballSize / 2
  const centerY = ballPos.y + ballSize / 2
  
  const toLeft = centerX
  const toRight = window.innerWidth - centerX
  const toTop = centerY
  const toBottom = window.innerHeight - centerY
  
  const minDistance = Math.min(toLeft, toRight, toTop, toBottom)
  
  if (minDistance === toLeft) {
    ballPos.x = margin
  } else if (minDistance === toRight) {
    ballPos.x = window.innerWidth - ballSize - margin
  } else if (minDistance === toTop) {
    ballPos.y = margin
  } else {
    ballPos.y = window.innerHeight - ballSize - margin
  }
}

onMounted(() => {
  // 初始化位置
  ballPos.x = window.innerWidth - 80
  ballPos.y = window.innerHeight - 80
  
  window.addEventListener('resize', () => {
    const ballSize = 60
    const margin = 20
    if (ballPos.x > window.innerWidth - ballSize - margin) {
      ballPos.x = window.innerWidth - ballSize - margin
    }
    if (ballPos.y > window.innerHeight - ballSize - margin) {
      ballPos.y = window.innerHeight - ballSize - margin
    }
  })
})
</script>

<template>
  <div class="chat-widget">
    <!-- 悬浮球 -->
    <div 
      class="chat-floating-ball" 
      v-show="!isChatOpen"
      @click="openChat"
      ref="ballRef"
      :style="ballStyle"
      @mousedown="startDrag"
    >
      <i class="fas fa-robot"></i>
    </div>
    
    <!-- 聊天窗口 -->
    <div class="chat-container" :class="{ open: isChatOpen }">
      <div class="chat-header">
        <div class="chat-title">智能招聘助手</div>
        <button class="chat-close" @click="closeChat">
          <i class="fas fa-times"></i>
        </button>
      </div>
      
      <div class="chat-body">
        <div class="chat-sidebar">
          <div 
            class="chat-model-option" 
            :class="{ active: currentModel === 'smart' }"
            @click="switchModel('smart')"
          >
            <div class="chat-model-icon">
              <i class="fas fa-code"></i>
            </div>
            <div class="chat-model-name">功能对话</div>
          </div>
          <div 
            class="chat-model-option" 
            :class="{ active: currentModel === 'database' }"
            @click="switchModel('database')"
          >
            <div class="chat-model-icon">
              <i class="fas fa-search"></i>
            </div>
            <div class="chat-model-name">知识检索</div>
          </div>
        </div>
        
        <div class="chat-content">
          <div class="chat-messages" ref="messagesRef">
            <TransitionGroup name="msg-list">
              <div 
                v-for="(msg, index) in messages" 
                :key="index"
                :class="['message', msg.role]"
              >
                <template v-if="msg.role === 'system'">
                  <div class="message-bubble system-bubble">
                    {{ msg.content }}
                  </div>
                </template>
                <template v-else>
                  <div class="message-avatar">
                    <i :class="['fas', msg.role === 'user' ? 'fa-user' : 'fa-robot']"></i>
                  </div>
                  <div class="message-bubble">
                    <div v-html="renderMarkdown(msg.content)"></div>
                    <span class="model-indicator" v-if="msg.role === 'bot' && msg.mode">
                      {{ msg.mode === 'smart' ? '功能对话' : '知识检索' }}
                    </span>
                  </div>
                </template>
              </div>
            </TransitionGroup>
            
            <div class="typing-indicator" v-if="isTyping">
              <div>智能助手输入中</div>
              <div class="typing-dots">
                <div class="typing-dot"></div>
                <div class="typing-dot"></div>
                <div class="typing-dot"></div>
              </div>
            </div>
          </div>
          
          <div class="chat-input-area">
            <textarea 
              class="chat-input" 
              v-model="inputMsg" 
              placeholder="请输入您的问题..."
              @keydown.enter.prevent="sendMessage"
            ></textarea>
            <div class="chat-actions">
              <div class="input-icons">
                <button class="action-btn"><i class="far fa-smile"></i></button>
                <button class="action-btn"><i class="fas fa-paperclip"></i></button>
              </div>
              <button 
                class="send-button" 
                @click="sendMessage" 
                :disabled="!inputMsg.trim() || isTyping"
              >发送</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.chat-floating-ball {
    position: fixed;
    width: 60px;
    height: 60px;
    border-radius: 50%;
    background: linear-gradient(135deg, #409EFF 0%, #337ecc 100%);
    box-shadow: 0 4px 20px rgba(64, 158, 255, 0.4);
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    z-index: 9999;
    transition: transform 0.3s ease, box-shadow 0.3s ease;
    color: white;
    font-size: 24px;
}

.chat-floating-ball:hover {
    transform: scale(1.1);
    box-shadow: 0 6px 25px rgba(64, 158, 255, 0.6);
}

.chat-container {
    position: fixed;
    right: -50vw;
    top: 50%;
    transform: translateY(-50%) scale(0.9);
    width: 45vw;
    min-width: 380px;
    height: 600px;
    background: white;
    border-radius: 12px;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
    z-index: 10000;
    transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
    display: flex;
    flex-direction: column;
    overflow: hidden;
    border: 1px solid #e4e7ed;
    opacity: 0;
    pointer-events: none;
}

.chat-container.open {
    right: 20px;
    transform: translateY(-50%) scale(1);
    opacity: 1;
    pointer-events: auto;
}

.chat-header {
    background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);
    color: white;
    padding: 15px 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.chat-title {
    font-size: 18px;
    font-weight: 600;
}

.chat-close {
    background: none;
    border: none;
    color: white;
    font-size: 20px;
    cursor: pointer;
    transition: transform 0.2s;
}

.chat-close:hover {
    transform: rotate(90deg);
}

.chat-body {
    display: flex;
    flex: 1;
    overflow: hidden;
}

.chat-sidebar {
    width: 100px;
    background: #f5f7fa;
    border-right: 1px solid #e4e7ed;
    display: flex;
    flex-direction: column;
    padding: 10px 0;
}

.chat-model-option {
    padding: 15px 10px;
    text-align: center;
    cursor: pointer;
    transition: all 0.2s;
    border-left: 3px solid transparent;
}

.chat-model-option.active {
    background: #ecf5ff;
    border-left: 3px solid #409EFF;
    color: #409EFF;
}

.chat-model-option:hover:not(.active) {
    background: #ebeef5;
}

.chat-model-icon {
    font-size: 20px;
    margin-bottom: 5px;
}

.chat-model-name {
    font-size: 12px;
    font-weight: 500;
}

.chat-content {
    flex: 1;
    display: flex;
    flex-direction: column;
}

.chat-messages {
    flex: 1;
    padding: 15px;
    overflow-y: auto;
    background: #fafafa;
}

.message {
    margin-bottom: 15px;
    display: flex;
    align-items: flex-start;
}

.message.user {
    justify-content: flex-end;
}

.message.system {
  justify-content: center;
}

.message-bubble {
    max-width: 85%;
    padding: 6px 10px;
    border-radius: 10px;
    position: relative;
    line-height: 1.4;
    word-break: break-word;
}

/* 消息内容内部间距极致优化 */
:deep(p) {
    margin: 0;
}

:deep(p:first-child) {
    margin-top: 0;
}

:deep(p:last-child) {
    margin-bottom: 0;
}

/* Markdown 元素样式简化 */
:deep(h3) {
    margin: 8px 0 4px 0;
    color: inherit;
    font-size: 14px;
    font-weight: 600;
}

:deep(hr) {
    border: none;
    border-top: 1px solid #eee;
    margin: 8px 0;
}

:deep(strong) {
    color: inherit;
    font-weight: 600;
}

/* Markdown 表格样式 (保留兼容) */
:deep(table) {
    width: 100%;
    border-collapse: collapse;
    margin: 8px 0;
    font-size: 12px;
    background: #fff;
    border-radius: 6px;
    border: 1px solid #ebeef5;
    box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
}

:deep(.message-bubble) {
    overflow-x: auto;
}

:deep(th), :deep(td) {
    border: 1px solid #ebeef5;
    padding: 12px 10px;
    text-align: left;
    min-width: 100px;
}

:deep(th) {
    background-color: #f5f7fa;
    color: #333;
    font-weight: 600;
}

:deep(tr:nth-child(even)) {
    background-color: #fafafa;
}

:deep(tr:hover) {
    background-color: #f0f9eb;
}

:deep(strong) {
    color: #409EFF;
    font-weight: 600;
}

:deep(ul), :deep(ol) {
    padding-left: 20px;
    margin: 8px 0;
}

:deep(li) {
    margin-bottom: 4px;
}

.system-bubble {
  background: #f0f0f0;
  color: #666;
  font-size: 12px;
  text-align: center;
  max-width: 90%;
}

.message.bot .message-bubble {
    background: white;
    border: 1px solid #e4e7ed;
    border-top-left-radius: 4px;
}

.message.user .message-bubble {
    background: #409EFF;
    color: white;
    border-top-right-radius: 4px;
}

.message-avatar {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 8px;
    flex-shrink: 0;
}

.message.bot .message-avatar {
    background: #f5f7fa;
    color: #409EFF;
}

.message.user .message-avatar {
    background: #ecf5ff;
    color: #409EFF;
    order: 1;
}

.chat-input-area {
    padding: 15px;
    border-top: 1px solid #e4e7ed;
    background: white;
}

.chat-input {
    width: 100%;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    padding: 10px 15px;
    resize: none;
    height: 60px;
    outline: none;
    transition: border 0.3s;
    font-family: inherit;
}

.chat-input:focus {
    border-color: #409EFF;
}

.chat-actions {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 10px;
}

.action-btn {
  background: none;
  border: none;
  color: #909399;
  cursor: pointer;
  padding: 5px;
  font-size: 16px;
}

.action-btn:hover {
  color: #409EFF;
}

.send-button {
    background: #409EFF;
    color: white;
    border: none;
    border-radius: 4px;
    padding: 8px 15px;
    cursor: pointer;
    transition: background 0.3s;
}

.send-button:hover:not(:disabled) {
    background: #66b1ff;
}

.send-button:disabled {
  background: #a0cfff;
  cursor: not-allowed;
}

.model-indicator {
    display: block;
    font-size: 10px;
    margin-top: 5px;
    color: #909399;
    text-align: right;
}

.typing-indicator {
    display: flex;
    align-items: center;
    padding: 10px 15px;
    background: white;
    border: 1px solid #e4e7ed;
    border-radius: 18px;
    border-top-left-radius: 4px;
    width: fit-content;
    margin-bottom: 15px;
    font-size: 12px;
    color: #666;
}

.typing-dots {
  display: flex;
  margin-left: 8px;
}

.typing-dot {
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: #409EFF;
    margin: 0 2px;
    animation: typing 1.4s infinite ease-in-out;
}

.typing-dot:nth-child(1) { animation-delay: -0.32s; }
.typing-dot:nth-child(2) { animation-delay: -0.16s; }

@keyframes typing {
    0%, 80%, 100% { transform: scale(0.8); opacity: 0.5; }
    40% { transform: scale(1); opacity: 1; }
}

/* 滚动条 */
.chat-messages::-webkit-scrollbar {
    width: 6px;
}
.chat-messages::-webkit-scrollbar-track {
    background: #f1f1f1;
}
.chat-messages::-webkit-scrollbar-thumb:hover {
    background: #a8a8a8;
}

/* 消息动画 */
.msg-list-enter-active,
.msg-list-leave-active {
  transition: all 0.3s ease;
}
.msg-list-enter-from {
  opacity: 0;
  transform: translateY(20px);
}
.msg-list-user-enter-from {
  transform: translateX(20px);
}
.msg-list-bot-enter-from {
  transform: translateX(-20px);
}

.message {
    margin-bottom: 15px;
    display: flex;
    align-items: flex-start;
    animation: fadeIn 0.3s ease forwards;
}

@keyframes fadeIn {
    from { opacity: 0; transform: translateY(10px); }
    to { opacity: 1; transform: translateY(0); }
}

.chat-model-option:active {
    transform: scale(0.95);
}

.send-button:active:not(:disabled) {
    transform: scale(0.95);
}
</style>
