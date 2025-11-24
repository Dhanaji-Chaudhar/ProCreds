import React, { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { toast } from 'react-hot-toast'
import ConfigForm from '../../components/ConfigForm'
import { bitbucketService } from '../../services/api'
import { testConnection } from '../../services/testConnection'

const BitbucketEdit = () => {
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
      const response = await bitbucketService.getById(id)
      setDefaultValues(response.data)
    } catch (error) {
      if (error.response?.status === 404) {
        toast.error('Configuration not found')
        navigate('/bitbucket')
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
      // Set default API URL if not provided
      const configData = {
        ...data,
        apiUrl: data.apiUrl || 'https://api.bitbucket.org/2.0'
      }
      
      await bitbucketService.update(id, configData)
      toast.success('Bitbucket configuration updated successfully')
      navigate('/bitbucket')
    } catch (error) {
      if (error.response?.status === 409) {
        toast.error('A configuration with this username already exists')
      } else {
        toast.error('Failed to update Bitbucket configuration')
      }
      console.error('Error updating configuration:', error)
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
        <h1 className="text-3xl font-bold text-gray-900">Edit Bitbucket Configuration</h1>
        <p className="mt-2 text-gray-600">
          Update your Bitbucket configuration settings and credentials.
        </p>
      </div>

      <ConfigForm
        title="Bitbucket Configuration"
        description="Update your Bitbucket app password and workspace settings"
        platform="bitbucket"
        onSubmit={handleSubmit}
        onTestConnection={handleTestConnection}
        defaultValues={defaultValues}
        isLoading={isLoading}
        submitText="Update Configuration"
      />
    </div>
  )
}

export default BitbucketEdit
