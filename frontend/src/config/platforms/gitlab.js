export const gitlabFields = [
  {
    name: 'accountName',
    label: 'Account Name',
    type: 'text',
    placeholder: 'Enter GitLab account name',
    validation: { 
      required: 'Account name is required',
      minLength: { value: 2, message: 'Account name must be at least 2 characters' }
    },
    description: 'Unique identifier for this GitLab configuration',
    section: 'basicInformation'
  },
  {
    name: 'personalAccessToken',
    label: 'Personal Access Token',
    type: 'password',
    placeholder: 'glpat-xxxxxxxxxxxxxxxxxxxx',
    validation: { 
      required: 'Personal access token is required',
      pattern: {
        value: /^glpat-[a-zA-Z0-9_-]{20,}$/,
        message: 'Invalid GitLab token format. Must start with glpat-'
      }
    },
    description: 'GitLab personal access token with API permissions',
    section: 'credentials'
  },
  {
    name: 'groupId',
    label: 'Group ID',
    type: 'number',
    placeholder: 'Enter group ID (optional)',
    validation: {
      min: { value: 1, message: 'Group ID must be a positive number' }
    },
    description: 'GitLab group ID if working with a specific group',
    section: 'optionalConfiguration'
  },
  {
    name: 'apiUrl',
    label: 'API URL',
    type: 'url',
    placeholder: 'https://gitlab.com/api/v4',
    validation: {
      pattern: {
        value: /^https?:\/\/.+/,
        message: 'API URL must be a valid HTTP or HTTPS URL'
      }
    },
    description: 'GitLab API URL (use default for GitLab.com, or custom for self-hosted)',
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

export const gitlabConfig = {
  name: 'GitLab',
  icon: 'Gitlab',
  color: 'orange-600',
  fields: gitlabFields,
  testConnection: {
    endpoint: '/gitlab/test-connection',
    method: 'POST',
    requiredFields: ['personalAccessToken', 'apiUrl']
  }
}

