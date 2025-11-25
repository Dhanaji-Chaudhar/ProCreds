import React, { createContext, useContext, useReducer, useEffect } from 'react';
import { authService } from '../services/authService';

const AuthContext = createContext();

// Auth reducer for state management
const authReducer = (state, action) => {
  switch (action.type) {
    case 'LOGIN_START':
      return {
        ...state,
        loading: true,
        error: null
      };
    case 'LOGIN_SUCCESS':
      return {
        ...state,
        loading: false,
        isAuthenticated: true,
        user: action.payload.user,
        token: action.payload.accessToken,
        refreshToken: action.payload.refreshToken,
        roles: action.payload.roles,
        platforms: action.payload.platforms,
        error: null
      };
    case 'LOGIN_FAILURE':
      return {
        ...state,
        loading: false,
        isAuthenticated: false,
        user: null,
        token: null,
        refreshToken: null,
        roles: [],
        platforms: [],
        error: action.payload
      };
    case 'LOGOUT':
      return {
        ...state,
        isAuthenticated: false,
        user: null,
        token: null,
        refreshToken: null,
        roles: [],
        platforms: [],
        error: null,
        loading: false
      };
    case 'UPDATE_PROFILE':
      return {
        ...state,
        user: { ...state.user, ...action.payload }
      };
    case 'SET_LOADING':
      return {
        ...state,
        loading: action.payload
      };
    case 'SET_ERROR':
      return {
        ...state,
        error: action.payload,
        loading: false
      };
    case 'CLEAR_ERROR':
      return {
        ...state,
        error: null
      };
    default:
      return state;
  }
};

const initialState = {
  isAuthenticated: false,
  user: null,
  token: null,
  refreshToken: null,
  roles: [],
  platforms: [],
  loading: false,
  error: null
};

export const AuthProvider = ({ children }) => {
  const [state, dispatch] = useReducer(authReducer, initialState);

  // Initialize auth state from localStorage on app start
  useEffect(() => {
    const initializeAuth = async () => {
      const token = localStorage.getItem('accessToken');
      const refreshToken = localStorage.getItem('refreshToken');
      const user = localStorage.getItem('user');
      const roles = localStorage.getItem('roles');
      const platforms = localStorage.getItem('platforms');

      if (token && user) {
        try {
          // Verify token is still valid
          const isValid = await authService.validateToken(token);
          if (isValid) {
            dispatch({
              type: 'LOGIN_SUCCESS',
              payload: {
                accessToken: token,
                refreshToken: refreshToken,
                user: JSON.parse(user),
                roles: JSON.parse(roles || '[]'),
                platforms: JSON.parse(platforms || '[]')
              }
            });
          } else {
            // Try to refresh token
            if (refreshToken) {
              await refreshAuthToken();
            } else {
              clearAuthData();
            }
          }
        } catch (error) {
          console.error('Auth initialization error:', error);
          clearAuthData();
        }
      }
    };

    initializeAuth();
  }, []);

  const login = async (credentials) => {
    dispatch({ type: 'LOGIN_START' });
    
    try {
      const response = await authService.login(credentials);
      
      if (response.success) {
        const { accessToken, refreshToken, user, roles, platforms } = response.data;
        
        // Store in localStorage
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', refreshToken);
        localStorage.setItem('user', JSON.stringify(user));
        localStorage.setItem('roles', JSON.stringify(roles));
        localStorage.setItem('platforms', JSON.stringify(platforms));
        
        dispatch({
          type: 'LOGIN_SUCCESS',
          payload: response.data
        });
        
        return { success: true };
      } else {
        dispatch({
          type: 'LOGIN_FAILURE',
          payload: response.message
        });
        return { success: false, message: response.message };
      }
    } catch (error) {
      const errorMessage = error.response?.data?.message || 'Login failed';
      dispatch({
        type: 'LOGIN_FAILURE',
        payload: errorMessage
      });
      return { success: false, message: errorMessage };
    }
  };

  const logout = async () => {
    try {
      await authService.logout();
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      clearAuthData();
      dispatch({ type: 'LOGOUT' });
    }
  };

  const refreshAuthToken = async () => {
    try {
      const refreshToken = localStorage.getItem('refreshToken');
      if (!refreshToken) {
        throw new Error('No refresh token available');
      }

      const response = await authService.refreshToken(refreshToken);
      
      if (response.success) {
        const { accessToken, refreshToken: newRefreshToken, user, roles, platforms } = response.data;
        
        // Update localStorage
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', newRefreshToken);
        localStorage.setItem('user', JSON.stringify(user));
        localStorage.setItem('roles', JSON.stringify(roles));
        localStorage.setItem('platforms', JSON.stringify(platforms));
        
        dispatch({
          type: 'LOGIN_SUCCESS',
          payload: response.data
        });
        
        return accessToken;
      } else {
        throw new Error('Token refresh failed');
      }
    } catch (error) {
      console.error('Token refresh error:', error);
      clearAuthData();
      dispatch({ type: 'LOGOUT' });
      throw error;
    }
  };

  const clearAuthData = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    localStorage.removeItem('roles');
    localStorage.removeItem('platforms');
  };

  const updateProfile = (profileData) => {
    dispatch({
      type: 'UPDATE_PROFILE',
      payload: profileData
    });
    
    // Update localStorage
    const updatedUser = { ...state.user, ...profileData };
    localStorage.setItem('user', JSON.stringify(updatedUser));
  };

  const hasRole = (role) => {
    return state.roles.includes(role);
  };

  const hasPlatformAccess = (platform) => {
    return state.platforms.includes(platform) || hasRole('ADMIN');
  };

  const isAdmin = () => {
    return hasRole('ADMIN');
  };

  const isPlatformAdmin = () => {
    return hasRole('PLATFORM_ADMIN') || hasRole('ADMIN');
  };

  const setLoading = (loading) => {
    dispatch({ type: 'SET_LOADING', payload: loading });
  };

  const setError = (error) => {
    dispatch({ type: 'SET_ERROR', payload: error });
  };

  const clearError = () => {
    dispatch({ type: 'CLEAR_ERROR' });
  };

  const value = {
    ...state,
    login,
    logout,
    refreshAuthToken,
    updateProfile,
    hasRole,
    hasPlatformAccess,
    isAdmin,
    isPlatformAdmin,
    setLoading,
    setError,
    clearError
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export default AuthContext;
