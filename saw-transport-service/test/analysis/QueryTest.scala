import org.json4s.native.JsonMethods.parse
import org.scalatest.FunSpec
import org.scalatest.MustMatchers

import model.QueryBuilder

class QueryTest extends FunSpec with MustMatchers {
  describe("Report with spec") {
    it("should have query") {
      val query = QueryBuilder.build(parse(json))
      query must be ("SELECT 1 FROM foo")
    }
  }

  val json = """
{
    "artifacts": 
    [
        {
            "artifact_name": "foo",
            "artifact_position": [500, 200],
            "artifact_attributes" : 
            [
                {
                    "column_name" : "",
                    "display_name" : "",
                    "type" : "int|String",
                    "join_elgible" : "",
                    "filter_eligible" : "",
                    "hide" : "",
                    "checked" : ""
                },
                {
                    "column_name" : "",
                    "display_name" : "",
                    "type" : "int|String",
                    "join_elgible" : "",
                    "filter_eligible" : "",
                    "hide" : "",
                    "checked" : ""
                }
            ],
            "sql_builder": {
                "group_by_columns": [],
                "order_by_columns": [],
                "joins": [
                    {
                        "type": "inner",
                        "criteria": [
                            {
                                "table_name": "Orders",
                                "column_name": "Shipper",
                                "side": "right"
                            },
                            {
                                "table_name": "Shippers",
                                "column_name": "ShipperID",
                                "side": "left"
                            }
                        ]
                    },
                    {
                        "type": "inner",
                        "criteria": [
                            {
                                "table_name": "Orders",
                                "column_name": "Customer",
                                "side": "right"
                            },
                            {
                                "table_name": "Customers",
                                "column_name": "CustomerID",
                                "side": "left"
                            }
                        ]
                    },
                    {
                        "type": "inner",
                        "criteria": [
                            {
                                "table_name": "Orders",
                                "column_name": "Warehouse",
                                "side": "right"
                            },
                            {
                                "table_name": "Warehouses",
                                "column_name": "WarehouseID",
                                "side": "left"
                            }
                        ]
                    }
                ],
                "filters": [
                    {
                        "column_name": "TotalPrice",
                        "boolean_criteria": "AND",
                        "operator": ">=",
                        "search_conditions": [
                            1
                        ]
                    }
                ]
            }
        }
    ]
}
"""
}
