import {AccordionMenu, AccordionMenuLink} from './accordionMenu';
import {BadgeComponent} from './badge';
import {
  JSPlumbCanvas,
  JSPlumbTable,
  JSPlumbEndpoint,
  JSPlumbConnector,
  JSPlumbJoinLabel,
  JSPlumbJoinDialog
} from './jsPlumb';
import {PanelComponent} from './panel';
import {RangeSliderComponent} from './range-slider/range-slider.component';
import {mdButtonGroupComponent} from './md-button-group/md-button-group.component';
import {ChoiceGroupComponent} from './choice-group';
import {ChartsModule} from './charts';
import {SidenavComponent, SidenavBtnComponent} from './sidenav';

export const ComponentsModule = 'ComponentsModule';

angular
  .module(ComponentsModule, [ChartsModule])
  .component('accordionMenu', AccordionMenu)
  .component('accordionMenuLink', AccordionMenuLink)
  .component('badge', BadgeComponent)
  .component('jsPlumbCanvas', JSPlumbCanvas)
  .component('jsPlumbTable', JSPlumbTable)
  .component('jsPlumbConnector', JSPlumbConnector)
  .component('jsPlumbEndpoint', JSPlumbEndpoint)
  .component('jsPlumbJoinLabel', JSPlumbJoinLabel)
  .component('jsPlumbJoinDialog', JSPlumbJoinDialog)
  .component('panel', PanelComponent)
  .component('rangeSlider', RangeSliderComponent)
  .component('choiceGroup', ChoiceGroupComponent)
  .component('mdButtonGroup', mdButtonGroupComponent)
  .component('sidenav', SidenavComponent)
  .component('sidenavBtn', SidenavBtnComponent);
