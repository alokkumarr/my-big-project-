import {analysisMethods, metrics} from '../../../analyze/new-analysis.mock';

export default [
  analysisMethods,
  metrics,
  {
    method: 'GET',
    url: '/api/menu',
    response: () => {
      return [200, [
        {
          name: 'Getting Started',
          url: ''
        }, {
          name: 'CSS',
          active: true,
          children: [
            {
              name: 'Typography',
              url: '/typography'
            }, {
              name: 'Button',
              url: ''
            }, {
              name: 'Checkbox',
              url: '',
              hidden: true
            }
          ]
        }, {
          name: 'Theming',
          children: [
            {
              name: 'Introduction and Terms',
              url: ''
            }, {
              name: 'Declarative Syntax',
              url: ''
            }, {
              name: 'Configuring a Theme',
              url: ''
            }, {
              name: 'Multiple Themes',
              url: ''
            }, {
              name: 'Under the Hood',
              url: ''
            }, {
              name: 'Browser Color',
              url: ''
            }
          ]
        }, {
          name: 'Contributors',
          url: ''
        }
      ]];
    }
  }
];
