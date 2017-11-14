import Key from './key.model';
import Analysis from './analysis.model';

export interface Contents {
  analyze: Analysis[];
  action:  string;
  keys:    Key[];
}
