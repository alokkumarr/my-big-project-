import * as defaultsDeep from 'lodash/defaultsDeep';

export default class AbstractComponentController {
  constructor($injector) {
    this.$log = $injector.get('$log');
    this.$state = $injector.get('$state');
    this.$mdDialog = $injector.get('$mdDialog');
    this.$eventEmitter = $injector.get('$eventEmitter');
  }

  $onInit() {
  }

  $onDestroy() {
  }

  on(...args) {
    return this.$eventEmitter.on(...args);
  }

  emit(...args) {
    this.$eventEmitter.emit(...args);
  }

  showDialog(config) {
    config = defaultsDeep(config, {
      controllerAs: '$ctrl',
      multiple: false,
      autoWrap: false,
      focusOnOpen: false,
      clickOutsideToClose: true,
      fullscreen: false
    });

    return this.$mdDialog.show(config);
  }
}
