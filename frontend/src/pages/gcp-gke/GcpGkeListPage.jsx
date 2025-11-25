import React, { useState, useEffect } from 'react'
import { toast } from 'react-hot-toast'
import ConfigList from '../../components/ConfigList'
import { gcpGkeService } from '../../services/api'
import { testConnection } from '../../services/testConnection'

const GcpGkeListPage = () => {
  const [configurations, setConfigurations] = useState([])
  const [isLoading, setIsLoading] = useState(true)

  const columns = [
    { key: 'accountName', label: 'Account Name' },
    { key: 'clusterName', label: 'Cluster Name' },
    { key: 'projectId', label: 'Project ID' },
    { key: 'zone', label: 'Zone' },
    { key: 'createdAt', label: 'Created', type: 'date' },
  ]

  useEffect(() => {
    fetchConfigurations()
  }, [])

  const fetchConfigurations = async () => {
    try {
      setIsLoading(true)
      const response = await gcpGkeService.getAll()
      setConfigurations(response.data.content || [])
    } catch (error) {
      toast.error('Failed to fetch GCP GKE configurations')
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
      await gcpGkeService.delete(id)
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
      title="GCP GKE Configurations"
      description="Manage your GCP GKE cluster connections and service account credentials"
      data={configurations}
      columns={columns}
      onDelete={handleDelete}
      onTestConnection={handleTestConnection}
      platform="gcp-gke"
      createPath="/gcp-gke/create"
      editPath="/gcp-gke/edit/:id"
      isLoading={isLoading}
      searchPlaceholder="Search GCP GKE configurations..."
      showTestConnection={true}
    />
  )
}

export default GcpGkeListPage
