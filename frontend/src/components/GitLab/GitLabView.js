import React from 'react';
import { Link } from 'react-router-dom';
import { ArrowLeft, GitMerge } from 'lucide-react';

const GitLabView = () => {
  return (
    <div className="space-y-6">
      <div className="flex items-center space-x-4">
        <Link to="/gitlab" className="text-gray-600 hover:text-gray-900">
          <ArrowLeft className="h-6 w-6" />
        </Link>
        <div>
          <h1 className="text-2xl font-bold text-gray-900 flex items-center">
            <GitMerge className="mr-3 h-8 w-8 text-gray-700" />
            GitLab Configuration Details
          </h1>
        </div>
      </div>

      <div className="card p-8 text-center">
        <GitMerge className="mx-auto h-12 w-12 text-gray-400" />
        <h3 className="mt-4 text-lg font-medium text-gray-900">View Coming Soon</h3>
        <p className="mt-2 text-sm text-gray-600">
          The GitLab configuration view is being developed.
        </p>
      </div>
    </div>
  );
};

export default GitLabView;

