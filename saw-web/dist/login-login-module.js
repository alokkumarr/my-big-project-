(window["webpackJsonp"] = window["webpackJsonp"] || []).push([["login-login-module"],{

/***/ "./src/app/login/components/index.ts":
/*!*******************************************!*\
  !*** ./src/app/login/components/index.ts ***!
  \*******************************************/
/*! exports provided: LoginComponent, PasswordChangeComponent, PasswordPreResetComponent, PasswordResetComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _login__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./login */ "./src/app/login/components/login/index.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "LoginComponent", function() { return _login__WEBPACK_IMPORTED_MODULE_0__["LoginComponent"]; });

/* harmony import */ var _password_change__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./password-change */ "./src/app/login/components/password-change/index.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "PasswordChangeComponent", function() { return _password_change__WEBPACK_IMPORTED_MODULE_1__["PasswordChangeComponent"]; });

/* harmony import */ var _password_pre_reset__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./password-pre-reset */ "./src/app/login/components/password-pre-reset/index.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "PasswordPreResetComponent", function() { return _password_pre_reset__WEBPACK_IMPORTED_MODULE_2__["PasswordPreResetComponent"]; });

/* harmony import */ var _password_reset__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./password-reset */ "./src/app/login/components/password-reset/index.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "PasswordResetComponent", function() { return _password_reset__WEBPACK_IMPORTED_MODULE_3__["PasswordResetComponent"]; });







/***/ }),

/***/ "./src/app/login/components/login/index.ts":
/*!*************************************************!*\
  !*** ./src/app/login/components/login/index.ts ***!
  \*************************************************/
/*! exports provided: LoginComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _login_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./login.component */ "./src/app/login/components/login/login.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "LoginComponent", function() { return _login_component__WEBPACK_IMPORTED_MODULE_0__["LoginComponent"]; });




/***/ }),

/***/ "./src/app/login/components/login/login.component.html":
/*!*************************************************************!*\
  !*** ./src/app/login/components/login/login.component.html ***!
  \*************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<div>\n  <h1>Login</h1>\n\n  <div class=\"login-info\">\n    <span>Use the login credentials given at time of sign up. If you don't</span>\n    <br />\n    <span>have an account yet contact your system administrator</span>\n  </div>\n\n  <form #loginForm=\"ngForm\" name=\"loginForm\" class=\"login-form\" method=\"post\">\n    <div class=\"error-msg\" e2e=\"error-msg-section\" [innerHtml]=\"states.error\"></div>\n\n    <mat-form-field>\n      <input matInput maxlength=\"30\" [(ngModel)]=\"dataHolder.username\" [ngModelOptions]=\"{standalone: true}\"\n        placeholder=\"Username\" e2e=\"username-input\" required />\n    </mat-form-field>\n    <br />\n\n    <mat-form-field>\n      <input matInput type=\"password\" maxlength=\"30\" [(ngModel)]=\"dataHolder.password\" placeholder=\"Password\"\n        [ngModelOptions]=\"{standalone: true}\" e2e=\"password-input\" required />\n    </mat-form-field>\n    <div class=\"login-buttons button-row\">\n      <div class=\"forgot-password\">\n        <a (click)=\"reset()\" e2e=\"forgot-password\">Forgot Password?</a>\n      </div>\n      <button mat-raised-button color=\"primary\" e2e=\"login-btn\" type=\"submit\" (click)=\"login()\">Login</button>\n    </div>\n  </form>\n</div>\n"

/***/ }),

