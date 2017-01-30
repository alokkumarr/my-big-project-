import cloneDeep from 'lodash/cloneDeep';
import isEmpty from 'lodash/isEmpty';
import forEach from 'lodash/forEach';
import {Subject} from 'rxjs/Subject';

import template from './analyze-filter-group.component.html';

export const AnalyzeFilterGroupComponent = {
  template,
  bindings: {
    filters: '<'
  }
};
