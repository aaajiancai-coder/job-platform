// RAG文件管理API
export const uploadRagFile = (file, userId) => {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('userId', userId)
  
  const token = sessionStorage.getItem('token')
  return fetch('/api/vector/embedding', {
    method: 'POST',
    headers: {
      'Authorization': token ? `Bearer ${token}` : ''
    },
    body: formData
  }).then(res => res.json())
}

export const deleteRagFile = (fileId, userId) => {
  const token = sessionStorage.getItem('token')
  return fetch(`/api/vector/file/${fileId}?userId=${userId}`, {
    method: 'DELETE',
    headers: {
      'Authorization': token ? `Bearer ${token}` : '',
      'Content-Type': 'application/json'
    }
  }).then(res => res.json())
}

export const getRagFiles = (userId) => {
  const token = sessionStorage.getItem('token')
  return fetch(`/api/vector/files?userId=${userId}`, {
    method: 'GET',
    headers: {
      'Authorization': token ? `Bearer ${token}` : '',
      'Content-Type': 'application/json'
    }
  }).then(res => res.json())
}

export const checkFileType = (fileName) => {
  const token = sessionStorage.getItem('token')
  return fetch(`/api/vector/check-file-type?fileName=${encodeURIComponent(fileName)}`, {
    method: 'GET',
    headers: {
      'Authorization': token ? `Bearer ${token}` : '',
      'Content-Type': 'application/json'
    }
  }).then(res => res.json())
}