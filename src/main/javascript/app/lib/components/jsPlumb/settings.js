export const JS_PLUMB_DEFAULT_SETTINGS = {
  endpoints: {
    source: {
      endpoint: 'Dot',
      isSource: true,
      isTarget: true,
      maxConnections: 1,
      connector: ['Flowchart', {
        cornerRadius: 10
      }],
      endpointStyle: {
        radius: 9,
        stroke: '#B0BFC8',
        strokeWidth: 3
      },
      connectorStyle: {
        stroke: '#B0BFC8',
        strokeWidth: 3,
        outlineStroke: 'white',
        outlineWidth: 2
      },
      connectorHoverStyle: {
        stroke: '#B0BFC8'
      },
      endpointHoverStyle: {
        stroke: '#B0BFC8'
      }
    },
    target: {
      endpoint: 'Dot',
      isTarget: true,
      maxConnections: -1,
      endpointStyle: {
        radius: 9,
        stroke: '#B0BFC8',
        strokeWidth: 3
      },
      endpointHoverStyle: {
        stroke: '#B0BFC8'
      }
    }
  }
};
