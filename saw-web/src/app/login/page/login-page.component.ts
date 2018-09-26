import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
const template = require('./login-page.component.html');

@Component({
  selector: 'login-page',
  template
})

export class LoginPageComponent implements OnInit {
  constructor(
    private _title: Title
  ) {}

  ngOnInit() {
    this._title.setTitle(`Login`);
  }
}