/***/ "./src/app/login/components/login/login.component.scss":
/*!*************************************************************!*\
  !*** ./src/app/login/components/login/login.component.scss ***!
  \*************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ".login-form mat-form-field {\n  width: 100%; }\n\n.login-buttons .mat-button {\n  padding: 0 !important; }\n\n.login-buttons .forgot-password {\n  margin-top: 10px;\n  float: left; }\n\n.login-buttons .mat-raised-button {\n  float: right; }\n\nlayout-footer .powered-logo-container {\n  text-align: center; }\n\nlayout-footer .powered-logo-container .powered-logo {\n    margin: 0 auto; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9sb2dpbi9jb21wb25lbnRzL2xvZ2luL2xvZ2luLmNvbXBvbmVudC5zY3NzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiJBQUFBO0VBQ0UsV0FBVyxFQUFBOztBQUdiO0VBRUkscUJBQXFCLEVBQUE7O0FBRnpCO0VBTUksZ0JBQWdCO0VBQ2hCLFdBQVcsRUFBQTs7QUFQZjtFQVdJLFlBQVksRUFBQTs7QUFJaEI7RUFDRSxrQkFBa0IsRUFBQTs7QUFEcEI7SUFJSSxjQUFjLEVBQUEiLCJmaWxlIjoic3JjL2FwcC9sb2dpbi9jb21wb25lbnRzL2xvZ2luL2xvZ2luLmNvbXBvbmVudC5zY3NzIiwic291cmNlc0NvbnRlbnQiOlsiLmxvZ2luLWZvcm0gbWF0LWZvcm0tZmllbGQge1xuICB3aWR0aDogMTAwJTtcbn1cblxuLmxvZ2luLWJ1dHRvbnMge1xuICAubWF0LWJ1dHRvbiB7XG4gICAgcGFkZGluZzogMCAhaW1wb3J0YW50O1xuICB9XG5cbiAgLmZvcmdvdC1wYXNzd29yZCB7XG4gICAgbWFyZ2luLXRvcDogMTBweDtcbiAgICBmbG9hdDogbGVmdDtcbiAgfVxuXG4gIC5tYXQtcmFpc2VkLWJ1dHRvbiB7XG4gICAgZmxvYXQ6IHJpZ2h0O1xuICB9XG59XG5cbmxheW91dC1mb290ZXIgLnBvd2VyZWQtbG9nby1jb250YWluZXIge1xuICB0ZXh0LWFsaWduOiBjZW50ZXI7XG5cbiAgLnBvd2VyZWQtbG9nbyB7XG4gICAgbWFyZ2luOiAwIGF1dG87XG4gIH1cbn1cblxuIl19 */"

/***/ }),

/***/ "./src/app/login/components/login/login.component.ts":
/*!***********************************************************!*\
  !*** ./src/app/login/components/login/login.component.ts ***!
  \***********************************************************/
/*! exports provided: LoginComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LoginComponent", function() { return LoginComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/router */ "./node_modules/@angular/router/fesm5/router.js");
/* harmony import */ var lodash_isEmpty__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! lodash/isEmpty */ "./node_modules/lodash/isEmpty.js");
/* harmony import */ var lodash_isEmpty__WEBPACK_IMPORTED_MODULE_3___default = /*#__PURE__*/__webpack_require__.n(lodash_isEmpty__WEBPACK_IMPORTED_MODULE_3__);
/* harmony import */ var _common_services__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../../../common/services */ "./src/app/common/services/index.ts");





var LoginComponent = /** @class */ (function () {
    function LoginComponent(_JwtService, _UserService, _configService, _router, _route) {
        this._JwtService = _JwtService;
        this._UserService = _UserService;
        this._configService = _configService;
        this._router = _router;
        this._route = _route;
        this.dataHolder = {
            username: null,
            password: null
        };
        this.states = {
            error: null
        };
    }
    LoginComponent.prototype.ngOnInit = function () {
        var _this = this;
        this.states.error = '';
        this._route.queryParams.subscribe(function (_a) {
            var changePassMsg = _a.changePassMsg;
            if (changePassMsg) {
                _this.states.error = changePassMsg;
            }
        });
    };
    LoginComponent.prototype.login = function () {
        var _this = this;
        if (lodash_isEmpty__WEBPACK_IMPORTED_MODULE_3__(this.dataHolder.username) || lodash_isEmpty__WEBPACK_IMPORTED_MODULE_3__(this.dataHolder.password)) {
            this.states.error = 'Please enter a valid Username and Password';
            return false;
        }
        var params = {
            masterLoginId: this.dataHolder.username,
            authpwd: this.dataHolder.password
        };
        this._UserService.attemptAuth(params).then(function (data) {
            _this.states.error = '';
            if (_this._JwtService.isValid(data)) {
                _this._configService.getConfig().subscribe(function () {
                    _this._router.navigate(['']);
                }, function () {
                    _this._router.navigate(['']);
                });
            }
            else {
                _this.states.error = _this._JwtService.getValidityReason(data);
            }
        });
    };
    LoginComponent.prototype.reset = function () {
        this._router.navigate(['login', 'preResetPwd']);
    };
    LoginComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'login-form',
            template: __webpack_require__(/*! ./login.component.html */ "./src/app/login/components/login/login.component.html"),
            styles: [__webpack_require__(/*! ./login.component.scss */ "./src/app/login/components/login/login.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_common_services__WEBPACK_IMPORTED_MODULE_4__["JwtService"],
            _common_services__WEBPACK_IMPORTED_MODULE_4__["UserService"],
            _common_services__WEBPACK_IMPORTED_MODULE_4__["ConfigService"],
            _angular_router__WEBPACK_IMPORTED_MODULE_2__["Router"],
            _angular_router__WEBPACK_IMPORTED_MODULE_2__["ActivatedRoute"]])
    ], LoginComponent);
    return LoginComponent;
}());



