import { Injectable } from '@angular/core';
import * as html2pdf from 'html2pdf.js';
import * as lodashMap from 'lodash/map';
import * as forEach from 'lodash/forEach';
import * as isEmpty from 'lodash/isEmpty';
import { saveAs } from 'file-saver/FileSaver';

import { dataURItoBlob } from '../utils/dataURItoBlob';

function downloadDataUrlFromJavascript(filename, dataUrl) {
  const blob = dataURItoBlob(dataUrl);
  saveAs(blob, filename);
}

@Injectable()
export class HtmlDownloadService {
  constructor() {}

  downloadDashboard(element, fileName) {
    this.changeMapCanvasesToImage(element).then(backupImgCanvasPairs => {
      this.turnHtml2pdf(element, fileName).then(() => {
        forEach(backupImgCanvasPairs, ({ canvas, imageElem }) => {
          imageElem.replaceWith(canvas);
        });
      });
    });
  }

  private changeMapCanvasesToImage(element) {
    const mapBoxComponents = Array.from(
      element.getElementsByTagName('map-box')
    );

    const backupImgCanvasPairs = [];
    const onImgLoadPromises = lodashMap(mapBoxComponents, comp => {
      const imageUrl = comp.dataset['imageUrl'];
      const canvasContainer = comp.querySelector('.mapboxgl-canvas-container');
      const canvas = canvasContainer.querySelector('canvas');
      const { height, width } = canvas.style;
      const imageElem = document.createElement('img');

      return this.imageUrl2DataUrl(imageUrl).then((dataUrl: string) => {
        imageElem.src = dataUrl;
        imageElem.crossOrigin = 'anonymous';
        imageElem.height = parseInt(height, 10);
        imageElem.width = parseInt(width, 10);
        backupImgCanvasPairs.push({ canvas, imageElem });
        canvas.replaceWith(imageElem);
      });
    });

    if (isEmpty(onImgLoadPromises)) {
      return Promise.resolve([]);
    } else {
      return Promise.all(onImgLoadPromises).then(() => {
        return backupImgCanvasPairs;
      });
    }
  }

  turnHtml2pdf(elem, fileName) {
    /* Set overflow to visible manually to fix safari's bug. Without this,
     * safari downloads a blank image */
    const overflow = elem.style.overflow;
    elem.style.overflow = 'visible';

    return (
      html2pdf()
        .from(elem)
        .set({
          margin: 1,
          filename: `${fileName}.pdf`,
          image: { type: 'jpeg', quality: 1 },
          html2canvas: {
            backgroundColor: '#f4f5f4',
            scale: 2,
            useCORS: true,
            width: elem.scrollWidth,
            height: elem.scrollHeight,
            onclone: cloned => {
              // need this for html2pdf download to wirk with gridster library
              // https://github.com/niklasvh/html2canvas/issues/1720
              const gridsterItems = Array.from(
                cloned.querySelectorAll('gridster-item')
              );
              forEach(gridsterItems, item => {
                item.style.transition = 'unset';
              });
            }
          },
          jsPDF: {
            unit: 'px',
            orientation: 'landscape',
            format: [elem.scrollHeight + 50, elem.scrollWidth]
          }
        })
        // .save(); // comment this and uncomment following lines if png is needed instead of pdf
        .outputImg('datauristring')
        .then(uri => downloadDataUrlFromJavascript(`${fileName}.png`, uri))
        .then(() => {
          elem.style.overflow = overflow;
        })
    );
  }

  private imageUrl2DataUrl(imageUrl) {
    return fetch(imageUrl)
      .then(response => response.blob())
      .then(
        blob =>
          new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.onloadend = () => resolve(reader.result);
            reader.onerror = reject;
            reader.readAsDataURL(blob);
          })
      );
  }
}
