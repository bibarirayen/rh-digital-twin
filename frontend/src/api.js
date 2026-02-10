const base = import.meta.env.VITE_API_BASE_URL || ''
const CACHE_TIMEOUT = 5 * 60 * 1000 // 5 minutes

const apiCache = new Map()

export async function apiGet(path){
  // Check cache first
  const cached = apiCache.get(path)
  if (cached && Date.now() - cached.time < CACHE_TIMEOUT) {
    return cached.data
  }

  const res = await fetch(base + path)
  if(!res.ok) throw new Error('Request failed: '+res.status)
  const data = await res.json()
  
  // Cache the result
  apiCache.set(path, { data, time: Date.now() })
  return data
}

export async function apiUpload(path, file){
  const fd = new FormData()
  fd.append('file', file)
  const res = await fetch(base + path, { method: 'POST', body: fd })
  if(!res.ok) throw new Error('Upload failed: '+res.status)
  return res.json()
}

export function clearCache(){
  apiCache.clear()
}
