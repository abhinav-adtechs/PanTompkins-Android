# Contributing to PanTompkins-Android

Thanks for your interest! This is a 2016 project with a 2019 toolchain refresh. The most valuable contributions are the ones on the [roadmap](README.md#roadmap): getting `HEAD` to build cleanly, fixing the derivative stage, adding tests, and implementing the decision stage.

Please follow the [Code of Conduct](https://github.com/abhinav-adtechs/.github/blob/main/CODE_OF_CONDUCT.md). To report a security problem, see [SECURITY.md](https://github.com/abhinav-adtechs/.github/blob/main/SECURITY.md) and don't open a public issue.

## Development setup

1. Fork and clone the repo.
2. Use an Android Studio release that supports AGP 3.4.1 / Gradle 5.1.1 (the 3.4–3.6 line), with JDK 8 and SDK Platform 28. If you are migrating the build to a current toolchain, say so in your PR. That migration is a roadmap item of its own.
3. Apply the one-line manifest fix from the README's [Quick start](README.md#quick-start) if it hasn't landed yet.
4. Put a 250 Hz single-column ECG CSV at `/sdcard/data.csv` on your device or emulator and grant storage permission:

   ```bash
   adb push data.csv /sdcard/data.csv
   adb shell pm grant io.medicaldev.pantompkinstesting android.permission.READ_EXTERNAL_STORAGE
   ```

5. Run the `app` configuration. Watch stage values with `adb logcat | grep "readCSV:"`.

There is no `gradlew` script in the repo, only `gradle/wrapper/gradle-wrapper.properties`. To build from the command line, generate the wrapper with a local Gradle install (`gradle wrapper --gradle-version 5.1.1`), but **don't commit the generated files** unless your PR is specifically about the build.

## Working on the DSP code

- Read [`docs/ALGORITHM.md`](docs/ALGORITHM.md) first. It maps every equation to a method and line number.
- Keep each stage **streaming**: one call per sample, state in fields, no look-ahead.
- If you change a filter's behaviour, show before/after evidence in the PR: an impulse response, a step response, or Logcat output on the same input.
- Update `docs/ALGORITHM.md` in the same PR whenever a stage's equation, gain, delay or line numbers change.
- Don't extend `HeartyFilter.java`. It is CC BY-NC-SA 3.0 (see [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md)), so new code belongs in MIT-licensed files.

## Branches and commits

- Branch from `master` and use a descriptive name, such as `fix/derivative-delay-line` or `feat/adaptive-thresholds`.
- Use [Conventional Commits](https://www.conventionalcommits.org/):

  ```text
  fix(filter): shift derivative delay line before applying taps
  feat(peaks): add SPKI/NPKI adaptive thresholds and 200 ms refractory period
  docs(algorithm): document high-pass group delay
  test(filter): impulse-response tests for LowPassFilter
  build: migrate to AndroidX and mavenCentral
  ```

- Keep commits focused, and write the subject in the imperative mood in under 72 characters.

## Filing issues

- **Bugs:** say what you ran (device/emulator API level, Android Studio version), describe the input file (sampling rate and an excerpt of the first lines), and state what you expected versus what you saw. Logcat output helps.
- **Features:** describe the use case and, for algorithm changes, point to the relevant section of the paper or of `docs/ALGORITHM.md`.
- Search [existing issues](https://github.com/abhinav-adtechs/PanTompkins-Android/issues?q=is%3Aissue) first.

## Pull request checklist

- [ ] The app builds and runs on at least one device or emulator (state which in the PR).
- [ ] The change is scoped to one concern, with a Conventional Commit title.
- [ ] DSP changes include before/after evidence on the same input.
- [ ] `docs/ALGORITHM.md` / `docs/ARCHITECTURE.md` are updated if behaviour, equations or line references changed.
- [ ] No machine-specific files (`local.properties`, `.idea/workspace.xml`, build outputs) added or modified.
- [ ] No new code added to `HeartyFilter.java`.

## License

By contributing, you agree that your contributions are licensed under the [MIT License](LICENSE).
