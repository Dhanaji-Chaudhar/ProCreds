export const githubFields = [
  {
    name: 'accountName',
    label: 'Account Name',
    type: 'text',
    placeholder: 'Enter GitHub account name',
    validation: { 
      required: 'Account name is required',
      minLength: { value: 2, message: 'Account name must be at least 2 characters' },
      pattern: { 
        value: /^[a-zA-Z0-9-_]+$/, 
        message: 'Account name can only contain letters, numbers, hyphens, and underscores' 
      }
    },
    description: 'Unique identifier for this GitHub configuration',
    section: 'basicInformation'
  },
  {
    name: 'accessToken',
    label: 'Personal Access Token',
    type: 'password',
    placeholder: 'ghp_xxxxxxxxxxxxxxxxxxxx',
    validation: { 
      required: 'Access token is required',
      pattern: {
        value: /^(ghp_|gho_|ghu_|ghs_|ghr_)[a-zA-Z0-9]{36,}$/,
        message: 'Invalid GitHub token format. Must start with ghp_, gho_, ghu_, ghs_, or ghr_'
      }
    },
    description: 'GitHub personal access token with appropriate permissions',
    section: 'credentials'
  },
  {
    name: 'organization',
    label: 'Organization',
    type: 'text',
    placeholder: 'Enter organization name (optional)',
    validation: {
      pattern: { 
        value: /^[a-zA-Z0-9-_]+$/, 
        message: 'Organization name can only contain letters, numbers, hyphens, and underscores' 
      }
    },
    description: 'GitHub organization name if applicable',
    section: 'optionalConfiguration'
  },
  {
    name: 'apiUrl',
    label: 'API URL',
    type: 'url',
    placeholder: 'https://api.github.com',
    validation: {
      pattern: {
        value: /^https?:\/\/.+/,
        message: 'API URL must be a valid HTTP or HTTPS URL'
      }
    },
    description: 'GitHub API URL (use default for GitHub.com, or custom for GitHub Enterprise)',
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

export const githubConfig = {
  name: 'GitHub',
  icon: 'Github',
  color: 'gray-900',
  fields: githubFields,
  testConnection: {
    endpoint: '/github/test-connection',
    method: 'POST',
    requiredFields: ['accessToken', 'apiUrl']
  }
}

