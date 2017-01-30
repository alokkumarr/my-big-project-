
export const EVENTS = {
  OPEN_SIDENAV: 'OPEN_SIDENAV',
  APPLY_FILTERS: 'APPLY_FILTERS',
  CLEAR_ALL_FILTERS: 'CLEAR_ALL_FILTERS'
};

export function PubSubService($rootScope) {
  'ngInject';

  return {
    on,
    emit
  };

  function on(eventName, listener) {
    const unregister = $rootScope.$on(eventName, listener);

    return unregister;
  }

  function emit(eventName, payload) {
    $rootScope.$emit(eventName, payload);
  }
}
