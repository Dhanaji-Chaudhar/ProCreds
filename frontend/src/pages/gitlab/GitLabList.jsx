import React, { useState, useEffect } from 'react'
import { toast } from 'react-hot-toast'
import ConfigList from '../../components/ConfigList'
import { gitlabService } from '../../services/api'
import { testConnection } from '../../services/testConnection'

const GitLabList = () => {
  const [configurations, setConfigurations] = useState([])
  const [isLoading, setIsLoading] = useState(true)

  const columns = [
    { key: 'accountName', label: 'Account Name' },
    { key: 'groupId', label: 'Group ID' },
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
      const response = await gitlabService.getAll()
      setConfigurations(response.data.content || [])
    } catch (error) {
      toast.error('Failed to fetch GitLab configurations')
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
      await gitlabService.delete(id)
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
        apiUrl: config.apiUrl || 'https://gitlab.com/api/v4'
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
      title="GitLab Configurations"
      description="Manage your GitLab personal access tokens and project settings"
      data={configurations}
      columns={columns}
      onDelete={handleDelete}
      onTestConnection={handleTestConnection}
      platform="gitlab"
      createPath="/gitlab/create"
      editPath="/gitlab/edit/:id"
      isLoading={isLoading}
      searchPlaceholder="Search GitLab configurations..."
      showTestConnection={true}
    />
  )
}

export default GitLabList
