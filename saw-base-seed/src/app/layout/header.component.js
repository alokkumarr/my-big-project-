import template from './header.component.html';

export const HeaderComponent = {
  template,
  controller: class HeaderController {
    constructor($transitions) {
      'ngInject';
      const match = {
        to: state => {
          return state.name === 'observe' ||
            state.name === 'analyze' ||
            state.name === 'alerts';
        }
      };
      $transitions.onEnter(match, (transition, state) => {
        this.stateName = state.name;
      });

    }
  }
};
