package pgraph.anchorage;

import pgraph.anchorage.distributions.*;
import pgraph.anchorage.policy.*;
import pgraph.anchorage.util.AnchorAreaFactory;
import pgraph.util.TimerUtil;
import pgraph.util.Pen;
import pgraph.util.Polygon2D;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 02.12.2013
 * Time: 10:52
 * To change this template use File | Settings | File Templates.
 */
public class AnchorageExperimenter {

    java.util.List<AnchorageExperimentConfig> configList = null;
    private AnchorArea anchorArea = null;
    private ArrivalGenerator arrivalGenerator=null;




    public AnchorageExperimenter(AnchorArea anchorArea, List<AnchorageExperimentConfig> configList, ArrivalGenerator arrivalGenerator) {
        this.anchorArea = anchorArea;
        this.configList = configList;
        this.arrivalGenerator = arrivalGenerator;
    }

    private boolean _performAnchorageSilent(AnchorArea a , AnchorPolicy p ,ArrivalInterface arrival) throws InterruptedException {

        if (a.isAreaFull())
            return false;



        TimerUtil.start("ANCHORPOLICY");
        Anchorage anchorage= p.createAnchorage(anchorArea,arrival);
        TimerUtil.stop("ANCHORPOLICY");

        if (anchorage == null)
        {
            a.setAreaFull(true);
            return false;
        }
        if (anchorage.getArea()==null)
        {
            return true;
        }

        TimerUtil.start("ADDEXISTING");
        a.addExistingAnchorage(anchorage);
        TimerUtil.stop("ADDEXISTING");

        return true;
    }



    private void performExperiment(AnchorArea a, AnchorPolicy p, ArrivalRepeater ar,int experimentCount) throws InterruptedException {
        System.out.println("Experiment started..");
        TimerUtil.start("EXPERIMENT");



        boolean finished = false;

        ar.getArrivalGenerator().reset();

        while (!finished)
        {
            ArrivalInterface arrival = ar.generate(experimentCount);
            finished = !_performAnchorageSilent(a,p,arrival);
        }

        TimerUtil.stop("EXPERIMENT");
        TimerUtil.printTotal();  TimerUtil.reset();

        System.out.println("Dijkstra Call Count : " + a.dijkstraCallCount);
        System.out.println("Experiment ended..");
    }

    public void performRepeatedExperiment(int repeatCount) throws InterruptedException, IOException
    {
        ArrivalRepeater ar = new ArrivalRepeater(arrivalGenerator,repeatCount);

        for (AnchorageExperimentConfig config : configList)
        {

            AnchorPolicy p = config.getAnchorPolicy();

            anchorArea.resetSummary();
            anchorArea.setOutputSummaryFile(config.getOutputSummaryFile());
            anchorArea.setOutputFile(config.getOutputFile());
            anchorArea.setMaxIntersection(config.getMaxIntersection());
            anchorArea.setRiskFactor(config.getRiskFactor());

            for (int i = 0; i<repeatCount;i++ )
            {
                System.out.println("EXPERIMENT: " + i+" -------------------------------------------------");
                anchorArea.reset();
                performExperiment(anchorArea, p,ar,i);
                anchorArea.updateSummaryStatistics();
            }

            anchorArea.printSummaryStatistics(config.getConfigHeader());
            ar.resetMemoryIndex();
        }

        ar.clearMemory();


    }

