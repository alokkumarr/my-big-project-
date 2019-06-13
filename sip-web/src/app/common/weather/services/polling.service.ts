import { Injectable, NgZone } from '@angular/core';
import { multicast, mergeMap, merge, refCount } from 'rxjs/operators';
import { Observable, Observer, Subject, Subscription, interval } from 'rxjs';

@Injectable()
export class PollingService {
  constructor(private zone: NgZone) {}

  // NOTE: Running the interval outside Angular ensures that e2e tests will not hang.
  execute<T>(
    operation: () => Observable<T>,
    frequency: number = 1000
  ): Observable<T> {
    const subject = new Subject();
    const source = Observable.create((observer: Observer<T>) => {
      let sub: Subscription;
      this.zone.runOutsideAngular(() => {
        const zone = this.zone;
        sub = interval(frequency)
          .pipe(mergeMap(operation))
          .subscribe({
            next(result) {
              zone.run(() => {
                observer.next(result);
              });
            },
            error(err) {
              zone.run(() => {
                observer.error(err);
              });
            }
          });
      });

      return () => {
        if (sub) {
          sub.unsubscribe();
        }
      };
    });

    return source.pipe(
      multicast(subject),
      refCount(),
      merge(operation())
    );
  }
}
