import {
  Directive,
  HostListener,
  ElementRef,
  Inject
} from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { ToastService } from '../services/toastMessage.service';

@Directive({
  selector: '[click-to-copy]'
})
export class ClickToCopyDirective {

  constructor(
    private _toastMessage: ToastService,
    private _elemRef: ElementRef,
    @Inject(DOCUMENT) private _doc: any
  ) {}

  @HostListener('click', ['$event'])
  onClick() {
    const elem = this._elemRef.nativeElement;
    const elemText = elem.value || elem.textContent
    const status = copyTextToClipboard(this._doc, elemText);
    /* eslint-disable */
    status && this._toastMessage.info('Success', 'Error details copied to clipboard');
    /* eslint-enable */
  }
}

function copyTextToClipboard($document, text) {
  /* Thanks https://stackoverflow.com/a/30810322/1825727 */

  const textArea = $document.createElement('textarea');

  textArea.style.position = 'fixed';
  textArea.style.top = 0;
  textArea.style.left = 0;

  // Ensure it has a small width and height. Setting to 1px / 1em
  // doesn't work as this gives a negative w/h on some browsers.
  textArea.style.width = '2em';
  textArea.style.height = '2em';

  // We don't need padding, reducing the size if it does flash render.
  textArea.style.padding = 0;

  // Clean up any borders.
  textArea.style.border = 'none';
  textArea.style.outline = 'none';
  textArea.style.boxShadow = 'none';

  // Avoid flash of white box if rendered for any reason.
  textArea.style.background = 'transparent';

  textArea.value = text;

  $document.body.appendChild(textArea);

  textArea.select();

  let status;
  try {
    const successful = $document.execCommand('copy');
    status = successful;
  } catch (err) {
    status = false;
  }

  $document.body.removeChild(textArea);
  return status;
}
