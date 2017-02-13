import template from './choice-group.component.html';

/**
 * A button group with big icons, acts like a radio button group
 * @bindings: {
 *  items: an array of items to select from, it should have the following properties: [type, icon, label, disabled]
 *  ng-model
 *  ng-change
 * }
 * it can also be used with choice-groups directive put on a parent element
 * @example:
 *  <choice-group items="items" ng-model="$ctrl.model" ngChange="$ctrl.itemSelected()"></choice-group>
 * @type {{template, require: {ngModelCtrl: string, choiceGroupsCtrl: string}, bindings: {items: string}, controller: ChoiceGroupController}}
 */
export const ChoiceGroupComponent = {
  template,
  require: {
    ngModelCtrl: 'ngModel'
  },
  bindings: {
    items: '<',
    ngModel: '<'
  },
  controller: class ChoiceGroupController {
    onItemSelected(item) {
      this.ngModelCtrl.$setViewValue(item.type);
      this.ngModel = item.type;
    }
  }
};
