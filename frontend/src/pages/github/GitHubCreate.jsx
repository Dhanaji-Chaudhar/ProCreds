import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { toast } from 'react-hot-toast'
import ConfigForm from '../../components/ConfigForm'
import { githubService } from '../../services/api'
import { testConnection } from '../../services/testConnection'

const GitHubCreate = () => {
  const navigate = useNavigate()
  const [isLoading, setIsLoading] = useState(false)

  const handleSubmit = async (data) => {
    try {
      setIsLoading(true)
      // Set default API URL if not provided
      const configData = {
        ...data,
        apiUrl: data.apiUrl || 'https://api.github.com'
      }
      
      await githubService.create(configData)
      toast.success('GitHub configuration created successfully')
      navigate('/github')
    } catch (error) {
      if (error.response?.status === 409) {
        toast.error('A configuration with this account name already exists')
      } else {
        toast.error('Failed to create GitHub configuration')
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
        apiUrl: data.apiUrl || 'https://api.github.com'
      }
      
      return await testConnection(platform, testData)
    } catch (error) {
      throw error
    }
  }

  return (
    <div className="max-w-4xl mx-auto">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">Create GitHub Configuration</h1>
        <p className="mt-2 text-gray-600">
          Add a new GitHub configuration to manage repository access and authentication.
        </p>
      </div>

      <ConfigForm
        title="GitHub Configuration"
        description="Configure your GitHub access token and repository settings"
        platform="github"
        onSubmit={handleSubmit}
        onTestConnection={handleTestConnection}
        isLoading={isLoading}
        submitText="Create Configuration"
      />
    </div>
  )
}

export default GitHubCreate
