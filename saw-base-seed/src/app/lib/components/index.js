import {AccordionMenu, AccordionMenuLink} from './accordionMenu';
import {BadgeComponent} from './badge';
import {JSPlumbCanvas, JSPlumbTable, JSPlumbEndpoint, JSPlumbConnection} from './jsPlumb';
import {PanelComponent} from './panel';
import {RangeSliderComponent} from './range-slider/range-slider.component';
import {mdButtonGroupComponent} from './md-button-group.component';
import {ChartsModule} from './charts';

export const ComponentsModule = 'ComponentsModule';

angular
  .module(ComponentsModule, [ChartsModule])
  .component('accordionMenu', AccordionMenu)
  .component('accordionMenuLink', AccordionMenuLink)
  .component('badge', BadgeComponent)
  .component('jsPlumbCanvas', JSPlumbCanvas)
  .component('jsPlumbTable', JSPlumbTable)
  .component('jsPlumbConnection', JSPlumbConnection)
  .component('jsPlumbEndpoint', JSPlumbEndpoint)
  .component('panel', PanelComponent)
  .component('rangeSlider', RangeSliderComponent)
  .component('mdButtonGroup', mdButtonGroupComponent);
