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
      .pipe(
        distinctUntilChanged(),
        debounce(() => timer(100))
      )
      .subscribe(fn);
  }

  show() {
    this._progressCounter++;
    this.onChange();
  }

  hide() {
    this._progressCounter--;
    this.onChange();
  }

  public onChange() {
    const showProgress = this._progressCounter > 0;
    this._subject$.next(showProgress);
  }
}
