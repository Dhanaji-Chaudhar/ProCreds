import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { toast } from 'react-hot-toast'
import ConfigForm from '../../components/ConfigForm'
import { githubService } from '../../services/api'

const GitHubCreate = () => {
  const navigate = useNavigate()
  const [isLoading, setIsLoading] = useState(false)

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

  const handleSubmit = async (data) => {
    try {
      setIsLoading(true)
      await githubService.create(data)
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
        fields={fields}
        onSubmit={handleSubmit}
        isLoading={isLoading}
        submitText="Create Configuration"
      />
    </div>
  )
}

export default GitHubCreate

