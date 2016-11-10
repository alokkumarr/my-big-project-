class AppHeaderCtrl {
  constructor(User,AppConstants) {
    'ngInject';
    this._User = User;
    this.appName = AppConstants.appName;
  }
  logout() {
	this._User.logout();
  }
}


let AppHeader = {
  controller: AppHeaderCtrl,
  templateUrl: 'layout/header.html'
};

export default AppHeader;
