import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api';

// Create axios instance with default config
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle token refresh
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const refreshToken = localStorage.getItem('refreshToken');
        if (refreshToken) {
          const response = await axios.post(`${API_BASE_URL}/auth/refresh-token`, {
            refreshToken: refreshToken
          });

          if (response.data.success) {
            const { accessToken, refreshToken: newRefreshToken } = response.data.data;
            localStorage.setItem('accessToken', accessToken);
            localStorage.setItem('refreshToken', newRefreshToken);
            
            // Retry original request with new token
            originalRequest.headers.Authorization = `Bearer ${accessToken}`;
            return api(originalRequest);
          }
        }
      } catch (refreshError) {
        // Refresh failed, redirect to login
        localStorage.clear();
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export const authService = {
  // Authentication endpoints
  async login(credentials) {
    try {
      const response = await api.post('/auth/login', credentials);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  async logout() {
    try {
      await api.post('/auth/logout');
    } catch (error) {
      console.error('Logout error:', error);
    }
  },

  async refreshToken(refreshToken) {
    try {
      const response = await api.post('/auth/refresh-token', { refreshToken });
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  async changePassword(passwordData) {
    try {
      const response = await api.post('/auth/change-password', passwordData);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  async forgotPassword(email) {
    try {
      const response = await api.post('/auth/forgot-password', { email });
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  async validateResetToken(token) {
    try {
      const response = await api.post('/auth/validate-reset-token', { token });
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  async resetPassword(resetData) {
    try {
      const response = await api.post('/auth/reset-password', resetData);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  async getCurrentUserProfile() {
    try {
      const response = await api.get('/users/profile');
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  async validateToken(token) {
    try {
      // Simple token validation by making an authenticated request
      const response = await axios.get(`${API_BASE_URL}/users/profile`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      return response.status === 200;
    } catch (error) {
      return false;
    }
  }
};

export const userService = {
  // User management endpoints (Admin only)
  async getAllUsers(params = {}) {
    try {
      const response = await api.get('/users', { params });
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  async getUserById(id) {
    try {
      const response = await api.get(`/users/${id}`);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  async createUser(userData) {
    try {
      const response = await api.post('/users', userData);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  async updateUser(id, userData) {
    try {
      const response = await api.put(`/users/${id}`, userData);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  async enableUser(id) {
    try {
      const response = await api.put(`/users/${id}/enable`);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  async disableUser(id) {
    try {
      const response = await api.put(`/users/${id}/disable`);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  async unlockUser(id) {
    try {
      const response = await api.put(`/users/${id}/unlock`);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  async deleteUser(id) {
    try {
      const response = await api.delete(`/users/${id}`);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  async adminResetPassword(id, newPassword) {
    try {
      const response = await api.post(`/users/${id}/reset-password`, { newPassword });
      return response.data;
    } catch (error) {
      throw error;
    }
  }
};

export const permissionService = {
  // Platform permission endpoints
  async getUserPermissions(userId) {
    try {
      const response = await api.get(`/permissions/users/${userId}`);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  async getPlatformUsers(platform, params = {}) {
    try {
      const response = await api.get(`/permissions/platforms/${platform}`, { params });
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  async grantPermission(userId, platform, permissionLevel) {
    try {
      const response = await api.post(`/permissions/users/${userId}/platforms/${platform}`, {
        permissionLevel
      });
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  async updatePermission(userId, platform, permissionLevel, enabled = true) {
    try {
      const response = await api.put(`/permissions/users/${userId}/platforms/${platform}`, {
        permissionLevel,
        enabled
      });
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  async revokePermission(userId, platform) {
    try {
      const response = await api.delete(`/permissions/users/${userId}/platforms/${platform}`);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  async getAvailablePlatforms() {
    try {
      const response = await api.get('/permissions/platforms');
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  async getPermissionLevels() {
    try {
      const response = await api.get('/permissions/permission-levels');
      return response.data;
    } catch (error) {
      throw error;
    }
  }
};

export default api;

