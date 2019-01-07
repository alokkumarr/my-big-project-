import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

const mapDataPath = 'assets/map-data/';
@Injectable()
export class MapDataService {

  constructor(private _http: HttpClient) { }

  getMapData(region) {
    return this._http.get(`${mapDataPath}${region.path}`);
  }
}
