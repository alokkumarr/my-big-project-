export function interceptor($httpProvider) {
  'ngInject';
  /* eslint-disable */
  $httpProvider.interceptors.push(function toastInterceptor($injector) {
    'ngInject';
    return {
      responseError: error => {
        // need to use injetor because using the toastr service
        // causes a circular dependency with $http
        const generalErrorMsgKey = 'ERROR_OOPS_SERVER';
        const toastMessage = $injector.get('toastMessage');
        const $q = $injector.get('$q');
        const $translate = $injector.get('$translate');

        $translate(generalErrorMsgKey).then(generalErrorMsg => {
          toastMessage.error(generalErrorMsg);
        });

        return $q.reject(error);
      }
    };
  });

  /* Add jwt auth token to all requests if present */
  $httpProvider.interceptors.push(function authInterceptor($injector) {
    'ngInject';
    const JwtService = $injector.get('JwtService');
    const token = JwtService.get();

    return {
      request: function (config) {
        if (token) {
          config.headers['Authorization'] = `Bearer ${token}`;
        }

        return config;
      }
    };
  });
  /* eslint-enable */
}
