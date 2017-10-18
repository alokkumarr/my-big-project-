import * as templateUrl from './collapser.component.html';
import style from './collapser.component.scss';

/**
 * @example usage in an html example:
 * <collapser title="Title as string">
 *   <div> My Content </div>
 * </collapser>
 *
 *
 * @class CollapserComponent
 */
export const CollapserComponent = {
  bindings: {
    title: '@',
    action: '<'
  },
  transclude: true,
  styles: [style],
  template: templateUrl,
  controller: class CollapserController {
    constructor($scope) {
      'ngInject';

      this.scope = $scope;
      this.isCollapsed = false;
    }

    toggleCollapse() {
      this.isCollapsed = !this.isCollapsed;
    }
  }
};
