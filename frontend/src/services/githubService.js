import { ApiService } from './api';
import api from './api';

class GitHubService extends ApiService {
  constructor() {
    super('/github');
  }

  async getByAccountName(accountName) {
    const response = await api.get(`${this.baseEndpoint}/account/${accountName}`);
    return response.data;
  }

  async existsByAccountName(accountName) {
    const response = await api.get(`${this.baseEndpoint}/exists/${accountName}`);
    return response.data;
  }
}

export const githubService = new GitHubService();

