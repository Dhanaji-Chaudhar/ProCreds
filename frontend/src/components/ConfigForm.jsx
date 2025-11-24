import React from 'react'
import { useForm, useWatch } from 'react-hook-form'
import { Button } from './ui/Button'
import { Input } from './ui/Input'
import { Label } from './ui/Label'
import { Textarea } from './ui/Textarea'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/Card'
import TestConnectionButton from './TestConnectionButton'
import { getPlatformConfig, getFieldsBySection } from '../config/platformFields'

const ConfigForm = ({ 
  title, 
  description, 
  fields, 
  platform,
  onSubmit, 
  onTestConnection,
  defaultValues = {}, 
  isLoading = false,
  submitText = 'Save Configuration'
}) => {
  const { register, handleSubmit, control, formState: { errors } } = useForm({
    defaultValues
  })

  // Watch form values for test connection
  const watchedValues = useWatch({ control })

  const renderField = (field) => {
    const commonProps = {
      id: field.name,
      ...register(field.name, field.validation),
      className: errors[field.name] ? 'border-red-500' : ''
    }

    switch (field.type) {
      case 'textarea':
        return <Textarea {...commonProps} placeholder={field.placeholder} />
      case 'password':
        return <Input {...commonProps} type="password" placeholder={field.placeholder} />
      default:
        return <Input {...commonProps} type={field.type || 'text'} placeholder={field.placeholder} />
    }
  }

  // Use platform-specific fields if platform is provided, otherwise use passed fields
  const actualFields = platform ? getPlatformConfig(platform)?.fields || [] : fields || []
  const platformConfig = platform ? getPlatformConfig(platform) : null
  
  // Group fields by section using the helper function if platform is provided
  const groupedFields = platform 
    ? getFieldsBySection(platform)
    : actualFields.reduce((acc, field) => {
        const section = field.section || 'default'
        if (!acc[section]) acc[section] = []
        acc[section].push(field)
        return acc
      }, {})

  return (
    <Card className="max-w-2xl mx-auto">
      <CardHeader>
        <CardTitle>{title}</CardTitle>
        <CardDescription>{description}</CardDescription>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
          {/* Basic Information Section */}
          {groupedFields.basicInformation && groupedFields.basicInformation.length > 0 && (
            <div>
              <div className="mb-4">
                <h3 className="text-lg font-medium text-gray-900">Basic Information</h3>
                <div className="mt-1 h-px bg-gray-200"></div>
              </div>
              <div className="grid grid-cols-1 gap-4">
                {groupedFields.basicInformation.map((field) => (
                  <div key={field.name}>
                    <Label htmlFor={field.name} className="block text-sm font-medium text-gray-700">
                      {field.label}
                      {field.validation?.required && (
                        <span className="text-red-500 ml-1">*</span>
                      )}
                    </Label>
                    <div className="mt-1">
                      {renderField(field)}
                    </div>
                    {field.description && (
                      <p className="mt-1 text-sm text-gray-500">{field.description}</p>
                    )}
                    {errors[field.name] && (
                      <p className="mt-1 text-sm text-red-600">
                        {errors[field.name].message || `${field.label} is required`}
                      </p>
                    )}
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Credentials Section */}
          {groupedFields.credentials && groupedFields.credentials.length > 0 && (
            <div>
              <div className="mb-4">
                <h3 className="text-lg font-medium text-gray-900">Credentials</h3>
                <div className="mt-1 h-px bg-gray-200"></div>
              </div>
              <div className="grid grid-cols-1 gap-4">
                {groupedFields.credentials.map((field) => (
                  <div key={field.name}>
                    <Label htmlFor={field.name} className="block text-sm font-medium text-gray-700">
                      {field.label}
                      {field.validation?.required && (
                        <span className="text-red-500 ml-1">*</span>
                      )}
                    </Label>
                    <div className="mt-1">
                      {renderField(field)}
                    </div>
                    {field.description && (
                      <p className="mt-1 text-sm text-gray-500">{field.description}</p>
                    )}
                    {errors[field.name] && (
                      <p className="mt-1 text-sm text-red-600">
                        {errors[field.name].message || `${field.label} is required`}
                      </p>
                    )}
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Test Connection Section */}
          {onTestConnection && platformConfig && (
            <div>
              <div className="mb-4">
                <h3 className="text-lg font-medium text-gray-900">Test Connection</h3>
                <div className="mt-1 h-px bg-gray-200"></div>
              </div>
              <div className="bg-gray-50 p-4 rounded-lg">
                <p className="text-sm text-gray-600 mb-3">
                  Test your configuration to ensure the credentials are working correctly.
                </p>
                <TestConnectionButton
                  platform={platform}
                  data={watchedValues}
                  onTest={onTestConnection}
                  disabled={!watchedValues || Object.keys(watchedValues).length === 0}
                />
              </div>
            </div>
          )}

          {/* Optional Configuration Section */}
          {groupedFields.optionalConfiguration && groupedFields.optionalConfiguration.length > 0 && (
            <div>
              <div className="mb-4">
                <h3 className="text-lg font-medium text-gray-900">Optional Configuration</h3>
                <div className="mt-1 h-px bg-gray-200"></div>
              </div>
              <div className="grid grid-cols-1 gap-4">
                {groupedFields.optionalConfiguration.map((field) => (
                  <div key={field.name}>
                    <Label htmlFor={field.name} className="block text-sm font-medium text-gray-700">
                      {field.label}
                      {field.validation?.required && (
                        <span className="text-red-500 ml-1">*</span>
                      )}
                    </Label>
                    <div className="mt-1">
                      {renderField(field)}
                    </div>
                    {field.description && (
                      <p className="mt-1 text-sm text-gray-500">{field.description}</p>
                    )}
                    {errors[field.name] && (
                      <p className="mt-1 text-sm text-red-600">
                        {errors[field.name].message || `${field.label} is required`}
                      </p>
                    )}
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Handle legacy grouped fields for backward compatibility */}
          {!platform && Object.entries(groupedFields).map(([sectionName, sectionFields]) => (
            sectionName !== 'basicInformation' && 
            sectionName !== 'credentials' && 
            sectionName !== 'optionalConfiguration' && (
              <div key={sectionName}>
                {sectionName !== 'default' && (
                  <div className="mb-4">
                    <h3 className="text-lg font-medium text-gray-900 capitalize">
                      {sectionName.replace(/([A-Z])/g, ' $1').trim()}
                    </h3>
                    <div className="mt-1 h-px bg-gray-200"></div>
                  </div>
                )}
                
                <div className="grid grid-cols-1 gap-4">
                  {sectionFields.map((field) => (
                    <div key={field.name}>
                      <Label htmlFor={field.name} className="block text-sm font-medium text-gray-700">
                        {field.label}
                        {field.validation?.required && (
                          <span className="text-red-500 ml-1">*</span>
                        )}
                      </Label>
                      <div className="mt-1">
                        {renderField(field)}
                      </div>
                      {field.description && (
                        <p className="mt-1 text-sm text-gray-500">{field.description}</p>
                      )}
                      {errors[field.name] && (
                        <p className="mt-1 text-sm text-red-600">
                          {errors[field.name].message || `${field.label} is required`}
                        </p>
                      )}
                    </div>
                  ))}
                </div>
              </div>
            )
          ))}

          <div className="flex justify-end space-x-3 pt-6 border-t border-gray-200">
            <Button type="button" variant="outline" onClick={() => window.history.back()}>
              Cancel
            </Button>
            <Button type="submit" disabled={isLoading}>
              {isLoading ? 'Saving...' : submitText}
            </Button>
          </div>
        </form>
      </CardContent>
    </Card>
  )
}

export default ConfigForm
