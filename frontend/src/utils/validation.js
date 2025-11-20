// Validation utility functions

export const validateRequired = (value, fieldName) => {
  if (!value || value.trim() === '') {
    return `${fieldName} is required`;
  }
  return null;
};

export const validateMinLength = (value, minLength, fieldName) => {
  if (value && value.length < minLength) {
    return `${fieldName} must be at least ${minLength} characters`;
  }
  return null;
};

export const validateMaxLength = (value, maxLength, fieldName) => {
  if (value && value.length > maxLength) {
    return `${fieldName} must not exceed ${maxLength} characters`;
  }
  return null;
};

export const validateEmail = (email) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (email && !emailRegex.test(email)) {
    return 'Please enter a valid email address';
  }
  return null;
};

export const validateUrl = (url, fieldName = 'URL') => {
  const urlRegex = /^https?:\/\/.+/;
  if (url && !urlRegex.test(url)) {
    return `${fieldName} must be a valid HTTP/HTTPS URL`;
  }
  return null;
};

export const validateAccountName = (accountName) => {
  const errors = [];
  
  const requiredError = validateRequired(accountName, 'Account name');
  if (requiredError) errors.push(requiredError);
  
  const minLengthError = validateMinLength(accountName, 2, 'Account name');
  if (minLengthError) errors.push(minLengthError);
  
  const maxLengthError = validateMaxLength(accountName, 50, 'Account name');
  if (maxLengthError) errors.push(maxLengthError);
  
  return errors.length > 0 ? errors[0] : null;
};

export const validateAccessToken = (token) => {
  const errors = [];
  
  const requiredError = validateRequired(token, 'Access token');
  if (requiredError) errors.push(requiredError);
  
  const minLengthError = validateMinLength(token, 10, 'Access token');
  if (minLengthError) errors.push(minLengthError);
  
  return errors.length > 0 ? errors[0] : null;
};

export const validateClusterName = (clusterName) => {
  const errors = [];
  
  const requiredError = validateRequired(clusterName, 'Cluster name');
  if (requiredError) errors.push(requiredError);
  
  const minLengthError = validateMinLength(clusterName, 2, 'Cluster name');
  if (minLengthError) errors.push(minLengthError);
  
  // Kubernetes cluster name validation
  const k8sNameRegex = /^[a-z0-9]([-a-z0-9]*[a-z0-9])?$/;
  if (clusterName && !k8sNameRegex.test(clusterName)) {
    errors.push('Cluster name must contain only lowercase letters, numbers, and hyphens');
  }
  
  return errors.length > 0 ? errors[0] : null;
};

// Platform-specific validation rules
export const platformValidationRules = {
  github: {
    accountName: { required: true, minLength: 2, maxLength: 50 },
    accessToken: { required: true, minLength: 10 },
    organization: { maxLength: 50 },
    apiUrl: { url: true },
    description: { maxLength: 255 },
  },
  bitbucket: {
    username: { required: true, minLength: 2, maxLength: 50 },
    appPassword: { required: true, minLength: 10 },
    workspace: { maxLength: 50 },
    apiUrl: { url: true },
    description: { maxLength: 255 },
  },
  gitlab: {
    accountName: { required: true, minLength: 2, maxLength: 50 },
    personalAccessToken: { required: true, minLength: 10 },
    groupId: { maxLength: 50 },
    apiUrl: { url: true },
    description: { maxLength: 255 },
  },
  jenkins: {
    accountName: { required: true, minLength: 2, maxLength: 50 },
    baseUrl: { required: true, url: true },
    username: { required: true, minLength: 2, maxLength: 50 },
    apiToken: { required: true, minLength: 10 },
    jobPrefix: { maxLength: 50 },
    description: { maxLength: 255 },
  },
  jira: {
    accountName: { required: true, minLength: 2, maxLength: 50 },
    baseUrl: { required: true, url: true },
    email: { required: true, email: true },
    apiToken: { required: true, minLength: 10 },
    projectKey: { maxLength: 20 },
    issueTypeDefault: { maxLength: 50 },
    description: { maxLength: 255 },
  },
  sonarqube: {
    accountName: { required: true, minLength: 2, maxLength: 50 },
    baseUrl: { required: true, url: true },
    token: { required: true, minLength: 10 },
    organization: { maxLength: 50 },
    description: { maxLength: 255 },
  },
  kubernetes: {
    clusterName: { required: true, minLength: 2, maxLength: 50 },
    kubeconfig: { required: true, minLength: 10 },
    namespace: { maxLength: 50 },
    region: { maxLength: 50 },
    description: { maxLength: 255 },
  },
  awsEks: {
    clusterName: { required: true, minLength: 2, maxLength: 50 },
    accessKey: { required: true, minLength: 10 },
    secretKey: { required: true, minLength: 10 },
    region: { required: true, minLength: 2, maxLength: 20 },
    namespace: { maxLength: 50 },
    roleArn: { maxLength: 255 },
    description: { maxLength: 255 },
  },
  azureAks: {
    clusterName: { required: true, minLength: 2, maxLength: 50 },
    tenantId: { required: true, minLength: 10 },
    clientId: { required: true, minLength: 10 },
    clientSecret: { required: true, minLength: 10 },
    subscriptionId: { maxLength: 50 },
    resourceGroup: { maxLength: 50 },
    namespace: { maxLength: 50 },
    description: { maxLength: 255 },
  },
  gcpGke: {
    clusterName: { required: true, minLength: 2, maxLength: 50 },
    serviceAccountJson: { required: true, minLength: 50 },
    projectId: { maxLength: 50 },
    zone: { maxLength: 50 },
    region: { maxLength: 50 },
    namespace: { maxLength: 50 },
    description: { maxLength: 255 },
  },
};

export const validateField = (value, fieldName, rules) => {
  const errors = [];
  
  if (rules.required && (!value || value.trim() === '')) {
    errors.push(`${fieldName} is required`);
    return errors[0]; // Return early for required validation
  }
  
  if (value) {
    if (rules.minLength && value.length < rules.minLength) {
      errors.push(`${fieldName} must be at least ${rules.minLength} characters`);
    }
    
    if (rules.maxLength && value.length > rules.maxLength) {
      errors.push(`${fieldName} must not exceed ${rules.maxLength} characters`);
    }
    
    if (rules.email && !validateEmail(value)) {
      errors.push('Please enter a valid email address');
    }
    
    if (rules.url && !validateUrl(value)) {
      errors.push(`${fieldName} must be a valid HTTP/HTTPS URL`);
    }
  }
  
  return errors.length > 0 ? errors[0] : null;
};