/***/ }),

/***/ "./src/app/login/components/password-change/index.ts":
/*!***********************************************************!*\
  !*** ./src/app/login/components/password-change/index.ts ***!
  \***********************************************************/
/*! exports provided: PasswordChangeComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _password_change_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./password-change.component */ "./src/app/login/components/password-change/password-change.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "PasswordChangeComponent", function() { return _password_change_component__WEBPACK_IMPORTED_MODULE_0__["PasswordChangeComponent"]; });




/***/ }),

/***/ "./src/app/login/components/password-change/password-change.component.html":
/*!*********************************************************************************!*\
  !*** ./src/app/login/components/password-change/password-change.component.html ***!
  \*********************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<div>\n  <h1 e2e=\"Change-Password\">Change Password</h1>\n  <div class=\"error-msg\" e2e=\"error-msg\" [innerHtml]=\"errorMsg\"></div>\n  <form name=\"changePwdForm\" method=\"post\">\n    <mat-form-field class=\"changePwd-formField\">\n      <input matInput type=\"password\" [(ngModel)]=\"formData.oldPwd\" [ngModelOptions]=\"{standalone: true}\" placeholder=\"Old Password\" required />\n    </mat-form-field>\n    <br/>\n\n    <mat-form-field  class=\"changePwd-formField\">\n      <input matInput type=\"password\" [(ngModel)]=\"formData.newPwd\" [ngModelOptions]=\"{standalone: true}\" placeholder=\"New Password\" required />\n    </mat-form-field>\n    <br/>\n\n    <mat-form-field  class=\"changePwd-formField\">\n      <input matInput type=\"password\" [(ngModel)]=\"formData.confNewPwd\" e2e=\"confirm-password\" [ngModelOptions]=\"{standalone: true}\" placeholder=\"Confirm Password\" required />\n    </mat-form-field>\n    <br/>\n\n    <div class=\"login-buttons\" layout=\"row\">\n      <button (click)=\"changePwd()\" class=\"changePwd-change\"\n            mat-raised-button color=\"primary\" [disabled]=\"!formData.oldPwd || !formData.newPwd || !formData.confNewPwd\" i18n>\n        Change\n      </button>\n      <button mat-button class=\"changePwd-cancel\" i18n (click)=\"cancel()\">Cancel</button>\n    </div>\n  </form>\n</div>\n"

/***/ }),

/***/ "./src/app/login/components/password-change/password-change.component.scss":
/*!*********************************************************************************!*\
  !*** ./src/app/login/components/password-change/password-change.component.scss ***!
  \*********************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ".changePwd-change {\n  float: left !important; }\n\n.changePwd-formField {\n  width: 420px !important; }\n\n.changePwd-cancel {\n  margin-left: 10px !important; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9sb2dpbi9jb21wb25lbnRzL3Bhc3N3b3JkLWNoYW5nZS9wYXNzd29yZC1jaGFuZ2UuY29tcG9uZW50LnNjc3MiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBQUE7RUFDRSxzQkFBc0IsRUFBQTs7QUFHeEI7RUFDRSx1QkFBdUIsRUFBQTs7QUFHekI7RUFDRSw0QkFBNEIsRUFBQSIsImZpbGUiOiJzcmMvYXBwL2xvZ2luL2NvbXBvbmVudHMvcGFzc3dvcmQtY2hhbmdlL3Bhc3N3b3JkLWNoYW5nZS5jb21wb25lbnQuc2NzcyIsInNvdXJjZXNDb250ZW50IjpbIi5jaGFuZ2VQd2QtY2hhbmdlIHtcbiAgZmxvYXQ6IGxlZnQgIWltcG9ydGFudDtcbn1cblxuLmNoYW5nZVB3ZC1mb3JtRmllbGQge1xuICB3aWR0aDogNDIwcHggIWltcG9ydGFudDtcbn1cblxuLmNoYW5nZVB3ZC1jYW5jZWwge1xuICBtYXJnaW4tbGVmdDogMTBweCAhaW1wb3J0YW50O1xufVxuIl19 */"

/***/ }),

/***/ "./src/app/login/components/password-change/password-change.component.ts":
/*!*******************************************************************************!*\
  !*** ./src/app/login/components/password-change/password-change.component.ts ***!
  \*******************************************************************************/
