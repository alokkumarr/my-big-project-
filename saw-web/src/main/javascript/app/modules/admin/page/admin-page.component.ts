import { Component } from '@angular/core';
import { Router }  from '@angular/router';

const template = require('./admin-page.component.html');

@Component({
  selector: 'admin-page',
  template: template
})
export class AdminPageComponent {
  constructor(
    private _router: Router
  ) {}

  ngOnInit() {
    this._router.navigate(['admin', 'user']);
  }
};
