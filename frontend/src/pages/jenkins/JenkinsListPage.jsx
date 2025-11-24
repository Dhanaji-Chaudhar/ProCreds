import React, { useState, useEffect } from 'react'
import { toast } from 'react-hot-toast'
import ConfigList from '../../components/ConfigList'
import { jenkinsService } from '../../services/api'
import { testConnection } from '../../services/testConnection'

const JenkinsListPage = () => {
  const [configurations, setConfigurations] = useState([])
  const [isLoading, setIsLoading] = useState(true)

  const columns = [
    { key: 'accountName', label: 'Account Name' },
    { key: 'username', label: 'Username' },
    { key: 'baseUrl', label: 'Base URL', type: 'truncate' },
    { key: 'jobPrefix', label: 'Job Prefix' },
    { key: 'description', label: 'Description', type: 'truncate' },
    { key: 'createdAt', label: 'Created', type: 'date' },
  ]

  useEffect(() => {
    fetchConfigurations()
  }, [])

  const fetchConfigurations = async () => {
    try {
      setIsLoading(true)
      const response = await jenkinsService.getAll()
      setConfigurations(response.data.content || [])
    } catch (error) {
      toast.error('Failed to fetch Jenkins configurations')
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
      await jenkinsService.delete(id)
      toast.success('Configuration deleted successfully')
      fetchConfigurations()
    } catch (error) {
      toast.error('Failed to delete configuration')
      console.error('Error deleting configuration:', error)
    }
  }

  const handleTestConnection = async (platform, config) => {
    try {
      const result = await testConnection(platform, config)
      toast.success(`Connection test successful for ${config.accountName}`)
      return result
    } catch (error) {
      toast.error(`Connection test failed for ${config.accountName}: ${error.message}`)
      throw error
    }
  }

  return (
    <ConfigList
      title="Jenkins Configurations"
      description="Manage your Jenkins server connections and API tokens"
      data={configurations}
      columns={columns}
      onDelete={handleDelete}
      onTestConnection={handleTestConnection}
      platform="jenkins"
      createPath="/jenkins/create"
      editPath="/jenkins/edit/:id"
      isLoading={isLoading}
      searchPlaceholder="Search Jenkins configurations..."
      showTestConnection={true}
    />
  )
}

export default JenkinsListPage
