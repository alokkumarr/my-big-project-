import { Injectable } from '@angular/core';
import {
  Resolve,
  ActivatedRouteSnapshot,
  RouterStateSnapshot
} from '@angular/router';
import { AnalyzeService } from '../services/analyze.service';
import { Analysis, AnalysisDSL } from '../types';

import { Observable, of, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable()
export class AnalysesResolver implements Resolve<any> {
  constructor(private analyseService: AnalyzeService) {}

  resolve(
    route: ActivatedRouteSnapshot,
    rstate: RouterStateSnapshot
  ): Observable<Array<Analysis | AnalysisDSL>> {
    return this.analyseService.getAnalysesFor(route.params.id).pipe(
      catchError(err => {
        if (err && err.status === 401) {
          return throwError(err);
        }
        return of([]);
      })
    );
  }
}
