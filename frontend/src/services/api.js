import axios from 'axios'

// Create axios instance with base configuration
const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor to handle auth errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('authToken')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

// Generic CRUD service factory
const createCrudService = (endpoint) => ({
  getAll: (params = {}) => api.get(`/${endpoint}`, { params }),
  getById: (id) => api.get(`/${endpoint}/${id}`),
  getByAccountName: (accountName) => api.get(`/${endpoint}/account/${accountName}`),
  create: (data) => api.post(`/${endpoint}`, data),
  update: (id, data) => api.put(`/${endpoint}/${id}`, data),
  delete: (id) => api.delete(`/${endpoint}/${id}`),
  exists: (accountName) => api.get(`/${endpoint}/exists/${accountName}`),
})

// Platform-specific services
export const githubService = createCrudService('github')
export const bitbucketService = createCrudService('bitbucket')
export const gitlabService = createCrudService('gitlab')
export const jenkinsService = createCrudService('jenkins')
export const jiraService = createCrudService('jira')
export const sonarqubeService = createCrudService('sonarqube')
export const kubernetesService = createCrudService('kubernetes')
export const awsEksService = createCrudService('aws-eks')
export const azureAksService = createCrudService('azure-aks')
export const gcpGkeService = createCrudService('gcp-gke')

export default api

