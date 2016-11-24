import angular from 'angular';
import 'devextreme/integration/angular';
import 'angular-ui-router';
import 'angular-ui-grid';
import 'angular-ui-grid/ui-grid.css';
import 'angular-material';
import 'angular-material/angular-material.css';
import '../fonts/style.css';

import {themeConfig} from '../src/theme';
import {routesConfig} from './routes';
import {LibModule} from '../src/app/lib';

import {StyleguideComponent} from './styleguide.component';

import {AccordionMenuComponent} from './sections/accordionMenu/accordion-menu.component';
import {AlertsComponent} from './sections/alerts/alerts.component';
import {AnalysisCardComponent} from './sections/analysisCard/analysis-card.component';
import {ChartsComponent} from './sections/charts/charts.component';
import {ControlsComponent} from './sections/controls/controls.component';
import {ModalsComponent} from './sections/modals/modals.component';
import {NavigationMenuComponent} from './sections/navigationMenu/navigation-menu.component';
import {PanelComponent} from './sections/panel/panel.component';
import {PivotgridComponent} from './sections/pivotgrid/pivotgrid.component';
import {SnapshotKpiComponent} from './sections/snapshotkpi/snapshotkpi.component';
import {SqlTableComponent} from './sections/sqlTable/sql-table.component';

angular.module('styleguideModule', [
  'dx',
  'ngMaterial',
  'ui.router',
  'ui.grid',
  LibModule
])
  .config(routesConfig)
  .config(themeConfig)
  .component('controlsSection', ControlsComponent)
  .component('pivotgridSection', PivotgridComponent)
  .component('chartsSection', ChartsComponent)
  .component('sqlTableSection', SqlTableComponent)
  .component('accordionMenuSection', AccordionMenuComponent)
  .component('panelSection', PanelComponent)
  .component('modalsSection', ModalsComponent)
  .component('snapshotKpiSection', SnapshotKpiComponent)
  .component('analysisCardSection', AnalysisCardComponent)
  .component('alertsSidenav', AlertsComponent)
  .component('navigationMenu', NavigationMenuComponent)
  .component('styleguide', StyleguideComponent);
