import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { 
  Github, 
  GitBranch, 
  Settings, 
  Bug, 
  Shield, 
  Cloud,
  Plus,
  TrendingUp,
  Server,
  Users
} from 'lucide-react';
import { githubService } from '../services/githubService';

const Dashboard = () => {
  const [stats, setStats] = useState({
    totalConfigurations: 0,
    githubConfigs: 0,
    recentActivity: []
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      // Fetch GitHub configurations for stats
      const githubData = await githubService.getAll({ page: 0, size: 5 });
      
      setStats({
        totalConfigurations: githubData.totalElements || 0,
        githubConfigs: githubData.totalElements || 0,
        recentActivity: githubData.content || []
      });
    } catch (error) {
      console.error('Error fetching dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  const platforms = [
    {
      name: 'GitHub',
      href: '/github',
      icon: Github,
      description: 'Manage GitHub credentials and organization settings',
      color: 'bg-gray-900',
      count: stats.githubConfigs,
      implemented: true
    },
    {
      name: 'Bitbucket',
      href: '/bitbucket',
      icon: GitBranch,
      description: 'Username and app password management',
      color: 'bg-blue-600',
      count: 0,
      implemented: false
    },
    {
      name: 'GitLab',
      href: '/gitlab',
      icon: GitBranch,
      description: 'Personal access token configuration',
      color: 'bg-orange-600',
      count: 0,
      implemented: false
    },
    {
      name: 'Jenkins',
      href: '/jenkins',
      icon: Settings,
      description: 'Server URL and API token management',
      color: 'bg-blue-800',
      count: 0,
      implemented: false
    },
    {
      name: 'Jira',
      href: '/jira',
      icon: Bug,
      description: 'Project configuration and API credentials',
      color: 'bg-blue-500',
      count: 0,
      implemented: false
    },
    {
      name: 'SonarQube',
      href: '/sonarqube',
      icon: Shield,
      description: 'Server and organization settings',
      color: 'bg-green-600',
      count: 0,
      implemented: false
    },
    {
      name: 'Kubernetes',
      href: '/kubernetes',
      icon: Cloud,
      description: 'Generic cluster configurations',
      color: 'bg-purple-600',
      count: 0,
      implemented: false
    },
    {
      name: 'AWS EKS',
      href: '/aws-eks',
      icon: Cloud,
      description: 'Amazon Elastic Kubernetes Service',
      color: 'bg-yellow-600',
      count: 0,
      implemented: false
    },
    {
      name: 'Azure AKS',
      href: '/azure-aks',
      icon: Cloud,
      description: 'Azure Kubernetes Service',
      color: 'bg-blue-600',
      count: 0,
      implemented: false
    },
    {
      name: 'GCP GKE',
      href: '/gcp-gke',
      icon: Cloud,
      description: 'Google Kubernetes Engine',
      color: 'bg-red-600',
      count: 0,
      implemented: false
    }
  ];

  const quickStats = [
    {
      name: 'Total Configurations',
      value: stats.totalConfigurations,
      icon: Server,
      color: 'text-blue-600',
      bgColor: 'bg-blue-100'
    },
    {
      name: 'Active Platforms',
      value: platforms.filter(p => p.count > 0).length,
      icon: TrendingUp,
      color: 'text-green-600',
      bgColor: 'bg-green-100'
    },
    {
      name: 'GitHub Configs',
      value: stats.githubConfigs,
      icon: Github,
      color: 'text-gray-600',
      bgColor: 'bg-gray-100'
    },
    {
      name: 'Implemented',
      value: platforms.filter(p => p.implemented).length,
      icon: Users,
      color: 'text-purple-600',
      bgColor: 'bg-purple-100'
    }
  ];

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="md:flex md:items-center md:justify-between">
        <div className="flex-1 min-w-0">
          <h2 className="text-2xl font-bold leading-7 text-gray-900 sm:text-3xl sm:truncate">
            Dashboard
          </h2>
          <p className="mt-1 text-sm text-gray-500">
            Manage your DevOps platform credentials and configurations
          </p>
        </div>
        <div className="mt-4 flex md:mt-0 md:ml-4">
          <Link
            to="/github/create"
            className="btn-primary inline-flex items-center"
          >
            <Plus className="h-4 w-4 mr-2" />
            Add Configuration
          </Link>
        </div>
      </div>

      {/* Quick Stats */}
      <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
        {quickStats.map((stat) => (
          <div key={stat.name} className="card">
            <div className="flex items-center">
              <div className={`flex-shrink-0 p-3 rounded-lg ${stat.bgColor}`}>
                <stat.icon className={`h-6 w-6 ${stat.color}`} />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-500">{stat.name}</p>
                <p className="text-2xl font-semibold text-gray-900">{stat.value}</p>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Platform Grid */}
      <div>
        <h3 className="text-lg font-medium text-gray-900 mb-4">Supported Platforms</h3>
        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
          {platforms.map((platform) => (
            <Link
              key={platform.name}
              to={platform.href}
              className="card hover:shadow-md transition-shadow duration-200 group"
            >
              <div className="flex items-center justify-between">
                <div className="flex items-center">
                  <div className={`flex-shrink-0 p-3 rounded-lg ${platform.color}`}>
                    <platform.icon className="h-6 w-6 text-white" />
                  </div>
                  <div className="ml-4">
                    <h4 className="text-lg font-medium text-gray-900 group-hover:text-primary-600">
                      {platform.name}
                    </h4>
                    <p className="text-sm text-gray-500">{platform.description}</p>
                  </div>
                </div>
                <div className="text-right">
                  <div className="text-2xl font-semibold text-gray-900">{platform.count}</div>
                  <div className="text-xs text-gray-500">configs</div>
                </div>
              </div>
              <div className="mt-4 flex items-center justify-between">
                <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                  platform.implemented 
                    ? 'bg-green-100 text-green-800' 
                    : 'bg-yellow-100 text-yellow-800'
                }`}>
                  {platform.implemented ? 'Implemented' : 'Coming Soon'}
                </span>
                <span className="text-sm text-primary-600 group-hover:text-primary-700">
                  View details →
                </span>
              </div>
            </Link>
          ))}
        </div>
      </div>

      {/* Recent Activity */}
      {stats.recentActivity.length > 0 && (
        <div>
          <h3 className="text-lg font-medium text-gray-900 mb-4">Recent Activity</h3>
          <div className="card">
            <div className="space-y-4">
              {stats.recentActivity.map((config) => (
                <div key={config.id} className="flex items-center justify-between py-3 border-b border-gray-200 last:border-b-0">
                  <div className="flex items-center">
                    <Github className="h-5 w-5 text-gray-400 mr-3" />
                    <div>
                      <p className="text-sm font-medium text-gray-900">{config.accountName}</p>
                      <p className="text-xs text-gray-500">
                        {config.organization ? `Organization: ${config.organization}` : 'Personal account'}
                      </p>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className="text-xs text-gray-500">
                      {new Date(config.updatedAt).toLocaleDateString()}
                    </p>
                    <Link
                      to={`/github/view/${config.id}`}
                      className="text-xs text-primary-600 hover:text-primary-700"
                    >
                      View details
                    </Link>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Dashboard;

