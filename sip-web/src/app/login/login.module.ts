import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { FlexLayoutModule } from '@angular/flex-layout';

import { MaterialModule } from '../material.module';
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

const GUARDS = [IsUserNotLoggedInGuard];
@NgModule({
  imports: [
    RouterModule.forChild(routes),
    HttpClientModule,
    FormsModule,
    MaterialModule,
    FlexLayoutModule
  ],
  declarations: COMPONENTS,
  entryComponents: COMPONENTS,
  providers: [...GUARDS],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  exports: COMPONENTS
})
export class LoginModule {}
