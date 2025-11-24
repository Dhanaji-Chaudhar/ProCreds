import React, { useState } from 'react'
import { Button } from './ui/Button'
import { LoadingSpinner } from './ui/LoadingSpinner'
import { Alert, AlertDescription, AlertIcon } from './ui/Alert'
import { CheckCircle, XCircle, Wifi } from 'lucide-react'

const TestConnectionButton = ({ 
  platform, 
  data, 
  onTest, 
  disabled = false, 
  size = "default",
  variant = "outline",
  className = ""
}) => {
  const [isLoading, setIsLoading] = useState(false)
  const [result, setResult] = useState(null)

  const handleTest = async () => {
    if (!onTest || disabled) return

    setIsLoading(true)
    setResult(null)

    try {
      const response = await onTest(platform, data)
      setResult({
        success: true,
        message: response.message || 'Connection successful!',
        details: response.details
      })
    } catch (error) {
      setResult({
        success: false,
        message: error.message || 'Connection failed',
        details: error.details || error.response?.data?.message
      })
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="space-y-3">
      <Button
        type="button"
        variant={variant}
        size={size}
        onClick={handleTest}
        disabled={disabled || isLoading}
        className={className}
      >
        {isLoading ? (
          <>
            <LoadingSpinner size="sm" className="mr-2" />
            Testing...
          </>
        ) : (
          <>
            <Wifi className="h-4 w-4 mr-2" />
            Test Connection
          </>
        )}
      </Button>

      {result && (
        <Alert variant={result.success ? "success" : "destructive"}>
          <AlertIcon variant={result.success ? "success" : "destructive"} />
          <AlertDescription>
            <div className="font-medium">
              {result.success ? 'Connection Successful' : 'Connection Failed'}
            </div>
            <div className="text-sm mt-1">
              {result.message}
            </div>
            {result.details && (
              <div className="text-xs mt-2 opacity-75">
                {result.details}
              </div>
            )}
          </AlertDescription>
        </Alert>
      )}
    </div>
  )
}

export default TestConnectionButton

