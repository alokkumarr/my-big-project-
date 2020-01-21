import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { CookiesService, UserService } from '../../common/services';
import { Router } from '@angular/router';

const SESSION_ID_COOKIE_KEY = 'sessionID';
const DOMAIN_NAME_COOKIE_KEY = 'domainName';
const CLIEND_ID_COOKIE_KEY = 'clientId';
@Component({
  selector: 'login-page',
  templateUrl: './login-page.component.html',
  styles: [
    `
      :host {
        height: 100%;
        display: block;
      }
    `
  ]
})
export class LoginPageComponent implements OnInit {
  constructor(
    public _title: Title,
    private _cookies: CookiesService,
    public _userService: UserService,
    public _router: Router
  ) {}

  ngOnInit() {
    const sessionID = this._cookies.get(SESSION_ID_COOKIE_KEY);
    const domainName = this._cookies.get(DOMAIN_NAME_COOKIE_KEY);
    const clientId = this._cookies.get(CLIEND_ID_COOKIE_KEY);
    if (sessionID && domainName && clientId) {
      this._userService
        .authenticateWithSessionID(sessionID, domainName, clientId)
        .then(() => {
          this._cookies.clear(SESSION_ID_COOKIE_KEY);
          this._cookies.clear(DOMAIN_NAME_COOKIE_KEY);
          this._cookies.clear(CLIEND_ID_COOKIE_KEY);
          this._router.navigate(['']);
        });
    }
    this._title.setTitle(`Login`);
  }
}
