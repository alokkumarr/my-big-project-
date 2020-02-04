import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import AppConfig from '../../../../../appConfig';
import { TinyColor } from '@ctrl/tinycolor';
import * as forEach from 'lodash/forEach';
import { Observable } from 'rxjs';
export interface Color {
  name: string;
  hex: string;
  darkContrast: boolean;
}


@Injectable()
export class BrandingService {
  constructor(private _http: HttpClient) {}

  uploadFile(filesToUpload, primaryColor): Observable<any> {
    const url = AppConfig.login.url;
    const endpoint = `${url}/auth/admin/cust/brand`;
    const headers = new HttpHeaders();
    headers.set('Content-Type', null);
    headers.set('Accept', 'multipart/form-data');

    const params = new HttpParams();
    const formData: FormData = new FormData();
    formData.append('brandColor', primaryColor);
    forEach(filesToUpload, file => {
      formData.append('brandLogo', file, file.name);
    });
    return this._http.post(endpoint, formData, { params, headers });
  }

  savePrimaryColor(color) {
    const primaryColorPalette = this.computeColors(color);
    for (const color of primaryColorPalette) {
      const key1 = `--theme-primary-${color.name}`;
      const value1 = color.hex;
      const key2 = `--theme-primary-contrast-${color.name}`;
      const value2 = color.darkContrast ? 'rgba(black, 0.87)' : 'white';
      document.documentElement.style.setProperty(key1, value1);
      document.documentElement.style.setProperty(key2, value2);
    }
  }

  computeColors(hex: string): Color[] {
    return [
      this.getColorObject(new TinyColor(hex).lighten(52), '50'),
      this.getColorObject(new TinyColor(hex).lighten(37), '100'),
      this.getColorObject(new TinyColor(hex).lighten(26), '200'),
      this.getColorObject(new TinyColor(hex).lighten(12), '300'),
      this.getColorObject(new TinyColor(hex).lighten(6), '400'),
      this.getColorObject(new TinyColor(hex), '500'),
      this.getColorObject(new TinyColor(hex).darken(6), '600'),
      this.getColorObject(new TinyColor(hex).darken(12), '700'),
      this.getColorObject(new TinyColor(hex).darken(18), '800'),
      this.getColorObject(new TinyColor(hex).darken(24), '900'),
      this.getColorObject(new TinyColor(hex).lighten(50).saturate(30), 'A100'),
      this.getColorObject(new TinyColor(hex).lighten(30).saturate(30), 'A200'),
      this.getColorObject(new TinyColor(hex).lighten(10).saturate(15), 'A400'),
      this.getColorObject(new TinyColor(hex).lighten(5).saturate(5), 'A700')
    ];
  }

  getColorObject(value, name): Color {
    const c = new TinyColor(value);
    return {
      name: name,
      hex: c.toHexString(),
      darkContrast: c.isLight()
    };
  }

  getBrandingDetails(): Observable<any> {
    const url = AppConfig.login.url;
    return this._http.get(`${url}/auth/user/cust/brand`);
  }

  reset(): Observable<any> {
    const url = AppConfig.login.url;
    return this._http.delete(`${url}/auth/admin/cust/brand`);
  }
}