/*! exports provided: PasswordChangeComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "PasswordChangeComponent", function() { return PasswordChangeComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/router */ "./node_modules/@angular/router/fesm5/router.js");
/* harmony import */ var _common_services__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../../../common/services */ "./src/app/common/services/index.ts");




var PasswordChangeComponent = /** @class */ (function () {
    function PasswordChangeComponent(_JwtService, _UserService, _router) {
        this._JwtService = _JwtService;
        this._UserService = _UserService;
        this._router = _router;
        this.formData = {
            oldPwd: null,
            newPwd: null,
            confNewPwd: null
        };
    }
    PasswordChangeComponent.prototype.changePwd = function () {
        var _this = this;
        var token = this._JwtService.get();
        if (!token) {
            this.errorMsg = 'Please login to change password';
            return;
        }
        this._UserService.changePwd(this).then(function (res) {
            if (res.valid) {
                _this._UserService.logout('logout').then(function () {
                    _this._router.navigate(['login'], {
                        queryParams: { changePassMsg: res.validityMessage }
                    });
                });
            }
            else {
                _this.errorMsg = res.validityMessage;
            }
        });
    };
    PasswordChangeComponent.prototype.cancel = function () {
        this._router.navigate(['login']);
    };
    PasswordChangeComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'password-change',
            template: __webpack_require__(/*! ./password-change.component.html */ "./src/app/login/components/password-change/password-change.component.html"),
            styles: [__webpack_require__(/*! ./password-change.component.scss */ "./src/app/login/components/password-change/password-change.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_common_services__WEBPACK_IMPORTED_MODULE_3__["JwtService"],
            _common_services__WEBPACK_IMPORTED_MODULE_3__["UserService"],
            _angular_router__WEBPACK_IMPORTED_MODULE_2__["Router"]])
    ], PasswordChangeComponent);
    return PasswordChangeComponent;
}());



/***/ }),

/***/ "./src/app/login/components/password-pre-reset/index.ts":
/*!**************************************************************!*\
  !*** ./src/app/login/components/password-pre-reset/index.ts ***!
  \**************************************************************/
/*! exports provided: PasswordPreResetComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _password_pre_reset_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./password-pre-reset.component */ "./src/app/login/components/password-pre-reset/password-pre-reset.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "PasswordPreResetComponent", function() { return _password_pre_reset_component__WEBPACK_IMPORTED_MODULE_0__["PasswordPreResetComponent"]; });




/***/ }),

/***/ "./src/app/login/components/password-pre-reset/password-pre-reset.component.html":
/*!***************************************************************************************!*\
  !*** ./src/app/login/components/password-pre-reset/password-pre-reset.component.html ***!
  \***************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<div class=\"reset-password-form\">\n  <h1 e2e=\"Reset-Password\">Reset Password</h1>\n  <div class=\"error-msg\" e2e=\"error-msg\" [innerHtml]=\"errorMsg\"></div>\n  <form name=\"changePwdForm\" method=\"post\">\n    <mat-form-field  class=\"changePwd-formField\">\n      <input matInput maxlength=\"30\" [(ngModel)]=\"dataHolder.masterLoginId\" [ngModelOptions]=\"{standalone: true}\" placeholder=\"Username\" e2e=\"Username\" required />\n    </mat-form-field>\n    <br/>\n    <div class=\"login-buttons\" layout=\"row\" layout-align=\"space-between center\">\n      <button mat-raised-button color=\"primary\" class=\"changePwd-change\" type=\"submit\" [disabled]=\"!dataHolder.masterLoginId\" (click)=\"resetPwd()\" e2e=\"Reset\">Reset</button>\n      <button mat-raised-button color=\"primary\" class=\"changePwd-change changePwd-cancel mat-primary\" (click)=\"login()\" e2e=\"Login\">Login</button>\n    </div>\n  </form>\n</div>\n"

/***/ }),

