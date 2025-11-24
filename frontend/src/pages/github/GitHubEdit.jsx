import React, { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { toast } from 'react-hot-toast'
import ConfigForm from '../../components/ConfigForm'
import { githubService } from '../../services/api'

const GitHubEdit = () => {
  const { id } = useParams()
  const navigate = useNavigate()
  const [isLoading, setIsLoading] = useState(false)
  const [isLoadingData, setIsLoadingData] = useState(true)
  const [defaultValues, setDefaultValues] = useState({})

  const fields = [
    {
      name: 'accountName',
      label: 'Account Name',
      type: 'text',
      placeholder: 'Enter GitHub account name',
      validation: { required: true },
      description: 'Unique identifier for this GitHub configuration',
      section: 'basicInformation'
    },
    {
      name: 'accessToken',
      label: 'Access Token',
      type: 'password',
      placeholder: 'Enter GitHub personal access token',
      validation: { required: true },
      description: 'GitHub personal access token with appropriate permissions',
      section: 'credentials'
    },
    {
      name: 'organization',
      label: 'Organization',
      type: 'text',
      placeholder: 'Enter organization name (optional)',
      description: 'GitHub organization name if applicable',
      section: 'optionalConfiguration'
    },
    {
      name: 'apiUrl',
      label: 'API URL',
      type: 'url',
      placeholder: 'https://api.github.com',
      description: 'GitHub API URL (use default for GitHub.com)',
      section: 'optionalConfiguration'
    },
    {
      name: 'description',
      label: 'Description',
      type: 'textarea',
      placeholder: 'Enter description for this configuration',
      description: 'Optional description to help identify this configuration',
      section: 'optionalConfiguration'
    }
  ]

  useEffect(() => {
    fetchConfiguration()
  }, [id])

  const fetchConfiguration = async () => {
    try {
      setIsLoadingData(true)
      const response = await githubService.getById(id)
      setDefaultValues(response.data)
    } catch (error) {
      if (error.response?.status === 404) {
        toast.error('Configuration not found')
        navigate('/github')
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
      await githubService.update(id, data)
      toast.success('GitHub configuration updated successfully')
      navigate('/github')
    } catch (error) {
      if (error.response?.status === 409) {
        toast.error('A configuration with this account name already exists')
      } else {
        toast.error('Failed to update GitHub configuration')
      }
      console.error('Error updating configuration:', error)
    } finally {
      setIsLoading(false)
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
        <h1 className="text-3xl font-bold text-gray-900">Edit GitHub Configuration</h1>
        <p className="mt-2 text-gray-600">
          Update your GitHub configuration settings and credentials.
        </p>
      </div>

      <ConfigForm
        title="GitHub Configuration"
        description="Update your GitHub access token and repository settings"
        fields={fields}
        onSubmit={handleSubmit}
        defaultValues={defaultValues}
        isLoading={isLoading}
        submitText="Update Configuration"
      />
    </div>
  )
}

export default GitHubEdit

