import React, { useState, useEffect } from 'react'
import { toast } from 'react-hot-toast'
import ConfigList from '../../components/ConfigList'
import { kubernetesService } from '../../services/api'
import { testConnection } from '../../services/testConnection'

const KubernetesListPage = () => {
  const [configurations, setConfigurations] = useState([])
  const [isLoading, setIsLoading] = useState(true)

  const columns = [
    { key: 'accountName', label: 'Account Name' },
    { key: 'clusterName', label: 'Cluster Name' },
    { key: 'namespace', label: 'Namespace' },
    { key: 'region', label: 'Region' },
    { key: 'description', label: 'Description', type: 'truncate' },
    { key: 'createdAt', label: 'Created', type: 'date' },
  ]

  useEffect(() => {
    fetchConfigurations()
  }, [])

  const fetchConfigurations = async () => {
    try {
      setIsLoading(true)
      const response = await kubernetesService.getAll()
      setConfigurations(response.data.content || [])
    } catch (error) {
      toast.error('Failed to fetch Kubernetes configurations')
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
      await kubernetesService.delete(id)
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
      title="Kubernetes Configurations"
      description="Manage your Kubernetes cluster connections and kubeconfig settings"
      data={configurations}
      columns={columns}
      onDelete={handleDelete}
      onTestConnection={handleTestConnection}
      platform="kubernetes"
      createPath="/kubernetes/create"
      editPath="/kubernetes/edit/:id"
      isLoading={isLoading}
      searchPlaceholder="Search Kubernetes configurations..."
      showTestConnection={true}
    />
  )
}

export default KubernetesListPage