/***/ "./src/app/login/components/password-pre-reset/password-pre-reset.component.scss":
/*!***************************************************************************************!*\
  !*** ./src/app/login/components/password-pre-reset/password-pre-reset.component.scss ***!
  \***************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ".reset-password-form {\n  width: 300px; }\n  .reset-password-form form mat-form-field {\n    width: 100% !important; }\n  .changePwd-cancel {\n  margin-left: 10px !important; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9sb2dpbi9jb21wb25lbnRzL3Bhc3N3b3JkLXByZS1yZXNldC9wYXNzd29yZC1wcmUtcmVzZXQuY29tcG9uZW50LnNjc3MiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBQUE7RUFDRSxZQUFZLEVBQUE7RUFEZDtJQUlJLHNCQUFzQixFQUFBO0VBSTFCO0VBQ0UsNEJBQTRCLEVBQUEiLCJmaWxlIjoic3JjL2FwcC9sb2dpbi9jb21wb25lbnRzL3Bhc3N3b3JkLXByZS1yZXNldC9wYXNzd29yZC1wcmUtcmVzZXQuY29tcG9uZW50LnNjc3MiLCJzb3VyY2VzQ29udGVudCI6WyIucmVzZXQtcGFzc3dvcmQtZm9ybSB7XG4gIHdpZHRoOiAzMDBweDtcblxuICBmb3JtIG1hdC1mb3JtLWZpZWxkIHtcbiAgICB3aWR0aDogMTAwJSAhaW1wb3J0YW50O1xuICB9XG59XG5cbi5jaGFuZ2VQd2QtY2FuY2VsIHtcbiAgbWFyZ2luLWxlZnQ6IDEwcHggIWltcG9ydGFudDtcbn1cbiJdfQ== */"

/***/ }),

/***/ "./src/app/login/components/password-pre-reset/password-pre-reset.component.ts":
/*!*************************************************************************************!*\
  !*** ./src/app/login/components/password-pre-reset/password-pre-reset.component.ts ***!
  \*************************************************************************************/
/*! exports provided: PasswordPreResetComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "PasswordPreResetComponent", function() { return PasswordPreResetComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _common_services__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../../../common/services */ "./src/app/common/services/index.ts");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/router */ "./node_modules/@angular/router/fesm5/router.js");




var PasswordPreResetComponent = /** @class */ (function () {
    function PasswordPreResetComponent(_UserService, _router) {
        this._UserService = _UserService;
        this._router = _router;
        this.dataHolder = {
            masterLoginId: null
        };
    }
    PasswordPreResetComponent.prototype.ngOnInit = function () {
        this.errorMsg = '';
    };
    PasswordPreResetComponent.prototype.resetPwd = function () {
        var _this = this;
        this._UserService.preResetPwd(this.dataHolder).then(function (res) {
            _this.errorMsg = res.validityMessage;
        });
    };
    PasswordPreResetComponent.prototype.login = function () {
        this._router.navigate(['/#/login']);
    };
    PasswordPreResetComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'password-pre-reset',
            template: __webpack_require__(/*! ./password-pre-reset.component.html */ "./src/app/login/components/password-pre-reset/password-pre-reset.component.html"),
            styles: [__webpack_require__(/*! ./password-pre-reset.component.scss */ "./src/app/login/components/password-pre-reset/password-pre-reset.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_common_services__WEBPACK_IMPORTED_MODULE_2__["UserService"], _angular_router__WEBPACK_IMPORTED_MODULE_3__["Router"]])
    ], PasswordPreResetComponent);
    return PasswordPreResetComponent;
}());



/***/ }),

/***/ "./src/app/login/components/password-reset/index.ts":
/*!**********************************************************!*\
  !*** ./src/app/login/components/password-reset/index.ts ***!
  \**********************************************************/
/*! exports provided: PasswordResetComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _password_reset_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./password-reset.component */ "./src/app/login/components/password-reset/password-reset.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "PasswordResetComponent", function() { return _password_reset_component__WEBPACK_IMPORTED_MODULE_0__["PasswordResetComponent"]; });




/***/ }),

/***/ "./src/app/login/components/password-reset/password-reset.component.html":
/*!*******************************************************************************!*\
  !*** ./src/app/login/components/password-reset/password-reset.component.html ***!
  \*******************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<div>\n  <h1 e2e=\"Reset-Password\">Reset Password</h1>\n  <div class=\"error-msg\" e2e=\"error-msg\" [innerHtml]=\"errorMsg\"></div>\n  <form name=\"resetPwdForm\" method=\"post\">\n    <mat-form-field class=\"changePwd-formField\">\n      <input matInput type=\"password\" maxlength=\"30\" [(ngModel)]=\"newPwd\"  placeholder=\"New Password\" e2e=\"New-Password\" [ngModelOptions]=\"{standalone: true}\" required />\n    </mat-form-field>\n    <br/>\n\n    <mat-form-field class=\"changePwd-formField\">\n      <input matInput type=\"password\" maxlength=\"30\" [(ngModel)]=\"confNewPwd\"  placeholder=\"Confirm New Password\" e2e=\"Confirm-New-Password\" [ngModelOptions]=\"{standalone: true}\" required />\n    </mat-form-field>\n    <br/>\n\n\n    <div class=\"login-buttons\" layout=\"row\" layout-align=\"space-between center\">\n      <button mat-raised-button color=\"primary\" class=\"changePwd-change\"  e2e=\"Change-btn\" type=\"submit\" [disabled]=\"!newPwd || !confNewPwd\"  (click)=\"resetPwd()\">Reset</button>\n      <button mat-raised-button color=\"primary\" class=\"changePwd-change changePwd-cancel mat-primary\" e2e=\"Cancel-btn\"  (click)=\"login()\">Login</button>\n    </div>\n  </form>\n</div>\n"

/***/ }),

