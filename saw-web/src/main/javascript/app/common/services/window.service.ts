import { Injectable } from '@angular/core';

export const DEVICES = {
  ipad: { w: 768, h: 1024 }
};

@Injectable()
export class WindowService {
  get windowRef() {
    return window;
  }

  isWiderThan({ w }) {
    return this.windowRef.innerWidth > w;
  }
}
