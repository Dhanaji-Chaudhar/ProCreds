export const jenkinsFields = [
  {
    name: 'accountName',
    label: 'Account Name',
    type: 'text',
    placeholder: 'Enter Jenkins account name',
    validation: { 
      required: 'Account name is required',
      minLength: { value: 2, message: 'Account name must be at least 2 characters' }
    },
    description: 'Unique identifier for this Jenkins configuration',
    section: 'basicInformation'
  },
  {
    name: 'baseUrl',
    label: 'Jenkins URL',
    type: 'url',
    placeholder: 'https://jenkins.example.com',
    validation: { 
      required: 'Jenkins URL is required',
      pattern: {
        value: /^https?:\/\/.+/,
        message: 'Jenkins URL must be a valid HTTP or HTTPS URL'
      }
    },
    description: 'Base URL of your Jenkins instance',
    section: 'basicInformation'
  },
  {
    name: 'username',
    label: 'Username',
    type: 'text',
    placeholder: 'Enter Jenkins username',
    validation: { 
      required: 'Username is required',
      minLength: { value: 2, message: 'Username must be at least 2 characters' }
    },
    description: 'Your Jenkins username',
    section: 'credentials'
  },
  {
    name: 'apiToken',
    label: 'API Token',
    type: 'password',
    placeholder: 'Enter Jenkins API token',
    validation: { 
      required: 'API token is required',
      minLength: { value: 10, message: 'API token must be at least 10 characters' }
    },
    description: 'Jenkins API token (generated from user settings)',
    section: 'credentials'
  },
  {
    name: 'jobPrefix',
    label: 'Job Prefix',
    type: 'text',
    placeholder: 'Enter job prefix (optional)',
    validation: {
      pattern: { 
        value: /^[a-zA-Z0-9-_/]+$/, 
        message: 'Job prefix can only contain letters, numbers, hyphens, underscores, and slashes' 
      }
    },
    description: 'Prefix for Jenkins job names (e.g., "team1/" or "project-")',
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

export const jenkinsConfig = {
  name: 'Jenkins',
  icon: 'Wrench',
  color: 'red-600',
  fields: jenkinsFields,
  testConnection: {
    endpoint: '/jenkins/test-connection',
    method: 'POST',
    requiredFields: ['baseUrl', 'username', 'apiToken']
  }
}

