import React from 'react';
import { Link } from 'react-router-dom';
import { Plus, Shield } from 'lucide-react';

const SonarQubeListPage = () => {
  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 flex items-center">
            <Shield className="mr-3 h-8 w-8 text-gray-700" />
            SonarQube Configurations
          </h1>
          <p className="mt-1 text-sm text-gray-600">
            Manage your SonarQube configurations and settings
          </p>
        </div>
        <Link to="/sonarqube/create" className="btn-primary flex items-center">
          <Plus className="mr-2 h-4 w-4" />
          Add SonarQube Config
        </Link>
      </div>

      <div className="card p-8 text-center">
        <Shield className="mx-auto h-12 w-12 text-gray-400" />
        <h3 className="mt-4 text-lg font-medium text-gray-900">SonarQube Configuration</h3>
        <p className="mt-2 text-sm text-gray-600">
          This feature is coming soon. The backend API is ready, and the frontend components are being developed.
        </p>
      </div>
    </div>
  );
};

export default SonarQubeListPage;
