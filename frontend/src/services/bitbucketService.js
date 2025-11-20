import { ApiService } from './api';
import api from './api';

class BitbucketService extends ApiService {
  constructor() {
    super('/bitbucket');
  }

  async getByUsername(username) {
    const response = await api.get(`${this.baseEndpoint}/username/${username}`);
    return response.data;
  }

  async existsByUsername(username) {
    const response = await api.get(`${this.baseEndpoint}/exists/${username}`);
    return response.data;
  }
}

export const bitbucketService = new BitbucketService();

