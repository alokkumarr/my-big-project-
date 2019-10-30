import { of, Observable } from 'rxjs';
import { PreloadingStrategy, Route } from '@angular/router';

export class SelectivePreloading implements PreloadingStrategy {
  preload(route: Route, preload: Function): Observable<any> {
    if (route.data && route.data.preload) {
      return preload();
    } else {
      return of(null);
    }
  }
}
