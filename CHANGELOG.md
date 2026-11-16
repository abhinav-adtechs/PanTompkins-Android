# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html). Versions before 1.0.0 were tagged retroactively at milestone commits from the original 2016 history.

## [Unreleased]

### Added

- README with quick start, usage/API reference and roadmap.
- `docs/ALGORITHM.md`: stage-by-stage maths of the implemented pipeline, with line references, verified cut-offs and delays, and the paper's decision rules for reference.
- `docs/ARCHITECTURE.md`: class map, runtime data flow, build configuration.
- MIT `LICENSE`, `THIRD_PARTY_NOTICES.md` (for `HeartyFilter.java`, CC BY-NC-SA 3.0), and `CONTRIBUTING.md`.

## [1.0.0] - 2019-05-28

Toolchain refresh and namespace rename. Matches `versionName "1.0"` in `app/build.gradle`.

### Changed

- Moved all sources from `io.cardeadev.pantompkinstesting` to `io.medicaldev.pantompkinstesting` and changed `applicationId` to match (`1e43b9f`, `55f5844`).
- Upgraded the Android Gradle Plugin from 2.1.0 to 3.4.1 and the Gradle wrapper from 2.10 to 5.1.1, and added the `google()` repository (`55f5844`).
- Raised `compileSdkVersion`/`targetSdkVersion` from 23 to 28 and `appcompat-v7` from 23.4.0 to 28.0.0. Dropped the explicit `buildToolsVersion` (`55f5844`).

### Removed

- Template `ApplicationTest` and `ExampleUnitTest` (`55f5844`).

### Known issues

- The manifest `package` (`io.medicaldev.pantompkins`) no longer matches the Java package (`io.medicaldev.pantompkinstesting`), so `R` doesn't resolve until they're aligned.
- `local.properties` and `.idea/` project files were committed (`1e43b9f`).

## [0.2.0] - 2016-07-13

The paper's integer-coefficient filters, tuned for 200 Hz.

### Added

- `Filter.LowPassFilter()` and `Filter.HighPassFilter()`: the Pan–Tompkins low-pass and high-pass recursions with double-write ring buffers (`8c0d480`).
- `HeartyFilter.java`, a third-party filter toolbox (CC BY-NC-SA 3.0) (`8c0d480`).
- `FindPeaks` scaffold for the decision stage (`8c0d480`, `e61661f`, `16d3a69`).

### Changed

- The live pipeline now uses the recursive filters instead of the coefficient-sum `lowPassNext`/`highPassNext` (`8c0d480`).
- The CSV reader drops every fifth row so a 250 Hz recording runs at ~200 Hz (`e61661f`).
- Input path set back to `/sdcard/data.csv` (from `/sdcard/Movies/data.csv`), per-sample logging of every stage value re-enabled, and the chart's left-axis minimum fixed at −20 (`e61661f`).
- Moving-window width retuned from 38 to 30 samples (`e61661f`), then to 80 (`16d3a69`).

## [0.1.0] - 2016-07-05

First streaming prototype.

### Added

- `Filter` class with low-pass, high-pass, derivative and squaring stages (`92ff28a`, `412b1da`, `033d0a3`).
- Moving-window integration backed by a bounded `FixedQueue` (`669e66a`).
- Android app that reads an ECG CSV on a worker thread and plots it live with MPAndroidChart (`c829f8f`, `49d1b3c`).
- Two-series chart: raw ECG and integrated envelope together, plus the `SampleMulti` multi-line prototype (`35a73d5`, `fcd1ee0`).

### Removed

- Offline prototype `QRS_temp.java` (`f54890c`).

[Unreleased]: https://github.com/abhinav-adtechs/PanTompkins-Android/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/abhinav-adtechs/PanTompkins-Android/compare/v0.2.0...v1.0.0
[0.2.0]: https://github.com/abhinav-adtechs/PanTompkins-Android/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/abhinav-adtechs/PanTompkins-Android/commits/v0.1.0
