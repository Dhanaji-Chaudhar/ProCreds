import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { toast } from 'react-hot-toast'
import ConfigForm from '../../components/ConfigForm'
import { azureAksService } from '../../services/api'
import { testConnection } from '../../services/testConnection'

const AzureAksCreatePage = () => {
  const navigate = useNavigate()
  const [isLoading, setIsLoading] = useState(false)

  const handleSubmit = async (data) => {
    try {
      setIsLoading(true)
      await azureAksService.create(data)
      toast.success('Azure AKS configuration created successfully')
      navigate('/azure-aks')
    } catch (error) {
      if (error.response?.status === 409) {
        toast.error('A configuration with this account name already exists')
      } else {
        toast.error('Failed to create Azure AKS configuration')
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
        <h1 className="text-3xl font-bold text-gray-900">Create Azure AKS Configuration</h1>
        <p className="mt-2 text-gray-600">
          Add a new Azure AKS configuration to manage managed Kubernetes clusters on Azure.
        </p>
      </div>

      <ConfigForm
        title="Azure AKS Configuration"
        description="Configure your Azure AKS cluster connection and service principal credentials"
        platform="azure-aks"
        onSubmit={handleSubmit}
        onTestConnection={handleTestConnection}
        isLoading={isLoading}
        submitText="Create Configuration"
      />
    </div>
  )
}

export default AzureAksCreatePage
