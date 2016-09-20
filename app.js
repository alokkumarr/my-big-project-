

angular.module('myApp', ['ngMaterial', 'sync.components', 'ui.grid'])
	.config(function ($mdThemingProvider) {
		$mdThemingProvider.theme('triton')
			.primaryPalette('blue', {
				'default': '700', // by default use shade from the palette for primary intentions
				'hue-1': '400', // use shade for the <code>md-hue-1</code> class
				'hue-2': '600', // use shade for the <code>md-hue-2</code> class
				'hue-3': 'A100' // use shade for the <code>md-hue-3</code> class
			});

		$mdThemingProvider.setDefaultTheme('triton');

	})

	.controller('MainController', function($scope, menu,  $mdDialog) {
		$scope.templates =
			[
				{ name: 'controls', url: 'examples/controls.html'},
				{ name: 'alerts', url: 'examples/alerts.html'},
				{ name: 'dropdowns', url: 'examples/dropdowns.html'},
				{ name: 'leftsidenav', url: 'examples/left-sidenav.html'},
				{ name: 'modals', url: 'examples/modals.html'},
				{ name: 'analysis-card', url: 'examples/analysis-card.html'},
				{ name: 'toolbars', url: 'examples/toolbars.html'},
				{ name: 'table cards', url: 'examples/table-cards.html'},
				{ name: 'pivot grid', url: 'examples/pivotgrid.html'},
				{ name: 'grid', url: 'examples/grid.html'},
				{ name: 'charts', url: 'examples/charts.html'}
			];

		$scope.template = $scope.templates[9];


		$scope.menu = menu;
		// Methods used by menuLink and menuToggle directives
		this.isOpen = isOpen;
		this.isSelected = isSelected;
		this.toggleOpen = toggleOpen;
		this.autoFocusContent = false;

		function isSelected(page) {
			return menu.isPageSelected(page);
		}

		function isSectionSelected(section) {
			var selected = false;
			var openedSection = menu.openedSection;
			if(openedSection === section){
				selected = true;
			}
			else if(section.children) {
				section.children.forEach(function(childSection) {
					if(childSection === openedSection){
						selected = true;
					}
				});
			}
			return selected;
		}

		function isOpen(section) {
			return menu.isSectionSelected(section);
		}

		function toggleOpen(section) {
			menu.toggleSelectSection(section);
		}



		// ------------- Dialogs --------------------
		$scope.status = '  ';
		$scope.customFullscreen = false;

		$scope.showAlert = function(ev) {
			// Appending dialog to document.body to cover sidenav in docs app
			// Modal dialogs should fully cover application
			// to prevent interaction outside of dialog
			$mdDialog.show(
				$mdDialog.alert()
					.parent(angular.element(document.querySelector('#popupContainer')))
					.clickOutsideToClose(true)
					.title('This is an alert title')
					.textContent('You can specify some description text in here.')
					.ariaLabel('Alert Dialog Demo')
					.ok('Got it!')
					.targetEvent(ev)
			);
		};

		$scope.showConfirm = function(ev) {
			// Appending dialog to document.body to cover sidenav in docs app
			var confirm = $mdDialog.confirm()
				.title('Would you like to delete your debt?')
				.textContent('All of the banks have agreed to forgive you your debts.')
				.ariaLabel('Lucky day')
				.targetEvent(ev)
				.ok('Please do it!')
				.cancel('Sounds like a scam');

			$mdDialog.show(confirm).then(function() {
				$scope.status = 'You decided to get rid of your debt.';
			}, function() {
				$scope.status = 'You decided to keep your debt.';
			});
		};

		$scope.showPrompt = function(ev) {
			// Appending dialog to document.body to cover sidenav in docs app
			var confirm = $mdDialog.prompt()
				.title('What would you name your dog?')
				.textContent('Bowser is a common name.')
				.placeholder('Dog name')
				.ariaLabel('Dog name')
				.initialValue('Buddy')
				.targetEvent(ev)
				.ok('Okay!')
				.cancel('I\'m a cat person');

			$mdDialog.show(confirm).then(function(result) {
				$scope.status = 'You decided to name your dog ' + result + '.';
			}, function() {
				$scope.status = 'You didn\'t name your dog.';
			});
		};

		$scope.showAdvanced = function(ev) {
			$mdDialog.show({
					controller: DialogController,
					templateUrl: 'examples/dialog1.tmpl.html',
					parent: angular.element(document.body),
					targetEvent: ev,
					clickOutsideToClose:true,
					fullscreen: $scope.customFullscreen // Only for -xs, -sm breakpoints.
				})
				.then(function(answer) {
					$scope.status = 'You said the information was "' + answer + '".';
				}, function() {
					$scope.status = 'You cancelled the dialog.';
				});
		};

		$scope.showTabDialog = function(ev) {
			$mdDialog.show({
					controller: DialogController,
					templateUrl: 'examples/tabDialog.tmpl.html',
					parent: angular.element(document.body),
					targetEvent: ev,
					clickOutsideToClose:true
				})
				.then(function(answer) {
					$scope.status = 'You said the information was "' + answer + '".';
				}, function() {
					$scope.status = 'You cancelled the dialog.';
				});
		};

		$scope.showPrerenderedDialog = function(ev) {
			$mdDialog.show({
				controller: DialogController,
				contentElement: '#myDialog',
				parent: angular.element(document.body),
				targetEvent: ev,
				clickOutsideToClose: true
			});
		};

	}).controller('AlertsCtrl', AlertsCtrl)
	.controller('DropDownsCtrl', DropDownsCtrl)
	.controller('ControlsCtrl', ControlsCtrl)
	.controller('DialogController', DialogController)
	.controller('TableCardsCtrl', TableCardsCtrl)
	.controller('PivotGridCtrl', PivotGridCtrl)
	.controller('GridCtrl', GridCtrl)
	.chart('simple', function() {
		return {
			chart: {
				type: 'area'
			},
			xAxis: {
				categories: ['Apples', 'Oranges', 'Pears', 'Grapes', 'Bananas']
			},
			series: [{
				name: 'John',
				data: [5, 3, 4, 7, 2]
			}, {
				name: 'Jane',
				data: [2, -2, -3, 2, 1]
			}, {
				name: 'Joe',
				data: [3, 4, 4, -2, 5]
			}]
		};
	});


	// ---- AlertsCtrl ---------------
	function AlertsCtrl($scope, $timeout, $mdSidenav, $mdPanel) {
		this._mdPanel = $mdPanel;

		this.desserts = [
			'Apple Pie',
			'Donut',
			'Fudge',
			'Cupcake',
			'Ice Cream',
			'Tiramisu'
		];

		this.selected = {favoriteDessert: 'Donut'};
		this.disableParentScroll = false;

		$scope.close = function () {
			// Component lookup should always be available since we are not using `ng-if`
			$mdSidenav('right').close();
		};

		$scope.toggleRight = buildToggler('right');
		$scope._mdPanel = $mdPanel;

		function buildToggler(navID) {
			return function() {
				// Component lookup should always be available since we are not using `ng-if`
				$mdSidenav(navID)
					.toggle();
			}
		}
	}

	AlertsCtrl.prototype.showMenu = function(ev) {
		var position = this._mdPanel.newPanelPosition()
			.relativeTo('.filter-open-button')
			.addPanelPosition(this._mdPanel.xPosition.ALIGN_END, this._mdPanel.yPosition.BELOW);

		var config = {
			attachTo: angular.element(document.body),
			controller: PanelMenuCtrl,
			controllerAs: 'ctrl',
			template:
			'<div class="demo-menu-example" ' +
			'     aria-label="Select your favorite dessert." ' +
			'     role="listbox">' +
			'  <div class="demo-menu-item" ' +
			'       ng-class="{selected : dessert == ctrl.favoriteDessert}" ' +
			'       aria-selected="{{dessert == ctrl.favoriteDessert}}" ' +
			'       tabindex="-1" ' +
			'       role="option" ' +
			'       ng-repeat="dessert in ctrl.desserts" ' +
			'       ng-click="ctrl.selectDessert(dessert)"' +
			'       ng-keydown="ctrl.onKeydown($event, dessert)">' +
			'    {{ dessert }} ' +
			'  </div>' +
			'</div>',
			panelClass: 'demo-menu-example',
			position: position,
			locals: {
				'selected': this.selected,
				'desserts': this.desserts
			},
			openFrom: ev,
			clickOutsideToClose: true,
			escapeToClose: true,
			focusOnOpen: false,
			zIndex: 200
		};

		this._mdPanel.open(config);
	};


	function PanelMenuCtrl(mdPanelRef, $timeout) {
		this._mdPanelRef = mdPanelRef;
		this.favoriteDessert = this.selected.favoriteDessert;
		$timeout(function() {
			var selected = document.querySelector('.demo-menu-item.selected');
			if (selected) {
				angular.element(selected).focus();
			} else {
				angular.element(document.querySelectorAll('.demo-menu-item')[0]).focus();
			}
		});
	}


	PanelMenuCtrl.prototype.selectDessert = function(dessert) {
		this.selected.favoriteDessert = dessert;
		this._mdPanelRef && this._mdPanelRef.close().then(function() {
			angular.element(document.querySelector('.demo-menu-open-button')).focus();
		});
	};


	PanelMenuCtrl.prototype.onKeydown = function($event, dessert) {
		var handled;
		switch ($event.which) {
			case 38: // Up Arrow.
				var els = document.querySelectorAll('.demo-menu-item');
				var index = indexOf(els, document.activeElement);
				var prevIndex = (index + els.length - 1) % els.length;
				els[prevIndex].focus();
				handled = true;
				break;

			case 40: // Down Arrow.
				var els = document.querySelectorAll('.demo-menu-item');
				var index = indexOf(els, document.activeElement);
				var nextIndex = (index + 1) % els.length;
				els[nextIndex].focus();
				handled = true;
				break;

			case 13: // Enter.
			case 32: // Space.
				this.selectDessert(dessert);
				handled = true;
				break;

			case 9: // Tab.
				this._mdPanelRef && this._mdPanelRef.close();
		}

		if (handled) {
			$event.preventDefault();
			$event.stopImmediatePropagation();
		}

		function indexOf(nodeList, element) {
			for (var item, i = 0; item = nodeList[i]; i++) {
				if (item === element) {
					return i;
				}
			}
			return -1;
		}
	};



	// ----- DropDownsCtrl ----------
	function DropDownsCtrl() {
		this.userState = 'Option 1';
	}


	// ------ ControlsCtrl ----------
	function ControlsCtrl() {
		this.vegObjs = [
			{
				'name' : 'Broccoli',
				'type' : 'Brassica'
			},
			{
				'name' : 'Cabbage',
				'type' : 'Brassica'
			},
			{
				'name' : 'Carrot',
				'type' : 'Umbelliferous'
			}
		];

		this.newVeg = function(chip) {
			return {
				name: chip,
				type: 'unknown'
			};
		};

		this.clickMe = function () {
			window.alert('Should not see me');
		};

		this.rangeSlider = {
			min: 0,
			max: 200000,
			lower: 0,
			upper: 50000
		};

		this.badgeCount = 5;
	}

	// ------ DialogController -------
	function DialogController($scope, $mdDialog) {
		$scope.hide = function() {
			$mdDialog.hide();
		};

		$scope.cancel = function() {
			$mdDialog.cancel();
		};

		$scope.answer = function(answer) {
			$mdDialog.hide(answer);
		};
	}

	// ------ TableCardsCtrl -------
	function TableCardsCtrl($scope) {
		$scope.zoomlevel = 70;
		$scope.pos_x = 214;
		$scope.pos_y = 148;

		$scope.targetEndpointStyle = {
			endpoint:"Dot",
			paintStyle:{ fillStyle:"#7AB02C",radius:11 },
			maxConnections:-1,
			isTarget:true,
			dropOptions: {
				tolerance: "touch",
				hoverClass: "dropHover",
				activeClass: "dragActive"
			}
		};

		$scope.sourceEndpointStyle = {
			endpoint:"Dot",
			paintStyle:{
				strokeStyle:"#7AB02C",
				fillStyle:"transparent",
				radius:7,
				lineWidth:3
			},
			isSource:true,
			isTarget: true,
			maxConnections:1,
			//connector:[ "Flowchart", { stub:[30, 30], gap:20, cornerRadius:10, alwaysRespectStubs:true } ],
			connector: ["Bezier", { curviness: 63 } ],
			connectorStyle:{
				lineWidth:4,
				strokeStyle:"#61B7CF",
				joinstyle:"round",
				outlineColor:"white",
				outlineWidth:2
			},
			connectorHoverStyle:{
				fillStyle:"#216477",
				strokeStyle:"#216477"
			},
			dropOptions: {
				tolerance: "touch",
				hoverClass: "dropHover",
				activeClass: "dragActive"
			}
		};

		$scope.removeIndex = function(index, object){
			object.splice(index, 1);
		};

		$scope.removeState = function(state){
			var index = $scope.stateObjects.indexOf(state);
			if(index !== -1){
				$scope.stateObjects.splice(index, 1);
			}
		};

		$scope.tableObjects =
			[
				{
					"name": "Customers",
					fields: [
						{
							name: 'ID',
							type: 'int',
							checked: false,
							"sources": [
								{
									"uuid": Date.now(),
									"anchor": "RightMiddle",
									"connections": [
										{
											"uuid": "26"
										}
									]
								}
							],
							"targets": []
						},
						{
							name: 'Name',
							checked: true,
							"sources": [],
							"targets": []
						},
						{
							name: 'Address',
							checked: false,
							"sources": [],
							"targets": []
						},
						{
							name: 'Phone',
							checked: false,
							"sources": [],
							"targets": []
						}
					],
					"x": 4,
					"y": 4
				},
				{
					"name": "Orders",
					fields: [
						{
							name: 'ID',
							checked: false,
							"sources": [],
							"targets": []
						},
						{
							name: 'Shipper',
							checked: true,
							"sources": [],
							"targets": []
						},
						{
							name: 'Total',
							checked: false,
							"sources": [],
							"targets": []
						},
						{
							name: 'Customer',
							checked: false,
							"sources": [],
							"targets": [
								{
									"uuid": 26,
									"anchor": "LeftMiddle",
									"connections": [
										{
											"uuid": "2"
										}
									]
								}
							]
						}
					],
					"x": 346,
					"y": 11
				}
		];

		$scope.newState = function(){
			$scope.stateObjects.push({
				'name': 'New State',
				'template': 'default',
				'sources': [
					{ uuid: getNextUUID()},
					{ uuid: getNextUUID()},
				],
				'targets': [
					{ uuid: getNextUUID()},
					{ uuid: getNextUUID()}
				],
				'x': 10,
				'y': 10
			});
		};

		$scope.tableConnections = [
			{ targetUUID:2063, sourceUUID:2058  }
		];



		$scope.activeState = null;

		$scope.setActiveState = function(state){
			$scope.activeState = state;
		};

		$scope.onConnection = function(instance, connection, targetUUID, sourceUUID){

			debugger;

			angular.forEach($scope.tableObjects, function(table){

				angular.forEach(table.sources, function(source){

					if(source.uuid == sourceUUID){
						if(typeof source.connections === 'undefined') {
							source.connections = [];
						}

						source.connections.push({'uuid': targetUUID });

						$scope.$apply();
					}
				});
			});

		}
	}

	function PivotGridCtrl($scope) {
		this.gridOptions = {
			input: [
				{product: "starburst", manufacturer: "Wrigley", shipper: 'Motion INC', region: 'North East', sum: 1000},
				{product: "starburst", manufacturer: "Mars", shipper: 'Motion INC', region: 'North East', sum: 500},
				{product: "starburst", manufacturer: "Mars", shipper: 'Motion INC', region: 'South East', sum: 300},
				{product: "starburst", manufacturer: "Wrigley", shipper: 'GRANGER', region: 'Mid West', sum: 400},
				{product: "starburst", manufacturer: "Wrigley", shipper: 'GRANGER', region: 'West', sum: 4000},
				{product: "starburst", manufacturer: "Wrigley", shipper: 'GRANGER', region: 'North', sum: 4050}
			],
			options: {
				rows: ["shipper", "region"],
				cols: ["product", "manufacturer"],
				vals: ["sum"],
				aggregator: $.pivotUtilities.aggregators.Sum(["sum"])
			}
		};
	}

	function GridCtrl(uiGridConstants) {
		this.gridOptions1 = {
			enableSorting: true,
			columnDefs: [
				{
					field: 'name',
					sort: {
						direction: uiGridConstants.DESC,
						priority: 1
					}
				},
				{
					field: 'gender',
					sort: {
						direction: uiGridConstants.ASC,
						priority: 0
					},
					suppressRemoveSort: true
				},
				{ field: 'company', enableSorting: false  }
			]
		};

		this.gridOptions1.data = [
			{
				"name": "Ethel Price",
				"gender": "female",
				"company": "Enersol"
			},
			{
				"name": "Claudine Neal",
				"gender": "female",
				"company": "Sealoud"
			},
			{
				"name": "Beryl Rice",
				"gender": "female",
				"company": "Velity"
			},
			{
				"name": "Wilder Gonzales",
				"gender": "male",
				"company": "Geekko"
			},
			{
				"name": "Georgina Schultz",
				"gender": "female",
				"company": "Suretech"
			}
		]
	}
