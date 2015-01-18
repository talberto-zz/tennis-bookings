(function() {
  var app = angular.module("TennisReservations", []);
  
  app.controller("ReservationListCtrl", function($http, $scope, $log) {
    // Get the list of requests
    $http.get("/requests").
      success(function(data, status, headers, config) {
        $log.debug("Succesfully retrieved list of requests: " + data);
        $scope.requests = data;
      }).
      error(function(data, status, headers, config) {
        $log.error("Error downloading the list of requests");
      });
  });
})();