    public static List<AnchorageExperimentConfig> prepareHybridPolicyExperimentSet(double wHoleDegreeSet[], double wSafetySet[],double wDistanceSet[],double wStackingSet[],double maximumdistance,String outputSummaryFileName, String outputFileNameHeader)
    {
        java.util.List<AnchorageExperimentConfig> configList = new ArrayList<AnchorageExperimentConfig>();

        for (int hd=0;hd<wHoleDegreeSet.length;hd++)
        {
            for (int s=0;s<wSafetySet.length;s++)
            {
                for (int d=0;d<wDistanceSet.length;d++)
                {
                    for (int st=0;st<wStackingSet.length;st++)
                    {
                        String paramStr= "_"+ wHoleDegreeSet[hd] + "_" + wSafetySet[s] + "_" + wDistanceSet[d] + "_" + wStackingSet[st];
                        String configHeader= "HYB" + paramStr;
                        String outputFileName = (outputFileNameHeader==null)? null:outputFileNameHeader+paramStr;
                        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicyV2(wHoleDegreeSet[hd],wSafetySet[s],wDistanceSet[d] ,wStackingSet[st],maximumdistance ),outputSummaryFileName,outputFileName,configHeader));
                    }
                }
            }
        }
        System.out.println(configList.size()+" experiment configurations have been prepared..");
        return configList;
    }


    private static void test1() throws InterruptedException, IOException {

        AnchorArea aa1 = new AnchorArea();
        aa1.setArea(new Polygon2D(new double[]{0, 0, 200, 200}, new double[]{0, 200, 200, 0}, 4));
        aa1.setEntrySide(new Line2D.Double(0,200,200,200));
        aa1.setFirstEntryPoint(new Point2D.Double(100,199));

        AnchorArea aa2 = new AnchorArea();
        aa2.setArea(new Polygon2D(new double[]{0, 0, 100, 100}, new double[]{0, 100, 100, 0}, 4));
        aa2.setEntrySide(new Line2D.Double(0,100,100,100));
        aa2.setFirstEntryPoint(new Point2D.Double(50,99));

        java.util.List<AnchorageExperimentConfig> configList = new ArrayList<AnchorageExperimentConfig>();

        configList.add(new AnchorageExperimentConfig(new MHDAnchoragePolicy(),"anchorSummary.txt",null,"MHD"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy(1,0.2,100 ,0,aa1.getFirstEntryPoint().distance(150,0) ),"anchorSummary.txt",null,"HYB_1_0.2_100"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy(5,20,1 ,0,aa1.getFirstEntryPoint().distance(150,0) ),"anchorSummary.txt",null,"HYB_5_20_1"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy(1,20,100 ,0,aa1.getFirstEntryPoint().distance(150,0) ),"anchorSummary.txt",null,"HYB_1_20_100"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy(1,20,20 ,0,aa1.getFirstEntryPoint().distance(150,0) ),"anchorSummary.txt",null,"HYB_1_20_20"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy(5,20,100 ,0,aa1.getFirstEntryPoint().distance(150,0) ),"anchorSummary.txt",null,"HYB_5_20_100"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy(5,20,20 ,0,aa1.getFirstEntryPoint().distance(150,0) ),"anchorSummary.txt",null,"HYB_5_20_20"));

        AnchorageExperimenter apc = new AnchorageExperimenter(aa2,configList,new UniformArrivalGenerator(1));

        apc.performRepeatedExperiment(50);

    }

    private static void test3() throws InterruptedException, IOException {

        AnchorArea aa2 = new AnchorArea();
        aa2.setArea(new Polygon2D(new double[]{0, 0, 100, 100}, new double[]{0, 100, 100, 0}, 4));
        aa2.setEntrySide(new Line2D.Double(0,100,100,100));
        aa2.setFirstEntryPoint(new Point2D.Double(50,99));

        java.util.List<AnchorageExperimentConfig> configList = prepareHybridPolicyExperimentSet(    new double[]{1,2,5,10},   // HOLE DEGREE PARAM SET
                                                                                                    new double[]{1,2,5,10},   // SAFETY PARAM SET
                                                                                                    new double[]{1,2,5,10},   // DISTANCE PARAM SET
                                                                                                    new double[]{1,2,5,10},   // STACKING PARAM SET
                                                                                                    aa2.getFirstEntryPoint().distance(0,0),"anchorSummary_14_01_2014",null );

        AnchorageExperimenter apc = new AnchorageExperimenter(aa2,configList,new AhkpArrivalGenerator(0.1));

        apc.performRepeatedExperiment(50);


    }

    private static void test2() throws InterruptedException, IOException {

        /*
        AnchorArea aa1 = new AnchorArea();
        aa1.setArea(new Polygon2D(new double[]{0, 0, 200, 200}, new double[]{0, 200, 200, 0}, 4));
        aa1.setEntrySide(new Line2D.Double(0,200,200,200));
        aa1.setEntryPoint(new Point2D.Double(100,199));
        */

        DepthZone dz1 = new DepthZone(new Polygon2D(new double[]{50, 30, 250, 250}, new double[]{0, 80, 60, 0}, 4),8,"8m");
        dz1.setPen(new Pen(new Color(0,0,255,90)));
        DepthZone dz2 = new DepthZone(new Polygon2D(new double[]{250, 250, 470, 450}, new double[]{0, 60, 80,0}, 4),15,"15m");
        dz2.setPen(new Pen(new Color(0,0,255,45)));
        DepthZone dz3 = new DepthZone(new Polygon2D(new double[]{30, 20,280, 480, 470,250}, new double[]{80, 120,150, 120, 80,60}, 6),25,"25m");
        dz3.setPen(new Pen(new Color(0,0,255,15)));

        AnchorArea aa = new AnchorArea();
        aa.setArea(new Polygon2D(new double[]{50, 0, 500, 450}, new double[]{0, 200, 200, 0}, 4));
        aa.setEntrySide(new Line2D.Double(0,200,500,200));
        aa.setFirstEntryPoint(new Point2D.Double(250,200));

        aa.addDepthZone(dz1);
        aa.addDepthZone(dz2);
        aa.addDepthZone(dz3);

        java.util.List<AnchorageExperimentConfig> configList = new ArrayList<AnchorageExperimentConfig>();

        configList.add(new AnchorageExperimentConfig(new MHDAnchoragePolicy(),"anchorSummary_28_01_2014.txt",null,"MHD"));
        configList.add(new AnchorageExperimentConfig(new WallPack_MHDAnchoragePolicy(),"anchorSummary_28_01_2014.txt",null,"WP_MHD"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicyV2(1,1,5 ,0,200 ),"anchorSummary_28_01_2014.txt",null,"HYB_1_1_5"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicyV2(1,1,10 ,0,200 ),"anchorSummary_28_01_2014.txt",null,"HYB_1_1_10"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicyV2(1,5,10 ,0,200 ),"anchorSummary_28_01_2014.txt",null,"HYB_1_5_10"));

        AnchorageExperimenter apc = new AnchorageExperimenter(aa,configList,new AhkpArrivalGenerator(0.1));

        apc.performRepeatedExperiment(50);

    }

    private static void testUniformAreaNoRiskFactorAhkpArrival(int repeatCount, String outputSuffix) throws InterruptedException, IOException
    {
        testUniformAreaNoRiskFactor(repeatCount,outputSuffix,new AhkpArrivalGenerator());
    }

    private static void testUniformAreaNoRiskFactorAhkpGammaArrival(int repeatCount, String outputSuffix) throws InterruptedException, IOException
    {
        testUniformAreaNoRiskFactor(repeatCount,outputSuffix,new AhkpGammaArrivalGenerator());
    }

    private static void testUniformAreaNoRiskFactorUniform(int repeatCount,String outputSuffix) throws InterruptedException, IOException
    {
        testUniformAreaNoRiskFactor(repeatCount,outputSuffix,new UniformArrivalGenerator());
    }

    private static void testUniformAreaNoRiskFactorChiSquare(int repeatCount,String outputSuffix) throws InterruptedException, IOException
    {
        testUniformAreaNoRiskFactor(repeatCount,outputSuffix,new ChiSquareArrivalGenerator(80,10));
    }

    private static void testUniformAreaNoRiskFactorNormal(int repeatCount,String outputSuffix) throws InterruptedException, IOException
    {
        testUniformAreaNoRiskFactor(repeatCount,outputSuffix,new NormalArrivalGenerator());
    }

    private static void testUniformAreaNoRiskFactorBeta(int repeatCount,String outputSuffix) throws InterruptedException, IOException
    {
        testUniformAreaNoRiskFactor(repeatCount,outputSuffix,new BetaArrivalGenerator());
    }

    private static void testUniformAreaNoRiskFactor(int repeatCount, String outputSuffix, ArrivalGenerator arrivals) throws IOException, InterruptedException {
        List<AnchorArea> areaList = AnchorAreaFactory.createExperimentAreaSet();

        String outputSummaryFile =   "anchorSummary_" + outputSuffix + ".txt";

        java.util.List<AnchorageExperimentConfig> configList = new ArrayList<AnchorageExperimentConfig>();
        configList.add(new AnchorageExperimentConfig(new RandomAnchoragePolicy(),outputSummaryFile,null,"RND"));
        //configList.add(new AnchorageExperimentConfig(new MHDAnchoragePolicy(),outputSummaryFile,null,"MHD"));
        //configList.add(new AnchorageExperimentConfig(new WallPack_MHDAnchoragePolicy(),outputSummaryFile,null,"WP_MHD"));
        //configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicyV2(1,0,10 ,0,4000 ),outputSummaryFile,null,"HYB_1_0_10"));

        for (AnchorArea a: areaList)
        {
            arrivals.setScale(a.getScale());
            AnchorageExperimenter ae = new AnchorageExperimenter(a,configList,arrivals);
            ae.performRepeatedExperiment(repeatCount);
        }
    }

    private static void testUniformAreaDEMOUniform(int repeatCount) throws InterruptedException, IOException
    {

        AnchorArea aA = AnchorAreaFactory.createDemoArea();
        aA.setRiskFactor(0);

        java.util.List<AnchorageExperimentConfig> configList = new ArrayList<AnchorageExperimentConfig>();

        configList.add(new AnchorageExperimentConfig(new MHDAnchoragePolicy(),"anchorSummary_14_02_2014_DEMO.txt",null,"MHD"));
        configList.add(new AnchorageExperimentConfig(new WallPack_MHDAnchoragePolicy(),"anchorSummary_14_02_2014_DEMO.txt",null,"WP_MHD"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicyV2(1,0,10 ,0,4000 ),"anchorSummary_14_02_2014_DEMO.txt",null,"HYB_1_0_10"));

        AnchorageExperimenter ae = new AnchorageExperimenter(aA,configList,new BetaArrivalGenerator());

        ae.performRepeatedExperiment(repeatCount);

    }

    private static void testParameterTuningForHybrid(int repeatCount, String outputSuffix) throws InterruptedException, IOException
    {
        List<AnchorArea> areaList = AnchorAreaFactory.createExperimentAreaSet();


        java.util.List<AnchorageExperimentConfig> configList = new ArrayList<AnchorageExperimentConfig>();

        String outputSummaryFile =   "anchorSummary_" + outputSuffix + ".txt";

        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicyV2(1,0,1 ,0,4000 ),outputSummaryFile,null,"HYB_1_0_1"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicyV2(1,0,2 ,0,4000 ),outputSummaryFile,null,"HYB_1_0_2"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicyV2(1,0,5 ,0,4000 ),outputSummaryFile,null,"HYB_1_0_5"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicyV2(1,0,10 ,0,4000 ),outputSummaryFile,null,"HYB_1_0_10"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicyV2(1,0,20 ,0,4000 ),outputSummaryFile,null,"HYB_1_0_20"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicyV2(1,0,50 ,0,4000 ),outputSummaryFile,null,"HYB_1_0_50"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicyV2(1,0,100 ,0,4000 ),outputSummaryFile,null,"HYB_1_0_100"));

        for (AnchorArea a: areaList)
        {
            AnchorageExperimenter ae = new AnchorageExperimenter(a,configList,new AhkpArrivalGenerator(a.getScale()));
            ae.performRepeatedExperiment(repeatCount);
        }

    }


    private static void testNonUniform_VS_Uniform2() throws InterruptedException, IOException
    {
        AnchorArea aa = AnchorAreaFactory.createNonUniform2();
        aa.setRiskFactor(0);


        java.util.List<AnchorageExperimentConfig> configList = new ArrayList<AnchorageExperimentConfig>();

        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicyV2(1,0,10 ,0,6000 ),"anchorSummary_27_04_2014_NU.txt",null,"HYB_1_0_10"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,50 ),"anchorSummary_27_04_2014_NU.txt",null,"HYB_UA_50"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,40 ),"anchorSummary_27_04_2014_NU.txt",null,"HYB_UA_40"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,30 ),"anchorSummary_27_04_2014_NU.txt",null,"HYB_UA_30"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,20 ),"anchorSummary_27_04_2014_NU.txt",null,"HYB_UA_20"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,15 ),"anchorSummary_27_04_2014_NU.txt",null,"HYB_UA_15"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,10 ),"anchorSummary_27_04_2014_NU.txt",null,"HYB_UA_10"));

        //configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,1 ),"anchorSummary_17_03_2014_NU.txt",null,"HYB_UA_10"));

        //AnchorageExperimenter apc = new AnchorageExperimenter(aa,configList,new UniformArrivalGenerator(25,340,aa.getScale()));
        AnchorageExperimenter apc = new AnchorageExperimenter(aa,configList,new AhkpArrivalGenerator(aa.getScale()));

        apc.performRepeatedExperiment(5);
    }


    private static void testNonUniform_VS_Uniform_AK1() throws InterruptedException, IOException
    {
        AnchorArea aa = AnchorAreaFactory.createNonUniform_AK1();
        aa.setRiskFactor(0);


        java.util.List<AnchorageExperimentConfig> configList = new ArrayList<AnchorageExperimentConfig>();

        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicyV2(1,0,10 ,0,6000 ),"anchorSummary_27_04_2014_NU_AK1.txt",null,"HYB_1_0_10"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,30 ),"anchorSummary_27_04_2014_NU_AK1.txt",null,"HYB_UA_30"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,25 ),"anchorSummary_27_04_2014_NU_AK1.txt",null,"HYB_UA_25"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,20 ),"anchorSummary_27_04_2014_NU_AK1.txt",null,"HYB_UA_20"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,15 ),"anchorSummary_27_04_2014_NU_AK1.txt",null,"HYB_UA_15"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,10 ),"anchorSummary_27_04_2014_NU_AK1.txt",null,"HYB_UA_10"));

        //configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,1 ),"anchorSummary_17_03_2014_NU.txt",null,"HYB_UA_10"));

        //AnchorageExperimenter apc = new AnchorageExperimenter(aa,configList,new UniformArrivalGenerator(25,340,aa.getScale()));
        AnchorageExperimenter apc = new AnchorageExperimenter(aa,configList,new AhkpArrivalGenerator(aa.getScale()));

        apc.performRepeatedExperiment(5);
    }

    private static void testNonUniform_VS_Uniform_AK2() throws InterruptedException, IOException
    {
        AnchorArea aa = AnchorAreaFactory.createNonUniform_AK2();
        aa.setRiskFactor(0);


        java.util.List<AnchorageExperimentConfig> configList = new ArrayList<AnchorageExperimentConfig>();

        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicyV2(1,0,10 ,0,6000 ),"anchorSummary_27_04_2014_NU_AK2.txt",null,"HYB_1_0_10"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,35 ),"anchorSummary_27_04_2014_NU_AK2.txt",null,"HYB_UA_35"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,30 ),"anchorSummary_27_04_2014_NU_AK2.txt",null,"HYB_UA_30"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,25 ),"anchorSummary_27_04_2014_NU_AK2.txt",null,"HYB_UA_25"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,20 ),"anchorSummary_27_04_2014_NU_AK2.txt",null,"HYB_UA_20"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,15 ),"anchorSummary_27_04_2014_NU_AK2.txt",null,"HYB_UA_15"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,10 ),"anchorSummary_27_04_2014_NU_AK2.txt",null,"HYB_UA_10"));

        //configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,1 ),"anchorSummary_17_03_2014_NU.txt",null,"HYB_UA_10"));

        //AnchorageExperimenter apc = new AnchorageExperimenter(aa,configList,new UniformArrivalGenerator(25,340,aa.getScale()));
        AnchorageExperimenter apc = new AnchorageExperimenter(aa,configList,new AhkpArrivalGenerator(aa.getScale()));

        apc.performRepeatedExperiment(5);
    }

    private static void testNonUniform_VS_Uniform_AK3() throws InterruptedException, IOException
    {
        AnchorArea aa = AnchorAreaFactory.createNonUniform_AK3();
        aa.setRiskFactor(0);


        java.util.List<AnchorageExperimentConfig> configList = new ArrayList<AnchorageExperimentConfig>();

        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicyV2(1,0,10 ,0,6000 ),"anchorSummary_27_04_2014_NU_AK3.txt",null,"HYB_1_0_10"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,70 ),"anchorSummary_27_04_2014_NU_AK3.txt",null,"HYB_UA_70"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,60 ),"anchorSummary_27_04_2014_NU_AK3.txt",null,"HYB_UA_60"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,50 ),"anchorSummary_27_04_2014_NU_AK3.txt",null,"HYB_UA_55"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,40 ),"anchorSummary_27_04_2014_NU_AK3.txt",null,"HYB_UA_40"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,30 ),"anchorSummary_27_04_2014_NU_AK3.txt",null,"HYB_UA_30"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,20 ),"anchorSummary_27_04_2014_NU_AK3.txt",null,"HYB_UA_20"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,10 ),"anchorSummary_27_04_2014_NU_AK3.txt",null,"HYB_UA_10"));

        //configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,1 ),"anchorSummary_17_03_2014_NU.txt",null,"HYB_UA_10"));

        //AnchorageExperimenter apc = new AnchorageExperimenter(aa,configList,new UniformArrivalGenerator(25,340,aa.getScale()));
        AnchorageExperimenter apc = new AnchorageExperimenter(aa,configList,new AhkpArrivalGenerator(aa.getScale()));

        apc.performRepeatedExperiment(5);
    }

    private static void testNonUniform_VS_Uniform() throws InterruptedException, IOException
    {
        AnchorArea aa = AnchorAreaFactory.createNonUniform1();
        aa.setRiskFactor(0);


        java.util.List<AnchorageExperimentConfig> configList = new ArrayList<AnchorageExperimentConfig>();

        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicyV2(1,0,10 ,0,200 ),"anchorSummary_27_04_2014_NU.txt",null,"HYB_1_0_10"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,5 ),"anchorSummary_27_04_2014_NU.txt",null,"HYB_UA_50"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,4.5 ),"anchorSummary_27_04_2014_NU.txt",null,"HYB_UA_45"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,4 ),"anchorSummary_27_04_2014_NU.txt",null,"HYB_UA_40"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,3.5 ),"anchorSummary_27_04_2014_NU.txt",null,"HYB_UA_35"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,3 ),"anchorSummary_27_04_2014_NU.txt",null,"HYB_UA_30"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,2.5 ),"anchorSummary_27_04_2014_NU.txt",null,"HYB_UA_25"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,2 ),"anchorSummary_27_04_2014_NU.txt",null,"HYB_UA_20"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,1.5 ),"anchorSummary_27_04_2014_NU.txt",null,"HYB_UA_15"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,1 ),"anchorSummary_27_04_2014_NU.txt",null,"HYB_UA_10"));

        //configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicy_UniformAssumption(200,1 ),"anchorSummary_17_03_2014_NU.txt",null,"HYB_UA_10"));

        //AnchorageExperimenter apc = new AnchorageExperimenter(aa,configList,new UniformArrivalGenerator(25,340,aa.getScale()));
        AnchorageExperimenter apc = new AnchorageExperimenter(aa,configList,new AhkpArrivalGenerator(aa.getScale()));

        apc.performRepeatedExperiment(10);
    }

    private static void testNonUniformAreaNoRiskFactor() throws InterruptedException, IOException
    {
        AnchorArea aa = AnchorAreaFactory.createNonUniform1();
        aa.setRiskFactor(0);


        java.util.List<AnchorageExperimentConfig> configList = new ArrayList<AnchorageExperimentConfig>();

        configList.add(new AnchorageExperimentConfig(new MHDAnchoragePolicy(),"anchorSummary_31_01_2014_NU.txt",null,"MHD"));
        configList.add(new AnchorageExperimentConfig(new WallPack_MHDAnchoragePolicy(),"anchorSummary_31_01_2014_NU.txt",null,"WP_MHD"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicyV2(1,1,5 ,0,200 ),"anchorSummary_31_01_2014_NU.txt",null,"HYB_1_1_5"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicyV2(1,1,10 ,0,200 ),"anchorSummary_31_01_2014_NU.txt",null,"HYB_1_1_10"));
        configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicyV2(1,5,10 ,0,200 ),"anchorSummary_31_01_2014_NU.txt",null,"HYB_1_5_10"));

        AnchorageExperimenter apc = new AnchorageExperimenter(aa,configList,new AhkpArrivalGenerator(aa.getScale()));

        apc.performRepeatedExperiment(50);
    }


    private static void testNonUniformAreaWithRiskFactor() throws InterruptedException, IOException
    {
        DepthZone dz1 = new DepthZone(new Polygon2D(new double[]{50, 30, 350, 350}, new double[]{0, 160, 120, 0}, 4),1.5,"15m");
        dz1.setPen(new Pen(new Color(0,0,255,90)));
        DepthZone dz2 = new DepthZone(new Polygon2D(new double[]{350, 350, 670, 650}, new double[]{0, 120, 160,0}, 4),2.5,"25m");
        dz2.setPen(new Pen(new Color(0,0,255,45)));
        DepthZone dz3 = new DepthZone(new Polygon2D(new double[]{30, 20,400, 680, 670,350}, new double[]{160, 240,300, 240, 160,120}, 6),3,"30m");
        dz3.setPen(new Pen(new Color(0,0,255,15)));


        AnchorArea aa = new AnchorArea();
        aa.setArea(new Polygon2D(new double[]{50, 0, 700, 650}, new double[]{0, 400, 400, 0}, 4));
        aa.setEntrySide(new Line2D.Double(0,400,700,400));
        aa.setFirstEntryPoint(new Point2D.Double(350, 400));
        aa.setMaximumDepth(3.5);

        aa.addDepthZone(dz1);
        aa.addDepthZone(dz2);
        aa.addDepthZone(dz3);

        aa.setShowStatistics(false);
        aa.setStatsPlace(AnchorArea.STATS_PLACE.SP_RIGHT);


        aa.setScale(0.1);

        java.util.List<AnchorageExperimentConfig> configList = new ArrayList<AnchorageExperimentConfig>();

        double riskFactors[]= { 0.7, 0.8 ,0.9 , 1};

        for (int i=0;i<riskFactors.length;i++)
        {
            //configList.add(new AnchorageExperimentConfig(new MHDAnchoragePolicy(),"anchorSummary_31_01_2014_NU_RF.txt",null,"MHD",riskFactors[i]));
            configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicyV2(1,5,10 ,0,200 ),"anchorSummary_31_01_2014_NU_RF.txt",null,"HYB_1_5_10",riskFactors[i]));
        }

        AnchorageExperimenter apc = new AnchorageExperimenter(aa,configList,new UniformArrivalGenerator(aa.getScale()));

        apc.performRepeatedExperiment(50);
    }


    private static void test4() throws InterruptedException, IOException {

        /*
        AnchorArea aa1 = new AnchorArea();
        aa1.setArea(new Polygon2D(new double[]{0, 0, 200, 200}, new double[]{0, 200, 200, 0}, 4));
        aa1.setEntrySide(new Line2D.Double(0,200,200,200));
        aa1.setEntryPoint(new Point2D.Double(100,199));
        */

        DepthZone dz1 = new DepthZone(new Polygon2D(new double[]{50, 30, 250, 250}, new double[]{0, 80, 60, 0}, 4),8,"8m");
        dz1.setPen(new Pen(new Color(0,0,255,90)));
        DepthZone dz2 = new DepthZone(new Polygon2D(new double[]{250, 250, 470, 450}, new double[]{0, 60, 80,0}, 4),15,"15m");
        dz2.setPen(new Pen(new Color(0,0,255,45)));
        DepthZone dz3 = new DepthZone(new Polygon2D(new double[]{30, 20,280, 480, 470,250}, new double[]{80, 120,150, 120, 80,60}, 6),25,"25m");
        dz3.setPen(new Pen(new Color(0,0,255,15)));

        AnchorArea aa = new AnchorArea();
        aa.setArea(new Polygon2D(new double[]{50, 0, 500, 450}, new double[]{0, 200, 200, 0}, 4));
        aa.setEntrySide(new Line2D.Double(0,200,500,200));
        aa.setFirstEntryPoint(new Point2D.Double(250,200));

        aa.addDepthZone(dz1);
        aa.addDepthZone(dz2);
        aa.addDepthZone(dz3);



        java.util.List<AnchorageExperimentConfig> configList = new ArrayList<AnchorageExperimentConfig>();

        for (int i=0; i<10; i++)
        {
            configList.add(new AnchorageExperimentConfig(new MHDAnchoragePolicy(),"anchorSummary_27_01_2014.txt",null,"MHD",i));
            configList.add(new AnchorageExperimentConfig(new WallPack_MHDAnchoragePolicy(),"anchorSummary_27_01_2014.txt",null,"WP_MHD",i));
            configList.add(new AnchorageExperimentConfig(new HybridAnchoragePolicyV2(1,5,10 ,0,200 ),"anchorSummary_27_01_2014.txt",null,"HYB_1_5_10",i));
        }


        AnchorageExperimenter apc = new AnchorageExperimenter(aa,configList,new AhkpArrivalGenerator(0.1));

        apc.performRepeatedExperiment(5);

    }



    public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, InterruptedException
    {
        //testNonUniform_VS_Uniform_AK1();
        //testNonUniform_VS_Uniform_AK2();
        //testNonUniform_VS_Uniform_AK3();
        //testNonUniform_VS_Uniform2();


      //  testNonUniformAreaNoRiskFactor();

       // testUniformAreaDEMOUniform(3);

        int repeatCount = 50;
       // String dateSuffix = StringUtil.now();

       // testUniformAreaNoRiskFactorBeta(repeatCount, dateSuffix + "_BETA");


     //   testUniformAreaNoRiskFactorNormal(repeatCount, dateSuffix+"_NORM");

        testUniformAreaNoRiskFactorAhkpArrival(repeatCount,"_AHKP");
        testUniformAreaNoRiskFactorAhkpGammaArrival(repeatCount,"_GAMMA");
        testUniformAreaNoRiskFactorUniform(repeatCount,"_UNF");
        testUniformAreaNoRiskFactorChiSquare(repeatCount,"_CS");
      /*
        testUniformAreaNoRiskFactorAhkpArrival(repeatCount, dateSuffix+"_AHKP");
        testUniformAreaNoRiskFactorAhkpGammaArrival(repeatCount,dateSuffix+"_GAMMA");
        testUniformAreaNoRiskFactorUniform(repeatCount,dateSuffix+"_UNF");
        testUniformAreaNoRiskFactorChiSquare(repeatCount,dateSuffix+"_CS");
        testParameterTuningForHybrid(repeatCount,dateSuffix+"_PT");
        */
    }

}
