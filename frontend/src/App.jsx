import React from 'react'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import { Toaster } from 'react-hot-toast'
import Layout from './components/Layout'
import Dashboard from './pages/Dashboard'
import GitHubList from './pages/github/GitHubList'
import GitHubCreate from './pages/github/GitHubCreate'
import GitHubEdit from './pages/github/GitHubEdit'
import BitbucketList from './pages/bitbucket/BitbucketList'
import BitbucketCreate from './pages/bitbucket/BitbucketCreate'
import BitbucketEdit from './pages/bitbucket/BitbucketEdit'
import GitLabList from './pages/gitlab/GitLabList'
import GitLabCreate from './pages/gitlab/GitLabCreate'
import GitLabEdit from './pages/gitlab/GitLabEdit'
import JenkinsListPage from './pages/jenkins/JenkinsListPage'
import JenkinsCreatePage from './pages/jenkins/JenkinsCreatePage'
import JenkinsEditPage from './pages/jenkins/JenkinsEditPage'
import JiraListPage from './pages/jira/JiraListPage'
import JiraCreatePage from './pages/jira/JiraCreatePage'
import JiraEditPage from './pages/jira/JiraEditPage'
import SonarQubeListPage from './pages/sonarqube/SonarQubeListPage'
import SonarQubeCreatePage from './pages/sonarqube/SonarQubeCreatePage'
import SonarQubeEditPage from './pages/sonarqube/SonarQubeEditPage'
import KubernetesListPage from './pages/kubernetes/KubernetesListPage'
import KubernetesCreatePage from './pages/kubernetes/KubernetesCreatePage'
import KubernetesEditPage from './pages/kubernetes/KubernetesEditPage'
import AwsEksListPage from './pages/aws-eks/AwsEksListPage'
import AwsEksCreatePage from './pages/aws-eks/AwsEksCreatePage'
import AwsEksEditPage from './pages/aws-eks/AwsEksEditPage'
import AzureAksListPage from './pages/azure-aks/AzureAksListPage'
import AzureAksCreatePage from './pages/azure-aks/AzureAksCreatePage'
import AzureAksEditPage from './pages/azure-aks/AzureAksEditPage'
import GcpGkeListPage from './pages/gcp-gke/GcpGkeListPage'
import GcpGkeCreatePage from './pages/gcp-gke/GcpGkeCreatePage'
import GcpGkeEditPage from './pages/gcp-gke/GcpGkeEditPage'

function App() {
  return (
    <Router>
      <div className="min-h-screen bg-gray-50">
        <Layout>
          <Routes>
            {/* Dashboard */}
            <Route path="/" element={<Dashboard />} />
            
            {/* GitHub Routes */}
            <Route path="/github" element={<GitHubList />} />
            <Route path="/github/create" element={<GitHubCreate />} />
            <Route path="/github/edit/:id" element={<GitHubEdit />} />
            
            {/* Bitbucket Routes */}
            <Route path="/bitbucket" element={<BitbucketList />} />
            <Route path="/bitbucket/create" element={<BitbucketCreate />} />
            <Route path="/bitbucket/edit/:id" element={<BitbucketEdit />} />
            
            {/* GitLab Routes */}
            <Route path="/gitlab" element={<GitLabList />} />
            <Route path="/gitlab/create" element={<GitLabCreate />} />
            <Route path="/gitlab/edit/:id" element={<GitLabEdit />} />
            
            {/* Jenkins Routes */}
            <Route path="/jenkins" element={<JenkinsListPage />} />
            <Route path="/jenkins/create" element={<JenkinsCreatePage />} />
            <Route path="/jenkins/edit/:id" element={<JenkinsEditPage />} />
            
            {/* Jira Routes */}
            <Route path="/jira" element={<JiraListPage />} />
            <Route path="/jira/create" element={<JiraCreatePage />} />
            <Route path="/jira/edit/:id" element={<JiraEditPage />} />
            
            {/* SonarQube Routes */}
            <Route path="/sonarqube" element={<SonarQubeListPage />} />
            <Route path="/sonarqube/create" element={<SonarQubeCreatePage />} />
            <Route path="/sonarqube/edit/:id" element={<SonarQubeEditPage />} />
            
            {/* Kubernetes Routes */}
            <Route path="/kubernetes" element={<KubernetesListPage />} />
            <Route path="/kubernetes/create" element={<KubernetesCreatePage />} />
            <Route path="/kubernetes/edit/:id" element={<KubernetesEditPage />} />
            
            {/* AWS EKS Routes */}
            <Route path="/aws-eks" element={<AwsEksListPage />} />
            <Route path="/aws-eks/create" element={<AwsEksCreatePage />} />
            <Route path="/aws-eks/edit/:id" element={<AwsEksEditPage />} />
            
            {/* Azure AKS Routes */}
            <Route path="/azure-aks" element={<AzureAksListPage />} />
            <Route path="/azure-aks/create" element={<AzureAksCreatePage />} />
            <Route path="/azure-aks/edit/:id" element={<AzureAksEditPage />} />
            
            {/* GCP GKE Routes */}
            <Route path="/gcp-gke" element={<GcpGkeListPage />} />
            <Route path="/gcp-gke/create" element={<GcpGkeCreatePage />} />
            <Route path="/gcp-gke/edit/:id" element={<GcpGkeEditPage />} />
          </Routes>
        </Layout>
        <Toaster position="top-right" />
      </div>
    </Router>
  )
}

export default App

