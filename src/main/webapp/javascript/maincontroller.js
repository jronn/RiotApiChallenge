var app = angular.module('myApp', ['angucomplete-alt','nvd3ChartDirectives']);
app.controller('myCtrl', function($scope, $http, $document) {
	
		$scope.loaded = false;
		$scope.champList = [];
		$scope.champdata = null;
		$scope.champdataFull = null;
		$scope.orderBy = 'name';
		$scope.orderReverse = true;
		$scope.displayTable = false;
		$scope.intro = true;
		
		$scope.tableHeaderIds = ['th1','th2','th3','th4','th5','th6','th7'];
		$scope.tableHeaderValues = ['Champion','Win rate', 'Match duration','% of teams total damage','% of teams total healing', 'Ban rate', 'Pick rate'];
		
		// Get champion info for search box
	    $http.get('util/champlist.json')
	       .success(function(res){
	    	   $scope.champList = res.champs;	    	   
	    });
	    
// Chart functions ---------------------------------------
	    var pbColorArray = ['#2F9BB9', '#FF4E3B', '#B6BFC4'];
	    $scope.pbPieColor = function() {
	    	return function(d, i) {
	        	return pbColorArray[i];
	        };
	    }
	    
	    var wlColorArray = ['#31D44D', '#FF4E3B'];
	    $scope.wlPieColor = function() {
	    	return function(d, i) {
	        	return wlColorArray[i];
	        };
	    }
	    
	    $scope.toolTipContentFunction = function(){
			return function(key, x, y, e, graph) {
		    	return  '<b>' + key + ': ' + x + '% </b>';
			}
		}
		
		$scope.xFunction = function(){
			return function(d) {
				return d.key;
			};
		}
		 
		$scope.yFunction = function(){
				return function(d){
					return d.y;
				};
		}
// ---------------------------------------------------
	    
		$scope.orderByVal = function(val, id) {
			$scope.orderBy=val; 
			$scope.orderReverse = !$scope.orderReverse;
			var arrow = " &#9660";
			
			if($scope.orderReverse) 
				var arrow = " &#9650";
			
			for (var i = 0; i < $scope.tableHeaderIds.length; i++) {
				if($scope.tableHeaderIds[i] != id)
					document.getElementById($scope.tableHeaderIds[i]).innerHTML = $scope.tableHeaderValues[i];
				else
					document.getElementById($scope.tableHeaderIds[i]).innerHTML = $scope.tableHeaderValues[i] + arrow;
			}
			

		}
		
	    $scope.getFullChampInfo = function() {
	    	if($scope.champdataFull == null) {
	    		document.getElementById('loading-icon').style.opacity = 1;
			    $http.get('champdata')
			       .success(function(res){
			    	   document.getElementById('loading-icon').style.opacity = 0;
			    	   $scope.champdataFull = res;
			    });
	    	}
	    	
			document.getElementById('champImage').style.backgroundImage = "url('images/team.jpg')";
			document.getElementById('champTitle').innerHTML = "Rankings";
	    	$scope.intro = false;
	    	$scope.displayTable = true;
	    	$scope.orderByVal('name', 'th1', 'Champion');
	    }
		
		// Fetch champion data from servlet
	    $scope.getChampInfo = function (selected) {
	    	document.getElementById('champSearchbox_value').style.backgroundImage = "url(images/ajax-loader.gif)";
	    	$scope.displayTable = false;
	    	$scope.intro = false;
	    	
			var name = selected.originalObject.name.toLowerCase();
			name = name.charAt(0).toUpperCase() + name.slice(1);
			$scope.name = name.replace(/[.'\s+]/g,"");
	    	
			img = new Image();
			img.src = "images/splashcomp/" + $scope.name + "_Splash_0-min.jpg";
			
			$http.get("champdata?id=" + selected.originalObject.id)
			.success(function(res) {			
				$scope.champdata = res;
				
				$scope.pickBanData = [{key:"Picked",y:$scope.champdata.pickrate},
				                      {key:"Banned",y:$scope.champdata.banrate},
				                      {key:"Not picked or banned",y:100 - $scope.champdata.pickrate - $scope.champdata.banrate}];
				
				$scope.winLossData = [{key:"Wins",y:$scope.champdata.winrate},
				                      {key:"Losses",y:100-$scope.champdata.winrate}];
				
				// Update DOM
				document.getElementById('champImage').style.backgroundImage = "url('images/splashcomp/" + $scope.name + "_Splash_0-min.jpg')";
				document.getElementById('champTitle').innerHTML = $scope.champdata.name;
				document.getElementById('winrate').innerHTML = $scope.champdata.winrate + "%";
				
				if($scope.champdata.winrate >= 50)
					document.getElementById('winrate').style.color = "green";
				else
					document.getElementById('winrate').style.color = "red";
				
				document.getElementById('champSearchbox_value').style.backgroundImage = "url(images/search.png)";
				$scope.loaded = true;
			}).error(function() {
				document.getElementById('champSearchbox_value').style.backgroundImage = "url(images/search.png)";
			});
	    };
});



