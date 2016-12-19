import find from 'lodash/find';

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
    constructor($element, $timeout) {
      'ngInject';

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

      const source = find(this.sourceEndpoint.connections, connection => {
        return connection.target === this.metadata.target;
      });

      this.connection = this.jsPlumbInst.connect({
        uuids: [
          this.metadata.target,
          this.sourceEndpoint.uuid
        ],
        overlays: [
          ['Label', {
            label: this.getLabelByType(source.type),
            location: 0
          }],
          ['Label', {
            label: this.getLabelByType(this.metadata.type),
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

    getLabelByType(type) {
      switch (type) {
        case 'one': return '1';
        case 'many': return '∞';
        default: return '';
      }
    }
  }
};
