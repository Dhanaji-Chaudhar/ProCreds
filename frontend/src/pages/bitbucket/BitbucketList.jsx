import React, { useState, useEffect } from 'react'
import { toast } from 'react-hot-toast'
import ConfigList from '../../components/ConfigList'
import { bitbucketService } from '../../services/api'

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

  return (
    <ConfigList
      title="Bitbucket Configurations"
      description="Manage your Bitbucket workspace credentials and settings"
      data={configurations}
      columns={columns}
      onDelete={handleDelete}
      createPath="/bitbucket/create"
      editPath="/bitbucket/edit/:id"
      isLoading={isLoading}
      searchPlaceholder="Search Bitbucket configurations..."
    />
  )
}

export default BitbucketList

