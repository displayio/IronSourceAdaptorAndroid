# Changelog

Display.io custom adapter for Unity LevelPlay (ironSource), Android.

## 5.8.1

### Added
- Rewarded video. The reward is granted once, after the ad has been watched to the end; closing early grants nothing.
- Rewarded slots only accept video and audio creatives. Any other creative is reported to LevelPlay as no fill, so users never sit through an ad that cannot reward them.

### Changed
- Built against Display.io SDK 5.8.1 and LevelPlay 9.6.1. LevelPlay is now resolved from Maven Central (`com.unity3d.ads-mediation:mediation-sdk`); the minimum supported LevelPlay version is 9.0.
- The adapter reports its own version to LevelPlay instead of the Display.io SDK version.
- Load errors are classified: no fill is reported as no fill, everything else as an internal error. A missing placement ID is reported as missing parameters.
- A banner placement now has to match the requested ad size (320x50 banner vs 300x250 infeed / medium rectangle); a mismatch fails the load instead of rendering the wrong size.
- Banners are centered in the LevelPlay ad view.

### Fixed
- Interstitial video reported "ad ended" twice.
- A show failure could be reported after the ad had already been displayed.
- A banner destroyed while it was still loading was delivered anyway and counted an impression.
- Destroying a displayed banner reported a spurious "screen dismissed" event.
