/*app.config(function($routeProvider){
    $routeProvider
        .when('/login',{
            templateUrl: 'resources/static/common/login/login.html',
            controller: 'LoginController'
        })
        .when('/logout',{
            templateUrl: 'resources/static/common/login/logout.html',
            controller: 'LogoutController'
        })
        .otherwise(
            { redirectTo: '/'}
        );
});*/
var app = angular.module('app', ['ngRoute','ngResource']);
app.config(function($routeProvider){
    $routeProvider
        .when('/login',{
            templateUrl: 'resources/static/common/login/views/login.html',
            controller: 'LoginController'
        })
        .when('/logout',{
            templateUrl: 'resources/static/common/login/views/logout.html',
            controller: 'LogoutController'
        })
        .otherwise(
            { redirectTo: '/'}
        );
});

