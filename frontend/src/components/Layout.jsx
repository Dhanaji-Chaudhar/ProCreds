import React, { useState } from 'react'
import { Link, useLocation } from 'react-router-dom'
import { 
  Menu, 
  X, 
  Home, 
  Github, 
  GitBranch, 
  Gitlab, 
  Wrench, 
  Bug, 
  Shield, 
  Server,
  Cloud,
  ChevronDown,
  ChevronRight
} from 'lucide-react'
import { cn } from '../utils/cn'

const Layout = ({ children }) => {
  const [sidebarOpen, setSidebarOpen] = useState(false)
  const [kubernetesOpen, setKubernetesOpen] = useState(false)
  const location = useLocation()

  const navigation = [
    { name: 'Dashboard', href: '/', icon: Home },
    { name: 'GitHub', href: '/github', icon: Github },
    { name: 'Bitbucket', href: '/bitbucket', icon: GitBranch },
    { name: 'GitLab', href: '/gitlab', icon: Gitlab },
    { name: 'Jenkins', href: '/jenkins', icon: Wrench },
    { name: 'Jira', href: '/jira', icon: Bug },
    { name: 'SonarQube', href: '/sonarqube', icon: Shield },
    {
      name: 'Kubernetes',
      icon: Server,
      children: [
        { name: 'Generic Kubernetes', href: '/kubernetes' },
        { name: 'AWS EKS', href: '/aws-eks' },
        { name: 'Azure AKS', href: '/azure-aks' },
        { name: 'GCP GKE', href: '/gcp-gke' },
      ]
    },
  ]

  const isActive = (href) => {
    if (href === '/') {
      return location.pathname === '/'
    }
    return location.pathname.startsWith(href)
  }

  const isKubernetesActive = () => {
    return ['/kubernetes', '/aws-eks', '/azure-aks', '/gcp-gke'].some(path => 
      location.pathname.startsWith(path)
    )
  }

  return (
    <div className="flex h-screen bg-gray-100">
      {/* Mobile sidebar overlay */}
      {sidebarOpen && (
        <div 
          className="fixed inset-0 z-40 lg:hidden"
          onClick={() => setSidebarOpen(false)}
        >
          <div className="absolute inset-0 bg-gray-600 opacity-75"></div>
        </div>
      )}

      {/* Sidebar */}
      <div className={cn(
        "fixed inset-y-0 left-0 z-50 w-64 bg-white shadow-lg transform transition-transform duration-300 ease-in-out lg:translate-x-0 lg:static lg:inset-0",
        sidebarOpen ? "translate-x-0" : "-translate-x-full"
      )}>
        <div className="flex items-center justify-between h-16 px-4 border-b border-gray-200">
          <div className="flex items-center">
            <Cloud className="h-8 w-8 text-blue-600" />
            <span className="ml-2 text-xl font-bold text-gray-900">ProCreds</span>
          </div>
          <button
            onClick={() => setSidebarOpen(false)}
            className="lg:hidden p-2 rounded-md text-gray-400 hover:text-gray-500 hover:bg-gray-100"
          >
            <X className="h-6 w-6" />
          </button>
        </div>

        <nav className="mt-5 px-2 space-y-1">
          {navigation.map((item) => {
            if (item.children) {
              return (
                <div key={item.name}>
                  <button
                    onClick={() => setKubernetesOpen(!kubernetesOpen)}
                    className={cn(
                      "w-full flex items-center justify-between px-2 py-2 text-sm font-medium rounded-md transition-colors",
                      isKubernetesActive()
                        ? "bg-blue-100 text-blue-900"
                        : "text-gray-600 hover:bg-gray-50 hover:text-gray-900"
                    )}
                  >
                    <div className="flex items-center">
                      <item.icon className="mr-3 h-5 w-5" />
                      {item.name}
                    </div>
                    {kubernetesOpen ? (
                      <ChevronDown className="h-4 w-4" />
                    ) : (
                      <ChevronRight className="h-4 w-4" />
                    )}
                  </button>
                  {kubernetesOpen && (
                    <div className="ml-6 mt-1 space-y-1">
                      {item.children.map((child) => (
                        <Link
                          key={child.name}
                          to={child.href}
                          className={cn(
                            "block px-2 py-2 text-sm rounded-md transition-colors",
                            isActive(child.href)
                              ? "bg-blue-100 text-blue-900 font-medium"
                              : "text-gray-600 hover:bg-gray-50 hover:text-gray-900"
                          )}
                          onClick={() => setSidebarOpen(false)}
                        >
                          {child.name}
                        </Link>
                      ))}
                    </div>
                  )}
                </div>
              )
            }

            return (
              <Link
                key={item.name}
                to={item.href}
                className={cn(
                  "flex items-center px-2 py-2 text-sm font-medium rounded-md transition-colors",
                  isActive(item.href)
                    ? "bg-blue-100 text-blue-900"
                    : "text-gray-600 hover:bg-gray-50 hover:text-gray-900"
                )}
                onClick={() => setSidebarOpen(false)}
              >
                <item.icon className="mr-3 h-5 w-5" />
                {item.name}
              </Link>
            )
          })}
        </nav>
      </div>

      {/* Main content */}
      <div className="flex-1 flex flex-col overflow-hidden">
        {/* Top bar */}
        <header className="bg-white shadow-sm border-b border-gray-200">
          <div className="flex items-center justify-between h-16 px-4">
            <button
              onClick={() => setSidebarOpen(true)}
              className="lg:hidden p-2 rounded-md text-gray-400 hover:text-gray-500 hover:bg-gray-100"
            >
              <Menu className="h-6 w-6" />
            </button>
            <div className="flex-1 lg:ml-0">
              <h1 className="text-2xl font-semibold text-gray-900">
                DevOps Credential Manager
              </h1>
            </div>
          </div>
        </header>

        {/* Page content */}
        <main className="flex-1 overflow-y-auto bg-gray-50">
          <div className="py-6">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
              {children}
            </div>
          </div>
        </main>
      </div>
    </div>
  )
}

export default Layout

