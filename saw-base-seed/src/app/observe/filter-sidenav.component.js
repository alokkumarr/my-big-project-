import template from './filter-sidenav.component.html';

export const OBSERVE_FILTER_SIDENAV_ID = 'observe-filter-sidenav';
export const FilterSidenavComponent = {
  template,
  controller: class FilterSidenavController {
    constructor() {
      this.id = OBSERVE_FILTER_SIDENAV_ID;
      this.affiliates = ['DIRECT TV', 'Red Ventures', 'ClearLink', 'All Connect', 'Q-ology', 'Acceller'];
      this.regions = ['Southeast', 'Southwest', 'Midwest', 'West', 'East', 'Unknown'];
      this.rangeSlider = {
        min: 0,
        max: 100,
        lower: 0,
        upper: 75,
        minGap: 1,
        step: 1
      };
    }
  }
};
