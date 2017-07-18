import angular from 'angular';

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
import {ErrorDetailComponent} from './error-detail/error-detail.component';
import {SearchBoxComponent} from './search-box/search-box.component';
import {CollapserComponent} from './collapser/collapser.component';
import {RangeSliderComponent} from './range-slider/range-slider.component';
import {mdButtonGroupComponent} from './md-button-group/md-button-group.component';
import {ChoiceGroupComponent} from './choice-group';
import {ChartsModule} from './charts';
import {SidenavComponent, SidenavBtnComponent} from './sidenav';
import {BinaryOptionComponent} from './binary-option/binary-option.component';

export const CommonComponentModule = 'CommonModule.Component';

angular
  .module(CommonComponentModule, [ChartsModule])
  .component('accordionMenu', AccordionMenu)
  .component('accordionMenuLink', AccordionMenuLink)
  .component('badge', BadgeComponent)
  .component('binaryOption', BinaryOptionComponent)
  .component('choiceGroup', ChoiceGroupComponent)
  .component('collapser', CollapserComponent)
  .component('errorDetail', ErrorDetailComponent)
  .component('jsPlumbCanvas', JSPlumbCanvas)
  .component('jsPlumbConnector', JSPlumbConnector)
  .component('jsPlumbEndpoint', JSPlumbEndpoint)
  .component('jsPlumbJoinDialog', JSPlumbJoinDialog)
  .component('jsPlumbJoinLabel', JSPlumbJoinLabel)
  .component('jsPlumbTable', JSPlumbTable)
  .component('mdButtonGroup', mdButtonGroupComponent)
  .component('panel', PanelComponent)
  .component('rangeSlider', RangeSliderComponent)
  .component('searchBox', SearchBoxComponent)
  .component('sidenav', SidenavComponent)
  .component('sidenavBtn', SidenavBtnComponent);
