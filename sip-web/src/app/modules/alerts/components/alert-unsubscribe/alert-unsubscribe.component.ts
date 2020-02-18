import { Component, OnInit } from '@angular/core';
import { AlertUnsubscribeService } from './alert-unsubscribe.service';
import { ActivatedRoute, Router } from '@angular/router';
import { JwtService } from './../../../../common/services/jwt.service';
import { UserService } from './../../../../common/services/user.service';

@Component({
  selector: 'alert-unsubscribe',
  templateUrl: './alert-unsubscribe.component.html',
  styleUrls: ['./alert-unsubscribe.component.scss']
})
export class AlertUnsubscribe implements OnInit {
  userLoggedIn: boolean;

  constructor(
    private _alertUnsubscribeService: AlertUnsubscribeService,
    private _jwtService: JwtService,
    private _userService: UserService,
    public _router: Router,
    public _route: ActivatedRoute
  ) { }

  ngOnInit() {
    this.userLoggedIn = this._userService.isLoggedIn();
    console.log(this.userLoggedIn);
    this._route.queryParams.subscribe(({ token }) => {
      console.log({token});
      console.log(this._jwtService.parseJWT(token));
    });
  }

  unSubscribeAlert() {
    const token = 'token';
    this._alertUnsubscribeService.unsubscribeAnAlert(token).subscribe(data => {
      console.log(data);
    });
  }

  close() {
    this._router.navigate([]);
  }
}
