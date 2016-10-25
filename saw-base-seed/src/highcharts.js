// column-background-fill
// ============
//
// Highcharts plugin for creating background fills in column charts


(function (H) {
    H.wrap(H.seriesTypes.column.prototype, 'translate', function (proceed) {
        var yAxisHeight = this.yAxis.height;
        var series = this;

        proceed.call(this);

        if (this.options.enableFullBackgroundColor) {
            H.each(this.points, function (point) {
                var shapeArgs = point.shapeArgs;

                point.shapeBackground = series.crispCol(shapeArgs.x, 0, shapeArgs.width, yAxisHeight);
            });
        }
    });

    H.wrap(H.seriesTypes.column.prototype, 'drawPoints', function (proceed) {
        var series = this,
            chart = this.chart,
            options = series.options,
            renderer = chart.renderer,
            animationLimit = options.animationLimit || 250,
            shapeArgs,
            pointAttr;

        // draw the columns

        if (this.options.enableFullBackgroundColor) {

            H.each(series.points, function (point) {
                var plotY = point.plotY,
                    graphic = point.shapeBackgroundGraphic,
                    borderAttr;

                if (H.isNumber(plotY) && point.y !== null) {
                    shapeArgs = point.shapeBackground;

                    borderAttr = series.borderWidth !== undefined ? {
                        'stroke-width': series.borderWidth
                    } : {};

                    pointAttr = point.pointAttr[point.selected ? 'select' : ''] || series.pointAttr[''];

                    if (graphic) { // update
                        stop(graphic);
                        graphic.attr(borderAttr).attr(pointAttr)[chart.pointCount < animationLimit ? 'animate' : 'attr'](merge(shapeArgs)); // #4267

                    } else {
                        point.shapeBackgroundGraphic = renderer['rect'](shapeArgs)
                            .attr(borderAttr)
                            .attr({
                                fill: options.barBgColor,
                                r: pointAttr.r
                            })
                            .add(point.group || series.group)
                            .shadow(options.shadow, null, options.stacking && !options.borderRadius);
                    }

                } else if (graphic) {
                    point.shapeBackgroundGraphic = graphic.destroy(); // #1269
                }
            });
        }

        proceed.call(this);
    });
}(Highcharts));
