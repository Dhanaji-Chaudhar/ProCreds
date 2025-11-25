import React, { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { toast } from 'react-hot-toast'
import ConfigForm from '../../components/ConfigForm'
import { gcpGkeService } from '../../services/api'
import { testConnection } from '../../services/testConnection'

const GcpGkeEditPage = () => {
  const { id } = useParams()
  const navigate = useNavigate()
  const [isLoading, setIsLoading] = useState(false)
  const [isLoadingData, setIsLoadingData] = useState(true)
  const [defaultValues, setDefaultValues] = useState({})

  useEffect(() => {
    fetchConfiguration()
  }, [id])

  const fetchConfiguration = async () => {
    try {
      setIsLoadingData(true)
      const response = await gcpGkeService.getById(id)
      setDefaultValues(response.data)
    } catch (error) {
      if (error.response?.status === 404) {
        toast.error('Configuration not found')
        navigate('/gcp-gke')
      } else {
        toast.error('Failed to fetch configuration')
      }
      console.error('Error fetching configuration:', error)
    } finally {
      setIsLoadingData(false)
    }
  }

  const handleSubmit = async (data) => {
    try {
      setIsLoading(true)
      await gcpGkeService.update(id, data)
      toast.success('GCP GKE configuration updated successfully')
      navigate('/gcp-gke')
    } catch (error) {
      if (error.response?.status === 409) {
        toast.error('A configuration with this account name already exists')
      } else {
        toast.error('Failed to update GCP GKE configuration')
      }
      console.error('Error updating configuration:', error)
    } finally {
      setIsLoading(false)
    }
  }

  const handleTestConnection = async (platform, data) => {
    try {
      return await testConnection(platform, data)
    } catch (error) {
      throw error
    }
  }

  if (isLoadingData) {
    return (
      <div className="max-w-4xl mx-auto">
        <div className="flex items-center justify-center py-12">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
          <span className="ml-2 text-gray-600">Loading configuration...</span>
        </div>
      </div>
    )
  }

  return (
    <div className="max-w-4xl mx-auto">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">Edit GCP GKE Configuration</h1>
        <p className="mt-2 text-gray-600">
          Update your GCP GKE configuration settings and credentials.
        </p>
      </div>

      <ConfigForm
        title="GCP GKE Configuration"
        description="Update your GCP GKE cluster connection and service account credentials"
        platform="gcp-gke"
        onSubmit={handleSubmit}
        onTestConnection={handleTestConnection}
        defaultValues={defaultValues}
        isLoading={isLoading}
        submitText="Update Configuration"
      />
    </div>
  )
}

export default GcpGkeEditPage
