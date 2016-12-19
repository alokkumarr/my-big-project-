import square from '../../main/javascript/square';

describe('The square function', () => {
  it('should square a number', () => {
    expect(square(3)).toBe(9);
  });
});
