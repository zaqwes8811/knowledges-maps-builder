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
>>>>>>> c5983ef610c841e143a85c560ff67ca58e71ffb2
});