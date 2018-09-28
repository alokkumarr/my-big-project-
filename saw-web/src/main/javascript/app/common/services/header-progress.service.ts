import { Injectable } from '@angular/core';
import { Subject } from 'rxjs/Subject';

@Injectable()
export class HeaderProgressService {
  private _progressCounter = 0;
  private _showProgress = false;
  private _subject$ = new Subject<boolean>();

  subscribe(fn) {
    return this._subject$.subscribe(fn);
  }

  show() {
    this._progressCounter++;
    this._showProgress = this._get();
  }

  hide() {
    this._progressCounter--;
    this._showProgress = this._get();
  }

  private _get() {
    const showProgress = this._progressCounter > 0;
    this._subject$.next(showProgress);
    return showProgress;
  }
}