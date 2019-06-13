import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { round } from 'lodash';
import { PollingService } from './polling.service';
import { WeatherApiConfig, WeatherApiService } from './weather.api.service';
import { iconCodes, IconCodeType } from './open-weather-map-2-weather-icons';
import {
  WeatherQueryParams,
  TemperatureScale,
  CurrentWeather,
  Forecast
} from '../weather.interfaces';

@Injectable()
export class OpenWeatherMapApiService extends WeatherApiService {
  iconCodes: IconCodeType;
  iconCodes$: Observable<any>;
  constructor(
    protected http: HttpClient,
    protected poolingService: PollingService,
    public apiConfig: WeatherApiConfig
  ) {
    super(http, poolingService, apiConfig);
    this.iconCodes = iconCodes;
  }

  protected mapQueryParams(
    params: WeatherQueryParams
  ): OpenWeatherMapLocationRequest {
    const mapped: OpenWeatherMapLocationRequest = {
      id: params.cityId,
      q: params.cityName,
      lat: params.latLng ? params.latLng.lat : undefined,
      lon: params.latLng ? params.latLng.lng : undefined,
      zip: params.zipCode,
      units: params.units ? this.mapUnits(params.units) : undefined,
      lang: params.lang
    };
    return mapped;
  }

  protected mapCurrentWeatherResponse(
    response: OpenWeatherMapCurrentWeatherResponse
  ): CurrentWeather {
    if (!response) {
      return <CurrentWeather>{};
    }
    const min =
      response.main && response.main.temp ? response.main.temp_min : undefined;
    const max =
      response.main && response.main.temp ? response.main.temp_max : undefined;

    const weather: CurrentWeather = {
      temp: round(response.main.temp),
      pressure: response.main ? response.main.pressure : undefined,
      humidity: response.main ? response.main.humidity : undefined,
      minTemp: round(min),
      maxTemp: round(max),
      sunrise: response.sys ? response.sys.sunrise : undefined,
      sunset: response.sys ? response.sys.sunset : undefined,
      location: response.name,
      bgImgUrl: this.mapResponseToBgImg(response),
      iconUrl: this.mapResponseToIconUrl(response),
      iconClass: this.mapResponseToIconClass(response),
      description: response.weather[0].description,
      wind: {
        deg: response.wind.deg,
        speed: response.wind.speed
      },
      date: response.dt,
      coord: response.coord
    };
    return weather;
  }

  protected mapForecastResponse(
    response: OpenWeatherMapForecastResponse
  ): Forecast[] {
    if (!response) {
      return <Forecast[]>[];
    }
    const city = response.city;
    return response.list.map((el: OpenWeatherMapForecastResponseElement) => {
      const forecast: Forecast = {
        temp: round(el.main.temp),
        pressure: el.main.pressure,
        humidity: el.main.humidity,
        minTemp: round(el.main.temp_min),
        maxTemp: round(el.main.temp_max),
        location: city.name,
        iconUrl: this.mapResponseToIconUrl(el),
        iconClass: this.mapResponseToIconClass(el),
        description: el.weather[0].description,
        data: new Date(el.dt * 1000),
        wind: {
          deg: el.wind.deg,
          speed: el.wind.speed
        }
      };
      return forecast;
    });
  }

  protected mapResponseToIconUrl(
    response:
      | OpenWeatherMapCurrentWeatherResponse
      | OpenWeatherMapForecastResponseElement
  ): string {
    return `https://openweathermap.org/img/w/${response.weather[0].icon}.png`;
  }

  protected mapResponseToBgImg(
    response: OpenWeatherMapCurrentWeatherResponse
  ): string {
    const code = response.weather[0].id;
    const prefix = 'assets/img/weather/';
    const img = `url(${prefix}${iconCodes[code].img}.svg)`;
    return img;
  }

  protected mapResponseToIconClass(
    response:
      | OpenWeatherMapCurrentWeatherResponse
      | OpenWeatherMapForecastResponseElement
  ): string {
    const code = response.weather[0].id;
    // For reference in-case moved to another API service
    // https://erikflowers.github.io/weather-icons/api-list.html
    const prefix = 'wi wi-owm-';
    const icon = `${prefix}${code}`;
    return icon;
  }

  protected setTokenKey() {
    return 'APPID';
  }

  private mapUnits(unit: TemperatureScale) {
    switch (unit) {
      case TemperatureScale.CELCIUS:
        return 'metric';
      case TemperatureScale.FAHRENHEIT:
        return 'imperial';
      case TemperatureScale.KELVIN:
        return;
      default:
        return 'metric';
    }
  }
}

export interface OpenWeatherMapLocationRequest {
  id?: number;
  q?: string;
  lat?: number;
  lon?: number;
  zip?: number;
  units?: 'imperial' | 'metric';
  lang?: string;
}

export interface OpenWeatherMapCurrentWeatherResponse {
  coord: { lon: number; lat: number };
  weather: [{ id: number; main: string; description: string; icon: string }];
  base: string;
  main: {
    temp: number;
    pressure: number;
    humidity: number;
    temp_min: number;
    temp_max: number;
  };
  visibility: number;
  wind: { speed: number; deg: number };
  clouds: { all: number };
  dt: number;
  sys: {
    type: number;
    id: number;
    message: number;
    country: string;
    sunrise: number;
    sunset: number;
  };
  id: number;
  name: string;
  cod: number;
}

export interface OpenWeatherMapForecastResponse {
  city: {
    coord: {
      lat: number;
      lon: number;
    };
    country: string;
    id: number;
    name: string;
  };
  message: number;
  cod: string;
  cnt: number;
  list: OpenWeatherMapForecastResponseElement[];
}

export interface OpenWeatherMapForecastResponseElement {
  clouds: {
    all: number;
  };
  dt: number;
  dt_txt: string;
  main: {
    grnd_level: number;
    temp: number;
    pressure: number;
    humidity: number;
    temp_min: number;
    temp_max: number;
    temp_kf: number;
    sea_level: number;
  };
  sys: {
    pod: string;
  };
  weather: [{ id: number; main: string; description: string; icon: string }];
  wind: { speed: number; deg: number };
}
