import { Component, OnInit } from '@angular/core';
import { UserService } from '../../../common/services';
import { Router } from '@angular/router';
import { FormControl, Validators } from '@angular/forms';
@Component({
  selector: 'password-pre-reset',
  templateUrl: 'password-pre-reset.component.html',
  styleUrls: ['./password-pre-reset.component.scss']
})
export class PasswordPreResetComponent implements OnInit {
  constructor(private _UserService: UserService, public _router: Router) {}

  public errorMsg;
  public loginIdControl = new FormControl('', Validators.required);

  ngOnInit() {
    this.errorMsg = '';
  }

  resetPwd() {
    const loginId = this.loginIdControl.value;
    this._UserService.preResetPwd({ loginId }).then((res: any) => {
      this.errorMsg = res.validityMessage;
    });
  }

  login() {
    this._router.navigate(['/#/login']);
  }
}
