export const JSPlumbConnector = {
  bindings: {
    model: '<'
  },
  controller: class JSPlumbConnectionCtrl {
    constructor($timeout, $compile, $scope) {
      'ngInject';

      this._$timeout = $timeout;
      this._$compile = $compile;
      this._$scope = $scope;

      this.connection = null;
    }

    $onInit() {
      this.model.component = this;

      this._canvas = this.model.canvas;
      this._jsPlumbInst = this._canvas.component.getInstance();
      this._settings = this._canvas.component.getSettings();

      this.render();
    }

    $onDestroy() {
      this.detach();
    }

    render() {
      this.detach();

      const leftSide = this.model.leftSide;
      const rightSide = this.model.rightSide;

      const leftEndpoint = leftSide.field.addEndpoint(leftSide.side);
      const rightEndpoint = rightSide.field.addEndpoint(rightSide.side);

      this._$timeout(() => {
        this.connection = this._jsPlumbInst.connect({
          uuids: [
            leftEndpoint.getIdentifier(),
            rightEndpoint.getIdentifier()
          ]
        });

        this.addJoinLabel();
      }, 300);
    }

    detach() {
      if (this.connection) {
        this._jsPlumbInst.detach(this.connection);
        this.connection = null;
      }
    }

    addOverlay(config) {
      this.connection.addOverlay(config);
    }

    addJoinLabel() {
      const html = `<js-plumb-join-label model="model"></js-plumb-join-label>`;
      const scope = this._$scope.$new();

      scope.model = {
        connector: this
      };

      const el = this._$compile(html)(scope);

      this.addOverlay(['Custom', {
        create: connection => {
          return el;
        },
        location: 0.5
      }]);
    }

    removeConnection() {
      this._canvas.removeJoin(this.model);
    }
  }
}
