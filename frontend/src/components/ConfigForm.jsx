import React from 'react'
import { useForm } from 'react-hook-form'
import { Button } from './ui/Button'
import { Input } from './ui/Input'
import { Label } from './ui/Label'
import { Textarea } from './ui/Textarea'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/Card'

const ConfigForm = ({ 
  title, 
  description, 
  fields, 
  onSubmit, 
  defaultValues = {}, 
  isLoading = false,
  submitText = 'Save Configuration'
}) => {
  const { register, handleSubmit, formState: { errors } } = useForm({
    defaultValues
  })

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

  const groupedFields = fields.reduce((acc, field) => {
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
          {Object.entries(groupedFields).map(([sectionName, sectionFields]) => (
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

