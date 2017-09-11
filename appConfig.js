let apiUrl;
const endpoints = {security: 'security', services: 'services'};

/* eslint-disable */
if (__PRODUCTION__) {
  apiUrl = window.location.origin;
} else {
  apiUrl = 'https://saw.bda.poc.velocity-va.synchronoss.net';
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
