#  GlassWing: A Tailored Static Analysis Approach for Flutter Android Apps

[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![GitHub Stars](https://img.shields.io/github/stars/papersubmit-anonymous/GlassWing?style=social)](https://github.com/papersubmit-anonymous/GlassWing/stargazers)
[![arXiv](https://img.shields.io/badge/arXiv-coming.soon-b31b1b.svg)](https://arxiv.org/abs/YOUR_PAPER_ID)

**GlassWing is the first tailored static analysis approach for Flutter Android apps, designed to bridge the gap between Dart and Java/Kotlin by revealing their implicit invocation relations for a more comprehensive analysis.**
![image](https://github.com/papersubmit-anonymous/GlassWing/blob/main/fig/flutterAppOverviewNew_00.png)

PS: The dataset is so huge , so we provide a [Dropbox](https://www.dropbox.com/scl/fo/mbasmdn12j7izokd4skuk/ANrcRucnYwg98fI1nAMq5eA?rlkey=uavpcsnbvz3zo0b5lpz5rj180&st=aieabayk&dl=0) link for download 

## 📖 Introduction

With the rise of cross-platform frameworks, **Flutter**, introduced by Google, has become the most popular choice for mobile app development. However, existing static analysis tools (e.g., Soot, FlowDroid) fail to "see" the **implicit invocations** between the Dart language used by Flutter and the native Java code of the Android platform. This analytical blind spot poses a significant threat to the security and completeness of mobile software analysis.

**GlassWing** is introduced to address this challenge. It is the first tailored static analysis approach for Flutter Android apps. By leveraging a data-flow-oriented approach, GlassWing extracts key program semantics and makes the previously invisible Dart-DEX invocation relations visible, effectively bridging the cross-language analysis gap.

## ✨ Core Features

- **🎯 First-of-its-Kind**: The pioneering static analysis enhancement solution specifically for Flutter Android apps.
- **🌉 Cross-Language Bridge**: Automatically identifies and resolves implicit calls between Flutter's Dart code and Android's native Java/Kotlin code.
- **🔍 In-Depth Analysis**: Significantly enhances existing tools like Soot by increasing parsed code volume and call graph completeness.
- **🛡️ Vulnerability Discovery**: Uncovers sensitive data leaks missed by traditional taint analysis tools like FlowDroid.
- **🔌 Seamless Integration**: Can be easily incorporated into existing Android analysis pipelines, empowering downstream research fields such as program graph analysis, taint analysis, and malware detection.

## 🛠️ How It Works

The core mechanism of GlassWing involves parsing Flutter's AOT (Ahead-of-Time) compiled artifacts, correlating Dart call-site information with Android's Jimple intermediate representation, and ultimately constructing a more complete program call graph that includes these cross-language calls.

## 🚀 Getting Started

### **Directory Structure**

```
artifacts/
├── flutter_apk_analyzer.py      # Main analysis script
├── flowdroid-config.yaml        # FlowDroid configuration template
├── Glasswing.jar               # Glasswing static analysis tool
├── SourcesAndSinks.txt         # Data flow sources and sinks definition
├── coreir_processor # CoreIR processor
├── blutter-patch/              # Blutter decompilation tool
│   ├── blutter.py             # Blutter main script
│   └── ...                    # Other Blutter files
├── apkdir/                     # APK files storage directory
└── outdir/                     # Analysis results output directory
```

### System Requirements

**Software Environment**

- **Python 3.10+**
- **Java 11+** (for running Glasswing.jar)

**Python Dependencies**

```bash
pip install PyYAML
```

**Other Tools**

- **Android SDK** (requires android.jar file)
- **CMake** and **Ninja** (for Blutter compilation, if needed)

### Configuration Steps

#### Environment Setup

**Install Python Dependencies**

```bash
# Install required Python packages
pip install PyYAML

# Or using conda
conda install pyyaml
```

**Configure Android SDK**

Ensure Android SDK is installed and note the `android.jar` file path, typically located at:
```
/Users/<username>/Library/Android/sdk/platforms/android-30/android.jar
```

#### Configuration File Setup

**Modify flowdroid-config.yaml**

Open the `flowdroid-config.yaml` file and modify the following paths according to your environment:

```yaml
# Android SDK platform JAR file path
androidJars: "/Users/your_username/Library/Android/sdk/platforms/android-30/android.jar"

# Sources and Sinks configuration file path
sourcesAndSinks: "/Users/xxxxx/Person/TMP/Batch/SourcesAndSinks.txt"

# DSIR batch processor executable path
dsirBatchProcessorPath: "/Users/xxxxxx/Person/TMP/Batch/coreir_processor"

# Analysis timeout configuration (in seconds)
dataFlowTimeout: 1800
callbackAnalysisTimeout: 1800
pathReconstructionTimeout: 1800
```

**Important Configuration Notes:**
- `androidJars`: Update to your Android SDK path
- `sourcesAndSinks`: Keep current path or modify based on actual location
- `dsirBatchProcessorPath`: Ensure CoreIR processor has execution permissions
- Timeout configuration: Adjust based on your hardware performance and APK complexity

**Ensure Executable Permissions**

```bash
# Add execution permission to CoreIR processor
chmod +x /Users/xxxxxx/Person/TMP/Batch/coreir_processor

# Add execution permission to Blutter script
chmod +x /Users/xxxxxx/Person/TMP/Batch/blutter-patch/blutter.py
```

#### Prepare APK Files

Place APK files to be analyzed in the `apkdir` directory:

```bash
# Create APK directory (if it doesn't exist)
mkdir -p apkdir

# Copy APK files to the directory
cp /path/to/your/app.apk apkdir/
cp /path/to/another/app.apk apkdir/
```

Supported file formats:
- `.apk` files
- `.APK` files (uppercase extension)

### Usage

```bash
# Basic syntax
python flutter_apk_analyzer.py <apk_directory> <output_directory>

# Example: Analyze all APKs in apkdir, output results to outdir
python flutter_apk_analyzer.py apkdir outdir

# Using absolute paths
python flutter_apk_analyzer.py /Users/xxxx/Person/TMP/Batch/apkdir /Users/xxxx/Person/TMP/Batch/outdir
```

## 📄 License

This project is licensed under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).
