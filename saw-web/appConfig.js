let apiUrl;
const endpoints = {security: 'security', services: 'services'};

/* eslint-disable */
if (__PRODUCTION__) {
  apiUrl = window.location.origin;
} else {
  // Note: To run against a local Docker environment, use localhost
  apiUrl = 'http://54.172.53.108';
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
