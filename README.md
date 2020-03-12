# Janus

<!-- badges -->
[![CircleCI](https://circleci.com/gh/lambdaisland/janus.svg?style=svg)](https://circleci.com/gh/lambdaisland/janus) [![cljdoc badge](https://cljdoc.org/badge/lambdaisland/janus)](https://cljdoc.org/d/lambdaisland/janus) [![Clojars Project](https://img.shields.io/clojars/v/lambdaisland/janus.svg)](https://clojars.org/lambdaisland/janus)
<!-- /badges -->

A parser for CHANGELOG files in the format used by Lambda Island Open Source projects.

<!-- opencollective -->
### Support Lambda Island Open Source

If you find value in our work please consider [becoming a backer on Open Collective](http://opencollective.com/lambda-island#section-contribute)
<!-- /opencollective -->

## Usage

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
({:version "Unreleased"
  :added ()
  :fixed ()
  :changed ()}
 {:version "0.0-71"
  :date "2020-02-24"
  :sha "773860f"
  :added ()
  :fixed ("Depend on actual version of Glogi, instead of \"RELEASE\"")
  :changed ()}
 {:version "0.0-68"
  :date "2019-12-25"
  :sha "71c2d86"
  :added ()
  :fixed ("Wait for websocket client namespace to load before attempting to connect. This should help in particular with reliability when running against a browser environment.")
  :changed ("Pick a free port for websockets automatically instead of using a hard-coded port")})
```

<!-- contributing -->
### Contributing

Everyone has a right to submit patches to this projects, and thus become a contributor.

Contributors MUST

- adhere to the [LambdaIsland Clojure Style Guide](https://nextjournal.com/lambdaisland/clojure-style-guide)
- write patches that solve a problem. Start by stating the problem, then supply a minimal solution. `*`
- agree to license their contributions as MPLv2.
- not break the contract with downstream consumers. `**`
- not break the tests.

Contributors SHOULD

- update the CHANGELOG and README.
- add tests for new functionality.

If you submit a pull request that adheres to these rules, then it will almost
certainly be merged immediately. However some things may require more
consideration. If you add new dependencies, or significantly increase the API
surface, then we need to decide if these changes are in line with the project's
goals. In this case you can start by [writing a
pitch](https://nextjournal.com/lambdaisland/pitch-template), and collecting
feedback on it.

`*` This goes for features too, a feature needs to solve a problem. State the problem it solves, then supply a minimal solution.

`**` As long as this project has not seen a public release (i.e. is not on Clojars)
we may still consider making breaking changes, if there is consensus that the
changes are justified.
<!-- /contributing -->

## License

Copyright &copy; 2020 Enyert Vinas and Contributors

Licensed under the term of the Mozilla Public License 2.0, see LICENSE.
