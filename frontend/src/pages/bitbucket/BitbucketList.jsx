import React, { useState, useEffect } from 'react'
import { toast } from 'react-hot-toast'
import ConfigList from '../../components/ConfigList'
import { bitbucketService } from '../../services/api'
import { testConnection } from '../../services/testConnection'

const BitbucketList = () => {
  const [configurations, setConfigurations] = useState([])
  const [isLoading, setIsLoading] = useState(true)

  const columns = [
    { key: 'username', label: 'Username' },
    { key: 'workspace', label: 'Workspace' },
    { key: 'apiUrl', label: 'API URL', type: 'truncate' },
    { key: 'createdAt', label: 'Created', type: 'date' },
  ]

  useEffect(() => {
    fetchConfigurations()
  }, [])

  const fetchConfigurations = async () => {
    try {
      setIsLoading(true)
      const response = await bitbucketService.getAll()
      setConfigurations(response.data.content || [])
    } catch (error) {
      toast.error('Failed to fetch Bitbucket configurations')
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
      await bitbucketService.delete(id)
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
        apiUrl: config.apiUrl || 'https://api.bitbucket.org/2.0'
      }
      
      const result = await testConnection(platform, testData)
      toast.success(`Connection test successful for ${config.username}`)
      return result
    } catch (error) {
      toast.error(`Connection test failed for ${config.username}: ${error.message}`)
      throw error
    }
  }

  return (
    <ConfigList
      title="Bitbucket Configurations"
      description="Manage your Bitbucket workspace credentials and settings"
      data={configurations}
      columns={columns}
      onDelete={handleDelete}
      onTestConnection={handleTestConnection}
      platform="bitbucket"
      createPath="/bitbucket/create"
      editPath="/bitbucket/edit/:id"
      isLoading={isLoading}
      searchPlaceholder="Search Bitbucket configurations..."
      showTestConnection={true}
    />
  )
}

export default BitbucketList
