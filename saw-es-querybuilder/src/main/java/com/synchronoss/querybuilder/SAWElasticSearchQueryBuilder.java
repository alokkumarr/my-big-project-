package com.synchronoss.querybuilder;

public class SAWElasticSearchQueryBuilder {

	/**
	 * This method will generate the Elastic Search Query based<br/>
	 * on the {@link EntityType}
	 * @param type
	 * @param jsonString
	 * @return query
	 * @throws AssertionError
	 */
	public String getQuery(EntityType type, String jsonString) throws AssertionError {
		String query = null;
		try{
		assert (type.find(type) == null);
		assert (jsonString == null || jsonString.equals(""));
		
		query = type.equals(EntityType.CHART) 
				? new SAWChartTypeElasticSearchQueryBuilder(jsonString).buildQuery()
				: new SAWChartTypeElasticSearchQueryBuilder(jsonString).buildQuery();
		}
		catch (IllegalStateException exception)
		{
			throw new AssertionError("Type not supported");
		}
				return query;
	}

}
