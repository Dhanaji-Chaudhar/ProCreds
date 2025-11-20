import axios from 'axios';
import { toast } from 'react-hot-toast';

// Create axios instance with default config
const api = axios.create({
  baseURL: process.env.REACT_APP_API_URL || '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor for authentication
api.interceptors.request.use(
  (config) => {
    // Add basic auth if credentials are available
    const username = process.env.REACT_APP_API_USERNAME || 'admin';
    const password = process.env.REACT_APP_API_PASSWORD || 'admin123';
    
    if (username && password) {
      const token = btoa(`${username}:${password}`);
      config.headers.Authorization = `Basic ${token}`;
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    // Handle common errors
    if (error.response) {
      const { status, data } = error.response;
      
      switch (status) {
        case 401:
          toast.error('Authentication failed. Please check your credentials.');
          break;
        case 403:
          toast.error('Access denied. You do not have permission to perform this action.');
          break;
        case 404:
          toast.error('Resource not found.');
          break;
        case 409:
          toast.error(data.message || 'Conflict: Resource already exists.');
          break;
        case 422:
          toast.error('Validation failed. Please check your input.');
          break;
        case 500:
          toast.error('Server error. Please try again later.');
          break;
        default:
          toast.error(data.message || 'An unexpected error occurred.');
      }
    } else if (error.request) {
      toast.error('Network error. Please check your connection.');
    } else {
      toast.error('An unexpected error occurred.');
    }
    
    return Promise.reject(error);
  }
);

// Generic API service class
export class ApiService {
  constructor(baseEndpoint) {
    this.baseEndpoint = baseEndpoint;
  }

  async getAll(params = {}) {
    const response = await api.get(this.baseEndpoint, { params });
    return response.data;
  }

  async getById(id) {
    const response = await api.get(`${this.baseEndpoint}/${id}`);
    return response.data;
  }

  async create(data) {
    const response = await api.post(this.baseEndpoint, data);
    return response.data;
  }

  async update(id, data) {
    const response = await api.put(`${this.baseEndpoint}/${id}`, data);
    return response.data;
  }

  async delete(id) {
    const response = await api.delete(`${this.baseEndpoint}/${id}`);
    return response.data;
  }

  async exists(identifier) {
    const response = await api.get(`${this.baseEndpoint}/exists/${identifier}`);
    return response.data;
  }
}

export default api;

