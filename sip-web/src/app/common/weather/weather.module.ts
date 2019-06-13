import { NgModule, ModuleWithProviders } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MaterialModule } from '../../material.module';
import { HttpClient, HttpClientModule } from '@angular/common/http';

import { WeatherWidgetComponent } from './widget/weather-widget.component';
import { WeatherForecastComponent } from './forecast/weather-forecast.component';
import { WeatherTodayComponent } from './today/weather-today.component';
import { PollingService } from './services/polling.service';
import { OpenWeatherMapApiService } from './services/open-weather-map.api.service';
import { WeatherApiService } from './services/weather.api.service';
import {
  WeatherApiName,
  WeatherApiConfig
} from './services/weather.api.service';

const COMPONENTS = [
  WeatherWidgetComponent,
  WeatherForecastComponent,
  WeatherTodayComponent
];

export function apiServiceFactory(
  http: HttpClient,
  pooling: PollingService,
  openWeather: WeatherApiConfig
) {
  switch (openWeather.name) {
    case WeatherApiName.OPEN_WEATHER_MAP:
      return new OpenWeatherMapApiService(http, pooling, openWeather);
    default:
      return new OpenWeatherMapApiService(http, pooling, openWeather);
  }
}

export function forRoot(config: WeatherApiConfig): ModuleWithProviders {
  return {
    ngModule: WeatherModule,
    providers: [
      PollingService,
      {
        provide: WeatherApiService,
        useFactory: apiServiceFactory,
        deps: [HttpClient, PollingService, 'WEATHER_CONFIG']
      },
      { provide: 'WEATHER_CONFIG', useValue: config }
    ]
  };
}

@NgModule({
  imports: [HttpClientModule, CommonModule, FlexLayoutModule, MaterialModule],
  declarations: COMPONENTS,
  entryComponents: COMPONENTS,
  exports: [WeatherWidgetComponent]
})
export class WeatherModule {
  static forRoot = forRoot;
}
