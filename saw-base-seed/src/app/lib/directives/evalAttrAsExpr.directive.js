class EvalAttrAsExprDirective {
  constructor() {
    this.restrict = 'A';
    this.priority = 9999;
  }

  /** @ngInject */
  controller($scope, $attrs) {
    const attrToEval = $attrs.evalAttrAsExpr;

    $attrs[attrToEval] = $scope.$eval($attrs[attrToEval]);
  }
}

export default () => {
  return new EvalAttrAsExprDirective();
};
