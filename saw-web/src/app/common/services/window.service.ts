import { Injectable } from '@angular/core';

export const DEVICES = {
  ipadPortrait: { w: 768, h: 1024 },
  ipadLandscape: { w: 1024, h: 768 }
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
