#!/bin/bash

# Define platforms and their icons
declare -A platforms=(
    ["Jenkins"]="Wrench"
    ["Jira"]="Bug"
    ["SonarQube"]="Shield"
    ["Kubernetes"]="Server"
    ["AwsEks"]="Cloud"
    ["AzureAks"]="Cloud"
    ["GcpGke"]="Cloud"
)

declare -A paths=(
    ["Jenkins"]="jenkins"
    ["Jira"]="jira"
    ["SonarQube"]="sonarqube"
    ["Kubernetes"]="kubernetes"
    ["AwsEks"]="aws-eks"
    ["AzureAks"]="azure-aks"
    ["GcpGke"]="gcp-gke"
)

declare -A names=(
    ["Jenkins"]="Jenkins"
    ["Jira"]="Jira"
    ["SonarQube"]="SonarQube"
    ["Kubernetes"]="Kubernetes"
    ["AwsEks"]="AWS EKS"
    ["AzureAks"]="Azure AKS"
    ["GcpGke"]="GCP GKE"
)

# Create components for each platform
for platform in "${!platforms[@]}"; do
    icon="${platforms[$platform]}"
    path="${paths[$platform]}"
    name="${names[$platform]}"
    
    # Create List component
    cat > "frontend/src/components/${platform}/${platform}ListPage.js" << EOL
import React from 'react';
import { Link } from 'react-router-dom';
import { Plus, ${icon} } from 'lucide-react';

const ${platform}ListPage = () => {
  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 flex items-center">
            <${icon} className="mr-3 h-8 w-8 text-gray-700" />
            ${name} Configurations
          </h1>
          <p className="mt-1 text-sm text-gray-600">
            Manage your ${name} configurations and settings
          </p>
        </div>
        <Link to="/${path}/create" className="btn-primary flex items-center">
          <Plus className="mr-2 h-4 w-4" />
          Add ${name} Config
        </Link>
      </div>

      <div className="card p-8 text-center">
        <${icon} className="mx-auto h-12 w-12 text-gray-400" />
        <h3 className="mt-4 text-lg font-medium text-gray-900">${name} Configuration</h3>
        <p className="mt-2 text-sm text-gray-600">
          This feature is coming soon. The backend API is ready, and the frontend components are being developed.
        </p>
      </div>
    </div>
  );
};

export default ${platform}ListPage;
EOL

    # Create Form component
    cat > "frontend/src/components/${platform}/${platform}FormPage.js" << EOL
import React from 'react';
import { Link } from 'react-router-dom';
import { ArrowLeft, ${icon} } from 'lucide-react';

const ${platform}FormPage = () => {
  return (
    <div className="space-y-6">
      <div className="flex items-center space-x-4">
        <Link to="/${path}" className="text-gray-600 hover:text-gray-900">
          <ArrowLeft className="h-6 w-6" />
        </Link>
        <div>
          <h1 className="text-2xl font-bold text-gray-900 flex items-center">
            <${icon} className="mr-3 h-8 w-8 text-gray-700" />
            ${name} Configuration Form
          </h1>
        </div>
      </div>

      <div className="card p-8 text-center">
        <${icon} className="mx-auto h-12 w-12 text-gray-400" />
        <h3 className="mt-4 text-lg font-medium text-gray-900">Form Coming Soon</h3>
        <p className="mt-2 text-sm text-gray-600">
          The ${name} configuration form is being developed.
        </p>
      </div>
    </div>
  );
};

export default ${platform}FormPage;
EOL

    # Create View component
    cat > "frontend/src/components/${platform}/${platform}ViewPage.js" << EOL
import React from 'react';
import { Link } from 'react-router-dom';
import { ArrowLeft, ${icon} } from 'lucide-react';

const ${platform}ViewPage = () => {
  return (
    <div className="space-y-6">
      <div className="flex items-center space-x-4">
        <Link to="/${path}" className="text-gray-600 hover:text-gray-900">
          <ArrowLeft className="h-6 w-6" />
        </Link>
        <div>
          <h1 className="text-2xl font-bold text-gray-900 flex items-center">
            <${icon} className="mr-3 h-8 w-8 text-gray-700" />
            ${name} Configuration Details
          </h1>
        </div>
      </div>

      <div className="card p-8 text-center">
        <${icon} className="mx-auto h-12 w-12 text-gray-400" />
        <h3 className="mt-4 text-lg font-medium text-gray-900">View Coming Soon</h3>
        <p className="mt-2 text-sm text-gray-600">
          The ${name} configuration view is being developed.
        </p>
      </div>
    </div>
  );
};

export default ${platform}ViewPage;
EOL

done

echo "All placeholder components created successfully!"
