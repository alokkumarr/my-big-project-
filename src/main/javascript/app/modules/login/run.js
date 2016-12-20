export function runConfig($rootScope, $location, JwtService) {
  'ngInject';

  const destroy = $rootScope.$on('$locationChangeStart', (event, newUrl) => {
    const path = new URL(newUrl).pathname;

    if (path !== '/login') {
      const token = JwtService.get();

      if (!token) {
        $location.path('/login');
      }
    }
  });
}
