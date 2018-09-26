import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { FlexLayoutModule } from '@angular/flex-layout';

import { MaterialModule } from '../material.module';
import { JwtService, UserService } from '../common/services';
import { IsUserNotLoggedInGuard } from './guards';

import {
  LoginComponent,
  PasswordChangeComponent,
  PasswordPreResetComponent,
  PasswordResetComponent
} from './components';
import { LoginPageComponent } from './page';
import { routes } from './routes';

const COMPONENTS = [
  LoginPageComponent,
  LoginComponent,
  PasswordChangeComponent,
  PasswordPreResetComponent,
  PasswordResetComponent
];

const SERVICES = [JwtService, UserService];

const GUARDS = [IsUserNotLoggedInGuard];
@NgModule({
  imports: [
    RouterModule.forChild(routes),
    BrowserModule,
    HttpClientModule,
    FormsModule,
    MaterialModule,
    FlexLayoutModule
  ],
  declarations: COMPONENTS,
  entryComponents: COMPONENTS,
  providers: [...SERVICES, ...GUARDS],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  exports: COMPONENTS
})
export class LoginModule {}
