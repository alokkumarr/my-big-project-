import { NgModule } from '@angular/core';
import { NgxMapboxGLModule } from 'ngx-mapbox-gl';
import { CommonModule } from '@angular/common';
import { CommonPipesModule } from '../pipes/common-pipes.module';
import { environment } from '../../../environments/environment';
import { MapBoxComponent } from './map-box.component';
import { MarkerPopupComponent } from './marker-popup';

const COMPONENTS = [
  MapBoxComponent,
  MarkerPopupComponent
];

@NgModule({
  imports: [
    CommonModule,
    CommonPipesModule,
    NgxMapboxGLModule.withConfig({
      accessToken: environment.mapbox.accessToken,
      geocoderAccessToken: environment.mapbox.accessToken
    })
  ],
  declarations: COMPONENTS,
  entryComponents: COMPONENTS,
  exports: COMPONENTS
})
export class MapBoxModule {}
