import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { CurrentWeather } from '../weather.interfaces';
import tz from 'tz-lookup';
import moment from 'moment-timezone';

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'weather-today',
  templateUrl: 'weather-today.component.html',
  styleUrls: ['weather-today.component.scss']
})
export class WeatherTodayComponent implements OnInit {
  public weather: CurrentWeather;
  public localDate;
  public localTime;
  public windIcon: string;

  @Output() bgURL: EventEmitter<any> = new EventEmitter<any>();

  @Input('weather') set setWeather(weather: CurrentWeather) {
    if (!weather) {
      return;
    }
    this.weather = weather;
    const { lon, lat } = weather.coord;
    const timezone = tz(lat, lon);
    this.localDate = moment()
      .tz(timezone)
      .format('ddd, D MMM');
    this.localTime = moment()
      .tz(timezone)
      .format('hh:mm a');
    this.windIcon = `wi wi-wind from-${weather.wind.deg}-deg`;

    const bgImgUrl = weather.bgImgUrl
      ? weather.bgImgUrl
      : 'url(assets/img/weather/cloudy.svg)';
    this.bgURL.emit(bgImgUrl);
  }

  constructor() {}

  ngOnInit() {}
}
