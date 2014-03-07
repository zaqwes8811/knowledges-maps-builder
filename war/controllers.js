var phonecatApp = angular.module('phonecatApp', []);
 
phonecatApp.controller('PhoneListCtrl', function ($scope) {
  $scope.translates = ['Хор', 'Хелло','MOTOROLA XOOM'];
  $scope.samples = ['Nexus S', 'Motorola XOOM with Wi-Fi','MOTOROLA XOOM'];
  $scope.word = "My word";
  
  $scope.showSamples = function() {
     $scope.contextSamplesHided = false;
     $scope.translateHided = true;
  };

  $scope.showTranslates = function() {
     $scope.contextSamplesHided = true;
     $scope.translateHided = false;
  };

  // Сперва контекст
  $scope.contextSamplesHided = false;
  $scope.translateHided = true;  
});