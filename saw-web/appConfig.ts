import { environment } from './src/environments/environment';

let apiUrl;
const endpoints = { security: 'security', services: 'services' };

/* eslint-disable */
if (environment.production) {
  apiUrl = window.location.origin;
} else {
  // Note: To run against a local Docker environment, use localhost
  apiUrl = 'http://saw01-rd-sip-vaste.sncrcorp.net';
}
/* eslint-enable */
apiUrl += '/saw';

export default {
  login: {
    url: `${apiUrl}/${endpoints.security}`,
    jwtKey: 'jwtToken'
  },
  api: {
    url: `${apiUrl}/${endpoints.services}`,
    pluginUrl: `${apiUrl}/web`
  }
};
