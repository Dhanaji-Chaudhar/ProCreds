import React, { useState, useEffect } from 'react'
import { toast } from 'react-hot-toast'
import ConfigList from '../../components/ConfigList'
import { githubService } from '../../services/api'
import { testConnection } from '../../services/testConnection'

const GitHubList = () => {
  const [configurations, setConfigurations] = useState([])
  const [isLoading, setIsLoading] = useState(true)

  const columns = [
    { key: 'accountName', label: 'Account Name' },
    { key: 'organization', label: 'Organization' },
    { key: 'apiUrl', label: 'API URL', type: 'truncate' },
    { key: 'description', label: 'Description', type: 'truncate' },
    { key: 'createdAt', label: 'Created', type: 'date' },
  ]

  useEffect(() => {
    fetchConfigurations()
  }, [])

  const fetchConfigurations = async () => {
    try {
      setIsLoading(true)
      const response = await githubService.getAll()
      setConfigurations(response.data.content || [])
    } catch (error) {
      toast.error('Failed to fetch GitHub configurations')
      console.error('Error fetching configurations:', error)
    } finally {
      setIsLoading(false)
    }
  }

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this configuration?')) {
      return
    }

    try {
      await githubService.delete(id)
      toast.success('Configuration deleted successfully')
      fetchConfigurations()
    } catch (error) {
      toast.error('Failed to delete configuration')
      console.error('Error deleting configuration:', error)
    }
  }

  const handleTestConnection = async (platform, config) => {
    try {
      const testData = {
        ...config,
        apiUrl: config.apiUrl || 'https://api.github.com'
      }
      
      const result = await testConnection(platform, testData)
      toast.success(`Connection test successful for ${config.accountName}`)
      return result
    } catch (error) {
      toast.error(`Connection test failed for ${config.accountName}: ${error.message}`)
      throw error
    }
  }

  return (
    <ConfigList
      title="GitHub Configurations"
      description="Manage your GitHub repository access tokens and settings"
      data={configurations}
      columns={columns}
      onDelete={handleDelete}
      onTestConnection={handleTestConnection}
      platform="github"
      createPath="/github/create"
      editPath="/github/edit/:id"
      isLoading={isLoading}
      searchPlaceholder="Search GitHub configurations..."
      showTestConnection={true}
    />
  )
}

export default GitHubList
