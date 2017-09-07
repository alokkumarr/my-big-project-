let apiUrl;
let endpoints;

/* eslint-disable */
if (__PRODUCTION__) {
  apiUrl = window.location.origin;
} else {
  apiUrl = 'https://saw.bda.poc.velocity-va.synchronoss.net';
}
/* eslint-enable */

/* Use old endpoints for velocity environments. It's difficult
   to configure/update the new ones for this */
if (/velocity-va/.test(apiUrl)) {
  endpoints = {security: 'saw-security', services: 'api'};
} else {
  endpoints = {security: 'security', services: 'services'};
}

export default {
  login: {
    url: `${apiUrl}/${endpoints.security}`,
    jwtKey: 'jwtToken'
  },
  api: {
    url: `${apiUrl}/${endpoints.services}`
  }
};
