import forEach from 'lodash/forEach';
// import $ from 'jquery';

class HighlightContainerDirective {
  constructor() {
    this.restrict = 'A';
    this.priority = -500;
  }

  // controller($scope, $element, $attrs, $timeout) {
  //   console.log($element);
  //
  //   const ESCAPE_EXPR = /([.?*+^$[\]\\(){}|-])/g;
  //
  //   const escapeRegexp = queryToEscape => {
  //     return queryToEscape.replace(ESCAPE_EXPR, '\\$1');
  //   };
  //
  //   const expr = escapeRegexp('REPORT') + '(?!([^<]+)?>)';
  //   const rx = new RegExp(expr, 'gi');
  //
  //   const itNodes = nodes => {
  //     forEach(nodes, node => {
  //       // is text node?
  //       if (node.nodeType === 3) {
  //         if (node.nodeValue === 'REPORT') {
  //           // node.nodeValue = node.nodeValue.replace(/\w+/g, '<span>$&</span>');
  //           console.log(node);
  //
  //           $(node).html((index, html) => {
  //             if (html) {
  //               html = html.replace(rx, '<strong>$&</strong>');
  //             }
  //
  //             return html;
  //           });
  //         }
  //       } else {
  //         itNodes(node.childNodes);
  //       }
  //     });
  //   };
  //
  //   $timeout(() => {
  //     itNodes($element[0].childNodes);
  //   }, 1000);
  // }
}

export default () => {
  return new HighlightContainerDirective();
};
