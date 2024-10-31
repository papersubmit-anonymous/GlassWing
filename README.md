# GlassWing
A Step Towards Whole Program Analysis for Flutter Android Apps

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

