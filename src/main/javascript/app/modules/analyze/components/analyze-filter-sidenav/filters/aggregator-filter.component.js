import template from './aggregator-filter.component.html';

const AGGREGATORS = [{
  type: 'sum',
  label: 'SUM'
}, {
  type: 'min',
  label: 'MIN'
}, {
  type: 'max',
  label: 'MAX'
}, {
  type: 'avg',
  label: 'AVG'
}, {
  type: 'count',
  label: 'COUNT'
}];

export const AggregatorFilterComponent = {
  template,
  bindings: {
    filter: '<'
  },
  controller: class AggregatorFilterController {
    constructor() {
      if (!this.filter.model) {
        this.filter.model = 'sum';
      }

      this.AGGREGATORS = AGGREGATORS;
    }
  }
};
