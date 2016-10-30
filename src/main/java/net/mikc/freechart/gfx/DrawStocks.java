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

        final DefaultHighLowDataset dataset = createDataset();
        final JFreeChart chart = createChart(dataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(1500, 600));
        chartPanel.setFillZoomRectangle(true);
        chartPanel.setMouseWheelEnabled(true);
        setContentPane(chartPanel);
//        getContentPane().add(chartPanel);
    }



    private DefaultHighLowDataset createDataset() {
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
        return dataset;

    }

    /**
     * Creates a chart.
     *
     * @param dataset  the data for the chart.
     *
     * @return a chart.
     */
    private JFreeChart createChart(final OHLCDataset dataset) {
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
        plot.setRenderer(renderer);



        return chart;

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
