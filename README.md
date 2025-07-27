#  GlassWing: A Tailored Static Analysis Approach for Flutter Android Apps

[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![GitHub Stars](https://img.shields.io/github/stars/papersubmit-anonymous/GlassWing?style=social)](https://github.com/papersubmit-anonymous/GlassWing/stargazers)
[![arXiv](https://img.shields.io/badge/arXiv-coming.soon-b31b1b.svg)](https://arxiv.org/abs/YOUR_PAPER_ID)
[![Build Status](https://img.shields.io/github/actions/workflow/status/papersubmit-anonymous/GlassWing/build.yml?branch=main)](https://github.com/papersubmit-anonymous/GlassWing/actions)

**GlassWing is the first tailored static analysis approach for Flutter Android apps, designed to bridge the gap between Dart and Java by revealing their implicit invocation relations for a more comprehensive analysis.**
![image](https://github.com/papersubmit-anonymous/GlassWing/blob/main/fig/flutterAppOverviewNew_00.png)

PS: The DataSet && artifacts, so we provide a [OneDrive](https://1drv.ms/f/c/a2905014e63119d7/EmeemqEEqNRJgHevcu8uN3YBKKyAmT2qFZ5PcdAMP_-oqA?e=sUEkTJ) link for download 

## 📖 Introduction

With the rise of cross-platform frameworks, **Flutter**, introduced by Google, has become the most popular choice for mobile app development. However, existing static analysis tools (e.g., Soot, FlowDroid) fail to "see" the **implicit invocations** between the Dart language used by Flutter and the native Java code of the Android platform. This analytical blind spot poses a significant threat to the security and completeness of mobile software analysis.

**GlassWing** is introduced to address this challenge. It is the first tailored static analysis approach for Flutter Android apps. By leveraging a data-flow-oriented approach, GlassWing extracts key program semantics and makes the previously invisible Dart-Java invocation relations visible, effectively bridging the cross-language analysis gap.

## ✨ Core Features

- **🎯 First-of-its-Kind**: The pioneering static analysis enhancement solution specifically for Flutter Android apps.
- **🌉 Cross-Language Bridge**: Automatically identifies and resolves implicit calls between Flutter's Dart code and Android's native Java code.
- **🔍 In-Depth Analysis**: Significantly enhances existing tools like Soot by increasing parsed code volume and call graph completeness.
- **🛡️ Vulnerability Discovery**: Uncovers sensitive data leaks missed by traditional taint analysis tools like FlowDroid.
- **🔌 Seamless Integration**: Can be easily incorporated into existing Android analysis pipelines, empowering downstream research fields such as program graph analysis, taint analysis, and malware detection.

## 🛠️ How It Works

The core mechanism of GlassWing involves parsing Flutter's AOT (Ahead-of-Time) compiled artifacts, correlating Dart call-site information with Android's Jimple intermediate representation, and ultimately constructing a more complete program call graph that includes these cross-language calls.

## 🚀 Getting Started

### Prerequisites

- Java 17 or later
- Python 3.9 or later

### Find Java side-channel information
```bash
java -jar target/flowdroidtest-1.0-SNAPSHOT-jar-with-dependencies.jar /path/to/your/app.apk
```

## 📜 How to cite

If you use GlassWing in your research, please cite our paper:

## 🤝 Contribution Guide

We welcome contributions in any form! Whether it's submitting issues, fixing bugs, or suggesting new features, please feel free to create a Pull Request.

## 📄 License

This project is licensed under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).
