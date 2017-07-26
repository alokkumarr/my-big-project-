/* on tap handler for error toast message. Used to expand a more detailed
 view of error */
function openErrorDetails(dialog, error) {
  dialog.show({
    template: `<error-detail error-obj="errorObj"></error-detail>`,
    controller: scope => {
      scope.errorObj = error;
    },
    controllerAs: '$ctrl',
    autoWrap: false,
    focusOnOpen: false,
    multiple: true,
    clickOutsideToClose: true
  });
}

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
        const $mdDialog = $injector.get('$mdDialog');
        const $translate = $injector.get('$translate');
        const ErrorDetail = $injector.get('ErrorDetail');

        $translate(generalErrorMsgKey).then(generalErrorMsg => {
          const msg = ErrorDetail.getTitle(error, generalErrorMsg);
          toastMessage.error('Tap to view details', msg, {
            tapToDismiss: true,
            onTap: () => openErrorDetails($mdDialog, error)
          });
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
