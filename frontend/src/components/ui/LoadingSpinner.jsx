import React from 'react'
import { cn } from '../../utils/cn'

const LoadingSpinner = React.forwardRef(({ className, size = "default", ...props }, ref) => {
  const sizes = {
    sm: "h-4 w-4",
    default: "h-6 w-6",
    lg: "h-8 w-8",
    xl: "h-12 w-12"
  }

  return (
    <div
      ref={ref}
      className={cn(
        "animate-spin rounded-full border-2 border-current border-t-transparent",
        sizes[size],
        className
      )}
      {...props}
    >
      <span className="sr-only">Loading...</span>
    </div>
  )
})
LoadingSpinner.displayName = "LoadingSpinner"

export { LoadingSpinner }

