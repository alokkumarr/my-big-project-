import templateUrl from './home.component.html';

class HomeController {
  constructor() {
    this.hello = 'Hello Home controller!';
    this.badgeCount = 5;

    this.incBadgeCount = () => {
      this.badgeCount++;
    };

    this.decBadgeCount = () => {
      this.badgeCount--;
    };
  }
}

export const HomeComponent = {
  template: templateUrl,
  controller: HomeController
};
