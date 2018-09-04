import { Injectable } from '@angular/core';

@Injectable()
export class AuthService {
  authorized = false;
  memorizedState = null;

  clear() {
    this.authorized = false;
    this.memorizedState = null;
  }

  go(fallback) {
    this.authorized = true;

    const targetState = this.memorizedState || fallback;
    const baseUrl = window.location.origin;
    const appUrl = `${baseUrl}` + targetState;

    window.location = appUrl;
  }
}
