# Handoff — Display.io LevelPlay adapter, Android 5.8.1

Status as of 2026-10-09. Everything below is in the working tree of this repo, **not committed and
not released**. iOS counterpart: `~/Desktop/iOSWork/IronSource_iOS/HANDOFF.md`.

## Context

- Display.io is a **custom network** in Unity LevelPlay (formerly ironSource), network key
  `15bfd7489`. LevelPlay loads the adapter classes by reflection from
  `com.ironsource.adapters.custom.dio`.
- Config keys entered by the publisher in LevelPlay: `dio_app_id` (app level),
  `dio_placement_id` (instance level). Rate-based (waterfall) only — custom networks cannot bid.
- Consent: not set by the adapter. The DIO SDK reads the IAB keys written by the publisher's CMP.
- Ad expiry: not handled by the adapter (out of scope).

## What was done

### Adapter
- Display.io SDK 5.7.1 → **5.8.1**; LevelPlay `com.ironsource.sdk:mediationsdk:9.2.0` →
  **`com.unity3d.ads-mediation:mediation-sdk:9.6.1`** (Maven Central; the is.com repo is gone).
  LevelPlay is `compileOnly` in the adapter — publishers add it themselves. Minimum LevelPlay 9.0.
- New **rewarded video** (`DIOCustomRewardedVideo`).
- Bug fixes and behaviour changes — see [`CHANGELOG.md`](./CHANGELOG.md) (5.8.1).
- Adapter version `5.8.1` (module `version` and `DIOCustomAdapter.ADAPTER_VERSION`).
- [`CLAUDE.md`](./CLAUDE.md) added with the architecture and the rules the adapter follows.

### Test app (`app/`, internal only)
- Tabs: Interstitial, Rewarded, **Banner (new)**, Interscroller, Infeed.
- Global **Use Feed** checkbox (on by default): on — banner, infeed and interscroller render in a
  feed; off — interscroller still in a feed, banner and infeed render below the buttons. Ads never
  cover controls.

### LevelPlay dashboard (test account)
- The old app `1dc3f8185` is rejected by LevelPlay init (HTTP 400 / code 2110) and is renamed
  **"DIO App Android (old)"**.
- New app **"DIO App Android v2"**, app key **`2899bebd5`**, used by the test app. DisplayIO
  instances (`dio_app_id` = `7729`):

| Ad unit | Ad unit ID | DIO placement |
|---|---|---|
| Interstitial | `24j8ex6kkizmok0c` | 5197 (interstitial video) |
| Rewarded | `juhbq4gbwkgiy69h` | 5197 (interstitial video) |
| Banner (320x50) | `png3j7cbl3vopz1x` | 15770 (banner video) |
| Infeed (300x250) | `82fh448z71tmdi71` | 5134 (infeed video) |
| IS — interscroller | `fkh04nw0iei42yl3` | 7022 (interscroller video) |

### Verified so far
- Through LevelPlay on the emulator (Pixel 8 Pro, API 34): all five formats load and show;
  rewarded grants the reward once after completion; clicks reach LevelPlay; Use Feed on/off layouts.
- Directly against the adapter: missing placement ID → missing params (1002); unknown/wrong-type
  placement → internal error; banner placement in a 300x250 slot fails; destroy during load
  delivers nothing; destroy after impression sends no "screen dismissed"; HTML creative in a
  rewarded slot → no fill.
- Not yet run on a physical Android device.

## What is left

### 1. QA (to be handed over)
Use the test app with app key `2899bebd5` on a physical device. For each format check:

| Area | Expected |
|---|---|
| Interstitial | loads, shows, `closed` only after the user closes it (not when the video ends) |
| Rewarded | `rewarded` exactly once, after the video ends, before `closed`; closing early → no reward |
| Banner 320x50 | loads, displays, click opens the landing page |
| Infeed 300x250 | loads, displays, click opens the landing page |
| Interscroller | displays inside the feed, scroll effect works |
| Use Feed on/off | banner/infeed switch between feed and inline; nothing covers the buttons |
| Leaving a tab / reloading | no stale ad, no crash |
| Dashboards | impressions and clicks in LevelPlay vs the Display.io dashboard (after ≥ 1 day of traffic) |

Known LevelPlay limitation: custom adapters are not listed in `validateIntegration` or the
Integration Test Suite, so "adapter version visible in the test suite" cannot be met. The version
is only in the LevelPlay log when the adapter is allocated.

### 2. Release (instructions to follow — do not release yet)
- `./gradlew :adaptorIronSource:makeAar` → `adaptorIronSource/outputs/adapter-ironsource-5.8.1.aar`.
- Publish to `https://maven.display.io/` as
  `com.ironsource.adapters.custom.dio:ironsource-adapter:5.8.1`. No POM file is kept in the repo
  and none is needed.

### 3. Publisher documentation (`~/Desktop/Docs/wp-documentation`)
Section `docs/android-adapters.html#ironsource` is outdated:
- **Screenshots to replace** (all from 2024-03-26, old ironSource UI; LevelPlay is now Unity LevelPlay):
  1. `Screenshot-2024-03-26-at-11.35.34` — Networks → Add custom network
  2. `Screenshot-2024-03-26-at-11.37.52` — entering network key `15bfd7489`
  3. `Screenshot-2024-03-26-at-11.39.13` — DisplayIO app/instance settings (`dio_app_id`, `dio_placement_id`, rate)
  4. `Screenshot-2024-03-26-at-11.41.17` — Management → Mediation (waterfall)
- In the new screenshots replace every real identifier with placeholders —
  `YOUR_APP_ID`, `YOUR_PLACEMENT_ID`, `YOUR_APP_KEY`, `YOUR_AD_UNIT_ID`. The network key
  `15bfd7489` stays — publishers need it.
- Text updates: dependency line `ironsource-adapter:5.7.1` → `5.8.1`; LevelPlay naming instead
  of ironSource; supported formats table (interstitial, rewarded, banner 320x50, infeed /
  medium rectangle 300x250, interscroller); a rewarded instance must point to an interstitial
  placement serving video or audio; consent comes from the publisher's CMP.
- Docs are not deployed by us.

### 4. Open items
- Decide whether to remove the extra custom network "display.io inc" (`15c135dd5`) that is also
  saved in the LevelPlay account. The "(old)" apps stay as they are.
- Commit this repo once approved.
