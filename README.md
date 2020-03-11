# janus

<!-- badges -->
[![CircleCI](https://circleci.com/gh/lambdaisland/janus.svg?style=svg)](https://circleci.com/gh/lambdaisland/janus) [![cljdoc badge](https://cljdoc.org/badge/lambdaisland/janus)](https://cljdoc.org/d/lambdaisland/janus) [![Clojars Project](https://img.shields.io/clojars/v/lambdaisland/janus.svg)](https://clojars.org/lambdaisland/janus)
<!-- /badges -->

A parser for CHANGELOG files in the format used by Lambda Island Open Source projects.

``` clojure
(parse "# Unreleased
## Added
## Fixed
## Changed
# 0.0-71 (2020-02-24 / 773860f)
## Fixed
- Depend on an actual version of Glogi, instead or "RELEASE"
# 0.0-68 (2019-12-25 / 71c2d86)
## Fixed
- Wait for websocket client namespace to load before attempting to connect. This
should help in particular with reliability when running against a browser
environment.
## Changed
- Pick a free port for websockets automatically instead of using a hard-coded port
")
;;=>
[{:version "Unreleased"}
 {:version "0.0-71"
  :date "2020-02-24"
  :sha "773860f"
  :fixed ["Depend on actual version of Glogi, instead of \"RELEASE\""]}
 {:version "0.0-68"
  :date "2019-12-25"
  :sha "71c2d86"
  :fixed ["Wait for websocket client namespace to load before attempting to connect. This should help in particular with reliability when running against a browser environment."]
  :changed ["Pick a free port for websockets automatically instead of using a hard-coded port"]}]
```

## License

Copyright &copy; 2020 Enyert Vinas and Contributors

Licensed under the term of the Mozilla Public License 2.0, see LICENSE.
