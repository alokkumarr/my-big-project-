import template from './controls.component.html';
import style from './controls.component.scss';

export const ControlsComponent = {
  template,
  styles: [style],
  controller: class ControlsController {
    constructor() {
      // filters & tags
      this.vegObjs = [
        {
          name: 'Broccoli',
          type: 'Brassica'
        },
        {
          name: 'Cabbage',
          type: 'Brassica'
        },
        {
          name: 'Carrot',
          type: 'Umbelliferous'
        }
      ];

      this.newVeg = chip => {
        return {
          name: chip,
          type: 'unknown'
        };
      };

      this.readonly = false;
      this.removable = true;

      // range slider
      this.rangeSlider = {
        min: 0,
        max: 10,
        lower: 3,
        upper: 8,
        minGap: 1,
        step: 1
      };

      // badge count
      this.badgeCount = 5;

      this.incBadgeCount = () => {
        this.badgeCount++;
      };

      this.decBadgeCount = () => {
        this.badgeCount--;
      };
    }
  }
};
