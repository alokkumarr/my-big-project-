import { Injectable } from '@angular/core';
import { Subject, timer } from 'rxjs';
import { distinctUntilChanged, debounce } from 'rxjs/operators';

@Injectable()
export class HeaderProgressService {
  public _progressCounter = 0;
  public _showProgress = false;
  public _subject$ = new Subject<boolean>();

  subscribe(fn) {
    return this._subject$
      .pipe(distinctUntilChanged(), debounce(() => timer(100)))
      .subscribe(fn);
  }

  show() {
    this._progressCounter++;
    this._showProgress = this._get();
  }

  hide() {
    this._progressCounter--;
    this._showProgress = this._get();
  }

  public _get() {
    const showProgress = this._progressCounter > 0;
    this._subject$.next(showProgress);
    return showProgress;
  }
}
