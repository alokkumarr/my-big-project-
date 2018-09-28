import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'login-page',
  templateUrl: './login-page.component.html',
  styles: [`
    :host {
      height: 100%;
      display: block;
    }
  `]
})

export class LoginPageComponent implements OnInit {
  constructor(
    private _title: Title
  ) {}

  ngOnInit() {
    this._title.setTitle(`Login`);
  }
}
