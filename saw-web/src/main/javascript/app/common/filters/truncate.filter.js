import * as truncate from 'lodash/truncate';

export function truncateFilter() {
  return (input, length) => {
    return truncate(input, {length, omission: ''});
  };
}
