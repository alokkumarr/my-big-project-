export const JSPlumbEndpoint = {
  bindings: {
    model: '<'
  },
  controller: class JSPlumbEndpointCtrl {
    constructor($element) {
      'ngInject';
      this._$element = $element;

      this.endpoint = null;
    }

    $onInit() {
      this.model.component = this;

      this._canvas = this.model.field.table.canvas;
      this._jsPlumbInst = this._canvas.component.getInstance();
      this._settings = this._canvas.component.getSettings();

      this.render();
    }

    $onDestroy() {
      this.detach();
    }

    render() {
      let endpointSettings;

      if (this._settings.endpoints) {
        endpointSettings = this._settings.endpoints.source || {};
      }

      const options = {
        uuid: this.model.getIdentifier(),
        anchor: this.model.getAnchor(),
        connectionsDetachable: true,
        reattachConnections: true,
        deleteEndpointsOnDetach: false
      };

      this._$element.addClass(`jsp-endpoint-${options.anchor}`);

      this.endpoint = this._jsPlumbInst.addEndpoint(this._$element, endpointSettings, options);
      this.endpoint.setParameter('component', this);
    }

    detach() {
      if (this.endpoint) {
        this._jsPlumbInst.deleteEndpoint(this.endpoint);
      }
    }
  }
};
