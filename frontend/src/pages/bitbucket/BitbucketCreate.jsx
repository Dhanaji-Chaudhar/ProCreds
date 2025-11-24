import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { toast } from 'react-hot-toast'
import ConfigForm from '../../components/ConfigForm'
import { bitbucketService } from '../../services/api'
import { testConnection } from '../../services/testConnection'

const BitbucketCreate = () => {
  const navigate = useNavigate()
  const [isLoading, setIsLoading] = useState(false)

  const handleSubmit = async (data) => {
    try {
      setIsLoading(true)
      // Set default API URL if not provided
      const configData = {
        ...data,
        apiUrl: data.apiUrl || 'https://api.bitbucket.org/2.0'
      }
      
      await bitbucketService.create(configData)
      toast.success('Bitbucket configuration created successfully')
      navigate('/bitbucket')
    } catch (error) {
      if (error.response?.status === 409) {
        toast.error('A configuration with this username already exists')
      } else {
        toast.error('Failed to create Bitbucket configuration')
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
        apiUrl: data.apiUrl || 'https://api.bitbucket.org/2.0'
      }
      
      return await testConnection(platform, testData)
    } catch (error) {
      throw error
    }
  }

  return (
    <div className="max-w-4xl mx-auto">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">Create Bitbucket Configuration</h1>
        <p className="mt-2 text-gray-600">
          Add a new Bitbucket configuration to manage workspace access and authentication.
        </p>
      </div>

      <ConfigForm
        title="Bitbucket Configuration"
        description="Configure your Bitbucket app password and workspace settings"
        platform="bitbucket"
        onSubmit={handleSubmit}
        onTestConnection={handleTestConnection}
        isLoading={isLoading}
        submitText="Create Configuration"
      />
    </div>
  )
}

export default BitbucketCreate
