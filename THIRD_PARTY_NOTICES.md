# Third-party notices

Everything in this repository is licensed under the [MIT License](LICENSE), **except** the file listed below.

## HeartyFilter.java

|                      |                                                                                                                                                |
| -------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------- |
| **File**             | [`app/src/main/java/io/medicaldev/pantompkinstesting/HeartyFilter.java`](app/src/main/java/io/medicaldev/pantompkinstesting/HeartyFilter.java) |
| **Origin**           | The _Hearty_ Android application, © 2012 Pattern Recognition Lab, University Erlangen-Nuremberg (author: Stefan Gradl)                         |
| **License**          | [Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported (CC BY-NC-SA 3.0)](https://creativecommons.org/licenses/by-nc-sa/3.0/)     |
| **Used by the app?** | No. The live pipeline in `Filter.java` doesn't reference it.                                                                                   |

The original licence header is preserved in the file. Its terms include **non-commercial use only** and **share-alike**, and its header asks anyone reusing the code to keep the comment or cite:

> S. Gradl, P. Kugler, C. Lohmüller, and B. Eskofier, "Real-time ECG monitoring and arrhythmia detection using Android-based mobile devices," in _34th Annual International Conference of the IEEE EMBS_, 2012, pp. 2452–2455.

If you need a codebase that is MIT end to end (for example, for commercial use), remove `HeartyFilter.java`. Nothing else in the project depends on it.
