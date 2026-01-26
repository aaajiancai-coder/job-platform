<script setup>
import ChatWidget from '@/components/common/ChatWidget.vue'
</script>

<template>
  <div class="app-container">
    <router-view></router-view>
    <ChatWidget />
  </div>
</template>

<style>
body {
  margin: 0;
  padding: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}


</style>
