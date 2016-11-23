export const JSPlumbEndpoint = {
  require: {
    canvas: '^jsPlumbCanvas'
  },
  bindings: {
    metadata: '<',
    table: '<',
    field: '<',
    settings: '<'
  },
  controller: class JSPlumbEndpointCtrl {
    /** @ngInject */
    constructor($element) {
      this.$element = $element;
    }

    $onInit() {
      this.jsPlumbInst = this.canvas.getInstance();
    }

    $postLink() {
      if (this.metadata) {
        this.addEndpoint(this.metadata);
      }
    }

    // $onDestroy() {
    //   this.deleteEndpoint();
    // }

    addEndpoint(metadata) {
      let endpointSettings;

      if (this.settings && this.settings.endpoints) {
        endpointSettings = this.settings.endpoints.source || {};
      }

      const options = {
        uuid: metadata.uuid,
        anchor: metadata.anchor,
        connectionsDetachable: true,
        reattachConnections: true,
        deleteEndpointsOnDetach: true
      };

      this.$element.addClass(`jsp-endpoint-${options.anchor}`);

      this.endpoint = this.jsPlumbInst.addEndpoint(this.$element, endpointSettings, options);
      this.endpoint.setParameter('instance', this);
    }

    // deleteEndpoint() {
    //   if (this.endpoint) {
    //     this.jsPlumbInst.deleteEndpoint(this.endpoint);
    //   }
    // }
  }
};
