var phonecatApp = angular.module('phonecatApp', []);
 
phonecatApp.controller('PhoneListCtrl', function ($scope, $http) {
  $scope.translates = [];
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
  
  $scope.getNewWord = function() {
	  $http.get('/pkg').success(function(data) {
		  $scope.word = data[1][1][0];
		  $scope.samples = data[1][0];
	  });
  };

  // Сперва контекст
  $scope.contextSamplesHided = false;
  $scope.translateHided = true;  
});