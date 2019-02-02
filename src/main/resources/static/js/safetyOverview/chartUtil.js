var ChartUtil = (function(p){

    function buildRectifyChartOption(options) {
        options = options ? options : {};
        var legendData = options['legendData'];
        var seriesData = options['seriesData'];
        var seriesName = options['seriesName'];
        var centerText = options['centerText'];
        var colors = options['colors'];
        var option = {
            title : {
                text: '',
                x:"center"
            },
            tooltip : {
                trigger: 'item',
                formatter: "{a} <br/>{b} : {c} ({d}%)"
            },
            legend: {
                show: true,
                orient: 'vertical',
                x:"right",
                y:"center",
                data: legendData
            },
            graphic:{
                type:'text',
                left:'center',
                top:'center',
                style:{
                    text: centerText,
                    textAlign:'center',
                    fill:'#000',
                    font: '1.5em "Microsoft YaHei", sans-serif'
                }
            },
            series : [
                {
                    name: seriesName,
                    type: 'pie',
                    radius : ['50%','70%'],
                    center: ['50%', '50%'],
                    data: seriesData,
                    label: {
                        normal: {
                            show: false
                        }
                    }
                }
            ],
            color:colors
        };
        return option;
    }

    /**
     * 构建垂直柱状图配置
     * @param options
     * @returns 垂直柱状图配置
     */
    function buildVerticalBarChartOption(options) {
        options = options ? options : {};
        var xAxisData = options.xAxisData;
        var seriesData = options.seriesData;
        var option = {
            title : {
                text: '',
                subtext: ''
            },
            tooltip : {
                trigger: 'item'
            },
            legend: {
                show:false,
                data:['总数']
            },
            calculable : false,
            xAxis : [
                {
                    type : 'category',
                    data : xAxisData, //['历史学院','化学学院','物理学院','工商学院','外语学院', '材料学院', '考古学院', '计算机学院'],
                    axisTick: {
                        show: false
                    },
                    splitLine: {
                        show: true,
                        lineStyle: {
                            type: 'dashed'
                        }
                    }
                }
            ],
            yAxis : [
                {
                    type : 'value',
                    axisLine: {
                        show: false
                    },
                    axisTick: {
                        show: false
                    },
                    splitLine: {
                        show: true,
                        lineStyle: {
                            type: 'dashed'
                        }
                    }
                }
            ],
            series : [
                {
                    name:'总数',
                    type:'bar',
                    data:  seriesData, //[33, 24, 14, 19, 12, 50, 9, 25],
                    itemStyle: {
                        normal: {
                            color: function(params) {
                                var colorList = ['#34BDDA', '#F9D24A'];
                                return colorList[params.dataIndex % 2]
                            }
                        }
                    }
                }
            ]
        };
        return option;
    }

    /**
     * 构建水平柱状图配置
     * 备注:
     * 1.实验室统计中的按学院统计
     *
     * @param options
     * @returns 水平柱状图配置
     */
    function buildHorizonBarChartOption(options) {
        var yAxisData = options.yAxisData;
        var seriesData = options.seriesData;
        var option = {
            title : {
                text: ''
            },
            tooltip : {
                trigger: 'item'
            },
            legend: {
                show: false
            },
            grid: {
                left: '25%'
            },
            calculable : false,
            xAxis : [
                {
                    show: false,
                    type : 'value',
                    boundaryGap : [0, 1]
                }
            ],
            yAxis : [
                {
                    type : 'category',
                    axisLine: {
                        show: false,
                        lineStyle :{
                            color: '#333',
                            width: 2,
                            type: 'solid'
                        }
                    },
                    axisTick: {
                        show: false
                    },
                    splitNumber: 1, // default 5
                    splitArea: {
                        show: false
                    },
                    data : yAxisData
                }
            ],
            series : [
                {
                    type:'bar',
                    data: seriesData,
                    itemStyle: {
                        color:'#46B8F9',
                        barBorderRadius: 5
                    },
                    barWidth: 10
                }
            ]
        };
        return option;
    }

    /**
     * 构建环形图表配置
     *
     * @param options
     * @returns 环形图表配置
     */
    function buildRingChartOption (options) {
        var legendData = options['legendData'];
        var seriesData = options['seriesData'];
        var seriesName = options['seriesName'];
        var centerText = options['centerText'];

        var option = {
            title : {
                text: '',
                x:"center"
            },
            tooltip : {
                trigger: 'item',
                formatter: "{a} <br/>{b} : {c} ({d}%)"
            },
            legend: {
                show: false,
                orient: 'horizontal',
                x:"center",
                y:"bottom",
                data: legendData
            },
            graphic:{
                type:'text',
                left:'center',
                top:'center',
                style:{
                    text: centerText,
                    textAlign:'center',
                    fill:'#000',
                    font: '1.5em "Microsoft YaHei", sans-serif'
                }
            },
            series : [
                {
                    name: seriesName,
                    type: 'pie',
                    radius : ['50%','70%'],
                    center: ['50%', '50%'],
                    data: seriesData,
                    labelLine: {
                        normal: {
                            length: 30,
                            length2: 0,
                            smooth: true,
                            lineStyle: {
                                color: '#99A3B0'
                            }
                        }
                    },
                    label: {
                        normal: {
                            formatter: '{a|{b}}\n{b|{c}}',
                            borderWidth: 0,
                            borderRadius: 4,
                            padding: [0, 0],
                            rich: {
                                a: {
                                    color: '#333',
                                    fontSize: 16,
                                    lineHeight: 20
                                },
                                hr: {
                                    borderColor: '#333',
                                    width: '100%',
                                    borderWidth: 0.5,
                                    height: 0
                                },
                                b: {
                                    fontSize: 16,
                                    lineHeight: 20,
                                    color: '#687789',
                                    align: 'center'
                                }
                            }
                        }
                    },
                    itemStyle: {
                        emphasis: {
                            shadowBlur: 10,
                            shadowOffsetX: 0,
                            shadowColor: 'rgba(0, 0, 0, 0.5)'
                        }
                    }
                }
            ],
            color:['#F9D24A','#FF822F','#34BDDA','#27E5B3', '#A0E527']
        };
        return option;
    }

    return {
        buildRingChartOption: buildRingChartOption,
        buildHorizonBarChartOption: buildHorizonBarChartOption, // 水平柱状图(条形图)
        buildVerticalBarChartOption: buildVerticalBarChartOption, // 垂直主状态
        buildRectifyChartOption: buildRectifyChartOption
    }
}());