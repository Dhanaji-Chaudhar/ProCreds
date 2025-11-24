export const sonarqubeFields = [
  {
    name: 'accountName',
    label: 'Account Name',
    type: 'text',
    placeholder: 'Enter SonarQube account name',
    validation: { 
      required: 'Account name is required',
      minLength: { value: 2, message: 'Account name must be at least 2 characters' }
    },
    description: 'Unique identifier for this SonarQube configuration',
    section: 'basicInformation'
  },
  {
    name: 'baseUrl',
    label: 'SonarQube URL',
    type: 'url',
    placeholder: 'https://sonarcloud.io',
    validation: { 
      required: 'SonarQube URL is required',
      pattern: {
        value: /^https?:\/\/.+/,
        message: 'SonarQube URL must be a valid HTTP or HTTPS URL'
      }
    },
    description: 'Base URL of your SonarQube instance',
    section: 'basicInformation'
  },
  {
    name: 'token',
    label: 'Authentication Token',
    type: 'password',
    placeholder: 'Enter SonarQube token',
    validation: { 
      required: 'Authentication token is required',
      minLength: { value: 20, message: 'Token must be at least 20 characters' }
    },
    description: 'SonarQube user token or project token',
    section: 'credentials'
  },
  {
    name: 'organization',
    label: 'Organization',
    type: 'text',
    placeholder: 'Enter organization key (optional)',
    validation: {
      pattern: { 
        value: /^[a-zA-Z0-9-_]+$/, 
        message: 'Organization key can only contain letters, numbers, hyphens, and underscores' 
      }
    },
    description: 'SonarQube organization key (required for SonarCloud)',
    section: 'optionalConfiguration'
  },
  {
    name: 'projectKey',
    label: 'Default Project Key',
    type: 'text',
    placeholder: 'Enter project key (optional)',
    validation: {
      pattern: { 
        value: /^[a-zA-Z0-9-_.:]+$/, 
        message: 'Project key can contain letters, numbers, hyphens, underscores, dots, and colons' 
      }
    },
    description: 'Default project key for analysis',
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

export const sonarqubeConfig = {
  name: 'SonarQube',
  icon: 'Shield',
  color: 'green-600',
  fields: sonarqubeFields,
  testConnection: {
    endpoint: '/sonarqube/test-connection',
    method: 'POST',
    requiredFields: ['baseUrl', 'token']
  }
}

