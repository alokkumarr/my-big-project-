import { environment } from './src/environments/environment';

let apiUrl;
const endpoints = {security: 'security', services: 'services'};

/* eslint-disable */
if (environment.production) {
  apiUrl = window.location.origin;
} else {
  // Note: To run against a local Docker environment, use localhost
  // apiUrl = 'http://saw-rd601.ana.dev.vaste.sncrcorp.net';
  // apiUrl = 'http://saw-rd602.ana.dev.vaste.sncrcorp.net';
  apiUrl = 'http://54.147.49.75';
}
/* eslint-enable */
apiUrl += '/saw';

export default {
  login: {
    url: `${apiUrl}/${endpoints.security}`,
    jwtKey: 'jwtToken'
  },
  api: {
    url: `${apiUrl}/${endpoints.services}`
  }
};
