import template from './controls.component.html';

export const ControlsComponent = {
  template,
  controller: class ControlsController {
    constructor() {
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

      this.clickMe = () => {
        // window.alert('Should not see me');
      };

      this.rangeSlider = {
        min: 0,
        max: 10,
        lower: 3,
        upper: 8,
        minGap: 1,
        step: 1
      };

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
