let apiUrl;

/* eslint-disable */
if (__PRODUCTION__) {
  apiUrl = window.location.origin;
} else {
  apiUrl = 'https://saw.bda.poc.velocity-va.synchronoss.net';
}
/* eslint-enable */

export default {
  login: {
    url: `${apiUrl}/saw-security`,
    jwtKey: 'jwtToken'
  },
  api: {
    url: `${apiUrl}/api`
  }
};
