let apiUrl;
const endpoints = {security: 'security', services: 'services'};

/* eslint-disable */
if (__PRODUCTION__) {
  apiUrl = window.location.origin;
} else {
  // Note: To run against a local Docker environment, use localhost
  apiUrl = 'http://localhost';
  // apiUrl = 'https://sawdev-bda-velocity-vacum-np.sncrcorp.net';
}
/* eslint-enable */

export default {
  login: {
    url: `${apiUrl}/${endpoints.security}`,
    jwtKey: 'jwtToken'
  },
  api: {
    url: `${apiUrl}/${endpoints.services}`
  }
};
