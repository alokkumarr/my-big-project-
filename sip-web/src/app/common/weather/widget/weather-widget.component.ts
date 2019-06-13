import { Component, Input } from '@angular/core';
import { Observable } from 'rxjs';
import { WeatherQueryParams } from '../weather.interfaces';
import { WeatherApiService } from '../services/weather.api.service';
import {
  CurrentWeather,
  Forecast,
  WeatherSettings
} from '../weather.interfaces';

// import 'weather-icons/css/weather-icons.css';

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'sip-weather-widget',
  templateUrl: 'weather-widget.component.html',
  styleUrls: ['weather-widget.component.scss']
})
export class WeatherWidgetComponent {
  public settings: WeatherSettings;
  public weather$: Observable<CurrentWeather>;
  public forecasts$: Observable<Forecast[]>;
  bgImgUrl: string;

  @Input('settings') set setSettings(settings: WeatherSettings) {
    this.settings = settings;
    const queryParams: WeatherQueryParams = {
      cityName: settings.location.cityName,
      units: settings.scale
    };
    this.weather$ = this._weatherApi.currentWeather(queryParams);
    this.forecasts$ = this._weatherApi.forecast(queryParams);
  }

  getbgURL(data) {
    this.bgImgUrl = data;
  }

  constructor(private _weatherApi: WeatherApiService) {}
}
