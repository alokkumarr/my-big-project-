module.exports = {

  analyzeElements: {
    analyzeCard: element(by.css('.analyze-card'))

  },

  validateCard: function () {
    expect(this.analyzeElements.analyzeCard.isPresent()).toBeTruthy();
  }
};
