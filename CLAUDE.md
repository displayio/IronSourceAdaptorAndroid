# CLAUDE.md

Guidance for working in this repository.

> ## ⛔ NEVER MAKE CHANGES WITHOUT APPROVAL
> Show the plan / diff first and wait for an explicit OK. Read-only investigation is fine.

## What this repo is

A **DIO (display.io / Brandio) custom adapter for Unity LevelPlay (ironSource) mediation**,
Android, Java. Remote: `displayio/IronSourceAdaptorAndroid`.

> **The adapter is the product.** The `app` module is only a manual test harness for it.

### `adaptorIronSource/` — the actual deliverable

Package `com.ironsource.adapters.custom.dio`. LevelPlay finds these classes by reflection from
the names registered for the custom network, so the package and class names must not change and
the consumer ProGuard rule (`consumer-rules.pro`) keeps the whole package.

- `DIOCustomAdapter` (`BaseAdapter`) — init: reads `dio_app_id`, calls `Controller.init`.
  `getAdapterVersion()` returns the adapter's own `ADAPTER_VERSION` (e.g. `5.8.1`, kept equal to
  the module `version` in `build.gradle.kts`); `getNetworkSDKVersion()` returns the DIO SDK version.
  Also holds the shared helpers `loadErrorType` (no fill → `NO_FILL`, anything else → `INTERNAL`)
  and `errorMessage`.
- `DIOCustomInterstitial` (`BaseInterstitial`) — interstitial placement → `ad.showAd(activity)`.
  `onAdEnded` only on completion (once), `onAdClosed` only on `onClosed`.
- `DIOCustomRewardedVideo` (`BaseRewardedVideo`) — same flow on an interstitial (or deprecated
  rewarded-video) placement. Accepts only `MediaAd` (video/audio) creatives — anything else is
  closed and reported as no fill. `onAdRewarded` fires once, from `onAdCompleted`, before close.
- `DIOCustomBanner` (`BaseBanner`) — banner / medium rectangle / infeed / interscroller placements.
  Rejects a placement that does not match the requested `ISBannerSize` (banner placement in a
  300x250 slot and vice versa). `destroyAd` cancels a load in flight and never reports
  "screen dismissed". Layout params carry `Gravity.CENTER`.

Shared rules for all ad classes:
- A missing `dio_placement_id` fails the load with `AdapterErrors.ADAPTER_ERROR_MISSING_PARAMS`.
- `onFailedToShow` after the ad was impressed is ignored (LevelPlay already counted the show).
- Main-thread work goes through `com.brandio.ads.tools.Utils.runOnMainThread`; `destroyAd` captures
  the ad into a local before posting `close`, so a quick destroy → load cannot close the new ad.

Config keys read from LevelPlay: `dio_app_id` (app level), `dio_placement_id` (instance level).
LevelPlay custom network key: `15bfd7489`. Consent is not set by the adapter — the DIO SDK reads
the IAB keys the publisher's CMP writes. Ad expiry is not handled by the adapter.

### `app/` — test app only

Manual harness for the adapter. **Not a shipped artifact.** LevelPlay app key and ad unit IDs are
constants in `MainActivity` (`APP_KEY`, `*_AD_UNIT_ID`). Tabs: Interstitial, Rewarded, Banner,
Interscroller, Infeed.

A global **Use Feed** checkbox (main layout, above the version labels, on by default):
- on — banner, infeed and interscroller are shown in a feed (`ui/AdFeed`, ad at position 5);
- off — interscroller still uses the feed, banner and infeed are placed straight into the
  fragment's `ad_container`, which sits below the load/show buttons, so ads never cover controls.

## Dependencies & build

- DIO SDK `com.brandio.ads:sdk:5.8.1` (repo `https://maven.display.io/`).
- LevelPlay `com.unity3d.ads-mediation:mediation-sdk:9.6.1` from **Maven Central**. The old
  `com.ironsource.sdk:mediationsdk` / `android-sdk.is.com` distribution ended in June 2025 — do not
  bring it back. The adapter uses the LevelPlay 9.x custom-adapter API (`loadAd(AdData, Context, …)`,
  `showAd(AdData, Activity, …)`), so LevelPlay 9.0 is the minimum.
- AGP 8.10.0, Gradle 8.11.1, compileSdk 36; adapter minSdk 21, test app minSdk 31.

```bash
./gradlew :app:assembleDebug          # build the test app (also compiles the adapter)
./gradlew :adaptorIronSource:makeAar  # release AAR → adaptorIronSource/outputs/adapter-ironsource-<version>.aar
```

Validate changes with compile tasks; only build the release AAR when actually releasing.

## Things to know

- Test IDs: LevelPlay appKey `2899bebd5` ("DIO App Android v2"), DIO appId `7729`.
  The old app `1dc3f8185` ("DIO App Android (old)") is rejected by LevelPlay init with HTTP 400
  (code 2110) — do not switch back to it.
- DIO placements of app 7729 used by the LevelPlay instances: interstitial / rewarded `5197`
  (interstitial video), banner `15770`, infeed `5134`, interscroller `7022`.
- LevelPlay never shows custom adapters in `validateIntegration` or the Test Suite; it only logs
  the adapter version when the adapter is allocated.
- The iOS counterpart lives in `~/Desktop/iOSWork/IronSource_iOS` — keep event mapping in parity.
- Adapter versions are three-part and **always equal the DIO SDK version** they ship with
  (e.g. `5.8.1`): bump the module `version` and `DIOCustomAdapter.ADAPTER_VERSION` on every DIO SDK
  update.
- On **every** update (SDK bump, fix, feature) update all three files in the same change:
  [`CHANGELOG.md`](./CHANGELOG.md) (new section for the version), [`HANDOFF.md`](./HANDOFF.md)
  (current state, open problems, next steps) and this `CLAUDE.md` (versions, rules, structure).
- Commits: Conventional Commits; **do not add a Co-Authored-By trailer and do not mention Claude
  in commit messages.**
