// export const chatStream = (prompt, userId, mode = 'smart') => {
//   const token = sessionStorage.getItem('token')
//   const url = new URL(`/api/ai/${mode}`, window.location.origin)
//   url.searchParams.append('prompt', prompt)
//   url.searchParams.append('userId', userId)
//
//   return fetch(url, {
//     method: 'GET',
//     headers: {
//       'Authorization': token ? `Bearer ${token}` : ''
//     }
//   })
// }

export const chatStream = (prompt, userId, mode = 'smart') => {
  const token = sessionStorage.getItem('token')
  const url = new URL(`/api/ai/${mode}`, window.location.origin)
  url.searchParams.append('prompt', prompt)
  url.searchParams.append('userId', userId)

  return fetch(url, {
    method: 'GET',
    headers: {
      'Authorization': token ? `Bearer ${token}` : ''
    }
  })
}
