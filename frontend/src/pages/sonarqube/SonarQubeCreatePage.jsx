import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { toast } from 'react-hot-toast'
import ConfigForm from '../../components/ConfigForm'
import { sonarqubeService } from '../../services/api'
import { testConnection } from '../../services/testConnection'

const SonarQubeCreatePage = () => {
  const navigate = useNavigate()
  const [isLoading, setIsLoading] = useState(false)

  const handleSubmit = async (data) => {
    try {
      setIsLoading(true)
      await sonarqubeService.create(data)
      toast.success('SonarQube configuration created successfully')
      navigate('/sonarqube')
    } catch (error) {
      if (error.response?.status === 409) {
        toast.error('A configuration with this account name already exists')
      } else {
        toast.error('Failed to create SonarQube configuration')
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
        <h1 className="text-3xl font-bold text-gray-900">Create SonarQube Configuration</h1>
        <p className="mt-2 text-gray-600">
          Add a new SonarQube configuration to manage code quality analysis and metrics.
        </p>
      </div>

      <ConfigForm
        title="SonarQube Configuration"
        description="Configure your SonarQube server connection and authentication token"
        platform="sonarqube"
        onSubmit={handleSubmit}
        onTestConnection={handleTestConnection}
        isLoading={isLoading}
        submitText="Create Configuration"
      />
    </div>
  )
}

export default SonarQubeCreatePage
