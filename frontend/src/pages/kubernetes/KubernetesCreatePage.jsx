import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { toast } from 'react-hot-toast'
import ConfigForm from '../../components/ConfigForm'
import { kubernetesService } from '../../services/api'
import { testConnection } from '../../services/testConnection'

const KubernetesCreatePage = () => {
  const navigate = useNavigate()
  const [isLoading, setIsLoading] = useState(false)

  const handleSubmit = async (data) => {
    try {
      setIsLoading(true)
      await kubernetesService.create(data)
      toast.success('Kubernetes configuration created successfully')
      navigate('/kubernetes')
    } catch (error) {
      if (error.response?.status === 409) {
        toast.error('A configuration with this account name already exists')
      } else {
        toast.error('Failed to create Kubernetes configuration')
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
        <h1 className="text-3xl font-bold text-gray-900">Create Kubernetes Configuration</h1>
        <p className="mt-2 text-gray-600">
          Add a new Kubernetes configuration to manage cluster access and deployments.
        </p>
      </div>

      <ConfigForm
        title="Kubernetes Configuration"
        description="Configure your Kubernetes cluster connection and kubeconfig settings"
        platform="kubernetes"
        onSubmit={handleSubmit}
        onTestConnection={handleTestConnection}
        isLoading={isLoading}
        submitText="Create Configuration"
      />
    </div>
  )
}

export default KubernetesCreatePage
