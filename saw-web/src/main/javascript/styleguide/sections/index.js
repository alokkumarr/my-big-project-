import {AccordionMenuComponent} from './accordionMenu/accordion-menu.component';
import {AlertsComponent} from './alerts/alerts.component';
import {AnalysisCardComponent} from './analysisCard/analysis-card.component';
import {ChartsComponent} from './charts/charts.component';
import {ControlsComponent} from './controls/controls.component';
import {ModalsComponent} from './modals/modals.component';
import {NavigationMenuComponent} from './navigationMenu/navigation-menu.component';
import {PanelComponent} from './panel/panel.component';
import {PivotgridComponent} from './pivotgrid/pivotgrid.component';
import {SnapshotKpiComponent} from './snapshotkpi/snapshotkpi.component';
import {SqlTableComponent} from './sqlTable/sql-table.component';

export const SectionsModule = 'SectionsModule';

angular
  .module(SectionsModule, [])
  .component('accordionMenuSection', AccordionMenuComponent)
  .component('alertsSidenav', AlertsComponent)
  .component('analysisCardSection', AnalysisCardComponent)
  .component('chartsSection', ChartsComponent)
  .component('controlsSection', ControlsComponent)
  .component('modalsSection', ModalsComponent)
  .component('navigationMenu', NavigationMenuComponent)
  .component('panelSection', PanelComponent)
  .component('pivotgridSection', PivotgridComponent)
  .component('snapshotKpiSection', SnapshotKpiComponent)
  .component('sqlTableSection', SqlTableComponent);
