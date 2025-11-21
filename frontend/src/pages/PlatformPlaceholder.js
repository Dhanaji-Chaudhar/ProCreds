import React from 'react';
import { Link } from 'react-router-dom';
import { 
  Construction, 
  Github, 
  ArrowRight,
  CheckCircle,
  Clock
} from 'lucide-react';

const PlatformPlaceholder = ({ platform }) => {
  const implementedFeatures = [
    'Full CRUD operations (Create, Read, Update, Delete)',
    'Search and pagination support',
    'Input validation with error handling',
    'Responsive UI with Tailwind CSS',
    'REST API with Swagger documentation',
    'MongoDB persistence with audit trails',
    'Authentication and security',
    'Real-time form validation'
  ];

  const upcomingFeatures = [
    'Platform-specific field validation',
    'Bulk import/export functionality',
    'Configuration templates',
    'Integration testing',
    'Advanced search filters',
    'Configuration backup/restore',
    'Role-based access control',
    'API rate limiting'
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="text-center">
        <div className="flex items-center justify-center mb-4">
          <Construction className="h-12 w-12 text-yellow-500" />
        </div>
        <h2 className="text-3xl font-bold text-gray-900">{platform}</h2>
        <p className="mt-2 text-lg text-gray-600">
          Coming Soon - Implementation in Progress
        </p>
      </div>

      {/* Status Card */}
      <div className="card max-w-4xl mx-auto">
        <div className="text-center mb-8">
          <div className="inline-flex items-center px-4 py-2 rounded-full text-sm font-medium bg-yellow-100 text-yellow-800">
            <Clock className="h-4 w-4 mr-2" />
            Under Development
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* Implemented Features */}
          <div>
            <h3 className="text-lg font-semibold text-gray-900 mb-4 flex items-center">
              <CheckCircle className="h-5 w-5 text-green-500 mr-2" />
              Already Implemented (GitHub Template)
            </h3>
            <ul className="space-y-2">
              {implementedFeatures.map((feature, index) => (
                <li key={index} className="flex items-start">
                  <CheckCircle className="h-4 w-4 text-green-500 mr-2 mt-0.5 flex-shrink-0" />
                  <span className="text-sm text-gray-700">{feature}</span>
                </li>
              ))}
            </ul>
          </div>

          {/* Upcoming Features */}
          <div>
            <h3 className="text-lg font-semibold text-gray-900 mb-4 flex items-center">
              <Clock className="h-5 w-5 text-yellow-500 mr-2" />
              Upcoming for {platform}
            </h3>
            <ul className="space-y-2">
              {upcomingFeatures.map((feature, index) => (
                <li key={index} className="flex items-start">
                  <Clock className="h-4 w-4 text-yellow-500 mr-2 mt-0.5 flex-shrink-0" />
                  <span className="text-sm text-gray-700">{feature}</span>
                </li>
              ))}
            </ul>
          </div>
        </div>
      </div>

      {/* Implementation Progress */}
      <div className="card max-w-4xl mx-auto">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">Implementation Progress</h3>
        
        <div className="space-y-4">
          <div>
            <div className="flex justify-between text-sm text-gray-600 mb-1">
              <span>Backend Architecture</span>
              <span>100%</span>
            </div>
            <div className="w-full bg-gray-200 rounded-full h-2">
              <div className="bg-green-500 h-2 rounded-full" style={{ width: '100%' }}></div>
            </div>
          </div>

          <div>
            <div className="flex justify-between text-sm text-gray-600 mb-1">
              <span>Frontend Components</span>
              <span>100%</span>
            </div>
            <div className="w-full bg-gray-200 rounded-full h-2">
              <div className="bg-green-500 h-2 rounded-full" style={{ width: '100%' }}></div>
            </div>
          </div>

          <div>
            <div className="flex justify-between text-sm text-gray-600 mb-1">
              <span>{platform} Specific Implementation</span>
              <span>0%</span>
            </div>
            <div className="w-full bg-gray-200 rounded-full h-2">
              <div className="bg-yellow-500 h-2 rounded-full" style={{ width: '5%' }}></div>
            </div>
          </div>

          <div>
            <div className="flex justify-between text-sm text-gray-600 mb-1">
              <span>Testing & Documentation</span>
              <span>0%</span>
            </div>
            <div className="w-full bg-gray-200 rounded-full h-2">
              <div className="bg-gray-300 h-2 rounded-full" style={{ width: '0%' }}></div>
            </div>
          </div>
        </div>
      </div>

      {/* GitHub Example */}
      <div className="card max-w-4xl mx-auto">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">See the GitHub Implementation</h3>
        <p className="text-gray-600 mb-6">
          The GitHub platform is fully implemented and serves as the template for all other platforms. 
          You can explore the complete functionality including CRUD operations, search, validation, and more.
        </p>
        
        <div className="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
          <div className="flex items-center">
            <Github className="h-8 w-8 text-gray-900 mr-3" />
            <div>
              <h4 className="font-medium text-gray-900">GitHub Configuration</h4>
              <p className="text-sm text-gray-500">Fully implemented with all features</p>
            </div>
          </div>
          <Link
            to="/github"
            className="btn-primary inline-flex items-center"
          >
            View GitHub
            <ArrowRight className="h-4 w-4 ml-2" />
          </Link>
        </div>
      </div>

      {/* Technical Details */}
      <div className="card max-w-4xl mx-auto">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">Technical Implementation</h3>
        <div className="prose prose-sm text-gray-600">
          <p>
            Each platform will follow the same architectural pattern established by the GitHub implementation:
          </p>
          <ul>
            <li><strong>Backend:</strong> Spring Boot entity, DTO, repository, service, and controller</li>
            <li><strong>Frontend:</strong> React components for list, create, edit, and view operations</li>
            <li><strong>Database:</strong> MongoDB collection with platform-specific fields and indexes</li>
            <li><strong>Validation:</strong> Both client-side and server-side validation rules</li>
            <li><strong>API:</strong> RESTful endpoints with comprehensive Swagger documentation</li>
          </ul>
          <p>
            The modular design ensures consistent user experience across all platforms while allowing 
            for platform-specific customizations and field requirements.
          </p>
        </div>
      </div>

      {/* Call to Action */}
      <div className="text-center">
        <Link
          to="/"
          className="btn-secondary inline-flex items-center"
        >
          <ArrowRight className="h-4 w-4 mr-2 rotate-180" />
          Back to Dashboard
        </Link>
      </div>
    </div>
  );
};

export default PlatformPlaceholder;

