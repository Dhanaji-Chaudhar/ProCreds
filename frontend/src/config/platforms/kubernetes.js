export const kubernetesFields = [
  {
    name: 'accountName',
    label: 'Account Name',
    type: 'text',
    placeholder: 'Enter Kubernetes account name',
    validation: { 
      required: 'Account name is required',
      minLength: { value: 2, message: 'Account name must be at least 2 characters' }
    },
    description: 'Unique identifier for this Kubernetes configuration',
    section: 'basicInformation'
  },
  {
    name: 'clusterName',
    label: 'Cluster Name',
    type: 'text',
    placeholder: 'Enter cluster name',
    validation: { 
      required: 'Cluster name is required',
      pattern: { 
        value: /^[a-zA-Z0-9-_]+$/, 
        message: 'Cluster name can only contain letters, numbers, hyphens, and underscores' 
      }
    },
    description: 'Name of the Kubernetes cluster',
    section: 'basicInformation'
  },
  {
    name: 'kubeconfig',
    label: 'Kubeconfig',
    type: 'textarea',
    placeholder: 'Paste your kubeconfig content here',
    validation: { 
      required: 'Kubeconfig is required',
      minLength: { value: 100, message: 'Kubeconfig seems too short' }
    },
    description: 'Complete kubeconfig file content for cluster access',
    section: 'credentials'
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
    description: 'Default namespace for operations (optional)',
    section: 'optionalConfiguration'
  },
  {
    name: 'region',
    label: 'Region',
    type: 'text',
    placeholder: 'Enter region (optional)',
    validation: {
      pattern: { 
        value: /^[a-zA-Z0-9-_]+$/, 
        message: 'Region can only contain letters, numbers, hyphens, and underscores' 
      }
    },
    description: 'Cloud provider region where cluster is located',
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

export const kubernetesConfig = {
  name: 'Kubernetes',
  icon: 'Server',
  color: 'purple-600',
  fields: kubernetesFields,
  testConnection: {
    endpoint: '/kubernetes/test-connection',
    method: 'POST',
    requiredFields: ['clusterName', 'kubeconfig']
  }
}

