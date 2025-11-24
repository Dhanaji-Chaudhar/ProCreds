import api from './api'

/**
 * Test connection for a specific platform
 * @param {string} platform - Platform identifier (github, bitbucket, etc.)
 * @param {object} data - Configuration data to test
 * @returns {Promise<object>} Test result with success status and message
 */
export const testConnection = async (platform, data) => {
  try {
    const endpoint = getTestEndpoint(platform)
    const response = await api.post(endpoint, data)
    
    return {
      success: true,
      message: response.data.message || 'Connection successful!',
      details: response.data.details
    }
  } catch (error) {
    const errorMessage = error.response?.data?.message || error.message || 'Connection failed'
    const errorDetails = error.response?.data?.details || error.response?.data?.error
    
    throw {
      success: false,
      message: errorMessage,
      details: errorDetails
    }
  }
}

/**
 * Get the test connection endpoint for a platform
 * @param {string} platform - Platform identifier
 * @returns {string} API endpoint path
 */
const getTestEndpoint = (platform) => {
  const endpoints = {
    'github': '/github/test-connection',
    'bitbucket': '/bitbucket/test-connection',
    'gitlab': '/gitlab/test-connection',
    'jenkins': '/jenkins/test-connection',
    'jira': '/jira/test-connection',
    'sonarqube': '/sonarqube/test-connection',
    'kubernetes': '/kubernetes/test-connection',
    'aws-eks': '/aws-eks/test-connection',
    'azure-aks': '/azure-aks/test-connection',
    'gcp-gke': '/gcp-gke/test-connection'
  }
  
  return endpoints[platform] || `/${platform}/test-connection`
}

/**
 * Test multiple connections in batch
 * @param {Array} configurations - Array of {platform, data} objects
 * @returns {Promise<Array>} Array of test results
 */
export const testMultipleConnections = async (configurations) => {
  const results = await Promise.allSettled(
    configurations.map(config => testConnection(config.platform, config.data))
  )
  
  return results.map((result, index) => ({
    platform: configurations[index].platform,
    accountName: configurations[index].data.accountName,
    success: result.status === 'fulfilled',
    result: result.status === 'fulfilled' ? result.value : result.reason
  }))
}

export default {
  testConnection,
  testMultipleConnections
}