/***/ "./src/app/login/components/password-reset/password-reset.component.scss":
/*!*******************************************************************************!*\
  !*** ./src/app/login/components/password-reset/password-reset.component.scss ***!
  \*******************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ".changePwd-cancel {\n  margin-left: 10px !important; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9sb2dpbi9jb21wb25lbnRzL3Bhc3N3b3JkLXJlc2V0L3Bhc3N3b3JkLXJlc2V0LmNvbXBvbmVudC5zY3NzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiJBQUFBO0VBQ0UsNEJBQTRCLEVBQUEiLCJmaWxlIjoic3JjL2FwcC9sb2dpbi9jb21wb25lbnRzL3Bhc3N3b3JkLXJlc2V0L3Bhc3N3b3JkLXJlc2V0LmNvbXBvbmVudC5zY3NzIiwic291cmNlc0NvbnRlbnQiOlsiLmNoYW5nZVB3ZC1jYW5jZWwge1xuICBtYXJnaW4tbGVmdDogMTBweCAhaW1wb3J0YW50O1xufVxuIl19 */"

/***/ }),

/***/ "./src/app/login/components/password-reset/password-reset.component.ts":
/*!*****************************************************************************!*\
  !*** ./src/app/login/components/password-reset/password-reset.component.ts ***!
  \*****************************************************************************/
/*! exports provided: PasswordResetComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "PasswordResetComponent", function() { return PasswordResetComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var lodash_isEmpty__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! lodash/isEmpty */ "./node_modules/lodash/isEmpty.js");
/* harmony import */ var lodash_isEmpty__WEBPACK_IMPORTED_MODULE_2___default = /*#__PURE__*/__webpack_require__.n(lodash_isEmpty__WEBPACK_IMPORTED_MODULE_2__);
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/router */ "./node_modules/@angular/router/fesm5/router.js");
/* harmony import */ var _common_services__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../../../common/services */ "./src/app/common/services/index.ts");





var PasswordResetComponent = /** @class */ (function () {
    function PasswordResetComponent(_UserService, _router, _route) {
        this._UserService = _UserService;
        this._router = _router;
        this._route = _route;
    }
    PasswordResetComponent.prototype.ngOnInit = function () {
        var _this = this;
        this.errorMsg = '';
        this._route.queryParams.subscribe(function (_a) {
            var rhc = _a.rhc;
            var params = { rhc: rhc };
            _this._UserService.verify(params).then(function (res) {
                if (res.valid) {
                    _this.username = res.masterLoginID;
                    _this.rhcToken = rhc;
                }
                else {
                    _this.errorMsg =
                        res.validityReason + '. Please regenerate the link once again';
                }
            });
        });
    };
    PasswordResetComponent.prototype.resetPwd = function () {
        var _this = this;
        if (lodash_isEmpty__WEBPACK_IMPORTED_MODULE_2__(this.newPwd) || lodash_isEmpty__WEBPACK_IMPORTED_MODULE_2__(this.confNewPwd)) {
            this.errorMsg = 'Please enter all required fields';
        }
        else {
            this._UserService.resetPwd(this).then(function (res) {
                _this.errorMsg = res.validityMessage;
            });
        }
    };
    PasswordResetComponent.prototype.login = function () {
        this._router.navigate(['/#/login']);
    };
    PasswordResetComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'password-reset',
            template: __webpack_require__(/*! ./password-reset.component.html */ "./src/app/login/components/password-reset/password-reset.component.html"),
            styles: [__webpack_require__(/*! ./password-reset.component.scss */ "./src/app/login/components/password-reset/password-reset.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_common_services__WEBPACK_IMPORTED_MODULE_4__["UserService"],
            _angular_router__WEBPACK_IMPORTED_MODULE_3__["Router"],
            _angular_router__WEBPACK_IMPORTED_MODULE_3__["ActivatedRoute"]])
    ], PasswordResetComponent);
    return PasswordResetComponent;
}());



/***/ }),

