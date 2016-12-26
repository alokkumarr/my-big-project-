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
    constructor($element, $timeout, $compile, $scope) {
      'ngInject';

      this.$element = $element;
      this.$timeout = $timeout;
      this.$compile = $compile;
      this.$scope = $scope;
    }

    $onInit() {
      this.jsPlumbInst = this.canvas.getInstance();
    }

    $postLink() {
      if (this.metadata && this.sourceEndpoint) {
        this.$timeout(() => {
          this.connect(this.metadata.target, this.sourceEndpoint.uuid);
        }, 300);
      }
    }

    $onDestroy() {
      this.detach();
    }

    connect(source, target) {
      this.detach();

      this.connection = this.jsPlumbInst.connect({
        uuids: [
          source,
          target
        ],
        overlays: [
          ['Label', {
            label: this.getLabelByType('one'),
            location: 0
          }],
          ['Label', {
            label: this.getLabelByType('one'),
            location: 1
          }],
          ['Custom', {
            create: connection => {
              const html = `<js-plumb-join-label connection="connection"></js-plumb-join-label>`;
              const scope = this.$scope.$new();

              scope.connection = this;

              return this.$compile(html)(scope);
            },
            location: 0.5
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
        case 'one':
          return '1';
        case 'many':
          return '∞';
        default:
          return '';
      }
    }
  }
};
