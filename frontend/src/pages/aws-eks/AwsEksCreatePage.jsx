import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { toast } from 'react-hot-toast'
import ConfigForm from '../../components/ConfigForm'
import { awsEksService } from '../../services/api'
import { testConnection } from '../../services/testConnection'

const AwsEksCreatePage = () => {
  const navigate = useNavigate()
  const [isLoading, setIsLoading] = useState(false)

  const handleSubmit = async (data) => {
    try {
      setIsLoading(true)
      await awsEksService.create(data)
      toast.success('AWS EKS configuration created successfully')
      navigate('/aws-eks')
    } catch (error) {
      if (error.response?.status === 409) {
        toast.error('A configuration with this account name already exists')
      } else {
        toast.error('Failed to create AWS EKS configuration')
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
        <h1 className="text-3xl font-bold text-gray-900">Create AWS EKS Configuration</h1>
        <p className="mt-2 text-gray-600">
          Add a new AWS EKS configuration to manage managed Kubernetes clusters on AWS.
        </p>
      </div>

      <ConfigForm
        title="AWS EKS Configuration"
        description="Configure your AWS EKS cluster connection and credentials"
        platform="aws-eks"
        onSubmit={handleSubmit}
        onTestConnection={handleTestConnection}
        isLoading={isLoading}
        submitText="Create Configuration"
      />
    </div>
  )
}

export default AwsEksCreatePage