/***/ "./src/app/login/guards/index.ts":
/*!***************************************!*\
  !*** ./src/app/login/guards/index.ts ***!
  \***************************************/
/*! exports provided: IsUserNotLoggedInGuard */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _not_logged_in_guard_service__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./not-logged-in-guard.service */ "./src/app/login/guards/not-logged-in-guard.service.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "IsUserNotLoggedInGuard", function() { return _not_logged_in_guard_service__WEBPACK_IMPORTED_MODULE_0__["IsUserNotLoggedInGuard"]; });




/***/ }),

/***/ "./src/app/login/guards/not-logged-in-guard.service.ts":
/*!*************************************************************!*\
  !*** ./src/app/login/guards/not-logged-in-guard.service.ts ***!
  \*************************************************************/
/*! exports provided: IsUserNotLoggedInGuard */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "IsUserNotLoggedInGuard", function() { return IsUserNotLoggedInGuard; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/router */ "./node_modules/@angular/router/fesm5/router.js");
/* harmony import */ var _common_services__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../../common/services */ "./src/app/common/services/index.ts");




var IsUserNotLoggedInGuard = /** @class */ (function () {
    function IsUserNotLoggedInGuard(_user, _router) {
        this._user = _user;
        this._router = _router;
    }
    IsUserNotLoggedInGuard.prototype.canActivate = function () {
        return this.isLoggedOut();
    };
    IsUserNotLoggedInGuard.prototype.canActivateChild = function () {
        return this.isLoggedOut();
    };
    IsUserNotLoggedInGuard.prototype.isLoggedOut = function () {
        var _this = this;
        if (this._user.isLoggedIn()) {
            // redirect to app
            setTimeout(function () { return _this._router.navigate(['']); });
            return false;
        }
        return true;
    };
    IsUserNotLoggedInGuard = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Injectable"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_common_services__WEBPACK_IMPORTED_MODULE_3__["UserService"],
            _angular_router__WEBPACK_IMPORTED_MODULE_2__["Router"]])
    ], IsUserNotLoggedInGuard);
    return IsUserNotLoggedInGuard;
}());



/***/ }),

/***/ "./src/app/login/login.module.ts":
/*!***************************************!*\
  !*** ./src/app/login/login.module.ts ***!
  \***************************************/
/*! exports provided: LoginModule */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LoginModule", function() { return LoginModule; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_common_http__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/common/http */ "./node_modules/@angular/common/fesm5/http.js");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/router */ "./node_modules/@angular/router/fesm5/router.js");
/* harmony import */ var _angular_forms__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! @angular/forms */ "./node_modules/@angular/forms/fesm5/forms.js");
/* harmony import */ var _angular_flex_layout__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! @angular/flex-layout */ "./node_modules/@angular/flex-layout/esm5/flex-layout.es5.js");
/* harmony import */ var _material_module__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ../material.module */ "./src/app/material.module.ts");
/* harmony import */ var _guards__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ./guards */ "./src/app/login/guards/index.ts");
/* harmony import */ var _components__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ./components */ "./src/app/login/components/index.ts");
/* harmony import */ var _page__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ./page */ "./src/app/login/page/index.ts");
/* harmony import */ var _routes__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! ./routes */ "./src/app/login/routes.ts");











var COMPONENTS = [
    _page__WEBPACK_IMPORTED_MODULE_9__["LoginPageComponent"],
    _components__WEBPACK_IMPORTED_MODULE_8__["LoginComponent"],
    _components__WEBPACK_IMPORTED_MODULE_8__["PasswordChangeComponent"],
    _components__WEBPACK_IMPORTED_MODULE_8__["PasswordPreResetComponent"],
    _components__WEBPACK_IMPORTED_MODULE_8__["PasswordResetComponent"]
];
var GUARDS = [_guards__WEBPACK_IMPORTED_MODULE_7__["IsUserNotLoggedInGuard"]];
var LoginModule = /** @class */ (function () {
    function LoginModule() {
    }
    LoginModule = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["NgModule"])({
            imports: [
                _angular_router__WEBPACK_IMPORTED_MODULE_3__["RouterModule"].forChild(_routes__WEBPACK_IMPORTED_MODULE_10__["routes"]),
                _angular_common_http__WEBPACK_IMPORTED_MODULE_2__["HttpClientModule"],
                _angular_forms__WEBPACK_IMPORTED_MODULE_4__["FormsModule"],
                _material_module__WEBPACK_IMPORTED_MODULE_6__["MaterialModule"],
                _angular_flex_layout__WEBPACK_IMPORTED_MODULE_5__["FlexLayoutModule"]
            ],
            declarations: COMPONENTS,
            entryComponents: COMPONENTS,
            providers: GUARDS.slice(),
            schemas: [_angular_core__WEBPACK_IMPORTED_MODULE_1__["CUSTOM_ELEMENTS_SCHEMA"]],
            exports: COMPONENTS
        })
    ], LoginModule);
    return LoginModule;
}());



