import { Injectable } from '@angular/core';

@Injectable()
export class HeaderProgressService {
  progressCounter = 0;
  showProgress = false;

  show() {
    this.progressCounter++;
    this.showProgress = this.get();
  }

  hide() {
    this.progressCounter--;
    this.showProgress = this.get();
  }

  toggle() {
    this.showProgress = !this.showProgress;
    if (this.showProgress) {
      this.progressCounter--;
    } else {
      this.progressCounter++;
    }
    this.showProgress = this.get();
  }

  get() {
    return this.progressCounter > 0;
  }
}
