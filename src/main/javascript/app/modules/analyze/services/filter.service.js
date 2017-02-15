import {ANALYZE_FILTER_SIDENAV_ID} from '../components/analyze-filter-sidenav/analyze-filter-sidenav.component';

export const DEFAULT_FILTER_OPERATOR = 'AND';

const EVENTS = {
  OPEN_SIDENAV: 'OPEN_SIDENAV',
  APPLY_FILTERS: 'APPLY_FILTERS',
  CLEAR_ALL_FILTERS: 'CLEAR_ALL_FILTERS'
};

export function FilterService($mdSidenav, $eventEmitter) {
  'ngInject';

  const unRegisterFuncs = [];

  return {
    onOpenFilterSidenav,
    onApplyFilters,
    onClearAllFilters,
    offOpenFilterSidenav,
    offApplyFilters,
    offClearAllFilters,
    openFilterSidenav,
    applyFilters,
    clearAllFilters
  };

  function onOpenFilterSidenav(callback) {
    unRegisterFuncs[EVENTS.OPEN_SIDENAV] = $eventEmitter.on(EVENTS.OPEN_SIDENAV, callback);
  }

  function onApplyFilters(callback) {
    unRegisterFuncs[EVENTS.APPLY_FILTERS] = $eventEmitter.on(EVENTS.APPLY_FILTERS, callback);
  }

  function onClearAllFilters(callback) {
    unRegisterFuncs[EVENTS.CLEAR_ALL_FILTERS] = $eventEmitter.on(EVENTS.CLEAR_ALL_FILTERS, callback);
  }

  function offOpenFilterSidenav() {
    unRegisterFuncs[EVENTS.OPEN_SIDENAV]();
  }

  function offApplyFilters() {
    unRegisterFuncs[EVENTS.APPLY_FILTERS]();
  }

  function offClearAllFilters() {
    unRegisterFuncs[EVENTS.CLEAR_ALL_FILTERS]();
  }

  function openFilterSidenav(payload) {
    $eventEmitter.emit(EVENTS.OPEN_SIDENAV, payload);
    $mdSidenav(ANALYZE_FILTER_SIDENAV_ID).open();
  }

  function applyFilters(payload) {
    $eventEmitter.emit(EVENTS.APPLY_FILTERS, payload);
    $mdSidenav(ANALYZE_FILTER_SIDENAV_ID).close();
  }

  function clearAllFilters() {
    $eventEmitter.emit(EVENTS.CLEAR_ALL_FILTERS);
    $mdSidenav(ANALYZE_FILTER_SIDENAV_ID).close();
  }
}
