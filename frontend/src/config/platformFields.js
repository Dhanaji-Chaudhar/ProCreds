import { githubConfig } from './platforms/github'
import { bitbucketConfig } from './platforms/bitbucket'
import { gitlabConfig } from './platforms/gitlab'
import { jenkinsConfig } from './platforms/jenkins'
import { jiraConfig } from './platforms/jira'
import { sonarqubeConfig } from './platforms/sonarqube'
import { kubernetesConfig } from './platforms/kubernetes'
import { awsEksConfig } from './platforms/awsEks'
import { azureAksConfig } from './platforms/azureAks'
import { gcpGkeConfig } from './platforms/gcpGke'

export const platformConfigs = {
  github: githubConfig,
  bitbucket: bitbucketConfig,
  gitlab: gitlabConfig,
  jenkins: jenkinsConfig,
  jira: jiraConfig,
  sonarqube: sonarqubeConfig,
  kubernetes: kubernetesConfig,
  'aws-eks': awsEksConfig,
  'azure-aks': azureAksConfig,
  'gcp-gke': gcpGkeConfig
}

export const getPlatformConfig = (platformKey) => {
  return platformConfigs[platformKey] || null
}

export const getAllPlatforms = () => {
  return Object.keys(platformConfigs).map(key => ({
    key,
    ...platformConfigs[key]
  }))
}

export const getPlatformFields = (platformKey) => {
  const config = getPlatformConfig(platformKey)
  return config ? config.fields : []
}

export const getFieldsBySection = (platformKey) => {
  const fields = getPlatformFields(platformKey)
  
  return {
    basicInformation: fields.filter(field => field.section === 'basicInformation'),
    credentials: fields.filter(field => field.section === 'credentials'),
    optionalConfiguration: fields.filter(field => field.section === 'optionalConfiguration')
  }
}

export const validateRequiredFields = (platformKey, data) => {
  const fields = getPlatformFields(platformKey)
  const errors = {}
  
  fields.forEach(field => {
    if (field.validation?.required && (!data[field.name] || data[field.name].trim() === '')) {
      errors[field.name] = typeof field.validation.required === 'string' 
        ? field.validation.required 
        : `${field.label} is required`
    }
  })
  
  return errors
}

