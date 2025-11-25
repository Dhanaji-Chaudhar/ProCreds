import React, { useState, useEffect } from 'react'
import { toast } from 'react-hot-toast'
import ConfigList from '../../components/ConfigList'
import { azureAksService } from '../../services/api'
import { testConnection } from '../../services/testConnection'

const AzureAksListPage = () => {
  const [configurations, setConfigurations] = useState([])
  const [isLoading, setIsLoading] = useState(true)

  const columns = [
    { key: 'accountName', label: 'Account Name' },
    { key: 'clusterName', label: 'Cluster Name' },
    { key: 'tenantId', label: 'Tenant ID', type: 'truncate' },
    { key: 'clientId', label: 'Client ID', type: 'truncate' },
    { key: 'createdAt', label: 'Created', type: 'date' },
  ]

  useEffect(() => {
    fetchConfigurations()
  }, [])

  const fetchConfigurations = async () => {
    try {
      setIsLoading(true)
      const response = await azureAksService.getAll()
      setConfigurations(response.data.content || [])
    } catch (error) {
      toast.error('Failed to fetch Azure AKS configurations')
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
      await azureAksService.delete(id)
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
      title="Azure AKS Configurations"
      description="Manage your Azure AKS cluster connections and service principal credentials"
      data={configurations}
      columns={columns}
      onDelete={handleDelete}
      onTestConnection={handleTestConnection}
      platform="azure-aks"
      createPath="/azure-aks/create"
      editPath="/azure-aks/edit/:id"
      isLoading={isLoading}
      searchPlaceholder="Search Azure AKS configurations..."
      showTestConnection={true}
    />
  )
}

export default AzureAksListPage
