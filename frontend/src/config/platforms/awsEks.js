export const awsEksFields = [
  {
    name: 'accountName',
    label: 'Account Name',
    type: 'text',
    placeholder: 'Enter AWS EKS account name',
    validation: { 
      required: 'Account name is required',
      minLength: { value: 2, message: 'Account name must be at least 2 characters' }
    },
    description: 'Unique identifier for this AWS EKS configuration',
    section: 'basicInformation'
  },
  {
    name: 'clusterName',
    label: 'EKS Cluster Name',
    type: 'text',
    placeholder: 'Enter EKS cluster name',
    validation: { 
      required: 'Cluster name is required',
      pattern: { 
        value: /^[a-zA-Z0-9-_]+$/, 
        message: 'Cluster name can only contain letters, numbers, hyphens, and underscores' 
      }
    },
    description: 'Name of the AWS EKS cluster',
    section: 'basicInformation'
  },
  {
    name: 'region',
    label: 'AWS Region',
    type: 'text',
    placeholder: 'us-west-2',
    validation: { 
      required: 'AWS region is required',
      pattern: { 
        value: /^[a-z]{2}-[a-z]+-\d+$/, 
        message: 'Invalid AWS region format (e.g., us-west-2)' 
      }
    },
    description: 'AWS region where the EKS cluster is located',
    section: 'basicInformation'
  },
  {
    name: 'accessKey',
    label: 'AWS Access Key ID',
    type: 'text',
    placeholder: 'AKIA...',
    validation: { 
      required: 'Access Key ID is required',
      pattern: {
        value: /^AKIA[0-9A-Z]{16}$/,
        message: 'Invalid AWS Access Key ID format'
      }
    },
    description: 'AWS Access Key ID with EKS permissions',
    section: 'credentials'
  },
  {
    name: 'secretKey',
    label: 'AWS Secret Access Key',
    type: 'password',
    placeholder: 'Enter AWS secret access key',
    validation: { 
      required: 'Secret Access Key is required',
      minLength: { value: 40, message: 'Secret Access Key must be at least 40 characters' }
    },
    description: 'AWS Secret Access Key',
    section: 'credentials'
  },
  {
    name: 'sessionToken',
    label: 'Session Token',
    type: 'password',
    placeholder: 'Enter session token (optional)',
    validation: {
      minLength: { value: 10, message: 'Session token must be at least 10 characters if provided' }
    },
    description: 'AWS session token (for temporary credentials)',
    section: 'credentials'
  },
  {
    name: 'roleArn',
    label: 'IAM Role ARN',
    type: 'text',
    placeholder: 'arn:aws:iam::123456789012:role/EKSRole',
    validation: {
      pattern: {
        value: /^arn:aws:iam::\d{12}:role\/[a-zA-Z0-9+=,.@_-]+$/,
        message: 'Invalid IAM Role ARN format'
      }
    },
    description: 'IAM role ARN to assume for EKS access (optional)',
    section: 'optionalConfiguration'
  },
  {
    name: 'namespace',
    label: 'Default Namespace',
    type: 'text',
    placeholder: 'default',
    validation: {
      pattern: { 
        value: /^[a-z0-9-]+$/, 
        message: 'Namespace must contain only lowercase letters, numbers, and hyphens' 
      }
    },
    description: 'Default Kubernetes namespace for operations',
    section: 'optionalConfiguration'
  },
  {
    name: 'description',
    label: 'Description',
    type: 'textarea',
    placeholder: 'Enter description for this configuration',
    validation: {
      maxLength: { value: 500, message: 'Description cannot exceed 500 characters' }
    },
    description: 'Optional description to help identify this configuration',
    section: 'optionalConfiguration'
  }
]

export const awsEksConfig = {
  name: 'AWS EKS',
  icon: 'Cloud',
  color: 'yellow-600',
  fields: awsEksFields,
  testConnection: {
    endpoint: '/aws-eks/test-connection',
    method: 'POST',
    requiredFields: ['clusterName', 'region', 'accessKey', 'secretKey']
  }
}

