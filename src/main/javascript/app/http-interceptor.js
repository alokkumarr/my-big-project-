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
        const $translate = $injector.get('$translate');

        $translate(generalErrorMsgKey).then(generalErrorMsg => {
          toastMessage.error(generalErrorMsg);
        });

        return error;
      }
    };
  });
  /* eslint-enable */
}
