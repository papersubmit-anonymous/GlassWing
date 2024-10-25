package glasswing;

import reunify.ReactUtil;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.dart.text.DartTypeFactory;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.entryPointCreators.DefaultEntryPointCreator;
import soot.jimple.infoflow.handlers.PreAnalysisHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class FlutterDRModulesCount implements PreAnalysisHandler {

    SetupApplication app;
    String resultsDir;
    String appName;

    static String outFile = "/home/syc/outs/DetectNumOfDASideSize.csv";

    public FlutterDRModulesCount(SetupApplication app, String resultsDir, String appName) {
        this.app = app;
        this.resultsDir = resultsDir;
        this.appName = appName;
    }

    @Override
    public void onBeforeCallgraphConstruction() {
        String[] result = new String[6];
        result[0] = appName;
        int numberOfDartMethod = 0;
        try{
            SootClass dartClass = glasswing.FlutterUtil.getDartClass();
            SootClass cl = Scene.v().getSootClass(DartTypeFactory.DART_HERMES);
            System.out.println(dartClass.getMethods().size());
            System.out.println("React entry point is created");
            Map<SootClass, Collection<SootMethod>> dartMethods = FlutterUtil.getFlutterModules();
            List<SootMethod> allMethods = new ArrayList<>();

            for (Map.Entry<SootClass, Collection<SootMethod>> entry : dartMethods.entrySet()) {
                Collection<SootMethod> methods = entry.getValue();
                allMethods.addAll(methods);
            }

            int dartMethodConut = dartClass.getMethods().size() + cl.getMethodCount(); //+ allMethods.size();


            int sizeOfReactNativeJSUnits = 0;
            for (SootMethod sootMethod : dartClass.getMethods()) {
                sizeOfReactNativeJSUnits += sootMethod.getActiveBody().getUnits().size();
            }
            for (SootMethod sootMethod : cl.getMethods()) {
                try{
                    sizeOfReactNativeJSUnits += sootMethod.getActiveBody().getUnits().size();
                }catch (Exception e){

                }
            }
            // Initialize counters
            int nonDartMethodCount = FlutterUtil.getnonDartMethodCount();
            int nonDartMethodSize = FlutterUtil.getnonDartMethodSize();
            result[1] = Integer.toString(dartMethodConut);
            result[2] = Integer.toString(nonDartMethodCount);
            result[3] = Integer.toString(sizeOfReactNativeJSUnits);
            result[4] = Integer.toString(nonDartMethodSize);
        }catch (Exception e){
            e.printStackTrace();
            result[1] = "unknown";
            result[2] = "unknown";
            result[3] = "unknown";
            result[4] = "unknown";
        }
        updateCSVRow(result);
    }

    @Override
    public void onAfterCallgraphConstruction() {

    }

    public static void updateCSVRow(String[] rowData) {
        try (FileWriter writer = new FileWriter(outFile, true)) { // true to append data to file
            writer.write(String.join(",", rowData) + "\n"); // write new row to file
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
