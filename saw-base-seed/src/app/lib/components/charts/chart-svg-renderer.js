
export function setSVGRenderer(Highcharts) {
  const orig = Highcharts.SVGRenderer.prototype.html;

  Highcharts.SVGRenderer.prototype.html = function (...args) {
    const wrapper = orig.apply(this, args);
    const origSetter = wrapper.textSetter;

    wrapper.textSetter = function (text) {
      if (text.linkFn) {
        delete wrapper.bBox;
        if (!wrapper.scope) {
          text.linkFn(text.scope.$new(), (element, scope) => {
            wrapper.scope = scope;
            angular.extend(wrapper.scope, text.data);
            while (wrapper.element.firstChild) {
              wrapper.element.removeChild(wrapper.element.firstChild);
            }
            wrapper.element.appendChild(element[0]);
            wrapper.textStr = element[0].innerHTML;
            wrapper.htmlUpdateTransform();
          });
        } else {
          angular.extend(wrapper.scope, text.data);
          text.scope.$applyAsync(() => {
            wrapper.textStr = wrapper.element.innerHTML;
            wrapper.htmlUpdateTransform();
          });
        }
      } else {
        origSetter.apply(this, arguments);
      }
    };

    if (arguments.length && arguments[0]) {
      wrapper.attr({text: arguments[0]});
    }

    return wrapper;
  };
}
