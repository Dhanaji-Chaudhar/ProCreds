export const azureAksFields = [
  {
    name: 'accountName',
    label: 'Account Name',
    type: 'text',
    placeholder: 'Enter Azure AKS account name',
    validation: { 
      required: 'Account name is required',
      minLength: { value: 2, message: 'Account name must be at least 2 characters' }
    },
    description: 'Unique identifier for this Azure AKS configuration',
    section: 'basicInformation'
  },
  {
    name: 'clusterName',
    label: 'AKS Cluster Name',
    type: 'text',
    placeholder: 'Enter AKS cluster name',
    validation: { 
      required: 'Cluster name is required',
      pattern: { 
        value: /^[a-zA-Z0-9-_]+$/, 
        message: 'Cluster name can only contain letters, numbers, hyphens, and underscores' 
      }
    },
    description: 'Name of the Azure AKS cluster',
    section: 'basicInformation'
  },
  {
    name: 'resourceGroup',
    label: 'Resource Group',
    type: 'text',
    placeholder: 'Enter resource group name',
    validation: { 
      required: 'Resource group is required',
      pattern: { 
        value: /^[a-zA-Z0-9-_().]+$/, 
        message: 'Resource group name contains invalid characters' 
      }
    },
    description: 'Azure resource group containing the AKS cluster',
    section: 'basicInformation'
  },
  {
    name: 'subscriptionId',
    label: 'Subscription ID',
    type: 'text',
    placeholder: 'xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx',
    validation: { 
      required: 'Subscription ID is required',
      pattern: {
        value: /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i,
        message: 'Invalid Azure subscription ID format'
      }
    },
    description: 'Azure subscription ID',
    section: 'basicInformation'
  },
  {
    name: 'tenantId',
    label: 'Tenant ID',
    type: 'text',
    placeholder: 'xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx',
    validation: { 
      required: 'Tenant ID is required',
      pattern: {
        value: /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i,
        message: 'Invalid Azure tenant ID format'
      }
    },
    description: 'Azure Active Directory tenant ID',
    section: 'credentials'
  },
  {
    name: 'clientId',
    label: 'Client ID',
    type: 'text',
    placeholder: 'xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx',
    validation: { 
      required: 'Client ID is required',
      pattern: {
        value: /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i,
        message: 'Invalid Azure client ID format'
      }
    },
    description: 'Azure service principal client ID',
    section: 'credentials'
  },
  {
    name: 'clientSecret',
    label: 'Client Secret',
    type: 'password',
    placeholder: 'Enter Azure client secret',
    validation: { 
      required: 'Client secret is required',
      minLength: { value: 10, message: 'Client secret must be at least 10 characters' }
    },
    description: 'Azure service principal client secret',
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

export const azureAksConfig = {
  name: 'Azure AKS',
  icon: 'Cloud',
  color: 'blue-700',
  fields: azureAksFields,
  testConnection: {
    endpoint: '/azure-aks/test-connection',
    method: 'POST',
    requiredFields: ['clusterName', 'resourceGroup', 'subscriptionId', 'tenantId', 'clientId', 'clientSecret']
  }
}

