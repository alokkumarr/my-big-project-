import 'fonts/icomoon.css';
import 'zone.js';
import '../../../../themes/_triton.scss';
import 'reflect-metadata';
import { NgModule, enableProdMode } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { UpgradeModule } from '@angular/upgrade/static';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { FlexLayoutModule } from '@angular/flex-layout';
import { RouterModule } from '@angular/router';

import { routes } from './routes';
import { MaterialModule } from '../app/material.module';
import { JwtService } from './services/jwt.service';
import { UserService } from './services/user.service';
import { IsUserNotLoggedInGuard } from './guards';

import { LayoutContentComponent, LayoutFooterComponent } from './layout';

import {
  LoginComponent,
  PasswordChangeComponent,
  PasswordPreResetComponent,
  PasswordResetComponent
} from './components';

const COMPONENTS = [
  LayoutContentComponent,
  LoginComponent,
  PasswordChangeComponent,
  PasswordPreResetComponent,
  PasswordResetComponent,
  LayoutFooterComponent
];

const SERVICES = [JwtService, UserService];

const GUARDS = [IsUserNotLoggedInGuard];
@NgModule({
  imports: [
    BrowserModule,
    RouterModule.forRoot(routes, { useHash: true }),
    UpgradeModule,
    HttpClientModule,
    FormsModule,
    MaterialModule,
    FlexLayoutModule
  ],
  declarations: COMPONENTS,
  entryComponents: COMPONENTS,
  bootstrap: [LayoutContentComponent],
  providers: [...SERVICES, ...GUARDS]
})
export class NewLoginModule {}

if (__PRODUCTION__) {
  enableProdMode();
}
platformBrowserDynamic().bootstrapModule(NewLoginModule);
