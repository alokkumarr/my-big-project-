import { BehaviorSubject } from 'rxjs';
import { Injectable } from '@angular/core';

@Injectable()
export class SideNavService {
  public _sidenavEvent = new BehaviorSubject(false);

  get sidenavEvent() {
    return this._sidenavEvent;
  }
}
