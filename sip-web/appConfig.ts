import { environment } from './src/environments/environment';

let apiUrl;
const endpoints = {
  security: 'security',
  services: 'services',
  productModules: 'pm',
  weatherData: 'weatherdata',
  staticMap: 'staticmap'
};

/* eslint-disable */
if (environment.production) {
  apiUrl = window.location.origin;
} else {
  // Note: To run against a local Docker environment, use localhost
  apiUrl = 'https://sip-iot-dev-us.synchronoss.net';
  // apiUrl = 'http://saw-rd611.eng-sip.dev01.us-west.sncrcloud.net';
  // apiUrl = 'https://saw-cert-sip-vaste.sncrcorp.net';
  // apiUrl = 'http://saw-rd601.ana.dev.vaste.sncrcorp.net';
  // apiUrl = 'http://saw-rd602.ana.dev.vaste.sncrcorp.net';
  // apiUrl = 'https://saw-pac-sip-vaste.sncrcorp.net';
}
/* eslint-enable */
apiUrl += '/sip';

export default {
  login: {
    url: `${apiUrl}/${endpoints.security}`,
    jwtKey: 'jwtToken'
  },
  api: {
    url: `${apiUrl}/${endpoints.services}`,
    productModulesUrl: `${apiUrl}/${endpoints.productModules}`,
    weatherDataUrl: `${apiUrl}/${endpoints.weatherData}`,
    staticMapUrl: `${apiUrl}/${endpoints.staticMap}`
  }
};
