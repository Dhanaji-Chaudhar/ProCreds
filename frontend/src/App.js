import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import Layout from './components/Layout';
import Dashboard from './pages/Dashboard';
import GitHubList from './pages/github/GitHubList';
import GitHubCreate from './pages/github/GitHubCreate';
import GitHubEdit from './pages/github/GitHubEdit';
import GitHubView from './pages/github/GitHubView';
import PlatformPlaceholder from './pages/PlatformPlaceholder';

function App() {
  return (
    <Router>
      <div className="App">
        <Toaster 
          position="top-right"
          toastOptions={{
            duration: 4000,
            style: {
              background: '#363636',
              color: '#fff',
            },
            success: {
              duration: 3000,
              theme: {
                primary: '#4aed88',
              },
            },
          }}
        />
        <Layout>
          <Routes>
            {/* Dashboard */}
            <Route path="/" element={<Dashboard />} />
            
            {/* GitHub Routes */}
            <Route path="/github" element={<GitHubList />} />
            <Route path="/github/create" element={<GitHubCreate />} />
            <Route path="/github/edit/:id" element={<GitHubEdit />} />
            <Route path="/github/view/:id" element={<GitHubView />} />
            
            {/* Placeholder routes for other platforms */}
            <Route path="/bitbucket" element={<PlatformPlaceholder platform="Bitbucket" />} />
            <Route path="/gitlab" element={<PlatformPlaceholder platform="GitLab" />} />
            <Route path="/jenkins" element={<PlatformPlaceholder platform="Jenkins" />} />
            <Route path="/jira" element={<PlatformPlaceholder platform="Jira" />} />
            <Route path="/sonarqube" element={<PlatformPlaceholder platform="SonarQube" />} />
            <Route path="/kubernetes" element={<PlatformPlaceholder platform="Kubernetes" />} />
            <Route path="/aws-eks" element={<PlatformPlaceholder platform="AWS EKS" />} />
            <Route path="/azure-aks" element={<PlatformPlaceholder platform="Azure AKS" />} />
            <Route path="/gcp-gke" element={<PlatformPlaceholder platform="GCP GKE" />} />
            
            {/* Catch all route */}
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </Layout>
      </div>
    </Router>
  );
}

export default App;

