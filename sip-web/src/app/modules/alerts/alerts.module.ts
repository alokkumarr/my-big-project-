import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgxsModule } from '@ngxs/store';
import { DxTemplateModule } from 'devextreme-angular/core/template';
import { DxDataGridModule } from 'devextreme-angular/ui/data-grid';
import { DxSelectBoxModule } from 'devextreme-angular/ui/select-box';

import { MaterialModule } from '../../material.module';
import { FlexLayoutModule } from '@angular/flex-layout';
import { RouterModule } from '@angular/router';
import { routes } from './routes';
import { AlertsPageComponent } from './components/alerts-page/alerts-page.component';
import {
  AlertsConfigurationComponent,
  AddAlertComponent,
  ConfirmActionDialogComponent
} from './components/configure/index';
import {
  AlertsViewComponent,
  AlertsGridComponent,
  AlertDetailComponent,
  AlertsFilterComponent
} from './components/alerts-view/index';
import { AlertsFilterState } from './components/alerts-view/state/alerts.state';
import { IsAdminGuard } from '../admin/guards';

const components = [
  AlertsPageComponent,
  AlertsViewComponent,
  AlertsConfigurationComponent,
  AddAlertComponent,
  ConfirmActionDialogComponent,
  AlertsGridComponent,
  AlertDetailComponent,
  AlertsFilterComponent
];

@NgModule({
  declarations: components,
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    FormsModule,
    ReactiveFormsModule,
    MaterialModule,
    FlexLayoutModule,
    DxTemplateModule,
    DxDataGridModule,
    DxSelectBoxModule,
    NgxsModule.forFeature([AlertsFilterState])
  ],
  entryComponents: components,
  providers: [IsAdminGuard],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AlertsModule {}
