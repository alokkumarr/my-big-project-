export enum TemperatureScale {
  CELCIUS = <any>'celcius',
  KELVIN = <any>'kelvin',
  FAHRENHEIT = <any>'fahrenheit'
}
export class WeatherSettings {
  location: WeatherQueryParams = {
    cityName: 'Los Angeles'
  };
  scale: TemperatureScale = TemperatureScale.FAHRENHEIT;
}

export enum ForecastMode {
  GRID = <any>'GRID',
  DETAILED = <any>'DETAILED'
}

export interface WeatherQueryParams {
  cityId?: number;
  cityName?: string;
  latLng?: {
    lat: number;
    lng: number;
  };
  zipCode?: number;
  units?: TemperatureScale;
  lang?: string;
}

export enum WeatherLayout {
  WIDE = <any>'wide',
  NARROW = <any>'narrow'
}

export interface CurrentWeather {
  location: string;
  temp: number;
  pressure?: number;
  humidity?: number;
  minTemp?: number;
  maxTemp?: number;
  sunrise?: number;
  sunset?: number;
  bgImgUrl?: string;
  iconClass?: string;
  iconUrl?: string;
  description?: string;
  wind?: {
    deg: number;
    speed: number;
  };
  date?: number;
  coord?: {
    lon: number;
    lat: number;
  };
}

export interface Forecast extends CurrentWeather {
  data: Date;
}
