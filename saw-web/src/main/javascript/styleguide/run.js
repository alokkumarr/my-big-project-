export function runConfig($rootScope, $state) {
  'ngInject';

  $rootScope.getPageTitle = () => {
    const data = $state.$current.data;

    if (data && data.title) {
      return data.title;
    }

    return 'Synchronoss';
  };
}
