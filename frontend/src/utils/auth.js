import axios from 'axios';

// Configure axios to include the JWT token in requests
export const setupAxiosInterceptors = () => {
  axios.interceptors.request.use(
    (config) => {
      const token = localStorage.getItem('token');
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    },
    (error) => {
      return Promise.reject(error);
    }
  );

  // Add response interceptor to handle token expiration
  axios.interceptors.response.use(
    (response) => response,
    (error) => {
      if (error.response?.status === 401) {
        localStorage.removeItem('token');
        localStorage.removeItem('userEmail');
        window.location.href = '/';
      }
      return Promise.reject(error);
    }
  );
};

// Check if user is authenticated
export const isAuthenticated = () => {
  const token = localStorage.getItem('token');
  return !!token;
};

// Get current user email
export const getCurrentUserEmail = () => {
  return localStorage.getItem('userEmail');
};

// Logout user
export const logout = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('userEmail');
  window.location.href = '/';
};
