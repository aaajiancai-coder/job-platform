<script setup>
import { ref, reactive, onMounted, nextTick, computed } from 'vue'
import { useUserStore } from '@/store/user'
import { chatStream } from '@/api/ai'

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
    let botMsg = {
      role: 'bot',
      content: '',
      mode: mode
    }
    messages.push(botMsg)

    try {
      while (true) {
        const { done, value } = await reader.read()
        if (done) break
        
        let chunk = decoder.decode(value, { stream: true })
        
        // 处理 SSE 格式 (data: chunk\n\n)
        const lines = chunk.split('\n')
        for (const line of lines) {
          const trimmedLine = line.trim()
          if (trimmedLine.startsWith('data:')) {
            try {
              // SSE 数据格式，提取 data: 后面的内容
              const jsonStr = trimmedLine.substring(5).trim()
              if (jsonStr) {
                // 尝试解析为 JSON（ServerSentEvent 格式）
                const eventData = JSON.parse(jsonStr)
                if (eventData && eventData.data) {
                  botMsg.content += eventData.data
                }
              }
            } catch (e) {
              // 如果不是 JSON，直接当作纯文本
              const content = trimmedLine.substring(5).trim()
              if (content) {
                botMsg.content += content
              }
            }
          } else if (trimmedLine && !trimmedLine.startsWith(':')) {
            // 如果不是 data: 前缀也不是注释，可能是原始内容
            if (trimmedLine) {
              botMsg.content += trimmedLine
            }
          }
        }
        scrollToBottom()
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
  // 简单处理换行符，如果没有 marked 库的话
  return content.replace(/\n/g, '<br>')
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
    right: -400px;
    top: 50%;
    transform: translateY(-50%);
    width: 380px;
    height: 600px;
    background: white;
    border-radius: 12px;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
    z-index: 10000;
    transition: right 0.3s ease;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    border: 1px solid #e4e7ed;
}

.chat-container.open {
    right: 20px;
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
    max-width: 70%;
    padding: 10px 15px;
    border-radius: 18px;
    position: relative;
    line-height: 1.4;
    word-break: break-word;
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
.chat-messages::-webkit-scrollbar-thumb {
    background: #c1c1c1;
    border-radius: 3px;
}
</style>
