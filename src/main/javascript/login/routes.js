export function routesConfig($stateProvider, $urlRouterProvider) {
  'ngInject';

  // $locationProvider.html5Mode(true);
  $urlRouterProvider.otherwise('/');

  const states = [
    {
      name: 'login',
      url: '/',
      component: 'loginComponent',
      data: {
        title: 'Login'
      }
    },
    {
      name: 'changePassword',
      url: '/changePwd',
      component: 'passwordChangeComponent',
      data: {
        title: 'Change Password'
      }
    },
    {
      name: 'preResetPassword',
      url: '/preResetPwd',
      component: 'passwordPreResetComponent',
      data: {
        title: 'Reset Password'
      }
    },
    {
      name: 'resetPassword',
      url: '/resetPassword',
      component: 'passwordResetComponent',
      data: {
        title: 'Reset Password'
      }
    }
  ];

  states.forEach(state => {
    $stateProvider.state(state);
  });
}
