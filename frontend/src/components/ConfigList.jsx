import React, { useState } from 'react'
import { Link } from 'react-router-dom'
import { Search, Plus, Edit, Trash2, Eye, Wifi } from 'lucide-react'
import { Button } from './ui/Button'
import { Input } from './ui/Input'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/Card'
import { Badge } from './ui/Badge'
import TestConnectionButton from './TestConnectionButton'

const ConfigList = ({ 
  title, 
  description, 
  data = [], 
  columns = [], 
  onDelete, 
  onTestConnection,
  platform,
  createPath, 
  editPath, 
  viewPath,
  isLoading = false,
  searchPlaceholder = "Search configurations...",
  showTestConnection = false
}) => {
  const [searchTerm, setSearchTerm] = useState('')
  const [currentPage, setCurrentPage] = useState(1)
  const itemsPerPage = 10

  const filteredData = data.filter(item =>
    columns.some(column => 
      String(item[column.key] || '').toLowerCase().includes(searchTerm.toLowerCase())
    )
  )

  const totalPages = Math.ceil(filteredData.length / itemsPerPage)
  const startIndex = (currentPage - 1) * itemsPerPage
  const paginatedData = filteredData.slice(startIndex, startIndex + itemsPerPage)

  const formatValue = (value, column) => {
    if (column.type === 'date' && value) {
      return new Date(value).toLocaleDateString()
    }
    if (column.type === 'password') {
      return '••••••••'
    }
    if (column.type === 'truncate' && value && value.length > 50) {
      return value.substring(0, 50) + '...'
    }
    return value || '-'
  }

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">{title}</h1>
          <p className="mt-2 text-gray-600">{description}</p>
        </div>
        <Button asChild>
          <Link to={createPath}>
            <Plus className="h-4 w-4 mr-2" />
            Add Configuration
          </Link>
        </Button>
      </div>

      {/* Search */}
      <Card>
        <CardContent className="pt-6">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
            <Input
              type="text"
              placeholder={searchPlaceholder}
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="pl-10"
            />
          </div>
        </CardContent>
      </Card>

      {/* Results */}
      <Card>
        <CardHeader>
          <CardTitle>Configurations ({filteredData.length})</CardTitle>
          <CardDescription>
            {filteredData.length === 0 
              ? "No configurations found. Create your first configuration to get started."
              : `Showing ${paginatedData.length} of ${filteredData.length} configurations`
            }
          </CardDescription>
        </CardHeader>
        <CardContent>
          {filteredData.length === 0 ? (
            <div className="text-center py-12">
              <div className="mx-auto h-12 w-12 text-gray-400">
                <Search className="h-12 w-12" />
              </div>
              <h3 className="mt-2 text-sm font-medium text-gray-900">No configurations</h3>
              <p className="mt-1 text-sm text-gray-500">
                Get started by creating a new configuration.
              </p>
              <div className="mt-6">
                <Button asChild>
                  <Link to={createPath}>
                    <Plus className="h-4 w-4 mr-2" />
                    Add Configuration
                  </Link>
                </Button>
              </div>
            </div>
          ) : (
            <>
              {/* Table */}
              <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200">
                  <thead className="bg-gray-50">
                    <tr>
                      {columns.map((column) => (
                        <th
                          key={column.key}
                          className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
                        >
                          {column.label}
                        </th>
                      ))}
                      <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Actions
                      </th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {paginatedData.map((item) => (
                      <tr key={item.id} className="hover:bg-gray-50">
                        {columns.map((column) => (
                          <td key={column.key} className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                            {formatValue(item[column.key], column)}
                          </td>
                        ))}
                        <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                          <div className="flex items-center justify-end space-x-2">
                            {showTestConnection && onTestConnection && (
                              <Button
                                variant="ghost"
                                size="sm"
                                onClick={() => onTestConnection(platform, item)}
                                className="text-blue-600 hover:text-blue-900"
                                title="Test Connection"
                              >
                                <Wifi className="h-4 w-4" />
                              </Button>
                            )}
                            {viewPath && (
                              <Button asChild variant="ghost" size="sm" title="View Details">
                                <Link to={viewPath.replace(':id', item.id)}>
                                  <Eye className="h-4 w-4" />
                                </Link>
                              </Button>
                            )}
                            <Button asChild variant="ghost" size="sm" title="Edit Configuration">
                              <Link to={editPath.replace(':id', item.id)}>
                                <Edit className="h-4 w-4" />
                              </Link>
                            </Button>
                            <Button
                              variant="ghost"
                              size="sm"
                              onClick={() => onDelete && onDelete(item.id)}
                              className="text-red-600 hover:text-red-900"
                              title="Delete Configuration"
                            >
                              <Trash2 className="h-4 w-4" />
                            </Button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              {/* Pagination */}
              {totalPages > 1 && (
                <div className="flex items-center justify-between mt-6">
                  <div className="text-sm text-gray-700">
                    Showing {startIndex + 1} to {Math.min(startIndex + itemsPerPage, filteredData.length)} of {filteredData.length} results
                  </div>
                  <div className="flex space-x-2">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => setCurrentPage(prev => Math.max(prev - 1, 1))}
                      disabled={currentPage === 1}
                    >
                      Previous
                    </Button>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => setCurrentPage(prev => Math.min(prev + 1, totalPages))}
                      disabled={currentPage === totalPages}
                    >
                      Next
                    </Button>
                  </div>
                </div>
              )}
            </>
          )}
        </CardContent>
      </Card>
    </div>
  )
}

export default ConfigList