/***/ }),

/***/ "./src/app/login/page/index.ts":
/*!*************************************!*\
  !*** ./src/app/login/page/index.ts ***!
  \*************************************/
/*! exports provided: LoginPageComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _login_page_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./login-page.component */ "./src/app/login/page/login-page.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "LoginPageComponent", function() { return _login_page_component__WEBPACK_IMPORTED_MODULE_0__["LoginPageComponent"]; });




/***/ }),

/***/ "./src/app/login/page/login-page.component.html":
/*!******************************************************!*\
  !*** ./src/app/login/page/login-page.component.html ***!
  \******************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<div class=\"login-view\">\n  <div class=\"bg-container-wrapper\"\n       fxLayout=\"row\"\n       fxLayoutAlign=\"center center\"\n  >\n    <div class=\"bg-container\"\n         fxLayout=\"row\"\n         fxLayoutAlign=\"center center\"\n    >\n      <div class=\"login-container\"\n           fxLayout=\"column\"\n           fxLayoutAlign=\"space-around start\"\n      >\n        <div class=\"brand-logo\" e2e=\"brand-logo\"></div>\n        <router-outlet></router-outlet>\n      </div>\n      <div class=\"middle-logo-container\"></div>\n    </div>\n    <div class=\"white-container\"></div>\n  </div>\n</div>\n"

/***/ }),

/***/ "./src/app/login/page/login-page.component.ts":
/*!****************************************************!*\
  !*** ./src/app/login/page/login-page.component.ts ***!
  \****************************************************/
/*! exports provided: LoginPageComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LoginPageComponent", function() { return LoginPageComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_platform_browser__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/platform-browser */ "./node_modules/@angular/platform-browser/fesm5/platform-browser.js");



var LoginPageComponent = /** @class */ (function () {
    function LoginPageComponent(_title) {
        this._title = _title;
    }
    LoginPageComponent.prototype.ngOnInit = function () {
        this._title.setTitle("Login");
    };
    LoginPageComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'login-page',
            template: __webpack_require__(/*! ./login-page.component.html */ "./src/app/login/page/login-page.component.html"),
            styles: ["\n    :host {\n      height: 100%;\n      display: block;\n    }\n  "]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_platform_browser__WEBPACK_IMPORTED_MODULE_2__["Title"]])
    ], LoginPageComponent);
    return LoginPageComponent;
}());



/***/ }),

/***/ "./src/app/login/routes.ts":
/*!*********************************!*\
  !*** ./src/app/login/routes.ts ***!
  \*********************************/
/*! exports provided: routes */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "routes", function() { return routes; });
/* harmony import */ var _components__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./components */ "./src/app/login/components/index.ts");
/* harmony import */ var _guards__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./guards */ "./src/app/login/guards/index.ts");
/* harmony import */ var _page__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./page */ "./src/app/login/page/index.ts");



var routes = [
    {
        path: '',
        component: _page__WEBPACK_IMPORTED_MODULE_2__["LoginPageComponent"],
        children: [
            {
                path: '',
                component: _components__WEBPACK_IMPORTED_MODULE_0__["LoginComponent"],
                canActivate: [_guards__WEBPACK_IMPORTED_MODULE_1__["IsUserNotLoggedInGuard"]],
                pathMatch: 'full'
            },
            {
                // name: 'changePassword',
                path: 'changePwd',
                component: _components__WEBPACK_IMPORTED_MODULE_0__["PasswordChangeComponent"]
            },
            {
                // name: 'preResetPassword',
                path: 'preResetPwd',
                canActivate: [_guards__WEBPACK_IMPORTED_MODULE_1__["IsUserNotLoggedInGuard"]],
                component: _components__WEBPACK_IMPORTED_MODULE_0__["PasswordPreResetComponent"]
            },
            {
                // name: 'resetPassword',
                path: 'resetPassword',
                canActivate: [_guards__WEBPACK_IMPORTED_MODULE_1__["IsUserNotLoggedInGuard"]],
                component: _components__WEBPACK_IMPORTED_MODULE_0__["PasswordResetComponent"]
            }
        ]
    }
];


/***/ })

}]);
//# sourceMappingURL=login-login-module.js.map