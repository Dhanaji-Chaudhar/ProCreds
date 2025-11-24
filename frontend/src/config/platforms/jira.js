export const jiraFields = [
  {
    name: 'accountName',
    label: 'Account Name',
    type: 'text',
    placeholder: 'Enter Jira account name',
    validation: { 
      required: 'Account name is required',
      minLength: { value: 2, message: 'Account name must be at least 2 characters' }
    },
    description: 'Unique identifier for this Jira configuration',
    section: 'basicInformation'
  },
  {
    name: 'baseUrl',
    label: 'Jira URL',
    type: 'url',
    placeholder: 'https://yourcompany.atlassian.net',
    validation: { 
      required: 'Jira URL is required',
      pattern: {
        value: /^https?:\/\/.+/,
        message: 'Jira URL must be a valid HTTP or HTTPS URL'
      }
    },
    description: 'Base URL of your Jira instance',
    section: 'basicInformation'
  },
  {
    name: 'email',
    label: 'Email',
    type: 'email',
    placeholder: 'Enter your Jira email',
    validation: { 
      required: 'Email is required',
      pattern: {
        value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
        message: 'Please enter a valid email address'
      }
    },
    description: 'Your Jira account email address',
    section: 'credentials'
  },
  {
    name: 'apiToken',
    label: 'API Token',
    type: 'password',
    placeholder: 'Enter Jira API token',
    validation: { 
      required: 'API token is required',
      minLength: { value: 10, message: 'API token must be at least 10 characters' }
    },
    description: 'Jira API token (generated from account settings)',
    section: 'credentials'
  },
  {
    name: 'projectKey',
    label: 'Project Key',
    type: 'text',
    placeholder: 'Enter project key (optional)',
    validation: {
      pattern: { 
        value: /^[A-Z][A-Z0-9]*$/, 
        message: 'Project key must start with a letter and contain only uppercase letters and numbers' 
      }
    },
    description: 'Default Jira project key (e.g., "PROJ", "DEV")',
    section: 'optionalConfiguration'
  },
  {
    name: 'issueTypeDefault',
    label: 'Default Issue Type',
    type: 'text',
    placeholder: 'Task',
    validation: {
      minLength: { value: 2, message: 'Issue type must be at least 2 characters' }
    },
    description: 'Default issue type for creating tickets (e.g., "Task", "Bug", "Story")',
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

export const jiraConfig = {
  name: 'Jira',
  icon: 'Bug',
  color: 'blue-500',
  fields: jiraFields,
  testConnection: {
    endpoint: '/jira/test-connection',
    method: 'POST',
    requiredFields: ['baseUrl', 'email', 'apiToken']
  }
}

