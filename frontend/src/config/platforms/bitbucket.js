export const bitbucketFields = [
  {
    name: 'accountName',
    label: 'Account Name',
    type: 'text',
    placeholder: 'Enter Bitbucket account name',
    validation: { 
      required: 'Account name is required',
      minLength: { value: 2, message: 'Account name must be at least 2 characters' }
    },
    description: 'Unique identifier for this Bitbucket configuration',
    section: 'basicInformation'
  },
  {
    name: 'username',
    label: 'Username',
    type: 'text',
    placeholder: 'Enter Bitbucket username',
    validation: { 
      required: 'Username is required',
      pattern: { 
        value: /^[a-zA-Z0-9-_]+$/, 
        message: 'Username can only contain letters, numbers, hyphens, and underscores' 
      }
    },
    description: 'Your Bitbucket username',
    section: 'credentials'
  },
  {
    name: 'appPassword',
    label: 'App Password',
    type: 'password',
    placeholder: 'Enter Bitbucket app password',
    validation: { 
      required: 'App password is required',
      minLength: { value: 8, message: 'App password must be at least 8 characters' }
    },
    description: 'Bitbucket app password (not your account password)',
    section: 'credentials'
  },
  {
    name: 'workspace',
    label: 'Workspace',
    type: 'text',
    placeholder: 'Enter workspace name (optional)',
    validation: {
      pattern: { 
        value: /^[a-zA-Z0-9-_]+$/, 
        message: 'Workspace name can only contain letters, numbers, hyphens, and underscores' 
      }
    },
    description: 'Bitbucket workspace name if applicable',
    section: 'optionalConfiguration'
  },
  {
    name: 'apiUrl',
    label: 'API URL',
    type: 'url',
    placeholder: 'https://api.bitbucket.org/2.0',
    validation: {
      pattern: {
        value: /^https?:\/\/.+/,
        message: 'API URL must be a valid HTTP or HTTPS URL'
      }
    },
    description: 'Bitbucket API URL (use default for Bitbucket Cloud)',
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

export const bitbucketConfig = {
  name: 'Bitbucket',
  icon: 'GitBranch',
  color: 'blue-600',
  fields: bitbucketFields,
  testConnection: {
    endpoint: '/bitbucket/test-connection',
    method: 'POST',
    requiredFields: ['username', 'appPassword', 'apiUrl']
  }
}

