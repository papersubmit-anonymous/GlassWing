# GlassWing: A Step Towards Whole Program Analysis for Flutter Android Apps

![image](https://github.com/papersubmit-anonymous/GlassWing/blob/main/fig/flutterAppOverview_00.png)


The variety of mobile operating systems available in the market has led to the emergence of cross-platform frameworks, which simplify the development and deployment of mobile applications across multiple platforms simultaneously. Among these, the Flutter framework promoted by Google has become the most widely used cross-platform development framework. To date, no work has provided support for the static analysis of Flutter applications on the Android platform. State-of-the-art static analyzers fail to “see” the implicit invocation between the Dart language used by Flutter framework and the Java used by the native Android platform. It poses a significant threat to the completeness of the mobile software analysis community. To solve the problems, we present GlassWing, a step towards a comprehensive static analysis for Flutter Android apps, to build a unified Soot-based intermediate representation, connecting Java and Dart invocation relations, thereby making cross-language data flow visible. We evaluate GlassWing on 1,023 popular real-world Flutter apps, increasing the number of parsed Jimple code lines by 141% and enhancing the call graph with nodes by 28.2% and edges by 28.1%. The enhanced static analysis capability of GlassWing enables existing analyzers to reveal almost triple the sensitive data leaks that were previously undetectable in Flutter apps. We also illustrate the future potential of GlassWing in the downstream fields of program graph analysis, taint analysis, malicious software analysis, and complex cross-platform app generic analysis.

# RQ1

**Table 1：** Ten self-developed benchmark applications with different channels, components (RQ1) and
sensitive data leaks (RQ3).

| **ID** | **Components**    | **#Comps.** | **Channels** | **#Chans.** | **Leaks**           | **#Leaks** |
| ------ | ----------------- | ----------- | ------------ | ----------- | ------------------- | ---------- |
| 1      | F. Act.           | 1           | MC           | 2           | Dev. ID             | 1          |
| 2      | F. Act            | 2           | EC           | 2           | Loc.                | 2          |
| 3      | F. Act            | 3           | BMC          | 2           | Net.                | 1          |
| 4      | F. Act + F. Frag. | 6           | MC + EC      | 5           | Sens.               | 2          |
| 5      | F. Act + F. Frag. | 4           | MC + BMC     | 7           | DB                  | 3          |
| 6      | F. Frag.          | 3           | MC + BMC     | 8           | Clipbd.             | 2          |
| 7      | F. Act + F. Frag. | 4           | EC + BMC     | 3           | Cont. Res           | 3          |
| 8      | F. Frag.          | 5           | MC + EC      | 6           | Dev. ID + Cont. Res | 2          |
| 9      | F. Act + F. Frag. | 8           | MC + EC      | 13          | Net. + Sens.        | 3          |
| 10     | F. Act + F. Frag. | 6           | EC + BMC     | 12          | Clipbd. + DB        | 3          |

*Note：* MethodChannel (MC)，EventChannel (EC)，BasicMessageChannel (BMC)，Device Identification (Dev. ID)，Location (Loc.)，Network (Net.)，Sensor (Sens.)，Database (DB)，Clipboard (Clipbd.)，ContentResolver (Cont. Res)。

# Complex Cross-Platform Applications

| Index |    Package Name     |
| :---: | :-----------------: |
|   1   | com.soulapp.android |
|   2   |     com.yipiao      |
|   3   | com.cubic.autohome  |
|   4   | strip.android.view  |
|   5   |     com.videogo     |
|   6   |      com.Qunar      |

