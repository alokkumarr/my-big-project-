import { Inject, Injectable } from '@angular/core';
import { HttpHeaders, HttpClient, HttpParams } from '@angular/common/http';
import { map, filter } from 'rxjs/operators';
import { Observable } from 'rxjs';
import APP_CONFIG from '../../../../../appConfig';
import { PollingService } from './polling.service';
import {
  WeatherQueryParams,
  CurrentWeather,
  Forecast
} from '../weather.interfaces';
import { get, isUndefined } from 'lodash';
@Injectable()
export abstract class WeatherApiService {
  public api = get(APP_CONFIG, 'api.sipApiUrl');
  // public api = 'http://saw-rd601.ana.dev.vaste.sncrcorp.net/sip';
  pollingInterval = 60000 * 60;
  constructor(
    protected http: HttpClient,
    protected pollingService: PollingService,
    @Inject('WEATHER_CONFIG') public apiConfig
  ) {}

  currentWeather(queryParams: WeatherQueryParams): Observable<CurrentWeather> {
    return this.callApi(queryParams, 'weather').pipe(
      map(this.mapCurrentWeatherResponse.bind(this))
    );
  }

  forecast(queryParams: WeatherQueryParams): Observable<Forecast[]> {
    return this.callApi(queryParams, 'forecast').pipe(
      map(this.mapForecastResponse.bind(this))
    );
  }

  protected callApi(
    queryParams: WeatherQueryParams,
    endpoint: string
  ): Observable<any> {
    const params = this.mapQueryParams(queryParams);
    const requestOptions = this.getRequestOptions(params);
    const apiCall: Observable<any> = this.http
      .get(`${this.api}/weatherdata/${endpoint}`, requestOptions)
      .pipe(filter(el => !!el));
    return this.wrapWithPoll(apiCall);
  }

  protected setTokenKey(): string {
    // Implement it in child service
    return '';
  }

  protected mapQueryParams(params: WeatherQueryParams): any {
    // Implement it in child service
    return;
  }

  protected mapCurrentWeatherResponse(response: any): CurrentWeather {
    // Implement it in child service
    return <CurrentWeather>{};
  }

  protected mapForecastResponse(response: any): Forecast[] {
    // Implement it in child service
    return <Forecast[]>[];
  }

  protected mapResponseToIconUrl(response: any): string {
    return '';
  }
  protected mapResponseToIconClass(response: any): string {
    return '';
  }

  private wrapWithPoll(apiCall: Observable<any>) {
    return this.pollingService.execute(() => apiCall, this.pollingInterval);
  }

  private getRequestOptions(queryParams: Object) {
    const headers = new HttpHeaders();

    return {
      headers,
      params: this.getQueryParams(queryParams)
    };
  }

  private getQueryParams(obj: { [key: string]: any }): HttpParams {
    let queryParams = new HttpParams();

    for (const key in obj) {
      if (obj.hasOwnProperty(key) && !isUndefined(obj[key])) {
        queryParams = queryParams.append(key.toString(), obj[key]);
      }
    }
    return queryParams;
  }
}

export class WeatherApiConfig {
  name: WeatherApiName;
}

export enum WeatherApiName {
  OPEN_WEATHER_MAP = <any>'Open Weather Map'
}
