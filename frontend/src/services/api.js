import axios from 'axios';
import toast from 'react-hot-toast';

// Create axios instance with base configuration
const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add authentication to all requests
api.interceptors.request.use(
  (config) => {
    // Add Basic Auth header (admin:admin123)
    const credentials = btoa('admin:admin123');
    config.headers.Authorization = `Basic ${credentials}`;
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Add response interceptor for error handling
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    console.error('API Error:', error);
    
    if (error.response) {
      // Server responded with error status
      const { status, data } = error.response;
      
      switch (status) {
        case 400:
          if (data.validationErrors) {
            // Handle validation errors
            Object.entries(data.validationErrors).forEach(([field, message]) => {
              toast.error(`${field}: ${message}`);
            });
          } else {
            toast.error(data.message || 'Bad request');
          }
          break;
        case 401:
          toast.error('Authentication required');
          break;
        case 404:
          toast.error(data.message || 'Resource not found');
          break;
        case 409:
          toast.error(data.message || 'Resource already exists');
          break;
        case 500:
          toast.error('Internal server error. Please try again later.');
          break;
        default:
          toast.error(data.message || 'An unexpected error occurred');
      }
    } else if (error.request) {
      // Network error
      toast.error('Network error. Please check your connection.');
    } else {
      // Other error
      toast.error('An unexpected error occurred');
    }
    
    return Promise.reject(error);
  }
);

export default api;

