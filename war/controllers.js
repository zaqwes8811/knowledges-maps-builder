var phonecatApp = angular.module('phonecatApp', []);
 
phonecatApp.controller('PhoneListCtrl', function ($scope) {
  $scope.translates = ['Хор', 'Хелло','MOTOROLA XOOM'];

  $scope.samples = ['Nexus S', 'Motorola XOOM with Wi-Fi','MOTOROLA XOOM'];
  
  $scope.showSamples = function() {
     $scope.contextSamplesHided = false;
     $scope.translateHided = true;
  };

  $scope.showTranslates = function() {
     $scope.contextSamplesHided = true;
     $scope.translateHided = false;
  };

  $scope.word = "My word";

  $scope.contextSamplesHided = false;

  $scope.translateHided = true;  // Сперва контекст
});