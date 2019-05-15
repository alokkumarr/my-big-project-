import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as fpGet from 'lodash/fp/get';
import APP_CONFIG from '../../../../appConfig';

const ANALYZE_MODULE_NAME = 'ANALYZE';

interface MetricResponse {
  data: { contents: Array<{}> };
}

@Injectable()
export class CommonSemanticService {
  private api = APP_CONFIG.api.url;

  constructor(private http: HttpClient) {}

  getRequest<T>(path) {
    return this.http.get<T>(`${this.api}/${path}`);
  }

  getMetricList$(): Observable<any[]> {
    const projectId = 'workbench';
    return this.getRequest<MetricResponse>(
      `internal/semantic/md?projectId=${projectId}`
    ).pipe(map(fpGet(`contents.[0].${ANALYZE_MODULE_NAME}`)));
  }

  getArtifactsForDataSet$(semanticId: string) {
    return this.getRequest(`internal/semantic/workbench/${semanticId}`);
  }
}
