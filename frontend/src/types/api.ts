// Generic API Response wrapper
export interface ApiResponse<T = any> {
  data: T;
  message?: string;
  success: boolean;
  timestamp?: string;
}

// Error response structure
export interface ApiError {
  message: string;
  details?: string;
  field?: string;
  code?: string;
  timestamp?: string;
}

// Pagination types
export interface PageRequest {
  page: number;
  size: number;
  sort?: string;
  direction?: 'asc' | 'desc';
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  numberOfElements: number;
  empty: boolean;
}

// Search and filter types
export interface SearchRequest {
  query?: string;
  filters?: Record<string, any>;
  pagination?: PageRequest;
}

export interface SearchResponse<T> extends PageResponse<T> {
  query?: string;
  filters?: Record<string, any>;
}

// HTTP Methods
export type HttpMethod = 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH';

// API Request configuration
export interface ApiRequestConfig {
  method: HttpMethod;
  url: string;
  data?: any;
  params?: Record<string, any>;
  headers?: Record<string, string>;
  timeout?: number;
}

// Loading states
export interface LoadingState {
  isLoading: boolean;
  error: string | null;
  lastUpdated?: Date;
}

// Generic CRUD operations response types
export interface CreateResponse<T> {
  id: number;
  data: T;
  message: string;
}

export interface UpdateResponse<T> {
  data: T;
  message: string;
}

export interface DeleteResponse {
  success: boolean;
  message: string;
}

// Bulk operations
export interface BulkOperationRequest<T> {
  items: T[];
  operation: 'create' | 'update' | 'delete';
}

export interface BulkOperationResponse {
  successful: number;
  failed: number;
  errors: ApiError[];
  message: string;
}

