import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable()
export class SidenavMenuService {
  public _subject$ = new Subject<{ menu: any[]; module: string }>();

  subscribe(fn) {
    this._subject$.subscribe(fn);
  }

  updateMenu(menu, module) {
    setTimeout(() => {
      this._subject$.next({ menu, module });
    });
  }
}
