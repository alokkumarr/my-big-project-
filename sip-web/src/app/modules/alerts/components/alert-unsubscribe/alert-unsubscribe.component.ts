import { Component, OnInit } from '@angular/core';
import { AlertUnsubscribeService } from './alert-unsubscribe.service';
import { ActivatedRoute, Router } from '@angular/router';
import { JwtService } from './../../../../common/services/jwt.service';
import { UserService } from './../../../../common/services/user.service';
import { ToastService } from '../../../../common/services/toastMessage.service';
import * as get from 'lodash/get';

@Component({
  selector: 'alert-unsubscribe',
  templateUrl: './alert-unsubscribe.component.html',
  styleUrls: ['./alert-unsubscribe.component.scss']
})
export class AlertUnsubscribe implements OnInit {
  userLoggedIn: boolean;
  alertToken: string;
  alertDetails = {
    alertId: '',
    alertDesc: ''
  };

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
      this.alertToken = token;
      this.alertDetails = {
        alertId: get(this._jwtService.parseJWT(token), 'AlertSubscriber.alertRuleName') || '',
        alertDesc: get(this._jwtService.parseJWT(token), 'AlertSubscriber.alertRuleDescription' || '')
      }
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
