import template from './analyze-card.component.html';
import style from './analyze-card.component.scss';

export const AnalyzeCardComponent = {
  template,
  styles: [style],
  bindings: {
    metadata: '<'
  },
  controller: class AnalyzeCardController {
  }
};
