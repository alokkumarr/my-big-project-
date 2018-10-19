export const bubbleChartGrouped = {
  booleanCriteria: 'AND',
  filters: [],
  dataFields: [
    {
      name: 'AVAILABLE_ITEMS',
      type: 'long',
      columnName: 'AVAILABLE_ITEMS',
      displayName: 'Available Items',
      aliasName: '',
      table: 'MCT_CONTENT_SUMMARY',
      joinEligible: false,
      filterEligible: true,
      tableName: 'MCT_CONTENT_SUMMARY',
      checked: 'y',
      aggregate: 'sum'
    },
    {
      name: 'AVAILABLE_MB',
      type: 'float',
      columnName: 'AVAILABLE_MB',
      displayName: 'Available MB',
      aliasName: '',
      table: 'MCT_CONTENT_SUMMARY',
      joinEligible: false,
      filterEligible: true,
      tableName: 'MCT_CONTENT_SUMMARY',
      checked: 'z',
      aggregate: 'sum'
    }
  ],
  nodeFields: [
    {
      name: 'SOURCE_OS',
      type: 'string',
      columnName: 'SOURCE_OS.keyword',
      displayName: 'Source OS',
      aliasName: '',
      table: 'MCT_CONTENT_SUMMARY',
      joinEligible: false,
      filterEligible: true,
      tableName: 'MCT_CONTENT_SUMMARY',
      checked: 'g'
    },
    {
      name: 'SOURCE_MANUFACTURER',
      type: 'string',
      columnName: 'SOURCE_MANUFACTURER.keyword',
      displayName: 'Source Manufacturer',
      aliasName: '',
      table: 'MCT_CONTENT_SUMMARY',
      joinEligible: false,
      filterEligible: true,
      tableName: 'MCT_CONTENT_SUMMARY',
      checked: 'x'
    }
  ],
  sorts: []
};

export const columnChart = {
  booleanCriteria: 'AND',
  filters: [],
  dataFields: [
    {
      name: 'AVAILABLE_MB',
      type: 'float',
      columnName: 'AVAILABLE_MB',
      displayName: 'Available MB',
      aliasName: '',
      table: 'MCT_CONTENT_SUMMARY',
      joinEligible: false,
      filterEligible: true,
      tableName: 'MCT_CONTENT_SUMMARY',
      checked: 'y',
      aggregate: 'sum'
    }
  ],
  nodeFields: [
    {
      name: 'SOURCE_MANUFACTURER',
      type: 'string',
      columnName: 'SOURCE_MANUFACTURER.keyword',
      displayName: 'Source Manufacturer',
      aliasName: '',
      table: 'MCT_CONTENT_SUMMARY',
      joinEligible: false,
      filterEligible: true,
      tableName: 'MCT_CONTENT_SUMMARY',
      checked: 'x'
    }
  ],
  sorts: []
};

export const columnChartGrouped = {
  booleanCriteria: 'AND',
  filters: [],
  dataFields: [
    {
      name: 'AVAILABLE_MB',
      type: 'float',
      columnName: 'AVAILABLE_MB',
      displayName: 'Available MB',
      aliasName: '',
      table: 'MCT_CONTENT_SUMMARY',
      joinEligible: false,
      filterEligible: true,
      tableName: 'MCT_CONTENT_SUMMARY',
      checked: 'y',
      aggregate: 'sum'
    }
  ],
  nodeFields: [
    {
      name: 'SOURCE_OS',
      type: 'string',
      columnName: 'SOURCE_OS.keyword',
      displayName: 'Source OS',
      aliasName: '',
      table: 'MCT_CONTENT_SUMMARY',
      joinEligible: false,
      filterEligible: true,
      tableName: 'MCT_CONTENT_SUMMARY',
      checked: 'g'
    },
    {
      name: 'SOURCE_MANUFACTURER',
      type: 'string',
      columnName: 'SOURCE_MANUFACTURER.keyword',
      displayName: 'Source Manufacturer',
      aliasName: '',
      table: 'MCT_CONTENT_SUMMARY',
      joinEligible: false,
      filterEligible: true,
      tableName: 'MCT_CONTENT_SUMMARY',
      checked: 'x'
    }
  ],
  sorts: []
};

export const pivot = {
  booleanCriteria: 'AND',
  filters: [],
  sorts: [],
  rowFields: [
    {
      type: 'string',
      columnName: 'ACCOUNT_NAME'
    }
  ],
  columnFields: [
    {
      type: 'string',
      columnName: 'OPP_ADVISORY_NAME'
    }
  ],
  dataFields: [
    {
      type: 'double',
      columnName: 'OPP_ACV_ACTUAL',
      aggregate: 'sum',
      name: 'OPP_ACV_ACTUAL'
    }
  ]
};
