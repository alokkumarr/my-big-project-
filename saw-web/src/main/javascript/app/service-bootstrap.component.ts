// In hybrid apps using downgradeModule, the angular module is bootstrapped
// lazily when the first angular component is rendered. Any downgraded injectors aren't
// accessible until then. This component is a downgraded component from angular to angularjs
// meant to force bootstrapping angular module. It doesn't have (or need) any functionality.

import { Component } from '@angular/core';

@Component({
  selector: 'service-bootstrap',
  template: ''
})
export class ServiceBootstrapComponent{
  constructor() { }
<<<<<<< HEAD
<<<<<<< HEAD
}
=======
}
>>>>>>> 47174b002db30c2b4e1bb29001816d702bd958f3
=======
}
>>>>>>> ac1d79e1e5d9ffdf3ec25e91f831c57fef2a0c3a
