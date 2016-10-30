package net.mikc.freechart.gfx;

import net.mikc.freechart.algo.AbstractTradingAlgo;
import net.mikc.freechart.algo.AccountBalance;
import net.mikc.freechart.algo.AccountBalanceAtTime;
import net.mikc.freechart.entities.HistoricalQuoteMarketData;
import net.mikc.freechart.entities.HistoricalQuoteData;
import net.mikc.freechart.entities.RealTimeQuoteData;
import net.mikc.freechart.quotes.HistoricalData;
import net.mikc.freechart.quotes.RealTimeData;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SegmentedTimeline;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.MovingAverage;
import org.jfree.data.xy.*;
import org.jfree.ui.ApplicationFrame;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by michalconos on 08/10/2016.
 */
public class DrawStocks extends ApplicationFrame {
    private static final int NUMBER_OF_VALUES = 600;
    private final Float minPrice, maxPrice;
    private final List<HistoricalQuoteData> marketValues;
    private final String symbolName ;
    private final AbstractTradingAlgo tradingAlgo;

    public DrawStocks(String symbolName, HistoricalQuoteMarketData marketData, AbstractTradingAlgo tradingAlgo) {
        super("Chart for: " + symbolName);
        this.marketValues = marketData.getSymbolMarketValues();
        this.symbolName = symbolName;
        this.maxPrice = marketData.getMaxPrice();
        this.minPrice = marketData.getMinPrice();
        this.tradingAlgo = tradingAlgo;

        final TimeSeriesDataSet timeSeriesDataSet = createDataset();
        final JFreeChart chart = createChart(timeSeriesDataSet);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(1500, 600));
        chartPanel.setFillZoomRectangle(true);
        chartPanel.setMouseWheelEnabled(true);
        setContentPane(chartPanel);
//        getContentPane().add(chartPanel);
    }

    private static class MarkerDate {
        private Date date;
        private List<AbstractTradingAlgo.Marker> markers;

        MarkerDate(final Date date, List<AbstractTradingAlgo.Marker> markers) {
            this.date =date;
            this.markers = markers;
        }

        Date getDate() {
            return date;
        }

        List<AbstractTradingAlgo.Marker> getMarkers() {
            return markers;
        }
    }

    private static class TimeSeriesDataSet {
        private DefaultHighLowDataset defaultHighLowDataset;
        private List<MarkerDate> markerTimeSeries;

        TimeSeriesDataSet(final DefaultHighLowDataset defaultHighLowDataset, final List<MarkerDate> markerTimeSeries) {
            this.defaultHighLowDataset = defaultHighLowDataset;
            this.markerTimeSeries = markerTimeSeries;
        }

        public DefaultHighLowDataset getDefaultHighLowDataset() {
            return defaultHighLowDataset;
        }

        public List<MarkerDate> getMarkerTimeSeries() {
            return markerTimeSeries;
        }
    }



    private TimeSeriesDataSet createDataset() {
        final List<MarkerDate> markerDates = new ArrayList<>();
        int marketValuesSize = marketValues.size();
        int stride = marketValuesSize / NUMBER_OF_VALUES;

        //marketValuesSize = 380;

        double openPrice[] = new double[NUMBER_OF_VALUES];
        double closePrice[] = new double[NUMBER_OF_VALUES];
        double lowPrice[] = new double[NUMBER_OF_VALUES];
        double highPrice[] = new double[NUMBER_OF_VALUES];
        double volume[] = new double[NUMBER_OF_VALUES];
        Date date[] = new Date[NUMBER_OF_VALUES];
        for(int idx=0, j=0; idx<marketValuesSize && j < NUMBER_OF_VALUES; ++j, idx+=stride) {
            final HistoricalQuoteData symbolMarketValue = marketValues.get(idx);
            openPrice[j] = symbolMarketValue.getOpen();
            closePrice[j] = symbolMarketValue.getClose();
            highPrice[j] = symbolMarketValue.getHigh();
            lowPrice[j] = symbolMarketValue.getLow();
            date[j] = symbolMarketValue.getDate();
            volume[j] = symbolMarketValue.getVolume();

            markerDates.add(new MarkerDate(symbolMarketValue.getDate(), symbolMarketValue.getMarkers()));
        }
        final DefaultHighLowDataset dataset = new DefaultHighLowDataset(
                symbolName,
                date,
                highPrice,
                lowPrice,
                openPrice,
                closePrice,
                volume
        );
        return new TimeSeriesDataSet(dataset, markerDates);

    }

    /**
     * Creates a chart.
     *
     * @param timeSeriesDataSet  the data for the chart.
     *
     * @return a chart.
     */
    private JFreeChart createChart(final TimeSeriesDataSet timeSeriesDataSet) {
        final OHLCDataset dataset = timeSeriesDataSet.getDefaultHighLowDataset();
        long startTime = marketValues.get(0).getDate().getTime();

        SegmentedTimeline baseTimeLine = new SegmentedTimeline(SegmentedTimeline.DAY_SEGMENT_SIZE, 5, 2);
        baseTimeLine.setStartTime(startTime);
        SegmentedTimeline twentyFourHourSegTimeline = new SegmentedTimeline(SegmentedTimeline.DAY_SEGMENT_SIZE, 7
                , 0);
        twentyFourHourSegTimeline.setStartTime(startTime);
        twentyFourHourSegTimeline.setBaseTimeline(baseTimeLine);
       // twentyFourHourSegTimeline.addExceptions(lstGapDates); //lstGapDates are holiday dates

        // 6.5hrs working, 17.5h gap
        SegmentedTimeline oneHourSegTimeLine = new SegmentedTimeline(SegmentedTimeline.HOUR_SEGMENT_SIZE/2, 13 , 35);
        oneHourSegTimeLine.setStartTime(startTime);
        oneHourSegTimeLine.setBaseTimeline(twentyFourHourSegTimeline);

//        for (IntraDayHourGap intraDayHourGap : filterCriteria.getIntraDayHourGap()) {
//            oneHourSegTimeLine.addException(intraDayHourGapStart, intraDayHourGapEnd);
//        }


        // create the chart...
        final JFreeChart chart = ChartFactory.createHighLowChart(symbolName, "time", "Price [USD]", dataset, oneHourSegTimeLine, true);
        //final JFreeChart chart = ChartFactory.createCandlestickChart(symbolName, "time", "Price [USD]", dataset, true);
        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        chart.setBackgroundPaint(Color.black);
        chart.setAntiAlias(true);

        final XYDataset dataset2 = MovingAverage.createMovingAverage(
                dataset, "-MAVG", 3 * 24 * 60 * 60 * 1000L, 0L
        );

        final XYDataset accountBalance = createAccountBalance();

        CandlestickRenderer renderer = new CandlestickRenderer();
        renderer.setAutoWidthMethod(CandlestickRenderer.WIDTHMETHOD_SMALLEST);
        renderer.setVolumePaint(Color.yellow);
        XYPlot plot = chart.getXYPlot();
        plot.setDataset(1, dataset2);
        plot.setRenderer(1, new StandardXYItemRenderer());

        // Additional dataset
        int index = 1;
       // plot.setDataset(index, xyDataset);
        plot.mapDatasetToRangeAxis(index, 0);
        XYLineAndShapeRenderer renderer2 = new XYLineAndShapeRenderer(true, false);
        renderer2.setSeriesPaint(index, Color.blue);
        plot.setRenderer(index, renderer2);
        // Misc
        plot.setRangeGridlinePaint(Color.lightGray);
        plot.setBackgroundPaint(Color.black);
        NumberAxis numberAxis = (NumberAxis) plot.getRangeAxis();
        numberAxis.setAutoRangeIncludesZero(false);
        numberAxis.setLabelPaint(Color.lightGray);
        numberAxis.setAxisLinePaint(Color.lightGray);
        numberAxis.setTickLabelPaint(Color.lightGray);
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
        plot.getDomainAxis().setLabelPaint(Color.lightGray);
        plot.getDomainAxis().setAxisLinePaint(Color.lightGray);
        plot.getDomainAxis().setTickLabelPaint(Color.lightGray);

        plot.mapDatasetToRangeAxis(0, 0);
        plot.mapDatasetToRangeAxis(1, 0);
        plot.mapDatasetToRangeAxis(2, 2);
        plot.mapDatasetToRangeAxis(3, 3);

        final ValueAxis axis2 = new NumberAxis("Account Balance [USD]");
        axis2.setAxisLinePaint(Color.lightGray);
        axis2.setTickLabelPaint(Color.lightGray);
        axis2.setTickLabelPaint(Color.lightGray);
        axis2.setLabelPaint(Color.lightGray);
        plot.setDataset(2, accountBalance);
        StandardXYItemRenderer renderer3 = new StandardXYItemRenderer();
        renderer3.setSeriesPaint(0, Color.red);
        renderer3.setSeriesPaint(1, Color.green);
        plot.setRenderer(2, renderer3);
        plot.setRangeAxis(2, axis2);

        axis2.setAutoRange(true);


        final ValueAxis axis3 = new NumberAxis("Markers");
        axis3.setAxisLinePaint(Color.lightGray);
        axis3.setTickLabelPaint(Color.lightGray);
        axis3.setTickLabelPaint(Color.lightGray);
        axis3.setLabelPaint(Color.lightGray);
        final XYColoredDataSet coloredDataSet = createMarkers(timeSeriesDataSet.getMarkerTimeSeries());
        final XYDataset markers = coloredDataSet.getXyDataset();

        plot.setDataset(3, markers);
        StandardXYItemRenderer renderer4 = new StandardXYItemRenderer();
        int cidx = 0;
        for(final AbstractTradingAlgo.Marker.Color color: coloredDataSet.getColors()) {
            renderer4.setSeriesPaint(cidx, getColor(color));
            cidx++;
        }
        plot.setRenderer(3, renderer4);
        axis3.setAutoRange(true);
        plot.setRangeAxis(3, axis3);

        plot.setRenderer(renderer);



        return chart;

    }

    Color getColor(AbstractTradingAlgo.Marker.Color color) {
        switch (color) {
            case WHITE:
                return Color.white;
            case BLUE:
                return Color.blue;
            case RED:
                return Color.red;
            case YELLOW:
                return Color.yellow;
            case GREEN:
                return Color.green;
            default:
                return Color.black;

        }
    }

    private static class XYColoredDataSet {
        private List<AbstractTradingAlgo.Marker.Color> colors;
        private XYDataset xyDataset;

        XYColoredDataSet(final List<AbstractTradingAlgo.Marker.Color> color, XYDataset xyDataset) {
            this.colors =color;
            this.xyDataset = xyDataset;
        }

        public List<AbstractTradingAlgo.Marker.Color> getColors() {
            return colors;
        }

        public XYDataset getXyDataset() {
            return xyDataset;
        }
    }

    public XYColoredDataSet createMarkers(List<MarkerDate> markerDates) {
        XYSeriesCollection result = new XYSeriesCollection();

        List<AbstractTradingAlgo.Marker.Color> colors = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        int max = 0;
        for(final MarkerDate markerDate: markerDates) {
            List<AbstractTradingAlgo.Marker> markers = markerDate.getMarkers();
            if (markers != null && max < markers.size()) {
                max = markers.size();
                titles.clear();
                colors.clear();
                int midx=0;
                for(final AbstractTradingAlgo.Marker m: markers) {
                    final String description = (m.getDescription()==null) ? "marker_"+midx : m.getDescription();
                    midx++;
                    titles.add(description);
                    colors.add(m.getColor());
                }
            }
        }
        List<XYSeries> series = new ArrayList<>();

        for(int idx = 0; idx<max; ++idx) {
            XYSeries plot = new XYSeries(titles.get(idx));
            series.add(plot);

        }
        for(final MarkerDate markerDate: markerDates) {
            List<AbstractTradingAlgo.Marker> markers = markerDate.getMarkers();
            if(markers!=null) {
                for (int idx = 0; idx < markers.size(); ++idx) {
                    series.get(idx).add(markerDate.getDate().getTime(), markers.get(idx).getValue());
                }
            }
        }
        for(final XYSeries xy: series) {
            result.addSeries(xy);
        }
        return new XYColoredDataSet(colors, result);
    }

    public  XYDataset createAccountBalance() {
        XYSeriesCollection result = new XYSeriesCollection();

        XYSeries accountBalanceLow = new XYSeries("accountBalanceLow");
        XYSeries accountBalanceHigh = new XYSeries("accountBalanceHigh");
        for(final AccountBalanceAtTime accountBalanceAtTime: tradingAlgo.getAccountBalanceSnapshots()) {
            accountBalanceLow.add(accountBalanceAtTime.getDate().getTime(), accountBalanceAtTime.getLow().getCurrentValue());
            accountBalanceHigh.add(accountBalanceAtTime.getDate().getTime(), accountBalanceAtTime.getHigh().getCurrentValue());

        }
        result.addSeries(accountBalanceLow);
        result.addSeries(accountBalanceHigh);

        return result;
    }





}
