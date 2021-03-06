export function getFileContents(file) {
  return new Promise<string>((resolve, reject) => {
    if (typeof FileReader !== 'function') {
      reject(new Error('The file API isn\'t supported on this browser.'));
    }

    const fr = new FileReader();
    fr.onload = e => {
      const target = <any>e.target;
      resolve(target ? target.result : '');
    };
    fr.readAsText(file);
  });
}
