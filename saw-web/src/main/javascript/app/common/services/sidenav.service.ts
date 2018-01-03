import { BehaviorSubject } from 'rxjs/BehaviorSubject';

export class SideNavService {
  private _sidenavEvent = new BehaviorSubject(false);

  get sidenavEvent () {
    return this._sidenavEvent;
  }
}

