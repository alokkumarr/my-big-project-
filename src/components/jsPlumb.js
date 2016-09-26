angular.module('sync.components.jsPlumb')

    .directive('jsPlumbCanvas', function(){

        var jsPlumbZoomCanvas = function(instance, zoom, el, transformOrigin) {
            transformOrigin = transformOrigin || [ 0, 0];
            var p = [ "webkit", "moz", "ms", "o" ],
                s = "scale(" + zoom + ")",
                oString = (transformOrigin[0] * 100) + "% " + (transformOrigin[1] * 100) + "%";
            for (var i = 0; i < p.length; i++) {
                el.style[p[i] + "Transform"] = s;
                el.style[p[i] + "TransformOrigin"] = oString;
            }
            el.style["transform"] = s;
            el.style["transformOrigin"] = oString;
            instance.setZoom(zoom);
        };

        var def = {
            restrict: 'E',
            scope: {
                onConnection: '=onConnection',
                zoom: '=',
                x: '=',
                y: '='
            },
            controller: function ($scope) {
                this.scope = $scope;
            },
            transclude: true,
            template: '<div ng-transclude></div>',
            link: function(scope, element, attr){
                var instance = jsPlumb.getInstance();
                scope.jsPlumbInstance = instance;

    //           instance.bind("connectionDrag", function(connection, originalEvent) {
    //               console.log("connectionDrag " + connection.id + " is being dragged. suspendedElement is ", connection.suspendedElement, " of type ", connection.suspendedElementType);
    //               console.log("connectionDrag", connection, originalEvent);
    //           });
    //
              instance.bind("beforeDrop", function(info) {
                  var target = angular.element(info.connection.target).scope().table;
                  var source = angular.element(info.connection.source).scope().table;

                  return source.name !== target.name;
              });


                instance.bind("connection", function(info, origEvent) {
                    if(typeof origEvent !== 'undefined' && origEvent.type == 'mouseup'){
                        var target = angular.element(info.target).scope().endpoint;
                        var source = angular.element(info.source).scope().endpoint;

                        target.connections.push({
                            "uuid": source.uuid
                        });

                        scope.apply();

                        //scope.onConnection(instance, info.connection, target, source);
                    }
                });

                $(element).css({
                    minWidth: '500px',
                    minHeight: '500px',
                    display: 'block'
                }).draggable({
                    stop: function(event, ui) {
                        var position = $(this).position();
                        scope.x = position.left;
                        scope.y = position.top;
                        scope.$parent.$apply();
                    }
                });

                instance.setContainer($(element));

                // var zoom = (typeof scope.zoom === 'undefined') ? 1 : scope.zoom/100;
                //
                // jsPlumbZoomCanvas(instance, zoom, $(element)[0]);
                //
                // scope.$watch('zoom', function(newVal, oldVal){
                //     jsPlumbZoomCanvas(instance, newVal/100, $(element)[0]);
                // });

                // $(element).bind('mousewheel', function(e){
                //     if(e.originalEvent.wheelDelta /120 > 0) {
                //         scope.zoom += 10;
                //         scope.$apply();
                //
                //     }
                //     else{
                //         scope.zoom -= 10;
                //         scope.$apply();
                //     }
                // });


    //           scope.$watch('x', function(newVal, oldVal){
    //               $(element).css('left', newVal);
    //           });
    //           scope.$watch('y', function(newVal, oldVal){
    //               $(element).css('top', newVal);
    //           });


            }
        };

        return def;
    })

    .directive('jsPlumbObject', function($timeout) {
        var def = {
            restrict : 'E',
            require: '^jsPlumbCanvas',
            scope: {
                stateObject: '=stateObject'
            },
            controller: function ($scope) {
                $scope.newEndpoint = function(anchor) {
                    return {
                        uuid: Date.now(),
                        "anchor": anchor,
                        "connections": []
                    }
                };
            },
            transclude : true,
            template: '<div ng-transclude></div>',
            link : function(scope, element, attrs, jsPlumbCanvas) {
                var instance = jsPlumbCanvas.scope.jsPlumbInstance;
                var timer;
                var addEndpoints;
                var itemListXMiddle = element[0].offsetWidth / 2;

                console.log('constructing object');

                element.css({
                    left: scope.stateObject.x + 'px',
                    top: scope.stateObject.y + 'px'
                });

                instance.draggable(element, {
                    drag: function (event) {
                        scope.stateObject.x = event.pos[0];
                        scope.stateObject.y = event.pos[1];
                        scope.$apply();
                    }
                });

                $(element).delegate( "md-list-item", "mousedown", function(event) {
                    var element = angular.element(this);
                    var scope = element.scope();
                    var field = scope.field;

                    timer = $timeout(function () {
                        var anchor;

                        anchor = itemListXMiddle > event.offsetX ? 'LeftMiddle' : 'RightMiddle';

                        addEndpoints(field, anchor);
                    }, 1000);
                });

                $(element).delegate( "md-list-item", "mouseup", function () {
                    $timeout.cancel(timer);
                });

                scope.$on('$destroy', function(){

                });

                addEndpoints = function (field, anchor) {
                    field.sources.push(scope.newEndpoint(anchor));
                }
            }
        };
        return def;
    })

    .directive('jsPlumbEndpoint', function() {

        var def = {
            restrict : 'E',
            require: '^jsPlumbCanvas',
            scope: {
                settings: '=settings'
            },
            controller: function ($scope) {
                this.scope = $scope;
                this.connectionObjects = {};
            },
            transclude: true,
            template: '<div ng-transclude></div>',
            link : function(scope, element, attrs, jsPlumbCanvas) {
                var instance = jsPlumbCanvas.scope.jsPlumbInstance;
                var field = scope.$parent.endpoint;

                scope.jsPlumbInstance = jsPlumbCanvas.scope.jsPlumbInstance;
                scope.uuid = attrs.uuid;

                var options = {
                    anchor: field.anchor,
                    uuid: attrs.uuid
                };

                console.log('rigging up endpoint');

                $(element).addClass('_jsPlumb_endpoint');
                $(element).addClass('endpoint_' + options.anchor);

                var ep = instance.addEndpoint(element, scope.settings, options);

                scope.$on('$destroy', function(){
                    instance.deleteEndpoint(ep);
                });
            }
        };
        return def;
    })

    .directive('jsPlumbConnection', function($timeout) {

        var def = {
            restrict : 'E',
            require: '^jsPlumbEndpoint',
            scope: {
                ngClick: '&ngClick',
                ngModel: '=ngModel'
            },
            link : function(scope, element, attrs, jsPlumbEndpoint)
            {
                var instance = jsPlumbEndpoint.scope.jsPlumbInstance;
                var sourceUUID = jsPlumbEndpoint.scope.uuid;
                var targetUUID = scope.ngModel.uuid;

                //we delay the connections by just a small bit for loading
                console.log('[directive][jsPlumbConnection] ', scope, attrs);

                $timeout(function(){
                    if(typeof jsPlumbEndpoint.connectionObjects[targetUUID] === 'undefined'){
                        jsPlumbEndpoint.connectionObjects[targetUUID] = instance.connect({
                            uuids:[
                                targetUUID,
                                sourceUUID
                            ],
                            overlays:[
                                [ "Label", {
                                    label:"1",
                                    location: 0,
                                    id:"label"
                                    }
                                ], [
                                    "Label", {
                                        label:"&",
                                        location: 1,
                                        id:"label1"
                                    }
                                ]
                            ], editable:true});

                        console.log('[created---------][directive][jsPlumbConnection] ');

                    }

                    var connection = jsPlumbEndpoint.connectionObjects[targetUUID];

                    connection.bind("click", function(conn, originalEvent) {
                        scope.ngClick();
                        scope.$apply();
                    });

                    connection.bind("mouseenter", function(conn, originalEvent) {
                        scope.ngModel.mouseover = true;
                        scope.$apply();
                    });
                    connection.bind("mouseleave", function(conn, originalEvent) {
                        scope.ngModel.mouseover = false;
                        scope.$apply();
                    });


                    // not really using this... but we should fix it :)

                    var overlay = connection.getOverlay("label");
                    if(overlay){
                        console.log('[getOverlay][label]', connection.getOverlay("label"));
                        $(element).appendTo( overlay.canvas );
                    }


                }, 300);


                scope.$on('$destroy', function(){
                    console.log('jsPlumbConnection for $destroy');
                    try{
                        instance.detach(jsPlumbEndpoint.connectionObjects[targetUUID]);
                    } catch(err){
                        console.log('error', err, jsPlumbEndpoint.connectionObjects[targetUUID]);

                    }
                    // if the connection is destroyed, I am assuming the parent endPoint is also destroyed, and we need to remove
                    // the reference that a link exists, so it will be rendered again
                    jsPlumbEndpoint.connectionObjects[targetUUID] = undefined;
                });

            }
        };
        return def;
    });