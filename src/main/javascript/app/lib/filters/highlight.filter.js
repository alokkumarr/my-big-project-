import forEach from 'lodash/forEach';

class HighlightFilter {
  static highlightText(text, term, modifiers = 'ig', className = 'highlight') {
    if (text && term) {
      const expr = HighlightFilter.escapeRegexp(term) + '(?!([^<]+)?>)';
      const rx = new RegExp(expr, modifiers);

      return text.replace(rx, `<span class="${className}">$&</span>`);
    }

    return text;
  }

  static escapeRegexp(text) {
    const ESCAPE_EXPR = /([.?*+^$[\]\\(){}|-])/g;

    return text.replace(ESCAPE_EXPR, '\\$1');
  }
}

export default () => {
  return HighlightFilter.highlightText;
};
