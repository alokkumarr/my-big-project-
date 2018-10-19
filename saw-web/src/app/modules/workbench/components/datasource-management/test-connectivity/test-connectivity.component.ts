import { Component, OnInit, ViewChild } from '@angular/core';
import { MatSnackBarRef } from '@angular/material';

@Component({
  selector: 'test-connectivity',
  templateUrl: './test-connectivity.component.html',
  styleUrls: ['./test-connectivity.component.scss']
})
export class TestConnectivityComponent implements OnInit {
  private text = [
    'Last login: Fri Sep 28 17:29:06 2018 from 192.168.1.74',
    '** Authorized Use Only **',

    'Chef-Client: saw01.ana.demo.vaste.sncrcorp.net',
    'Node platform: centos 7.4.1708 (rhel)',
    'Node arch: x86_64',
    'Hostnawme: saw01 (saw01.ana.demo.vaste.sncrcorp.net)',

    'Chef Server: https://chef02-itopscorpvaste.sncrcorp.net:443/organizations/sncr-sip',
    'Environment: sip-demo',
    'Last Run: 2018-10-02 17:03:26 +0000'
  ];
  constructor(private snackBarRef: MatSnackBarRef<TestConnectivityComponent>) {}

  @ViewChild('screen')
  private screen;

  ngOnInit() {
    this.typeMessage(this.text, this.screen.nativeElement);
  }

  close() {
    this.snackBarRef.dismiss();
  }

  /**
   * Credits: https://codereview.stackexchange.com/a/97114
   *
   * @param {*} text
   * @param {*} screen
   * @returns
   * @memberof TestConnectivityComponent
   */
  typeMessage(text, screen) {
    //You have to check for lines and if the screen is an element
    if (!text || !text.length || !(screen instanceof Element)) {
      return;
    }

    //if it is not a string, you will want to make it into one
    if ('string' !== typeof text) {
      text = text.join('\n');
    }

    //normalize newlines, and split it to have a nice array
    text = text.replace(/\r\n?/g, '\n').split('');

    //the prompt is always the last child
    let prompt = screen.lastChild as HTMLDivElement;
    prompt.className = 'typing';

    let typer = () => {
      let character = text.shift();
      screen.insertBefore(
        //newlines must be written as a `<br>`
        character === '\n'
          ? document.createElement('br')
          : document.createTextNode(character),
        prompt
      );

      //only run this again if there are letters
      if (text.length) {
        setTimeout(typer, 10);
      } else {
        prompt.className = 'idle';
      }
    };
    setTimeout(typer, 10);
  }
}
