import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from '../../material.module';
import { FlexLayoutModule } from '@angular/flex-layout';
import { RouterModule } from '@angular/router';
import { routes } from './routes';
import { AlertsPageComponent } from './components/alerts-page/alerts-page.component';
import { AlertsViewComponent } from './components/alerts-view/alerts-view.component';
import { AlertsConfigurationComponent } from './components/configure/index';

import { IsAdminGuard } from '../admin/guards';

const components = [
  AlertsPageComponent,
  AlertsViewComponent,
  AlertsConfigurationComponent
];

@NgModule({
  declarations: components,
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    FormsModule,
    ReactiveFormsModule,
    MaterialModule,
    FlexLayoutModule
  ],
  entryComponents: components,
  providers: [IsAdminGuard],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AlertsModule {}
