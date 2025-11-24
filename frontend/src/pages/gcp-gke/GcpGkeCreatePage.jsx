import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { toast } from 'react-hot-toast'
import ConfigForm from '../../components/ConfigForm'
import { gcpGkeService } from '../../services/api'
import { testConnection } from '../../services/testConnection'

const GcpGkeCreatePage = () => {
  const navigate = useNavigate()
  const [isLoading, setIsLoading] = useState(false)

  const handleSubmit = async (data) => {
    try {
      setIsLoading(true)
      await gcpGkeService.create(data)
      toast.success('GCP GKE configuration created successfully')
      navigate('/gcp-gke')
    } catch (error) {
      if (error.response?.status === 409) {
        toast.error('A configuration with this account name already exists')
      } else {
        toast.error('Failed to create GCP GKE configuration')
      }
      console.error('Error creating configuration:', error)
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

  return (
    <div className="max-w-4xl mx-auto">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">Create GCP GKE Configuration</h1>
        <p className="mt-2 text-gray-600">
          Add a new GCP GKE configuration to manage managed Kubernetes clusters on Google Cloud Platform.
        </p>
      </div>

      <ConfigForm
        title="GCP GKE Configuration"
        description="Configure your GCP GKE cluster connection and service account credentials"
        platform="gcp-gke"
        onSubmit={handleSubmit}
        onTestConnection={handleTestConnection}
        isLoading={isLoading}
        submitText="Create Configuration"
      />
    </div>
  )
}

export default GcpGkeCreatePage
