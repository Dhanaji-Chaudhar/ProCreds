// User types
export interface User {
  id: number;
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
  role: UserRole;
  enabled: boolean;
  createdAt: string;
  updatedAt: string;
  lastLoginAt?: string;
}

// User roles
export type UserRole = 'ADMIN' | 'USER' | 'VIEWER';

// Authentication request/response types
export interface LoginRequest {
  username: string;
  password: string;
  rememberMe?: boolean;
}

export interface LoginResponse {
  token: string;
  refreshToken: string;
  user: User;
  expiresIn: number;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  firstName?: string;
  lastName?: string;
}

export interface RegisterResponse {
  user: User;
  message: string;
}

// Password management
export interface PasswordChangeRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

export interface PasswordResetRequest {
  email: string;
}

export interface PasswordResetConfirmRequest {
  token: string;
  newPassword: string;
  confirmPassword: string;
}

export interface PasswordResetResponse {
  message: string;
  success: boolean;
}

// Token management
export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface RefreshTokenResponse {
  token: string;
  refreshToken: string;
  expiresIn: number;
}

export interface TokenValidationRequest {
  token: string;
}

export interface TokenValidationResponse {
  valid: boolean;
  user?: User;
  expiresAt?: string;
}

// Authentication state
export interface AuthState {
  isAuthenticated: boolean;
  user: User | null;
  token: string | null;
  refreshToken: string | null;
  loading: boolean;
  error: string | null;
}

// Permission types
export interface UserPermission {
  id: number;
  userId: number;
  platform: string;
  permissionLevel: PermissionLevel;
  enabled: boolean;
  grantedAt: string;
  grantedBy: number;
}

export type PermissionLevel = 'READ' | 'WRITE' | 'ADMIN';

export interface PermissionRequest {
  userId: number;
  platform: string;
  permissionLevel: PermissionLevel;
}

export interface PermissionResponse {
  permission: UserPermission;
  message: string;
}

// Session management
export interface SessionInfo {
  sessionId: string;
  userId: number;
  createdAt: string;
  lastAccessedAt: string;
  ipAddress: string;
  userAgent: string;
  active: boolean;
}

// Security events
export interface SecurityEvent {
  id: number;
  userId: number;
  eventType: SecurityEventType;
  description: string;
  ipAddress: string;
  userAgent: string;
  timestamp: string;
  severity: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
}

export type SecurityEventType = 
  | 'LOGIN_SUCCESS' 
  | 'LOGIN_FAILURE' 
  | 'LOGOUT' 
  | 'PASSWORD_CHANGE' 
  | 'PASSWORD_RESET' 
  | 'PERMISSION_GRANTED' 
  | 'PERMISSION_REVOKED' 
  | 'ACCOUNT_LOCKED' 
  | 'ACCOUNT_UNLOCKED';

// Admin user management
export interface AdminPasswordResetRequest {
  userId: number;
  newPassword: string;
}

export interface UserUpdateRequest {
  firstName?: string;
  lastName?: string;
  email?: string;
  role?: UserRole;
  enabled?: boolean;
}

export interface UserCreateRequest {
  username: string;
  email: string;
  password: string;
  firstName?: string;
  lastName?: string;
  role: UserRole;
}
