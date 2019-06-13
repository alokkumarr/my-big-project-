import { Component, OnInit, Input } from '@angular/core';
import {
  pipe as fpPipe,
  filter as fpFilter,
  groupBy as fpGroupBy,
  toPairs as fpToPairs,
  map as fpMap,
  sortBy as fpSortBy
} from 'lodash/fp';
import { map, min, max, head, chain, get } from 'lodash';
import moment from 'moment';

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'weather-forecast',
  templateUrl: 'weather-forecast.component.html',
  styleUrls: ['weather-forecast.component.scss']
})
export class WeatherForecastComponent implements OnInit {
  public forecasts;
  @Input('forecasts') set setForecasts(forecasts) {
    const currentDay = new Date().getDate();
    const currentMonth = moment().month();
    this.forecasts = fpPipe(
      fpFilter((forecast: any) => {
        const day = forecast.data.getDate();
        const month = moment(forecast.data).month();
        if (currentMonth === month) {
          return currentDay < day;
        } else if (month > currentMonth) {
          return true;
        }
      }),
      fpGroupBy((forecast: any) => forecast.data.getDate()),
      fpToPairs,
      fpMap(([day, fores]) => {
        const forecast: any = head(fores);
        const iconDesArr = map(fores, 'iconClass');
        const iconClass = chain(iconDesArr)
          .countBy()
          .toPairs()
          .orderBy(1, 'desc')
          .get('0.0')
          .value();

        const minTempsARR = map(fores, 'minTemp');
        const maxTempsARR = map(fores, 'maxTemp');
        const minTemp = min(minTempsARR);
        const maxTemp = max(maxTempsARR);
        return {
          description: forecast.description,
          minTemp,
          maxTemp,
          iconUrl: forecast.iconUrl,
          iconClass,
          dayOfWeek: moment(forecast.data).format('ddd'),
          date: moment(forecast.data)
        };
      }),
      fpSortBy(forecast => forecast.date)
    )(forecasts);
  }

  constructor() {}

  ngOnInit() {}
}
