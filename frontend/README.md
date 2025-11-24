# ProCreds Frontend

A modern React frontend for the DevOps Platform Credential Manager built with Vite, React 18, and Tailwind CSS.

## 🚀 Quick Start

### Prerequisites
- Node.js 16+ 
- npm or yarn

### Installation & Development

```bash
# Install dependencies
npm install

# Start development server
npm run dev

# The app will be available at http://localhost:3000
```

### Production Build

```bash
# Build for production
npm run build

# Preview production build
npm run preview
```

## 🏗️ Architecture

### Tech Stack
- **React 18** - Latest React with concurrent features
- **Vite** - Fast build tool and dev server
- **Tailwind CSS** - Utility-first CSS framework
- **React Router** - Client-side routing
- **React Hook Form** - Form handling and validation
- **Axios** - HTTP client for API calls
- **Lucide React** - Beautiful icons
- **React Hot Toast** - Toast notifications

### Project Structure

```
src/
├── components/
│   ├── ui/                 # Reusable UI components
│   │   ├── Button.jsx
│   │   ├── Card.jsx
│   │   ├── Input.jsx
│   │   ├── Label.jsx
│   │   └── Textarea.jsx
│   ├── Layout.jsx          # Main layout with sidebar
│   ├── ConfigForm.jsx      # Generic form component
│   └── ConfigList.jsx      # Generic list/table component
├── pages/
│   ├── Dashboard.jsx       # Main dashboard
│   ├── github/            # GitHub platform pages
│   ├── bitbucket/         # Bitbucket platform pages
│   ├── gitlab/            # GitLab platform pages
│   ├── jenkins/           # Jenkins platform pages
│   ├── jira/              # Jira platform pages
│   ├── sonarqube/         # SonarQube platform pages
│   ├── kubernetes/        # Kubernetes platform pages
│   ├── aws-eks/           # AWS EKS platform pages
│   ├── azure-aks/         # Azure AKS platform pages
│   └── gcp-gke/           # GCP GKE platform pages
├── services/
│   └── api.js             # API service layer
├── utils/
│   └── cn.js              # Tailwind class utility
├── App.jsx                # Main app component with routing
├── main.jsx               # React 18 entry point
└── index.css              # Global styles with Tailwind
```

## 🎨 Features

### Responsive Design
- Mobile-first responsive layout
- Collapsible sidebar navigation
- Touch-friendly interface

### Platform Management
- **10 DevOps Platforms Supported:**
  - GitHub
  - Bitbucket  
  - GitLab
  - Jenkins
  - Jira
  - SonarQube
  - Kubernetes
  - AWS EKS
  - Azure AKS
  - GCP GKE

### CRUD Operations
- **List View:** Search, pagination, and bulk actions
- **Create Form:** Validation and field-specific inputs
- **Edit Form:** Pre-populated forms with update capability
- **Delete:** Confirmation dialogs and soft delete

### User Experience
- Real-time search across configurations
- Toast notifications for user feedback
- Loading states and error handling
- Form validation with helpful error messages

## 🔧 Development

### Component Patterns

#### Generic Components
The app uses generic, reusable components to maintain consistency:

- `ConfigForm` - Handles create/edit forms for all platforms
- `ConfigList` - Displays searchable, paginated lists
- UI components in `components/ui/` for consistent styling

#### Platform Pages
Each platform follows the same pattern:
- `PlatformList.jsx` - Lists all configurations
- `PlatformCreate.jsx` - Create new configuration  
- `PlatformEdit.jsx` - Edit existing configuration

### API Integration
- Centralized API service in `services/api.js`
- Automatic request/response interceptors
- Generic CRUD operations for all platforms
- Error handling and authentication

### Styling
- Tailwind CSS for utility-first styling
- Custom CSS variables for theming
- Consistent color scheme and spacing
- Dark mode ready (variables defined)

## 🌐 API Integration

The frontend expects a REST API running on `http://localhost:8080` with the following endpoints for each platform:

```
GET    /{platform}           # List all configurations
GET    /{platform}/{id}      # Get configuration by ID
POST   /{platform}           # Create new configuration
PUT    /{platform}/{id}      # Update configuration
DELETE /{platform}/{id}      # Delete configuration
```

## 📱 Browser Support

- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

## 🚀 Deployment

### Environment Variables
Create a `.env` file for environment-specific configuration:

```env
VITE_API_BASE_URL=http://localhost:8080
VITE_APP_TITLE=ProCreds
```

### Build Commands
```bash
# Development
npm run dev

# Production build
npm run build

# Preview production build locally
npm run preview
```

The build output will be in the `dist/` directory, ready for deployment to any static hosting service.

## 🔮 Future Enhancements

- [ ] Complete implementation of all platform forms
- [ ] Advanced search and filtering
- [ ] Bulk operations (import/export)
- [ ] Configuration templates
- [ ] Audit logs and history
- [ ] Role-based access control
- [ ] Dark mode toggle
- [ ] Offline support with service workers

## 🤝 Contributing

1. Follow the existing component patterns
2. Use TypeScript for new components (optional)
3. Maintain responsive design principles
4. Add proper error handling
5. Include loading states for async operations

## 📄 License

This project is part of the ProCreds DevOps Credential Manager.

