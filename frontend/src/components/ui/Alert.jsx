import React from 'react'
import { cn } from '../../utils/cn'
import { AlertCircle, CheckCircle, Info, XCircle } from 'lucide-react'

const Alert = React.forwardRef(({ className, variant = "default", ...props }, ref) => {
  const variants = {
    default: "bg-background text-foreground",
    destructive: "border-destructive/50 text-destructive dark:border-destructive [&>svg]:text-destructive",
    success: "border-green-500/50 text-green-700 bg-green-50 [&>svg]:text-green-600",
    warning: "border-yellow-500/50 text-yellow-700 bg-yellow-50 [&>svg]:text-yellow-600",
    info: "border-blue-500/50 text-blue-700 bg-blue-50 [&>svg]:text-blue-600"
  }

  return (
    <div
      ref={ref}
      role="alert"
      className={cn(
        "relative w-full rounded-lg border p-4 [&>svg~*]:pl-7 [&>svg+div]:translate-y-[-3px] [&>svg]:absolute [&>svg]:left-4 [&>svg]:top-4 [&>svg]:text-foreground",
        variants[variant],
        className
      )}
      {...props}
    />
  )
})
Alert.displayName = "Alert"

const AlertDescription = React.forwardRef(({ className, ...props }, ref) => (
  <div
    ref={ref}
    className={cn("text-sm [&_p]:leading-relaxed", className)}
    {...props}
  />
))
AlertDescription.displayName = "AlertDescription"

const AlertTitle = React.forwardRef(({ className, ...props }, ref) => (
  <h5
    ref={ref}
    className={cn("mb-1 font-medium leading-none tracking-tight", className)}
    {...props}
  />
))
AlertTitle.displayName = "AlertTitle"

const AlertIcon = ({ variant = "default" }) => {
  const icons = {
    default: Info,
    destructive: XCircle,
    success: CheckCircle,
    warning: AlertCircle,
    info: Info
  }
  
  const Icon = icons[variant]
  return <Icon className="h-4 w-4" />
}

export { Alert, AlertDescription, AlertTitle, AlertIcon }

