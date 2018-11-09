import { Component, OnInit } from '@angular/core';
import { UserService } from '../../../common/services';
import { Router } from '@angular/router';

@Component({
  selector: 'password-pre-reset',
  templateUrl: 'password-pre-reset.component.html',
  styleUrls: ['./password-pre-reset.component.scss']
})
export class PasswordPreResetComponent implements OnInit {
  constructor(private _UserService: UserService, public _router: Router) {}

  public dataHolder = {
    masterLoginId: null
  };

  public errorMsg;

  ngOnInit() {
    this.errorMsg = '';
  }

  resetPwd() {
    this._UserService.preResetPwd(this.dataHolder).then((res: any) => {
      this.errorMsg = res.validityMessage;
    });
  }

  login() {
    this._router.navigate(['login']);
  }
}
