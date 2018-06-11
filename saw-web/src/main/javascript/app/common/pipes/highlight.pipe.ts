import { Pipe, PipeTransform } from '@angular/core';

/**
 * A Highlight pipe, to highlight text Examples:
 * {{someText | highlight:highlightValue}}
 *
 * @export
 * @class HighlightPipe
 * @implements {PipeTransform}
 */
@Pipe({
  name: 'highlight'
})
export class HighlightPipe implements PipeTransform {

  transform(input: string, term: string, modifiers = 'ig', className = 'highlight'): string {
    if (input && term) {
      const expr = HighlightPipe.escapeRegexp(term) + '(?!([^<]+)?>)';
      const rx = new RegExp(expr, modifiers);

      return input.replace(rx, `<span class="${className}">$&</span>`);
    }

    return input;
  }

  static escapeRegexp(text) {
    const ESCAPE_EXPR = /([.?*+^$[\]\\(){}|-])/g;

    return text.replace(ESCAPE_EXPR, '\\$1');
  }
}
