export const gcpGkeFields = [
  {
    name: 'accountName',
    label: 'Account Name',
    type: 'text',
    placeholder: 'Enter GCP GKE account name',
    validation: { 
      required: 'Account name is required',
      minLength: { value: 2, message: 'Account name must be at least 2 characters' }
    },
    description: 'Unique identifier for this GCP GKE configuration',
    section: 'basicInformation'
  },
  {
    name: 'clusterName',
    label: 'GKE Cluster Name',
    type: 'text',
    placeholder: 'Enter GKE cluster name',
    validation: { 
      required: 'Cluster name is required',
      pattern: { 
        value: /^[a-z]([a-z0-9-]*[a-z0-9])?$/, 
        message: 'Cluster name must start with lowercase letter and contain only lowercase letters, numbers, and hyphens' 
      }
    },
    description: 'Name of the GCP GKE cluster',
    section: 'basicInformation'
  },
  {
    name: 'projectId',
    label: 'Project ID',
    type: 'text',
    placeholder: 'Enter GCP project ID',
    validation: { 
      required: 'Project ID is required',
      pattern: { 
        value: /^[a-z]([a-z0-9-]*[a-z0-9])?$/, 
        message: 'Project ID must start with lowercase letter and contain only lowercase letters, numbers, and hyphens' 
      }
    },
    description: 'GCP project ID containing the GKE cluster',
    section: 'basicInformation'
  },
  {
    name: 'zone',
    label: 'Zone/Region',
    type: 'text',
    placeholder: 'us-central1-a',
    validation: { 
      required: 'Zone or region is required',
      pattern: { 
        value: /^[a-z]+-[a-z]+\d+(-[a-z])?$/, 
        message: 'Invalid GCP zone/region format (e.g., us-central1-a or us-central1)' 
      }
    },
    description: 'GCP zone (for zonal clusters) or region (for regional clusters)',
    section: 'basicInformation'
  },
  {
    name: 'serviceAccountJson',
    label: 'Service Account JSON',
    type: 'textarea',
    placeholder: 'Paste your service account JSON key here',
    validation: { 
      required: 'Service account JSON is required',
      validate: {
        isValidJson: (value) => {
          try {
            const parsed = JSON.parse(value);
            if (!parsed.type || parsed.type !== 'service_account') {
              return 'Invalid service account JSON format';
            }
            if (!parsed.project_id || !parsed.private_key || !parsed.client_email) {
              return 'Service account JSON is missing required fields';
            }
            return true;
          } catch {
            return 'Invalid JSON format';
          }
        }
      }
    },
    description: 'Complete service account JSON key with GKE permissions',
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

export const gcpGkeConfig = {
  name: 'GCP GKE',
  icon: 'Cloud',
  color: 'red-500',
  fields: gcpGkeFields,
  testConnection: {
    endpoint: '/gcp-gke/test-connection',
    method: 'POST',
    requiredFields: ['clusterName', 'projectId', 'zone', 'serviceAccountJson']
  }
}

