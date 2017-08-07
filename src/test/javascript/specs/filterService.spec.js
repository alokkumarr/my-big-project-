import {AnalyzeModule} from '../../../main/javascript/app/modules/analyze';

describe('FilterService', () => {
  let service;

  beforeEach(() => {
    angular.mock.module(AnalyzeModule);
    angular.mock.inject($injector => {
      service = $injector.get('FilterService');
    });
  });

  it('exists', () => {
    expect(service).to.be.an('object');
  });

  describe('isFilterEmpty', () => {

    it('should return true for invalid values', () => {
      expect(service.isFilterEmpty()).to.equal(true);
      expect(service.isFilterEmpty({})).to.equal(true);
      expect(service.isFilterEmpty({type: 'asdfasdf'})).to.equal(true);
    });

    it('should work for string filters', () => {
      const filter = {type: 'string', model: {}};
      expect(service.isFilterEmpty(filter)).to.equal(true);
      filter.model.modelValues = [];
      expect(service.isFilterEmpty(filter)).to.equal(true);
      filter.model.modelValues.push('abc');
      expect(service.isFilterEmpty(filter)).to.equal(false);
    });

    it('should work for number filters', () => {
      const filter = {type: 'long', model: {}};
      expect(service.isFilterEmpty(filter)).to.equal(true);
      filter.model.value = 1;
      expect(service.isFilterEmpty(filter)).to.equal(false);
    });

    it('should work for date filters', () => {
      const filter = {type: 'date', model: {}};
      expect(service.isFilterEmpty(filter)).to.equal(true);
      filter.model.value = 1;
      expect(service.isFilterEmpty(filter)).to.equal(false);
    });
  });
});
