package glasswing;
import java.util.*;

import java.io.FileWriter;
import java.io.IOException;

import experiments.temPreAnalysisHandler.ReactJSModulesCount;
import reunify.ReactUtil;
import reunify.SootConfigForAndroidRN;

import java.io.BufferedReader;
import java.io.File;

import soot.Scene;
import soot.SootMethod;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.config.IInfoflowConfig;
import soot.jimple.infoflow.results.InfoflowResults;
import java.io.FileReader;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.xmlpull.v1.XmlPullParserException;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

public class DetectNumOfDASideSize {
    static String outFile = "/home/syc/outs/DetectNumOfDASideSize.csv";

    public static void main(String[] args) {
        String appFolder = "/home/syc/apks";
        String outputFolder = "/home/syc/flowdroid";
        String apkPath;
        String outputFile;
        String androidJars = "/home/syc/Android/Sdk/platforms";
        String sourcesAndSinks = "/home/syc/Downloads/SourcesAndSinks.txt";
        JSONArray sha256Array;
        File targetDirectory = new File(appFolder);
        // 存储APK文件名和路径的Map
        Map<String, String> apkFileMap = new HashMap<>();
        // 遍历目录并收集APK文件信息
        collectApkFiles(targetDirectory, apkFileMap);
        String[] data = new String[6];
        data[0] = "Name";
        data[1] = "DartMethods";
        data[2] = "Methods";
        data[3] = "DartLocs";
        data[4] = "Locs";
        //data[5] = "elapsedTime";
        updateCSVRow(data);
        int count = 0;
        // 打印结果
        for (Map.Entry<String, String> entry : apkFileMap.entrySet()) {
            long startTime = System.currentTimeMillis();
            System.out.println("APK Name: " + entry.getKey() + ", Path: " + entry.getValue());
            System.out.println("+++++++++++++++++++++++++++++" +"Currently, working on: " + count + "+++++++++++++++"+ entry.getKey() + "+++++++++++++++++++++++++++++");
            data = new String[6];
            data[0] = entry.getKey();
            apkPath = entry.getValue();
            outputFile = outputFolder + "/" + entry.getKey() + ".txt";
            SetupApplication setupApplication = new SetupApplication(androidJars, apkPath);
            IInfoflowConfig sootConfig = new SootConfigForAndroidFL("/home/syc/blutter");
            setupApplication.setSootConfig(sootConfig);
            InfoflowAndroidConfiguration config = setupApplication.getConfig();
            try {
                config.setMergeDexFiles(true);
                config.getAnalysisFileConfig().setOutputFile(outputFile);
                config.setDataFlowTimeout(600);
                config.getCallbackConfig().setCallbackAnalysisTimeout(600);
                config.getPathConfiguration().setPathReconstructionTimeout(600);
                setupApplication.addPreprocessor(new FlutterDRModulesCount(setupApplication, outputFolder, entry.getKey()));
                InfoflowResults infoflowResults = setupApplication.runInfoflow(sourcesAndSinks);
            } catch (XmlPullParserException | RuntimeException | IOException | OutOfMemoryError e ) {
                e.printStackTrace();
            }
            //updateCSVRow(data,  potentialSHA256);
            //updateCSVRow(data);
        }

    }

    private static void collectApkFiles(File directory, Map<String, String> apkFileMap) {
        // 检查目录是否存在且是否为目录
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        // 递归处理子目录
                        collectApkFiles(file, apkFileMap);
                    } else if (file.isFile() && file.getName().endsWith(".apk")) {
                        // 将APK文件名和路径存入Map
                        apkFileMap.put(file.getName(), file.getAbsolutePath());
                    }
                }
            }
        } else {
            System.out.println("目标路径不是一个有效的目录: " + directory.getAbsolutePath());
        }
    }

    public static List<String> readFromCSV(){
        String line = "";
        String csvDelimiter = ",";
        List<String> sha256List = new ArrayList<String>();
        try (BufferedReader br = new BufferedReader(new FileReader(outFile))) {
            while ((line = br.readLine()) != null) {
                String[] data = line.split(csvDelimiter);
                sha256List.add(data[0]);
            }

        } catch (IOException e) {
            writeAndConstructCSV();
            e.printStackTrace();
        }

        return sha256List;
    }

    public static void updateCSVRow(String[] rowData) {
        try (FileWriter writer = new FileWriter(outFile, true)) { // true to append data to file
            writer.write(String.join(",", rowData) + "\n"); // write new row to file
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[] readOneFailed(){
        String line = "";
        String csvDelimiter = ",";
        String[] targetedSha256 = {"null"};
        try (BufferedReader br = new BufferedReader(new FileReader(outFile))) {

            // Read the remaining lines (data)
            while ((line = br.readLine()) != null) {
                // Split the line using the specified delimiter
                String[] data = line.split(csvDelimiter);

                if(data.length < 6 & !data[1].equals("false")){
                    targetedSha256 = data;
                    break;
                }
            }

        } catch (IOException e) {
            writeAndConstructCSV();
            e.printStackTrace();
        }


        return targetedSha256;
    }



    public static void writeAndConstructCSV(){
        try {
            // create the CSVWriter object
            FileWriter writer = new FileWriter(outFile);

            // write the header row
            writer.write("sha256,isSuccess,numOfEdges,numOfMethods,numOfLeaks,elapsedTime");
            // close the writer
            writer.close();
            System.out.println("Data written to CSV file successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void addCSVRow(String[] rowData){
        String csvFile = outFile;
        FileWriter writer = null;
        try {
            writer = new FileWriter(csvFile, true); // true to append data to file
            writer.write("\n"); // start a new row
            writer.write(rowData[0]+","+rowData[1]+","+rowData[2]+","+rowData[3]+","+rowData[4]+","+rowData[5]); // write the new row

            System.out.println("New row added to CSV file");
        } catch (IOException e) {
            System.out.println("Error adding new row to CSV file: " + e.getMessage());
        } finally {
            try {
                writer.close(); // close the CSV file
            } catch (IOException e) {
                System.out.println("Error closing CSV file: " + e.getMessage());
            }
        }
    }


    public static void updateCSVRow(String[] rowData, String[] oldRowData){
        List<String[]> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(outFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                lines.add(fields);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file = new File(outFile);

        file.delete();

        try (
                FileWriter writer = new FileWriter(outFile, true)) {
            boolean rowUpdated = false;

            for(String[] line:lines){{
                if (line[0].equals(oldRowData[0])) { // found target row
                    writer.write(String.join(",", rowData) + "\n"); // write updated row to file
                    rowUpdated = true;
                } else {
                    writer.write(String.join(",", line) + "\n"); // write non-target rows to file
                }
            }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<SootMethod, SootMethod> getAllReachableMethods(SootMethod initialMethod){
        CallGraph callgraph = Scene.v().getCallGraph();
        List<SootMethod> queue = new ArrayList<>();
        queue.add(initialMethod);
        Map<SootMethod, SootMethod> parentMap = new HashMap<>();
        parentMap.put(initialMethod, null);
        for(int i=0; i< queue.size(); i++){
            SootMethod method = queue.get(i);
            for (Iterator<Edge> it = callgraph.edgesOutOf(method); it.hasNext(); ) {
                Edge edge = it.next();
                SootMethod childMethod = edge.tgt();
                if(parentMap.containsKey(childMethod))
                    continue;
                parentMap.put(childMethod, method);
                queue.add(childMethod);
            }
        }

        return parentMap;
    }
}