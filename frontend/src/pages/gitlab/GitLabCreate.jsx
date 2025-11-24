import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { toast } from 'react-hot-toast'
import ConfigForm from '../../components/ConfigForm'
import { gitlabService } from '../../services/api'
import { testConnection } from '../../services/testConnection'

const GitLabCreate = () => {
  const navigate = useNavigate()
  const [isLoading, setIsLoading] = useState(false)

  const handleSubmit = async (data) => {
    try {
      setIsLoading(true)
      // Set default API URL if not provided
      const configData = {
        ...data,
        apiUrl: data.apiUrl || 'https://gitlab.com/api/v4'
      }
      
      await gitlabService.create(configData)
      toast.success('GitLab configuration created successfully')
      navigate('/gitlab')
    } catch (error) {
      if (error.response?.status === 409) {
        toast.error('A configuration with this account name already exists')
      } else {
        toast.error('Failed to create GitLab configuration')
      }
      console.error('Error creating configuration:', error)
    } finally {
      setIsLoading(false)
    }
  }

  const handleTestConnection = async (platform, data) => {
    try {
      // Set default API URL for testing if not provided
      const testData = {
        ...data,
        apiUrl: data.apiUrl || 'https://gitlab.com/api/v4'
      }
      
      return await testConnection(platform, testData)
    } catch (error) {
      throw error
    }
  }

  return (
    <div className="max-w-4xl mx-auto">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">Create GitLab Configuration</h1>
        <p className="mt-2 text-gray-600">
          Add a new GitLab configuration to manage project access and authentication.
        </p>
      </div>

      <ConfigForm
        title="GitLab Configuration"
        description="Configure your GitLab personal access token and project settings"
        platform="gitlab"
        onSubmit={handleSubmit}
        onTestConnection={handleTestConnection}
        isLoading={isLoading}
        submitText="Create Configuration"
      />
    </div>
  )
}

export default GitLabCreate
