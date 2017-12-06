import * as get from 'lodash/get';

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
  /* tslint:disable */
  $httpProvider.interceptors.push($injector => {
    'ngInject';
    return {
      responseError: error => {
        // need to use injetor because using the toastr service
        // causes a circular dependency with $http
        const $q = $injector.get('$q');

        if (get(error, 'config._hideError', false) === true) {
          return $q.reject(error);
        }

        const generalErrorMsgKey = 'ERROR_OOPS_SERVER';
        const toastMessage = $injector.get('toastMessage');
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

  $httpProvider.interceptors.push($injector => {
    'ngInject';

    let refreshRequest = null;

    return {
      responseError: response => {
        const $q = $injector.get('$q');
        const errorMessage = get(response, 'data.message', '');
        const userService = $injector.get('UserService');
        const refreshRegexp = new RegExp(userService.refreshTokenEndpoint);

        if (!(response.status === 401 && /token has expired/i.test(errorMessage))) {
          return $q.reject(response);
        }

        if (refreshRegexp.test(get(response, 'config.url', ''))) {
          response.config._hideError = true;
          return $q.reject(response);
        }

        const deferred = $q.defer();

        if (!refreshRequest) {
          refreshRequest = userService.refreshAccessToken();
        }

        refreshRequest.then(() => {
          refreshRequest = null;
          $injector.get('$http')(response.config).then(
            deferred.resolve.bind(deferred),
            deferred.reject.bind(deferred)
          );
        }, error => {
          refreshRequest = null;
          $injector.get('JwtService').destroy();

          const state = $injector.get('$state');
          state.go(state.current.name, state.params, {reload: true});

          deferred.reject(error);
        });
        return deferred.promise;
      }
    };
  });

  /* Add jwt auth token to all requests if present */
  $httpProvider.interceptors.push($injector => {
    'ngInject';
    return {
      request(config) {
        const JwtService = $injector.get('JwtService');
        const token = JwtService.get();

        if (token && !/getNewAccessToken/.test(config.url)) {
          config.headers.Authorization = `Bearer ${token}`;
        }

        return config;
      }
    };
  });
  /* tslint:enable */
}
