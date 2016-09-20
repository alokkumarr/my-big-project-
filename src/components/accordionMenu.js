angular.module('sync.components.accordionMenu')
.factory('menu', [
    '$location',
    '$rootScope',
    function($location, $rootScope) {
        var self;

        var sections = [{
            name: 'Getting Started',
            url: 'getting-started',
            type: 'link'
        }];

        sections.push();

        sections.push({
            name: 'Customization',
            type: 'heading',
            children: [
                {
                    name: 'CSS',
                    type: 'toggle',
                    pages: [{
                        name: 'Typography',
                        url: 'CSS/typography',
                        type: 'link'
                    },
                        {
                            name : 'Button',
                            url: 'CSS/button',
                            type: 'link'
                        },
                        {
                            name : 'Checkbox',
                            url: 'CSS/checkbox',
                            type: 'link'
                        }]
                },
                {
                    name: 'Theming',
                    type: 'toggle',
                    pages: [
                        {
                            name: 'Introduction and Terms',
                            url: 'Theming/01_introduction',
                            type: 'link'
                        },
                        {
                            name: 'Declarative Syntax',
                            url: 'Theming/02_declarative_syntax',
                            type: 'link'
                        },
                        {
                            name: 'Configuring a Theme',
                            url: 'Theming/03_configuring_a_theme',
                            type: 'link'
                        },
                        {
                            name: 'Multiple Themes',
                            url: 'Theming/04_multiple_themes',
                            type: 'link'
                        },
                        {
                            name: 'Under the Hood',
                            url: 'Theming/05_under_the_hood',
                            type: 'link'
                        },
                        {
                            name: 'Browser Color',
                            url: 'Theming/06_browser_color',
                            type: 'link'
                        }
                    ]
                }
            ]
        });

        sections.push( {
            name: 'Contributors',
            url: 'contributors',
            type: 'link'
        } );

        sections.push({
            name: 'License',
            url:  'license',
            type: 'link',

            // Add a hidden section so that the title in the toolbar is properly set
            hidden: true
        });

        $rootScope.$on('$locationChangeSuccess', onLocationChange);

        return self = {
            sections: sections,

            selectSection: function(section) {
                self.openedSection = section;
            },
            toggleSelectSection: function(section) {
                self.openedSection = (self.openedSection === section ? null : section);
            },
            isSectionSelected: function(section) {
                return self.openedSection === section;
            },

            selectPage: function(section, page) {
                self.currentSection = section;
                self.currentPage = page;
            },
            isPageSelected: function(page) {
                return self.currentPage === page;
            }
        };

        function onLocationChange() {
            var path = $location.path();
            var introLink = {
                name: "Introduction",
                url:  "/",
                type: "link"
            };

            if (path == '/') {
                self.selectSection(introLink);
                self.selectPage(introLink, introLink);
                return;
            }

            var matchPage = function(section, page) {
                if (path.indexOf(page.url) !== -1) {
                    self.selectSection(section);
                    self.selectPage(section, page);
                }
            };

            sections.forEach(function(section) {
                if (section.children) {
                    // matches nested section toggles, such as API or Customization
                    section.children.forEach(function(childSection){
                        if(childSection.pages){
                            childSection.pages.forEach(function(page){
                                matchPage(childSection, page);
                            });
                        }
                    });
                }
                else if (section.pages) {
                    // matches top-level section toggles, such as Demos
                    section.pages.forEach(function(page) {
                        matchPage(section, page);
                    });
                }
                else if (section.type === 'link') {
                    // matches top-level links, such as "Getting Started"
                    matchPage(section, section);
                }
            });
        }
    }])

    .directive('menuLink', function() {
        return {
            scope: {
                section: '='
            },
            templateUrl: 'partials/menu-link.tmpl.html',
            link: function($scope, $element) {
                var controller = $element.parent().controller();

                $scope.isSelected = function() {
                    return controller.isSelected($scope.section);
                };

                $scope.focusSection = function() {
                    // set flag to be used later when
                    // $locationChangeSuccess calls openPage()
                    controller.autoFocusContent = true;
                };
            }
        };
    })

    .directive('menuToggle', [ '$timeout', '$mdUtil', function($timeout, $mdUtil) {
        return {
            scope: {
                section: '='
            },
            templateUrl: 'partials/menu-toggle.tmpl.html',
            link: function($scope, $element) {
                var controller = $element.parent().controller();

                $scope.isOpen = function() {
                    return controller.isOpen($scope.section);
                };
                $scope.toggle = function() {
                    controller.toggleOpen($scope.section);
                };

                $mdUtil.nextTick(function() {
                    $scope.$watch(
                        function () {
                            return controller.isOpen($scope.section);
                        },
                        function (open) {
                            // We must run this in a next tick so that the getTargetHeight function is correct
                            $mdUtil.nextTick(function() {
                                var $ul = $element.find('ul');
                                var $li = $ul[0].querySelector('a.active');
                                var docsMenuContent = document.querySelector('.sidenav-menu').parentNode;
                                var targetHeight = open ? getTargetHeight() : 0;

                                $timeout(function () {
                                    // Set the height of the list
                                    $ul.css({height: targetHeight + 'px'});

                                    // If we are open and the user has not scrolled the content div; scroll the active
                                    // list item into view.
                                    if (open && $li && $li.offsetParent && $ul[0].scrollTop === 0) {
                                        $timeout(function() {
                                            var activeHeight = $li.scrollHeight;
                                            var activeOffset = $li.offsetTop;
                                            var parentOffset = $li.offsetParent.offsetTop;

                                            // Reduce it a bit (2 list items' height worth) so it doesn't touch the nav
                                            var negativeOffset = activeHeight * 2;
                                            var newScrollTop = activeOffset + parentOffset - negativeOffset;

                                            $mdUtil.animateScrollTo(docsMenuContent, newScrollTop);
                                        }, 350, false);
                                    }
                                }, 0, false);

                                function getTargetHeight() {
                                    var targetHeight;
                                    $ul.addClass('no-transition');
                                    $ul.css('height', '');
                                    targetHeight = $ul.prop('clientHeight');
                                    $ul.css('height', 0);
                                    $ul.removeClass('no-transition');
                                    return targetHeight;
                                }
                            }, false);
                        }
                    );
                });


                // //Enable only when section heading is enabled, see the markup
                // var parentNode = $element[0].parentNode.parentNode.parentNode;
                //
                // if(parentNode.classList.contains('parent-list-item')) {
                //     var heading = parentNode.querySelector('h2');
                //     $element[0].firstChild.setAttribute('aria-describedby', heading.id);
                // }
            }
        };
    }])

    .filter('nospace', function () {
        return function (value) {
            return (!value) ? '' : value.replace(/ /g, '');
        };
    })
    .filter('humanizeDoc', function() {
        return function(doc) {
            if (!doc) return;
            if (doc.type === 'directive') {
                return doc.name.replace(/([A-Z])/g, function($1) {
                    return '-'+$1.toLowerCase();
                });
            }
            return doc.label || doc.name;
        };
    });
