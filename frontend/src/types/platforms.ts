// Base interface for all platform configurations
export interface BasePlatformConfig {
  id?: number;
  accountName: string;
  description?: string;
  createdAt?: string;
  updatedAt?: string;
}

// GitHub Configuration
export interface GitHubConfig extends BasePlatformConfig {
  accessToken: string;
  organization?: string;
  apiUrl?: string;
}

export interface GitHubConfigDTO extends GitHubConfig {}

// Bitbucket Configuration
export interface BitbucketConfig extends BasePlatformConfig {
  username: string;
  appPassword: string;
  workspace?: string;
  apiUrl?: string;
}

export interface BitbucketConfigDTO extends BitbucketConfig {}

// GitLab Configuration
export interface GitLabConfig extends BasePlatformConfig {
  personalAccessToken: string;
  groupId?: string;
  apiUrl?: string;
}

export interface GitLabConfigDTO extends GitLabConfig {}

// Jenkins Configuration
export interface JenkinsConfig extends BasePlatformConfig {
  baseUrl: string;
  username: string;
  apiToken: string;
  jobPrefix?: string;
}

export interface JenkinsConfigDTO extends JenkinsConfig {}

// Jira Configuration
export interface JiraConfig extends BasePlatformConfig {
  baseUrl: string;
  email: string;
  apiToken: string;
  projectKey?: string;
  issueTypeDefault?: string;
}

export interface JiraConfigDTO extends JiraConfig {}

// SonarQube Configuration
export interface SonarQubeConfig extends BasePlatformConfig {
  baseUrl: string;
  token: string;
  organization?: string;
}

export interface SonarQubeConfigDTO extends SonarQubeConfig {}

// Kubernetes Configuration
export interface KubernetesConfig extends BasePlatformConfig {
  clusterName: string;
  kubeconfig: string;
  namespace?: string;
  region?: string;
}

export interface KubernetesConfigDTO extends KubernetesConfig {}

// AWS EKS Configuration
export interface AwsEksConfig extends BasePlatformConfig {
  clusterName: string;
  accessKey: string;
  secretKey: string;
  region: string;
}

export interface AwsEksConfigDTO extends AwsEksConfig {}

// Azure AKS Configuration
export interface AzureAksConfig extends BasePlatformConfig {
  clusterName: string;
  tenantId: string;
  clientId: string;
  clientSecret: string;
}

export interface AzureAksConfigDTO extends AzureAksConfig {}

// GCP GKE Configuration
export interface GcpGkeConfig extends BasePlatformConfig {
  clusterName: string;
  serviceAccountJson: string;
}

export interface GcpGkeConfigDTO extends GcpGkeConfig {}

// Union types for all platform configurations
export type PlatformConfig = 
  | GitHubConfig 
  | BitbucketConfig 
  | GitLabConfig 
  | JenkinsConfig 
  | JiraConfig 
  | SonarQubeConfig 
  | KubernetesConfig 
  | AwsEksConfig 
  | AzureAksConfig 
  | GcpGkeConfig;

export type PlatformConfigDTO = 
  | GitHubConfigDTO 
  | BitbucketConfigDTO 
  | GitLabConfigDTO 
  | JenkinsConfigDTO 
  | JiraConfigDTO 
  | SonarQubeConfigDTO 
  | KubernetesConfigDTO 
  | AwsEksConfigDTO 
  | AzureAksConfigDTO 
  | GcpGkeConfigDTO;

// Platform types
export type PlatformType = 
  | 'github' 
  | 'bitbucket' 
  | 'gitlab' 
  | 'jenkins' 
  | 'jira' 
  | 'sonarqube' 
  | 'kubernetes' 
  | 'aws-eks' 
  | 'azure-aks' 
  | 'gcp-gke';

// Platform display information
export interface PlatformInfo {
  id: PlatformType;
  name: string;
  description: string;
  icon: string;
  category: 'scm' | 'cicd' | 'kubernetes';
}

// Form validation types
export interface ValidationError {
  field: string;
  message: string;
}

export interface FormState<T> {
  data: T;
  errors: ValidationError[];
  isSubmitting: boolean;
  isValid: boolean;
}

// Test connection response
export interface TestConnectionResponse {
  success: boolean;
  message: string;
  details?: string;
}

