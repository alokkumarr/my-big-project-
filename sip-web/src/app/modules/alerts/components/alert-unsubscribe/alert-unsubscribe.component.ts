import { Component, OnInit } from '@angular/core';
import { AlertUnsubscribeService } from './alert-unsubscribe.service';
import { ActivatedRoute, Router } from '@angular/router';
import { JwtService } from './../../../../common/services/jwt.service';
import { UserService } from './../../../../common/services/user.service';
import { ToastService } from '../../../../common/services/toastMessage.service';

@Component({
  selector: 'alert-unsubscribe',
  templateUrl: './alert-unsubscribe.component.html',
  styleUrls: ['./alert-unsubscribe.component.scss']
})
export class AlertUnsubscribe implements OnInit {
  userLoggedIn: boolean;
  alertToken: string;
  parsedToken: {};

  constructor(
    private _alertUnsubscribeService: AlertUnsubscribeService,
    private _jwtService: JwtService,
    private _userService: UserService,
    public _router: Router,
    public _route: ActivatedRoute,
    public _toastMessage: ToastService,
  ) { }

  ngOnInit() {
    this.userLoggedIn = this._userService.isLoggedIn();
    this._route.queryParams.subscribe(({ token }) => {
      this.parsedToken = this._jwtService.parseJWT(token);
      this.alertToken = token;
    });
  }

  unSubscribeAlert() {
    this._alertUnsubscribeService.unsubscribeAnAlert(this.alertToken).subscribe(data => {
      this._toastMessage.success(data);
    });
  }

  close() {
    this._router.navigate(['']);
  }
}
