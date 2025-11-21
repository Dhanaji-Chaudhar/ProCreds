import api from './api';

const GITHUB_BASE_URL = '/github';

export const githubService = {
  // Get all GitHub configurations with pagination and search
  getAll: async (params = {}) => {
    const { page = 0, size = 10, sortBy = 'accountName', sortDir = 'asc', search = '' } = params;
    const queryParams = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
      sortBy,
      sortDir,
      ...(search && { search })
    });
    
    const response = await api.get(`${GITHUB_BASE_URL}?${queryParams}`);
    return response.data;
  },

  // Get GitHub configuration by ID
  getById: async (id) => {
    const response = await api.get(`${GITHUB_BASE_URL}/${id}`);
    return response.data;
  },

  // Get GitHub configuration by account name
  getByAccountName: async (accountName) => {
    const response = await api.get(`${GITHUB_BASE_URL}/account/${accountName}`);
    return response.data;
  },

  // Create new GitHub configuration
  create: async (data) => {
    const response = await api.post(GITHUB_BASE_URL, data);
    return response.data;
  },

  // Update GitHub configuration
  update: async (id, data) => {
    const response = await api.put(`${GITHUB_BASE_URL}/${id}`, data);
    return response.data;
  },

  // Delete GitHub configuration
  delete: async (id) => {
    await api.delete(`${GITHUB_BASE_URL}/${id}`);
  },

  // Check if account name exists
  existsByAccountName: async (accountName) => {
    const response = await api.get(`${GITHUB_BASE_URL}/exists/${accountName}`);
    return response.data;
  }
};

export default githubService;

