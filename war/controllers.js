var phonecatApp = angular.module('phonecatApp', []);
 
phonecatApp.controller('PhoneListCtrl', function ($scope) {
  $scope.translates = [
    {'name': 'Nexus S',
     'snippet': 'Fast just got faster with Nexus S.'},
    {'name': 'Motorola XOOM™ with Wi-Fi',
     'snippet': 'The Next, Next Generation tablet.'},
    {'name': 'MOTOROLA XOOM™',
     'snippet': 'The Next, Next Generation tablet.'}
  ];
  
  $scope.contextOfWord = [
    {'name': 'Nexus S asfdasdf',
     'snippet': 'Fast just got faster with Nexus S.'},
    {'name': 'Motorola XOOM™ with Wi-Fi',
     'snippet': 'The Next, Next Generation tablet.'},
    {'name': 'MOTOROLA XOOM™',
     'snippet': 'The Next, Next Generation tablet.'}
  ];
  
  $scope.showTranslates = function() {
    $scope.myValue_tr = false;
    $scope.myValue_co = true;
  };
  
  $scope.showContextItems = function() {
    $scope.myValue_tr = true;
    $scope.myValue_co = false;
  };
  
  $scope.myValue_tr = true;
});