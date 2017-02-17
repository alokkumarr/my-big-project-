import template from './search-box.component.html';
import style from './search-box.component.scss';

export const SearchBoxComponent = {
  template,
  styles: [style],
  bindings: {
    model: '=ngModel',
    placeholder: '@',
    onChange: '&',
    delay: '<'
  },
  controller: class SearchBoxController {
    constructor($document, $element, $scope, $timeout) {
      'ngInject';
      this.$document = $document;
      this.$element = $element;
      this.$scope = $scope;
      this.$timeout = $timeout;

      this.states = {
        hovered: false,
        focused: false
      };
    }

    $onInit() {
      let hasClickedOnTheElement = false;

      this.$oldModel = this.model;
      this.delay = Number(this.delay) || 250;

      this.onDocumentClick = () => {
        if (!hasClickedOnTheElement) {
          this.states.focused = false;

          this.$scope.$evalAsync();
        }

        hasClickedOnTheElement = false;
      };

      this.$element.on('click', () => {
        hasClickedOnTheElement = true;
      });

      this.$document.on('click', this.onDocumentClick);
    }

    $onDestroy() {
      this.$document.off('click', this.onDocumentClick);
    }

    onModelChanged() {
      this.states.focused = true;

      if (this.$changeTID) {
        this.$timeout.cancel(this.$changeTID);
      }

      this.$changeTID = this.$timeout(() => {
        this.$changeTID = undefined;
        this.onChange({
          newValue: this.model,
          oldValue: this.$oldModel
        });

        this.$oldModel = this.model;
      }, this.delay);
    }
  }
};
