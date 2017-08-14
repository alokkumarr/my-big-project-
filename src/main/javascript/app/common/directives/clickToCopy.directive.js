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

class ClickToCopyDirective {

  controller($scope, $element, $document, toastMessage) {
    'ngInject';

    this.onClick = () => {
      const status = copyTextToClipboard($document[0], $element[0].value || $element[0].textContent);
      /* eslint-disable */
      status && toastMessage.info('Error details copied to clipboard');
      /* eslint-enable */
    };

    $element.css('cursor', 'pointer');
    $element.on('click', this.onClick);

    $scope.$on('$destroy', () => $element.off('click', this.onClick));
  }

}

export default () => new ClickToCopyDirective();
