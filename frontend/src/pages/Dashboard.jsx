import React from 'react'
import { Link } from 'react-router-dom'
import { 
  Github, 
  GitBranch, 
  Gitlab, 
  Wrench, 
  Bug, 
  Shield, 
  Server,
  Cloud,
  Plus
} from 'lucide-react'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/Card'
import { Button } from '../components/ui/Button'

const Dashboard = () => {
  const platforms = [
    {
      name: 'GitHub',
      description: 'Manage GitHub repositories and access tokens',
      icon: Github,
      href: '/github',
      color: 'bg-gray-900 text-white',
      count: 0
    },
    {
      name: 'Bitbucket',
      description: 'Configure Bitbucket workspaces and app passwords',
      icon: GitBranch,
      href: '/bitbucket',
      color: 'bg-blue-600 text-white',
      count: 0
    },
    {
      name: 'GitLab',
      description: 'Set up GitLab projects and personal access tokens',
      icon: Gitlab,
      href: '/gitlab',
      color: 'bg-orange-600 text-white',
      count: 0
    },
    {
      name: 'Jenkins',
      description: 'Configure Jenkins servers and API tokens',
      icon: Wrench,
      href: '/jenkins',
      color: 'bg-red-600 text-white',
      count: 0
    },
    {
      name: 'Jira',
      description: 'Manage Jira projects and API authentication',
      icon: Bug,
      href: '/jira',
      color: 'bg-blue-500 text-white',
      count: 0
    },
    {
      name: 'SonarQube',
      description: 'Set up SonarQube servers and tokens',
      icon: Shield,
      href: '/sonarqube',
      color: 'bg-green-600 text-white',
      count: 0
    },
    {
      name: 'Kubernetes',
      description: 'Configure Kubernetes clusters and kubeconfig',
      icon: Server,
      href: '/kubernetes',
      color: 'bg-purple-600 text-white',
      count: 0
    },
    {
      name: 'AWS EKS',
      description: 'Manage AWS EKS clusters and credentials',
      icon: Cloud,
      href: '/aws-eks',
      color: 'bg-yellow-600 text-white',
      count: 0
    },
    {
      name: 'Azure AKS',
      description: 'Configure Azure AKS clusters and service principals',
      icon: Cloud,
      href: '/azure-aks',
      color: 'bg-blue-700 text-white',
      count: 0
    },
    {
      name: 'GCP GKE',
      description: 'Set up GCP GKE clusters and service accounts',
      icon: Cloud,
      href: '/gcp-gke',
      color: 'bg-red-500 text-white',
      count: 0
    }
  ]

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Dashboard</h1>
          <p className="mt-2 text-gray-600">
            Manage your DevOps platform credentials and configurations
          </p>
        </div>
      </div>

      {/* Stats Overview */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Total Platforms</CardTitle>
            <Server className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">10</div>
            <p className="text-xs text-muted-foreground">
              DevOps platforms supported
            </p>
          </CardContent>
        </Card>
        
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Configurations</CardTitle>
            <Shield className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">0</div>
            <p className="text-xs text-muted-foreground">
              Total configurations stored
            </p>
          </CardContent>
        </Card>
        
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Active Platforms</CardTitle>
            <Cloud className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">0</div>
            <p className="text-xs text-muted-foreground">
              Platforms with configurations
            </p>
          </CardContent>
        </Card>
        
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Security Status</CardTitle>
            <Shield className="h-4 w-4 text-green-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-green-600">Secure</div>
            <p className="text-xs text-muted-foreground">
              All credentials encrypted
            </p>
          </CardContent>
        </Card>
      </div>

      {/* Platform Cards */}
      <div>
        <h2 className="text-2xl font-bold text-gray-900 mb-6">DevOps Platforms</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {platforms.map((platform) => {
            const Icon = platform.icon
            return (
              <Card key={platform.name} className="hover:shadow-lg transition-shadow">
                <CardHeader>
                  <div className="flex items-center justify-between">
                    <div className={`p-2 rounded-lg ${platform.color}`}>
                      <Icon className="h-6 w-6" />
                    </div>
                    <span className="text-sm text-gray-500">
                      {platform.count} configs
                    </span>
                  </div>
                  <CardTitle className="text-xl">{platform.name}</CardTitle>
                  <CardDescription>{platform.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="flex space-x-2">
                    <Button asChild className="flex-1">
                      <Link to={platform.href}>
                        View Configurations
                      </Link>
                    </Button>
                    <Button asChild variant="outline" size="icon">
                      <Link to={`${platform.href}/create`}>
                        <Plus className="h-4 w-4" />
                      </Link>
                    </Button>
                  </div>
                </CardContent>
              </Card>
            )
          })}
        </div>
      </div>

      {/* Quick Actions */}
      <Card>
        <CardHeader>
          <CardTitle>Quick Actions</CardTitle>
          <CardDescription>
            Common tasks to get you started with credential management
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <Button asChild variant="outline" className="h-auto p-4">
              <Link to="/github/create" className="flex flex-col items-center space-y-2">
                <Github className="h-8 w-8" />
                <span>Add GitHub Token</span>
              </Link>
            </Button>
            <Button asChild variant="outline" className="h-auto p-4">
              <Link to="/jenkins/create" className="flex flex-col items-center space-y-2">
                <Wrench className="h-8 w-8" />
                <span>Configure Jenkins</span>
              </Link>
            </Button>
            <Button asChild variant="outline" className="h-auto p-4">
              <Link to="/kubernetes/create" className="flex flex-col items-center space-y-2">
                <Server className="h-8 w-8" />
                <span>Add Kubernetes Cluster</span>
              </Link>
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}

export default Dashboard

