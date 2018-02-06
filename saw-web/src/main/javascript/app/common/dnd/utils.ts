
export function arrayMove(array, old_index, new_index) {
  if ( array.length === 0 ) {
      return array;
  }
  while (old_index < 0) {
      old_index += array.length;
  }
  while (new_index < 0) {
      new_index += array.length;
  }
  if (new_index >= array.length) {
      let k = new_index - array.length;
      while ((k--) + 1) {
          array.push(undefined);
      }
  }
  array.splice(new_index, 0, array.splice(old_index, 1)[0]);
  return array; // for testing purposes
}
