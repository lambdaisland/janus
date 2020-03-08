# janus

A Clojure library designed to ... well, that part is up to you.

## Usage

FIXME

## License

Copyright © 2020 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.


Example  THIS WILL BE REMOVED

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
