export const JSPlumbConnection = {
  require: {
    canvas: '^jsPlumbCanvas'
  },
  bindings: {
    metadata: '<',
    sourceEndpoint: '<',
    settings: '<'
  },
  controller: class JSPlumbConnectionCtrl {
    /** @ngInject */
    constructor($element, $timeout) {
      this.$element = $element;
      this.$timeout = $timeout;
    }

    $onInit() {
      this.jsPlumbInst = this.canvas.getInstance();
    }

    $postLink() {
      if (this.metadata && this.sourceEndpoint) {
        this.$timeout(() => {
          this.connect();
        }, 300);
      }
    }

    $onDestroy() {
      this.detach();
    }

    connect() {
      this.detach();

      this.connection = this.jsPlumbInst.connect({
        uuids: [
          this.metadata.target,
          this.sourceEndpoint.uuid
        ],
        overlays: [
          ['Label', {
            label: '1',
            location: 0
          }],
          ['Label', {
            label: '&',
            location: 1
          }],
          ['Label', {
            label: '<i class="jsp-connection-remove-icon">x</i>',
            location: 0.5,
            events: {
              tap: () => {
                this.detach();
              }
            }
          }]
        ]
      });
    }

    detach() {
      if (this.connection) {
        this.jsPlumbInst.detach(this.connection);
      }
    }
  }
};
