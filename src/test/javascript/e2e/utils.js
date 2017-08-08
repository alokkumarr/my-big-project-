/**
 * Check if an element has class
 */
function hasClass(element, cls) {
  return element
    .getAttribute('class')
    .then(classes => classes.split(' ').includes(cls));
}
module.exports = {
  hasClass
};
