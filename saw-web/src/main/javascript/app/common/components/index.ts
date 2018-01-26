import * as angular from 'angular';
import {downgradeComponent} from '@angular/upgrade/static';

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
import {ErrorDetailComponent} from './error-detail';
import {SearchBoxComponent} from './search-box/search-box.component';
import {CollapserComponent} from './collapser/collapser.component';
import {RangeSliderComponent} from './range-slider/range-slider.component';
import {mdButtonGroupComponent} from './md-button-group/md-button-group.component';
import {ChoiceGroupComponent} from './choice-group';
import {ChartsModule} from './charts';
import {SidenavComponent, SidenavBtnComponent} from './sidenav';
import {PivotGridComponent} from './pivot-grid/pivot-grid.component';
import {BinaryOptionComponent} from './binary-option/binary-option.component';
import {ReportGridDisplayComponent} from './report-grid-display/grid/report-grid-display.component';
import {ReportGridDisplayNodeComponent} from './report-grid-display/node/report-grid-display-node.component';
import {ReportGridDisplayContainerComponent} from './report-grid-display/container/report-grid-display-container.component';

export const CommonComponentModule = 'CommonModule.Component';

angular
  .module(CommonComponentModule, [ChartsModule])
  .component('accordionMenu', AccordionMenu)
  .component('accordionMenuLink', AccordionMenuLink)
  .component('badge', BadgeComponent)
  .component('binaryOption', BinaryOptionComponent)
  .component('choiceGroup', ChoiceGroupComponent)
  .component('collapser', CollapserComponent)
  .component('errorDetail', downgradeComponent({component: ErrorDetailComponent}))
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
  .directive('pivotGrid', downgradeComponent({component: PivotGridComponent}))
  .component('reportGridDisplay', ReportGridDisplayComponent)
  .component('reportGridDisplayNode', ReportGridDisplayNodeComponent)
  .component('reportGridDisplayContainer', ReportGridDisplayContainerComponent)
  .component('sidenavBtn', SidenavBtnComponent);
