import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { environment } from '../../../environments/environment';
import { NgxMapboxGLModule } from 'ngx-mapbox-gl';
import { MapBoxComponent } from './map-box.component';
import { SncrMarkerComponent } from './marker';

const COMPONENTS = [
  MapBoxComponent,
  SncrMarkerComponent
];

@NgModule({
  imports: [
    CommonModule,
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
