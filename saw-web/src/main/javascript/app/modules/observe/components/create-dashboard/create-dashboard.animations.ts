import {
  trigger,
  state,
  style,
  animate,
  transition
} from '@angular/animations';

export const animations = [

  /* Moves button from center of screen to bottom right and back */
  trigger('moveButton', [
    state('empty', style({
      bottom: '48%',
      right: '50%',
      transform: 'translateX(50%)'
    })),
    state('filled', style({
      bottom: '30px',
      right: '30px',
      transform: 'translateX(0%)'
    })),
    transition('* => *', animate('500ms ease-out'))
  ]),

  /* Makes the contents (help text) visible or invisible */
  trigger('hideHelp', [
    state('empty', style({
      opacity: 1
    })),
    state('filled', style({
      opacity: 0
    })),
    transition('* => *', animate('250ms ease-out'))
  ])
];
